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

package de.tentact.languageapi.database;

import com.zaxxer.hikari.HikariDataSource;
import de.tentact.languageapi.LanguageAPI;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Locale;

public class MySQLDatabaseProvider {

  private final String hostname;
  private final String database;
  private final String username;
  private final String password;
  private final int port;
  private HikariDataSource hikariDataSource;

  protected MySQLDatabaseProvider(String hostname, String database, String username, String password, int port) {
    this.hostname = hostname;
    this.database = database;
    this.username = username;
    this.password = password;
    this.port = port;

    this.setupHikariDataSource();
    this.createDefaultTables();
  }

  private void setupHikariDataSource() {
    this.hikariDataSource = new HikariDataSource();
    this.hikariDataSource.setJdbcUrl("jdbc:mysql://" + this.hostname + ":" + this.port + "/" + this.database);
    this.hikariDataSource.setUsername(this.username);
    this.hikariDataSource.setPassword(this.password);
  }

  public void closeConnection() {
    if (this.isNotConnected()) {
      return;
    }
    this.hikariDataSource.close();
  }

  private boolean isNotConnected() {
    return this.hikariDataSource == null || this.hikariDataSource.isClosed();
  }

  private void createDefaultTables() {
    LanguageAPI.getInstance().executeAsync(() -> {
      if (this.isNotConnected()) {
        return;
      }
      try (Connection connection = this.hikariDataSource.getConnection()) {
        connection.createStatement().execute("CREATE TABLE IF NOT EXISTS INDENTIFIER(translationkey VARCHAR(128) PRIMARY KEY, parameter TEXT)");
        connection.createStatement().execute("CREATE TABLE IF NOT EXISTS LANGUAGEENTITY(entityid VARCHAR(36) PRIMARY KEY, locale VARCHAR(32))");
        connection.createStatement().execute("CREATE TABLE IF NOT EXISTS LANGUAGE(locale VARCHAR(32) PRIMARY KEY)");
      } catch (SQLException throwables) {
        throwables.printStackTrace();
      }
    });
  }

  public void createLocaleTable(Locale locale) {
    if (this.isNotConnected()) {
      return;
    }
    try (Connection connection = this.hikariDataSource.getConnection()) {
      connection.createStatement().execute("CREATE TABLE IF NOT EXISTS "
          + locale.toLanguageTag().toUpperCase() +
          "(translationkey VARCHAR(128) PRIMARY KEY, translation TEXT)");
    } catch (SQLException throwables) {
      throwables.printStackTrace();
    }
  }

  public void deleteLocaleTable(Locale locale) {
    LanguageAPI.getInstance().executeAsync(() -> {
      if (this.isNotConnected()) {
        return;
      }
      try (Connection connection = this.hikariDataSource.getConnection()) {
        connection.createStatement().execute("DROP TABLE IF EXISTS "+locale.toLanguageTag().toUpperCase());
      } catch (SQLException throwables) {
        throwables.printStackTrace();
      }
    });
  }

  public HikariDataSource getDataSource() {
    return this.hikariDataSource;
  }
}
