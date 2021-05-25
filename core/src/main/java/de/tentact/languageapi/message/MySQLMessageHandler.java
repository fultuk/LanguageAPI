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

import com.zaxxer.hikari.HikariDataSource;
import de.tentact.languageapi.cache.CacheProvider;
import de.tentact.languageapi.database.MySQLDatabaseProvider;
import de.tentact.languageapi.language.LocaleHandler;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Locale;

public class MySQLMessageHandler extends DefaultMessageHandler implements MessageHandler {

  private final MySQLDatabaseProvider mySQLDatabaseProvider;

  public MySQLMessageHandler(LocaleHandler localeHandler, MySQLDatabaseProvider mySQLDatabaseProvider, CacheProvider cacheProvider) {
    super(localeHandler, cacheProvider);
    this.mySQLDatabaseProvider = mySQLDatabaseProvider;
  }

  @Override
  public Identifier loadIdentifier(Identifier identifier) {
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
  public void writeIdentifier(Identifier identifier) {
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
  public String getMessage(Identifier identifier, Locale locale) {
    String message = super.getMessage(identifier, locale);
    if (message != null) {
      return message;
    }
    if (!super.localeHandler.isAvailable(locale)) {
      throw new IllegalArgumentException("Language not found");
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
  public void translateMessage(Identifier identifier, Locale locale, String translation) {
    super.localeHandler.isAvailableAsync(locale).thenAcceptAsync(isAvailable -> {
      if (!isAvailable) {
        return;
      }
      try (Connection connection = this.getDataSource().getConnection();
           PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO translation (translationKey, translation) VALUES (?, ?) ON DUPLICATE KEY UPDATE translation=?;")) {
        String formattedTranslation = super.translateColor(translation);

        preparedStatement.setString(1, identifier.getTranslationKey());
        preparedStatement.setString(2, formattedTranslation);
        preparedStatement.setString(3, formattedTranslation);
        preparedStatement.execute();
      } catch (SQLException throwables) {
        throwables.printStackTrace();
      }

      super.cacheTranslation(identifier, locale, translation);
    });
  }

  private HikariDataSource getDataSource() {
    return this.mySQLDatabaseProvider.getDataSource();
  }
}
