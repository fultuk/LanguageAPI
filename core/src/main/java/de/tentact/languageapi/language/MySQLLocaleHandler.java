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
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class MySQLLocaleHandler extends DefaultLocaleHandler implements LocaleHandler {

  private final MySQLDatabaseProvider mySQLDatabaseProvider;

  public MySQLLocaleHandler(CacheProvider cacheProvider, MySQLDatabaseProvider mySQLDatabaseProvider) {
    super(cacheProvider);
    this.mySQLDatabaseProvider = mySQLDatabaseProvider;
  }

  @Override
  public void createLocale(Locale locale) {
    this.mySQLDatabaseProvider.createLocaleTable(locale);
    super.cacheLocale(locale);
  }

  @Override
  public boolean isAvailable(Locale locale) {
    Locale cachedLocale = super.localeCache.getIfPresent(locale.toLanguageTag());
    if (cachedLocale != null) {
      return true;
    }

    try (Connection connection = this.getDataSource().getConnection();
         PreparedStatement preparedStatement = connection.prepareStatement("SELECT locale FROM LANGUAGE WHERE locale=?")) {
      String languageTag = locale.toLanguageTag().toUpperCase();
      preparedStatement.setString(1, languageTag);
      try (ResultSet resultSet = preparedStatement.executeQuery()) {
        if (resultSet.next()) {
          super.localeCache.put(languageTag, locale);
          return true;
        }
        super.localeCache.invalidate(languageTag);
        return false;
      }
    } catch (SQLException throwables) {
      throwables.printStackTrace();
    }

    return false;
  }

  @Override
  public void deleteLocale(Locale locale) {
    this.mySQLDatabaseProvider.deleteLocaleTable(locale);
    this.localeCache.invalidate(locale.toLanguageTag().toUpperCase());
  }

  @Override
  public CompletableFuture<Collection<Locale>> getAvailableLocales() {
    return this.getAvailableLocales(false);
  }

  @Override
  public CompletableFuture<Collection<Locale>> getAvailableLocales(boolean fromCache) {
    return CompletableFuture.supplyAsync(() -> {
      Map<String, Locale> cachedLocales = super.localeCache.asMap();
      if (fromCache) {
        return cachedLocales.values();
      }
      try (Connection connection = this.getDataSource().getConnection();
           PreparedStatement preparedStatement = connection.prepareStatement("SELECT locale FROM LANGUAGE")) {
        try (ResultSet resultSet = preparedStatement.executeQuery()) {

          Map<String, Locale> locales = new HashMap<>();
          while (resultSet.next()) {
            String localeTag = resultSet.getString("locale");
            locales.put(localeTag.toUpperCase(), Locale.forLanguageTag(localeTag));
          }
          super.localeCache.clear();
          super.localeCache.putAll(locales);
          return locales.values();
        }
      } catch (SQLException throwables) {
        throwables.printStackTrace();
      }
      return Collections.emptySet();
    });
  }

  @Override
  public CompletableFuture<Boolean> copyLocale(Locale from, Locale to, boolean createIfNotExists) {
    return CompletableFuture.supplyAsync(() -> {
      if (!this.isAvailable(from)) {
        return false;
      }
      if (createIfNotExists) {
        this.createLocale(to);
      }
      String oldLocaleTag = from.toLanguageTag().toUpperCase();
      String localeTag = to.toLanguageTag().toUpperCase();
      try (Connection connection = this.getDataSource().getConnection()) {
        connection.createStatement().execute("INSERT INTO " +localeTag +" SELECT * FROM " + oldLocaleTag);
        return true;
      } catch (SQLException throwables) {
        throwables.printStackTrace();
      }
      return false;
    });
  }

  private HikariDataSource getDataSource() {
    return this.mySQLDatabaseProvider.getDataSource();
  }
}
