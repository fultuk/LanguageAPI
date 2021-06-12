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

package de.tentact.languageapi.message;

import com.google.common.base.Preconditions;
import com.zaxxer.hikari.HikariDataSource;
import de.tentact.languageapi.cache.CacheProvider;
import de.tentact.languageapi.database.MySQLDatabaseProvider;
import de.tentact.languageapi.language.LocaleHandler;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class MySQLMessageHandler extends DefaultMessageHandler implements MessageHandler {

  private final MySQLDatabaseProvider mySQLDatabaseProvider;

  public MySQLMessageHandler(LocaleHandler localeHandler, MySQLDatabaseProvider mySQLDatabaseProvider, CacheProvider cacheProvider) {
    super(localeHandler, cacheProvider);
    this.mySQLDatabaseProvider = mySQLDatabaseProvider;
  }

  @Override
  public @NotNull Identifier loadIdentifier(@NotNull Identifier identifier) {
    Preconditions.checkNotNull(identifier, "identifier");

    Identifier cachedIdentifier = super.loadIdentifier(identifier);
    if (cachedIdentifier != null) {
      return cachedIdentifier;
    }
    try (Connection connection = this.getDataSource().getConnection();
         PreparedStatement preparedStatement = connection.prepareStatement("SELECT parameters FROM IDENTIFIER WHERE translationKey=?;")) {
      preparedStatement.setString(1, identifier.getTranslationKey());
      try (ResultSet resultSet = preparedStatement.executeQuery()) {
        if (resultSet.next()) {
          String[] parameter = resultSet.getString("parameters").split(",");
          identifier.parameters(parameter);
        }
        super.cacheIdentifier(identifier);
        return identifier;
      }

    } catch (SQLException throwables) {
      throwables.printStackTrace();
    }
    return identifier;
  }

  @Override
  public void writeIdentifier(@NotNull Identifier identifier) {
    Preconditions.checkNotNull(identifier, "identifier");

    try (Connection connection = this.getDataSource().getConnection();
         PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO IDENTIFIER (translationKey, parameters) VALUES (?, ?) ON DUPLICATE KEY UPDATE parameters=?;")) {

      String joinedParameters = String.join(",", identifier.getParameters().values());
      preparedStatement.setString(1, identifier.getTranslationKey());
      preparedStatement.setString(2, joinedParameters);
      preparedStatement.setString(3, joinedParameters);
      preparedStatement.execute();

      super.cacheIdentifier(identifier);
    } catch (SQLException throwables) {
      throwables.printStackTrace();
    }
  }

  @Override
  public @NotNull String getMessage(@NotNull Identifier identifier, @NotNull Locale locale) {
    String message = super.getMessage(identifier, locale);
    if (message != null) {
      return message;
    }
    if (!super.localeHandler.isAvailable(locale)) {
      return identifier.getTranslationKey();
    }
    try (Connection connection = this.getDataSource().getConnection();
         PreparedStatement preparedStatement = connection.prepareStatement("SELECT translation FROM " +
             locale.toLanguageTag().toUpperCase() + " WHERE translationKey=?;")) {
      preparedStatement.setString(1, identifier.getTranslationKey());
      try (ResultSet resultSet = preparedStatement.executeQuery()) {
        if (resultSet.next()) {
          String translation = super.translateColor(resultSet.getString("translation"));
          super.cacheTranslation(identifier, locale, translation);

          return translation;
        }
      }
    } catch (SQLException throwables) {
      throwables.printStackTrace();
    }
    return identifier.getTranslationKey();
  }

  @Override
  public @NotNull CompletableFuture<Map<Identifier, String>> getMessages(@NotNull Locale locale, boolean fromCache) {
    Preconditions.checkNotNull(locale, "locale");

    return super.localeHandler.isAvailableAsync(locale).thenApplyAsync(isAvailable -> {
      if (!isAvailable) {
        return new HashMap<>();
      }
      if (fromCache) {
        Map<Identifier, String> cachedTranslations = super.translationCache.getIfPresent(locale);
        if (cachedTranslations == null) {
          cachedTranslations = new HashMap<>();
        }
        return cachedTranslations;
      }

      Map<Identifier, String> translations = new HashMap<>();

      try (Connection connection = this.getDataSource().getConnection();
           PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM " + locale.toLanguageTag().toUpperCase() + ";")) {

        try (ResultSet resultSet = preparedStatement.executeQuery()) {
          while (resultSet.next()) {
            translations.put(Identifier.of(resultSet.getString("translationkey")),
                resultSet.getString("translation"));
          }

          super.translationCache.put(locale, translations);
        }

      } catch (SQLException throwables) {
        throwables.printStackTrace();
      }
      return translations;
    });
  }

  @Override
  public @NotNull CompletableFuture<Set<Identifier>> getIdentifier(@NotNull Locale locale, boolean fromCache) {
    if (fromCache) {
      return CompletableFuture.completedFuture(super.translationCache.getIfPresent(locale).keySet());
    }
    return this.getMessages(locale, false).thenApply(Map::keySet);
  }

  @Override
  public @NotNull CompletableFuture<Set<Identifier>> getGlobalIdentifier(boolean fromCache) {
    return CompletableFuture.supplyAsync(() -> {
      Set<Identifier> identifiers = new HashSet<>(super.identifierCache.getValues());
      if (fromCache) {
        return identifiers;
      }

      try (Connection connection = this.getDataSource().getConnection();
           PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM IDENTIFIER;")) {

        try (ResultSet resultSet = preparedStatement.executeQuery()) {
          while (resultSet.next()) {
            Identifier identifier = Identifier.of(resultSet.getString("translationkey"),
                resultSet.getString("parameters").split(","));
            identifiers.remove(identifier);
            identifiers.add(identifier);
          }
        }

      } catch (SQLException throwables) {
        throwables.printStackTrace();
      }

      super.cacheIdentifier(identifiers);
      return identifiers;
    });
  }

  @Override
  public void translateMessage(@NotNull Identifier identifier, @NotNull Locale locale, @NotNull String translation, boolean replaceIfExists) {
    Preconditions.checkNotNull(identifier, "identifier");
    Preconditions.checkNotNull(locale, "locale");
    Preconditions.checkNotNull(translation, "translation");

    super.localeHandler.isAvailableAsync(locale).thenAcceptAsync(isAvailable -> {
      if (!isAvailable) {
        return;
      }
      try (Connection connection = this.getDataSource().getConnection();
           PreparedStatement selectStatement = connection.prepareStatement("SELECT * FROM " + locale.toLanguageTag().toUpperCase() + " WHERE translationkey=?;")) {
        selectStatement.setString(1, identifier.getTranslationKey());

        try (ResultSet resultSet = selectStatement.executeQuery()) {
          if (resultSet.next() && !replaceIfExists) {
            return;
          }
          try (PreparedStatement insertStatement = connection.prepareStatement(
              "INSERT INTO " + locale.toLanguageTag().toUpperCase() + " (translationkey, translation) VALUES (?, ?) ON DUPLICATE KEY UPDATE translation=?;")) {
            String formattedTranslation = super.translateColor(translation);

            insertStatement.setString(1, identifier.getTranslationKey());
            insertStatement.setString(2, formattedTranslation);
            insertStatement.setString(3, formattedTranslation);
            insertStatement.execute();
          }
        }
      } catch (SQLException throwables) {
        throwables.printStackTrace();
      }

      super.cacheTranslation(identifier, locale, translation);
    });
  }

  @Override
  public void translateMessage(@NotNull Map<Identifier, String> translations, @NotNull Locale locale, boolean replaceIfExists) {
    Preconditions.checkNotNull(translations, "translations");
    Preconditions.checkNotNull(locale, "locale");

    super.localeHandler.isAvailableAsync(locale).thenAcceptAsync(isAvailable -> {
      if (!isAvailable) {
        return;
      }

      Set<Identifier> existingTranslationKeys = new HashSet<>();
      try (Connection connection = this.getDataSource().getConnection();
           PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM " + locale.toLanguageTag().toUpperCase() + ";")) {
        try (ResultSet resultSet = preparedStatement.executeQuery()) {
          while (resultSet.next()) {
            Identifier translationIdentifier = Identifier.of(resultSet.getString("translationkey"));
            existingTranslationKeys.add(translationIdentifier);

            super.cacheTranslation(translationIdentifier, locale, resultSet.getString("translation"));
          }
        }
      } catch (SQLException throwables) {
        throwables.printStackTrace();
      }

      try (Connection connection = this.getDataSource().getConnection();
           PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO " + locale.toLanguageTag().toUpperCase() +
               " (translationkey, translation) VALUES (?, ?) ON DUPLICATE KEY UPDATE translation=?;")) {


        if (!replaceIfExists) {
          for (Identifier existingTranslationKey : existingTranslationKeys) {
            translations.remove(existingTranslationKey);
          }
        }

        Set<Map.Entry<Identifier, String>> translationEntries = translations.entrySet();

        for (Map.Entry<Identifier, String> translationEntry : translationEntries) {
          Identifier identifier = translationEntry.getKey();
          String formattedTranslation = super.translateColor(translationEntry.getValue());

          preparedStatement.setString(1, identifier.getTranslationKey());
          preparedStatement.setString(2, formattedTranslation);
          preparedStatement.setString(3, formattedTranslation);
          preparedStatement.addBatch();

          super.cacheTranslation(identifier, locale, formattedTranslation);
        }

        preparedStatement.executeBatch();
      } catch (SQLException throwables) {
        throwables.printStackTrace();
      }
    });
  }

  private HikariDataSource getDataSource() {
    return this.mySQLDatabaseProvider.getDataSource();
  }
}
