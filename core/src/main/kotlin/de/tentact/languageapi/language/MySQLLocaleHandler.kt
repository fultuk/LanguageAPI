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
package de.tentact.languageapi.language

import com.zaxxer.hikari.HikariDataSource
import de.tentact.languageapi.cache.CacheProvider
import de.tentact.languageapi.database.MySQLDatabaseProvider
import java.sql.SQLException
import java.util.*
import java.util.concurrent.CompletableFuture

class MySQLLocaleHandler(cacheProvider: CacheProvider, private val mySQLDatabaseProvider: MySQLDatabaseProvider) : DefaultLocaleHandler(cacheProvider), LocaleHandler {
  override fun createLocale(locale: Locale) {
    mySQLDatabaseProvider.createLocaleTable(locale)
    super.cacheLocale(locale)
  }

  override fun isAvailable(locale: Locale): Boolean {
    val cachedLocale = super.localeCache.getIfPresent(locale.toLanguageTag().uppercase(Locale.getDefault()))
    if (cachedLocale != null) {
      return true
    }
    try {
      dataSource.connection.use { connection ->
        connection.prepareStatement("SELECT locale FROM LANGUAGE WHERE locale=?").use { preparedStatement ->
          val languageTag = locale.toLanguageTag().uppercase(Locale.getDefault())
          preparedStatement.setString(1, languageTag)
          preparedStatement.executeQuery().use { resultSet ->
            if (resultSet.next()) {
              super.localeCache.put(languageTag, locale)
              return true
            }
            super.localeCache.invalidate(languageTag)
            return false
          }
        }
      }
    } catch (throwables: SQLException) {
      throwables.printStackTrace()
    }
    return false
  }

  override fun deleteLocale(locale: Locale) {
    mySQLDatabaseProvider.deleteLocaleTable(locale)
    localeCache.invalidate(locale.toLanguageTag().uppercase(Locale.getDefault()))
  }

  override fun getAvailableLocales(): CompletableFuture<Collection<Locale>> {
    return this.getAvailableLocales(false)
  }

  override fun getAvailableLocales(fromCache: Boolean): CompletableFuture<Collection<Locale>> {
    return CompletableFuture.supplyAsync {
      val cachedLocales = super.localeCache.asMap()
      if (fromCache) {
        return@supplyAsync cachedLocales.values
      }
      try {
        dataSource.connection.use { connection ->
          connection.prepareStatement("SELECT locale FROM LANGUAGE").use { preparedStatement ->
            preparedStatement.executeQuery().use { resultSet ->
              val locales: MutableMap<String, Locale> = HashMap()
              while (resultSet.next()) {
                val localeTag = resultSet.getString("locale")
                locales[localeTag.uppercase(Locale.getDefault())] = Locale.forLanguageTag(localeTag)
              }
              super.localeCache.clear()
              super.localeCache.putAll(locales)
              return@supplyAsync locales.values
            }
          }
        }
      } catch (throwables: SQLException) {
        throwables.printStackTrace()
      }
      emptySet()
    }
  }

  override fun copyLocale(from: Locale, to: Locale, createIfNotExists: Boolean): CompletableFuture<Boolean> {
    return CompletableFuture.supplyAsync {
      if (!isAvailable(from)) {
        return@supplyAsync false
      }
      if (createIfNotExists) {
        createLocale(to)
      }
      val oldLocaleTag = from.toLanguageTag().uppercase(Locale.getDefault())
      val localeTag = to.toLanguageTag().uppercase(Locale.getDefault())
      try {
        dataSource.connection.use { connection ->
          connection.createStatement().execute("INSERT INTO $localeTag SELECT * FROM $oldLocaleTag")
          return@supplyAsync true
        }
      } catch (throwables: SQLException) {
        throwables.printStackTrace()
      }
      false
    }
  }

  private val dataSource: HikariDataSource
    get() = mySQLDatabaseProvider.dataSource
}
