/*
 * MIT License
 *
 * Copyright (c) 2021 0utplay (Aldin Sijamhodzic)
 * Copyright (c) 2021 contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package de.tentact.languageapi.message

import com.zaxxer.hikari.HikariDataSource
import de.tentact.languageapi.cache.CacheProvider
import de.tentact.languageapi.database.MySQLDatabaseProvider
import de.tentact.languageapi.language.LocaleHandler
import java.sql.SQLException
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.function.Function
import java.util.function.Supplier

class MySQLMessageHandler(localeHandler: LocaleHandler,
                          private val mySQLDatabaseProvider: MySQLDatabaseProvider,
                          cacheProvider: CacheProvider
) : DefaultMessageHandler(localeHandler, cacheProvider), MessageHandler {

  override fun loadIdentifier(identifier: Identifier): Identifier {
    identifierCache.getIfPresent(identifier.translationKey)?.let {
      return it
    }
    try {
      dataSource.connection.use { connection ->
        connection.prepareStatement("SELECT parameters FROM IDENTIFIER WHERE translationKey=?;").use { preparedStatement ->
          preparedStatement.setString(1, identifier.translationKey)
          preparedStatement.executeQuery().use { resultSet ->
            if (resultSet.next()) {
              val parameter = resultSet.getString("parameters").split(",").toTypedArray()
              identifier.parameters(*parameter)
            }
            super.cacheIdentifier(identifier)
            return identifier
          }
        }
      }
    } catch (throwables: SQLException) {
      throwables.printStackTrace()
    }
    return identifier
  }

  override fun writeIdentifier(identifier: Identifier) {
    try {
      dataSource.connection.use { connection ->
        connection.prepareStatement("INSERT INTO IDENTIFIER (translationKey, parameters) VALUES (?, ?) ON DUPLICATE KEY UPDATE parameters=?;").use { preparedStatement ->
          val joinedParameters = java.lang.String.join(",", identifier.parameters.values)
          preparedStatement.setString(1, identifier.translationKey)
          preparedStatement.setString(2, joinedParameters)
          preparedStatement.setString(3, joinedParameters)
          preparedStatement.execute()
          super.cacheIdentifier(identifier)
        }
      }
    } catch (throwables: SQLException) {
      throwables.printStackTrace()
    }
  }

  override fun getMessage(identifier: Identifier, locale: Locale): String {
    val cacheMap: Map<Identifier, String>? = translationCache.getIfPresent(locale)

    cacheMap?.let {
      val cachedIdentifier = it[identifier]
      cachedIdentifier?.let {
        return cachedIdentifier
      }
    }
    if (!super.localeHandler.isAvailable(locale)) {
      return identifier.translationKey
    }
    try {
      dataSource.connection.use { connection ->
        connection.prepareStatement("SELECT translation FROM " +
          locale.toLanguageTag().uppercase(Locale.getDefault()) + " WHERE translationKey=?;").use { preparedStatement ->
          preparedStatement.setString(1, identifier.translationKey)
          preparedStatement.executeQuery().use { resultSet ->
            if (resultSet.next()) {
              val translation = super.translateColor(resultSet.getString("translation"))
              super.cacheTranslation(identifier, locale, translation)
              return translation
            }
          }
        }
      }

    } catch (throwables: SQLException) {
      throwables.printStackTrace()
    }
    return identifier.translationKey
  }

  override fun getMessages(locale: Locale, fromCache: Boolean): CompletableFuture<Map<Identifier, String>> {
    return super.localeHandler.isAvailableAsync(locale).thenApplyAsync(Function<Boolean, Map<Identifier, String>> { isAvailable: Boolean ->
      if (!isAvailable) {
        return@Function HashMap<Identifier, String>()
      }
      if (fromCache) {
        return@Function super.translationCache.getIfPresent(locale) ?: HashMap()
      }
      val translations: MutableMap<Identifier, String> = HashMap()
      try {
        dataSource.connection.use { connection ->
          connection.prepareStatement("SELECT * FROM " + locale.toLanguageTag().uppercase(Locale.getDefault()) + ";").use { preparedStatement ->
            preparedStatement.executeQuery().use { resultSet ->
              while (resultSet.next()) {
                translations[Identifier.of(resultSet.getString("translationkey"))] = resultSet.getString("translation")
              }
              super.translationCache.put(locale, translations)
            }
          }
        }
      } catch (throwables: SQLException) {
        throwables.printStackTrace()
      }
      translations
    })
  }

  override fun getIdentifier(locale: Locale, fromCache: Boolean): CompletableFuture<Set<Identifier>> {
    if (fromCache) {
      val translations = super.translationCache.getIfPresent(locale)
      return CompletableFuture.supplyAsync(Supplier {
        if (translations == null) {
          return@Supplier HashSet<Identifier>()
        }
        translations.keys
      })
    }
    return getMessages(locale, false).thenApply { obj: Map<Identifier, String?> -> obj.keys }
  }

  override fun getGlobalIdentifier(fromCache: Boolean): CompletableFuture<Set<Identifier>> {
    return CompletableFuture.supplyAsync {
      val identifiers: MutableSet<Identifier> = HashSet(super.identifierCache.values)
      if (fromCache) {
        return@supplyAsync identifiers
      }
      try {
        dataSource.connection.use { connection ->
          connection.prepareStatement("SELECT * FROM IDENTIFIER;").use { preparedStatement ->
            preparedStatement.executeQuery().use { resultSet ->
              while (resultSet.next()) {
                val identifier = Identifier.of(resultSet.getString("translationkey"),
                  *resultSet.getString("parameters").split(",").toTypedArray())
                identifiers.remove(identifier)
                identifiers.add(identifier)
              }
            }
          }
        }
      } catch (throwables: SQLException) {
        throwables.printStackTrace()
      }
      super.cacheIdentifier(identifiers)
      identifiers
    }
  }

  override fun translateMessage(identifier: Identifier, locale: Locale, translation: String, replaceIfExists: Boolean) {
    super.localeHandler.isAvailableAsync(locale).thenAcceptAsync { isAvailable: Boolean ->
      if (!isAvailable) {
        return@thenAcceptAsync
      }
      try {
        dataSource.connection.use { connection ->
          connection.prepareStatement("SELECT * FROM " + locale.toLanguageTag().uppercase(Locale.getDefault()) + " WHERE translationkey=?;").use { selectStatement ->
            selectStatement.setString(1, identifier.translationKey)
            selectStatement.executeQuery().use { resultSet ->
              if (resultSet.next() && !replaceIfExists) {
                return@thenAcceptAsync
              }
              connection.prepareStatement(
                "INSERT INTO " + locale.toLanguageTag().uppercase(Locale.getDefault()) + " (translationkey, translation) VALUES (?, ?) ON DUPLICATE KEY UPDATE translation=?;").use { insertStatement ->
                val formattedTranslation = super.translateColor(translation)
                insertStatement.setString(1, identifier.translationKey)
                insertStatement.setString(2, formattedTranslation)
                insertStatement.setString(3, formattedTranslation)
                insertStatement.execute()
              }
            }
          }
        }
      } catch (throwables: SQLException) {
        throwables.printStackTrace()
      }
      super.cacheTranslation(identifier, locale, translation)
    }
  }

  override fun translateMessage(translations: MutableMap<Identifier, String>, locale: Locale, replaceIfExists: Boolean) {
    super.localeHandler.isAvailableAsync(locale).thenAcceptAsync { isAvailable: Boolean ->
      if (!isAvailable) {
        return@thenAcceptAsync
      }
      val existingTranslationKeys: MutableSet<Identifier> = HashSet()
      try {
        dataSource.connection.use { connection ->
          connection.prepareStatement("SELECT * FROM " + locale.toLanguageTag().uppercase(Locale.getDefault()) + ";").use { preparedStatement ->
            preparedStatement.executeQuery().use { resultSet ->
              while (resultSet.next()) {
                val translationIdentifier = Identifier.of(resultSet.getString("translationkey"))
                existingTranslationKeys.add(translationIdentifier)
                super.cacheTranslation(translationIdentifier, locale, resultSet.getString("translation"))
              }
            }
          }
        }
      } catch (throwables: SQLException) {
        throwables.printStackTrace()
      }
      try {
        dataSource.connection.use { connection ->
          connection.prepareStatement("INSERT INTO " + locale.toLanguageTag().uppercase(Locale.getDefault()) +
            " (translationkey, translation) VALUES (?, ?) ON DUPLICATE KEY UPDATE translation=?;").use { preparedStatement ->
            if (!replaceIfExists) {
              for (existingTranslationKey in existingTranslationKeys) {
                translations.remove(existingTranslationKey)
              }
            }
            val translationEntries: Set<MutableMap.MutableEntry<Identifier, String>> = translations.entries
            for (translationEntry in translationEntries) {
              val identifier: Identifier = translationEntry.key
              val formattedTranslation = super.translateColor(translationEntry.value)
              preparedStatement.setString(1, identifier.translationKey)
              preparedStatement.setString(2, formattedTranslation)
              preparedStatement.setString(3, formattedTranslation)
              preparedStatement.addBatch()
              super.cacheTranslation(identifier, locale, formattedTranslation)
            }
            preparedStatement.executeBatch()
          }
        }
      } catch (throwables: SQLException) {
        throwables.printStackTrace()
      }
    }
  }

  private val dataSource: HikariDataSource
    get() = mySQLDatabaseProvider.dataSource
}
