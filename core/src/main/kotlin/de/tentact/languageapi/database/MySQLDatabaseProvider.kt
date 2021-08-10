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
package de.tentact.languageapi.database

import com.zaxxer.hikari.HikariDataSource
import de.tentact.languageapi.LanguageAPI
import de.tentact.languageapi.config.database.DatabaseConfiguration
import java.sql.SQLException
import java.util.*

class MySQLDatabaseProvider(hostname: String?, database: String?, username: String?, password: String?, port: Int) : DatabaseConfiguration(hostname, database, username, password, port) {
  @Transient
  lateinit var dataSource: HikariDataSource

  constructor(configuration: DatabaseConfiguration) : this(configuration.hostname, configuration.database, configuration.username,
    configuration.password, configuration.port)

  override fun init(languageAPI: LanguageAPI) {
    setupHikariDataSource()
    createDefaultTables(languageAPI)
  }

  private fun setupHikariDataSource() {
    dataSource = HikariDataSource()
    dataSource.let {
      it.jdbcUrl = "jdbc:mysql://$hostname:$port/$database"
      it.username = username
      it.password = password
    }

  }

  override fun closeConnection() {
    if (isNotConnected) {
      return
    }
    dataSource.close()
  }

  private val isNotConnected: Boolean
    get() = dataSource == null || dataSource.isClosed

  private fun createDefaultTables(languageAPI: LanguageAPI) {
    languageAPI.executeAsync {
      if (isNotConnected) {
        return@executeAsync
      }
      try {
        dataSource.connection.use { connection ->
          connection.createStatement().execute("CREATE TABLE IF NOT EXISTS IDENTIFIER(translationkey VARCHAR(128) PRIMARY KEY, parameter TEXT)")
          connection.createStatement().execute("CREATE TABLE IF NOT EXISTS LANGUAGEENTITY(entityid VARCHAR(36) PRIMARY KEY, locale VARCHAR(32))")
          connection.createStatement().execute("CREATE TABLE IF NOT EXISTS LANGUAGE(locale VARCHAR(32) PRIMARY KEY)")
        }
      } catch (throwables: SQLException) {
        throwables.printStackTrace()
      }
    }
  }

  fun createLocaleTable(locale: Locale) {
    if (isNotConnected) {
      return
    }
    try {
      dataSource.let {
        it.connection.use { connection ->
          connection.createStatement().execute("CREATE TABLE IF NOT EXISTS "
            + locale.toLanguageTag().uppercase(Locale.getDefault()) +
            "(translationkey VARCHAR(128) PRIMARY KEY, translation TEXT)")
        }
      }
    } catch (throwables: SQLException) {
      throwables.printStackTrace()
    }
  }

  fun deleteLocaleTable(locale: Locale) {
    LanguageAPI.getInstance().executeAsync {
      if (isNotConnected) {
        return@executeAsync
      }
      try {
        dataSource.let {
          it.connection.use { connection -> connection.createStatement().execute("DROP TABLE IF EXISTS " + locale.toLanguageTag().uppercase(Locale.getDefault())) }
        }
      } catch (throwables: SQLException) {
        throwables.printStackTrace()
      }
    }
  }
}
