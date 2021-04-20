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
import java.util.concurrent.CompletableFuture;
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
        this.dataSource = this.mySQL.getDataSource();
    }

    @NotNull
    @Override
    public String getPlayerLanguage(UUID playerId) {
        if (!this.isRegisteredPlayer(playerId)) {
            this.registerPlayer(playerId);
        }
        String cachedLanguage = this.languageCache.getIfPresent(playerId);
        if (cachedLanguage != null) {
            if (!this.languageAPI.isLanguage(cachedLanguage)) {
                this.languageCache.invalidate(playerId);
                return this.languageAPI.getDefaultLanguage();
            }
            return cachedLanguage;
        }
        try (Connection connection = this.mySQL.getDataSource().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT language FROM playerlanguage WHERE uuid=?;")) {
            preparedStatement.setString(1, playerId.toString());
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    String language = resultSet.getString("language").toLowerCase();
                    this.languageCache.put(playerId, language);
                    return language;
                }
            }
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
        return this.languageAPI.getDefaultLanguage();
    }

    @Override
    public @NotNull CompletableFuture<String> getPlayerLanguageAsync(UUID playerId) {
        return CompletableFuture.supplyAsync(() -> this.getPlayerLanguage(playerId));
    }

    @Override
    public boolean isPlayersLanguage(UUID playerId, String language) {
        if (!this.languageAPI.isLanguage(language)) {
            return false;
        }
        return this.getPlayerLanguage(playerId).equalsIgnoreCase(language);
    }

    @Override
    public void setPlayerLanguage(UUID playerId, String newLanguage, boolean orElseDefault) {
        if (!this.languageAPI.isLanguage(newLanguage)) {
            if (orElseDefault) {
                this.setPlayerLanguage(playerId, this.languageAPI.getDefaultLanguage());
                return;
            }
            return;
        }
        this.setPlayerLanguage(playerId, newLanguage);
    }

    @Override
    public void setPlayerLanguage(UUID playerId, String newLanguage) {
        this.languageAPI.executeAsync(() -> {
            if (!this.languageAPI.isLanguage(newLanguage)) {
                throw new IllegalArgumentException("Language " + newLanguage + " was not found!");
            }
            if (!this.isRegisteredPlayer(playerId)) {
                try (Connection connection = this.dataSource.getConnection();
                     PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO playerlanguage (uuid, language) VALUES (?,?);")) {
                    preparedStatement.setString(1, playerId.toString());
                    preparedStatement.setString(2, newLanguage.toLowerCase());
                    preparedStatement.execute();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
                this.languageCache.put(playerId, newLanguage.toLowerCase());
                return;
            }
            try (Connection connection = this.dataSource.getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement("UPDATE playerlanguage SET language=? WHERE uuid=?;")) {
                preparedStatement.setString(1, newLanguage.toLowerCase());
                preparedStatement.setString(2, playerId.toString());
                preparedStatement.execute();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            this.languageCache.put(playerId, newLanguage.toLowerCase());
        });
    }

    @Override
    public void registerPlayer(UUID playerId) {
        this.registerPlayer(playerId, this.languageAPI.getDefaultLanguage());
    }

    @Override
    public void registerPlayer(UUID playerId, String language) {
        this.languageAPI.executeAsync(() -> {
            String validLanguage = this.validateLanguage(language);

            if (!this.isRegisteredPlayer(playerId)) {
                this.setPlayerLanguage(playerId, validLanguage);
                this.debug("Creating user: " + playerId + " with language " + validLanguage);
            } else {
                String currentLanguage = this.getPlayerLanguage(playerId);
                if (!this.languageAPI.isLanguage(currentLanguage)) {
                    this.setPlayerLanguage(playerId, this.languageAPI.getDefaultLanguage());
                }
            }
        });
    }

    @Override
    public boolean isRegisteredPlayer(UUID playerId) {
        try (Connection connection = this.dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM playerlanguage WHERE uuid=?;")) {
            preparedStatement.setString(1, playerId.toString());
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSet.next();
            }
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
        return false;
    }

    @Override
    public CompletableFuture<Boolean> isRegisteredPlayerAsync(UUID playerId) {
        return CompletableFuture.supplyAsync(() -> this.isRegisteredPlayer(playerId));
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

    private void debug(String message) {
        this.languageConfig.debug(message);
    }
}
