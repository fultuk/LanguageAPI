/*
 * MIT License
 *
 * Copyright (c) 2020 0utplay (Aldin Sijamhodzic)
 * Copyright (c) 2020 contributors
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

package de.tentact.languageapi.player;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.zaxxer.hikari.HikariDataSource;
import de.tentact.languageapi.LanguageAPI;
import de.tentact.languageapi.configuration.LanguageConfig;
import de.tentact.languageapi.configuration.MySQL;
import de.tentact.languageapi.i18n.Translation;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public abstract class DefaultPlayerExecutor implements PlayerExecutor {

    private final MySQL mySQL;
    private final LanguageAPI languageAPI;
    private final HikariDataSource dataSource;
    private final LanguageConfig languageConfig;
    private final Cache<UUID, String> languageCache = CacheBuilder.newBuilder().expireAfterWrite(5L, TimeUnit.MINUTES).build();

    public DefaultPlayerExecutor(LanguageAPI languageAPI, LanguageConfig languageConfig) {
        this.languageConfig = languageConfig;
        this.mySQL = languageConfig.getMySQL();
        this.languageAPI = languageAPI;
        this.dataSource = mySQL.getDataSource();
    }

    @NotNull
    @Override
    public String getPlayerLanguage(UUID playerUUID) {
        if (!this.isRegisteredPlayer(playerUUID)) {
            this.registerPlayer(playerUUID);
        }
        String cachedLanguage = this.languageCache.getIfPresent(playerUUID);
        if (cachedLanguage != null) {
            return this.validateLanguage(cachedLanguage);
        }
        try (Connection connection = this.mySQL.getDataSource().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT language FROM playerlanguage WHERE uuid=?;")) {
            preparedStatement.setString(1, playerUUID.toString());
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    String language = resultSet.getString("language").toLowerCase();
                    this.languageCache.put(playerUUID, language);
                    return language;
                }
            }
        } catch (SQLException ignored) {
        }
        return this.languageAPI.getDefaultLanguage();
    }

    @Override
    public boolean isPlayersLanguage(UUID playerUUID, String language) {
        if (!this.languageAPI.isLanguage(language)) {
            return false;
        }
        return this.getPlayerLanguage(playerUUID).equalsIgnoreCase(language);
    }

    @Override
    public void setPlayerLanguage(UUID playerUUID, String newLanguage, boolean orElseDefault) {
        if (!languageAPI.isLanguage(newLanguage)) {
            if (orElseDefault) {
                this.setPlayerLanguage(playerUUID, languageAPI.getDefaultLanguage());
                return;
            }
            return;
        }
        this.setPlayerLanguage(playerUUID, newLanguage);
    }

    @Override
    public void setPlayerLanguage(UUID playerUUID, String newLanguage) {
        if (!this.languageAPI.isLanguage(newLanguage)) {
            throw new IllegalArgumentException("Language " + newLanguage + " was not found!");
        }
        if (!this.isRegisteredPlayer(playerUUID)) {
            try (Connection connection = this.dataSource.getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO playerlanguage (uuid, language) VALUES (?,?);")) {
                preparedStatement.setString(1, playerUUID.toString());
                preparedStatement.setString(2, newLanguage.toLowerCase());
                preparedStatement.execute();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            languageCache.put(playerUUID, newLanguage.toLowerCase());
            return;
        }
        if (this.isPlayersLanguage(playerUUID, newLanguage)) {
            return;
        }
        try (Connection connection = this.dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("UPDATE playerlanguage SET language=? WHERE uuid=?;")) {
            preparedStatement.setString(1, playerUUID.toString());
            preparedStatement.setString(2, newLanguage.toLowerCase());
            preparedStatement.execute();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        this.languageCache.put(playerUUID, newLanguage.toLowerCase());
    }

    @Override
    public void registerPlayer(UUID playerUUID) {
        this.registerPlayer(playerUUID, this.languageAPI.getDefaultLanguage());
    }

    @Override
    public void registerPlayer(UUID playerUUID, String language) {
        String validLanguage = this.validateLanguage(language);
        if (!this.isRegisteredPlayer(playerUUID)) {
            this.setPlayerLanguage(playerUUID, validLanguage);
            this.logInfo("Creating user: " + playerUUID.toString() + " with language " + validLanguage);
        } else {
            String currentLanguage = this.getPlayerLanguage(playerUUID);
            if (!this.languageAPI.isLanguage(currentLanguage)) {
                this.setPlayerLanguage(playerUUID, this.languageAPI.getDefaultLanguage());
                this.logInfo("Update old language: " + currentLanguage + " of player: " + playerUUID.toString());
            }
        }
    }

    @Override
    public boolean isRegisteredPlayer(UUID playerUUID) {
        try (Connection connection = this.dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM playerlanguage WHERE uuid=?;")) {
            preparedStatement.setString(1, playerUUID.toString());
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSet.next();
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return false;
    }

    @Override
    public void broadcastMessage(Translation translation) {
        this.getOnlineLanguagePlayers().forEach(languagePlayer -> languagePlayer.sendMessage(translation));
    }

    @Override
    public void kickAll(Translation translation) {
        this.getOnlineLanguagePlayers().forEach(languagePlayer -> languagePlayer.kickPlayer(translation));
    }

    @Override
    public @NotNull LanguageOfflinePlayer getLanguageOfflinePlayer(UUID playerId) {
        return new DefaultLanguageOfflinePlayer(playerId);
    }

    private String validateLanguage(String language) {
        if (!this.languageAPI.isLanguage(language)) {
            return this.languageAPI.getDefaultLanguage();
        }
        return language;
    }

    private void logInfo(String message) {
        this.languageConfig.getLogger().info(message);
    }
}
