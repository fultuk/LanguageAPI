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

package de.tentact.languageapi.language;

import com.zaxxer.hikari.HikariDataSource;
import de.tentact.languageapi.cache.CacheProvider;
import de.tentact.languageapi.database.MySQLDatabaseProvider;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class MySQLLocaleHandler extends DefaultLocaleHandler implements LocaleHandler {

  private final MySQLDatabaseProvider mySQLDatabaseProvider;

  public MySQLLocaleHandler(CacheProvider cacheProvider, MySQLDatabaseProvider mySQLDatabaseProvider) {
    super(cacheProvider);
    this.mySQLDatabaseProvider = mySQLDatabaseProvider;
  }

  @Override
  public void createLocale(Locale locale) {

  }

  @Override
  public boolean isAvailable(Locale locale) {
    return false;
  }

  @Override
  public void deleteLocale(Locale locale) {

  }

  @Override
  public CompletableFuture<Set<Locale>> getAvailableLocales() {
    return this.getAvailableLocales(false);
  }

  @Override
  public CompletableFuture<Set<Locale>> getAvailableLocales(boolean fromCache) {
    return CompletableFuture.supplyAsync(() -> {
      Set<Locale> cachedLocales = super.localeCache.getIfPresent(KEY);
      if (fromCache) {
        return cachedLocales == null ? Collections.emptySet() : cachedLocales;
      }
      try (Connection connection = this.getDataSource().getConnection();
           PreparedStatement preparedStatement = connection.prepareStatement("SELECT locale FROM LANGUAGE")) {
        try (ResultSet resultSet = preparedStatement.executeQuery()) {

          Set<Locale> locales = cachedLocales == null ? new HashSet<>() : new HashSet<>(cachedLocales);
          while (resultSet.next()) {
            locales.add(Locale.forLanguageTag(resultSet.getString("locale")));
          }

          super.localeCache.put(KEY, locales);
          return locales;
        }
      } catch (SQLException throwables) {
        throwables.printStackTrace();
      }
      return Collections.emptySet();
    });
  }

  @Override
  public void copyLocale(Locale from, Locale to, boolean createIfNotExists) {

  }

  private HikariDataSource getDataSource() {
    return this.mySQLDatabaseProvider.getDataSource();
  }
}
