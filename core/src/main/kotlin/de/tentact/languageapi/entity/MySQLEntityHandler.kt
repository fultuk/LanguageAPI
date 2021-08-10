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
package de.tentact.languageapi.entity

import de.tentact.languageapi.cache.CacheProvider
import de.tentact.languageapi.database.MySQLDatabaseProvider
import de.tentact.languageapi.language.LocaleHandler
import java.sql.SQLException
import java.util.*
import java.util.concurrent.CompletableFuture

class MySQLEntityHandler(cacheProvider: CacheProvider,
                         private val localeHandler: LocaleHandler,
                         private val mySQLDatabaseProvider: MySQLDatabaseProvider
) : DefaultEntityHandler(cacheProvider), EntityHandler {
  override fun getOfflineLanguageEntity(entityId: UUID): CompletableFuture<LanguageOfflineEntity> {
    return CompletableFuture.supplyAsync {
      val cachedEntity = getCachedEntity(entityId)
      if (cachedEntity != null) {
        return@supplyAsync cachedEntity
      }
      try {
        mySQLDatabaseProvider.dataSource.connection.use { connection ->
          connection.prepareStatement("SELECT locale FROM LANGUAGEENTITY WHERE entityid=?;").use { preparedStatement ->
            preparedStatement.setString(1, entityId.toString())
            preparedStatement.executeQuery().use { resultSet ->
              if (!resultSet.next()) {
                return@supplyAsync null
              }
              val locale = Locale.forLanguageTag(resultSet.getString("locale"))
              val offlineEntity: LanguageOfflineEntity = DefaultLanguageOfflineEntity(entityId, locale)
              super.cacheLanguageEntity(offlineEntity)
              return@supplyAsync offlineEntity
            }
          }
        }
      } catch (throwables: SQLException) {
        throwables.printStackTrace()
      }
      null
    }
  }

  override fun updateLanguageEntity(languageOfflineEntity: LanguageOfflineEntity): CompletableFuture<Void> {
    return localeHandler.isAvailableAsync(languageOfflineEntity.locale).thenAcceptAsync { isAvailable: Boolean ->
      if (!isAvailable) {
        return@thenAcceptAsync
      }
      try {
        mySQLDatabaseProvider.dataSource.connection.use { connection ->
          connection
            .prepareStatement("INSERT INTO LANGUAGEENTITY (entityid, locale) VALUES (?, ?) ON DUPLICATE KEY UPDATE locale=?;").use { preparedStatement ->
              val localeTag = languageOfflineEntity.locale.toLanguageTag().uppercase(Locale.getDefault())
              preparedStatement.setString(1, languageOfflineEntity.entityId.toString())
              preparedStatement.setString(2, localeTag)
              preparedStatement.setString(3, localeTag)
              preparedStatement.execute()
            }
        }
      } catch (throwables: SQLException) {
        throwables.printStackTrace()
      }
      super.cacheLanguageEntity(languageOfflineEntity)
    }
  }
}
