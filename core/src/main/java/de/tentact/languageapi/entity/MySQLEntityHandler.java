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

package de.tentact.languageapi.entity;

import com.google.common.base.Preconditions;
import de.tentact.languageapi.cache.CacheProvider;
import de.tentact.languageapi.database.MySQLDatabaseProvider;
import de.tentact.languageapi.language.LocaleHandler;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class MySQLEntityHandler extends DefaultEntityHandler implements EntityHandler {

  private final LocaleHandler localeHandler;
  private final MySQLDatabaseProvider mySQLDatabaseProvider;

  public MySQLEntityHandler(CacheProvider cacheProvider, LocaleHandler localeHandler, MySQLDatabaseProvider mySQLDatabaseProvider) {
    super(cacheProvider);
    this.localeHandler = localeHandler;
    this.mySQLDatabaseProvider = mySQLDatabaseProvider;
  }

  @Override
  public CompletableFuture<LanguageOfflineEntity> getOfflineLanguageEntity(@NotNull UUID entityId) {
    Preconditions.checkNotNull(entityId, "entityId");
    return CompletableFuture.supplyAsync(() -> {
      LanguageOfflineEntity cachedEntity = this.getCachedEntity(entityId);
      if (cachedEntity != null) {
        return cachedEntity;
      }
      try (Connection connection = this.mySQLDatabaseProvider.getDataSource().getConnection();
           PreparedStatement preparedStatement = connection.prepareStatement("SELECT locale FROM LANGUAGEENTITY WHERE entityid=?;")) {
        preparedStatement.setString(1, entityId.toString());
        try (ResultSet resultSet = preparedStatement.executeQuery()) {
          if (!resultSet.next()) {
            return null;
          }
          Locale locale = Locale.forLanguageTag(resultSet.getString("locale"));
          LanguageOfflineEntity offlineEntity = new DefaultLanguageOfflineEntity(entityId, locale);

          super.cacheLanguageEntity(offlineEntity);
          return offlineEntity;
        }
      } catch (SQLException throwables) {
        throwables.printStackTrace();
      }
      return null;
    });
  }

  @Override
  public CompletableFuture<Void> updateLanguageEntity(@NotNull LanguageOfflineEntity languageOfflineEntity) {
    return this.localeHandler.isAvailableAsync(languageOfflineEntity.getLocale()).thenAcceptAsync(isAvailable -> {
      if (!isAvailable) {
        return;
      }
      try (Connection connection = this.mySQLDatabaseProvider.getDataSource().getConnection();
           PreparedStatement preparedStatement = connection
               .prepareStatement("INSERT INTO LANGUAGEENTITY (entityid, locale) VALUES (?, ?) ON DUPLICATE KEY UPDATE locale=?;")) {
        String localeTag = languageOfflineEntity.getLocale().toLanguageTag().toUpperCase();

        preparedStatement.setString(1, languageOfflineEntity.getEntityId().toString());
        preparedStatement.setString(2, localeTag);
        preparedStatement.setString(3, localeTag);
        preparedStatement.execute();
      } catch (SQLException throwables) {
        throwables.printStackTrace();
      }
      super.cacheLanguageEntity(languageOfflineEntity);
    });
  }
}
