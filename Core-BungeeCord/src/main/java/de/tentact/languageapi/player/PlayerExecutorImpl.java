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
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

public class PlayerExecutorImpl extends PlayerManagerImpl implements PlayerExecutor {

    private final MySQL mySQL;
    private final LanguageAPI languageAPI;
    private final HikariDataSource dataSource;
    private final LanguageConfig languageConfig;
    private final Cache<UUID, String> languageCache = CacheBuilder.newBuilder().expireAfterWrite(5L, TimeUnit.MINUTES).build();

    public PlayerExecutorImpl(LanguageAPI languageAPI, LanguageConfig languageConfig) {
        this.languageConfig = languageConfig;
        this.mySQL = languageConfig.getMySQL();
        this.languageAPI = languageAPI;
        this.dataSource = mySQL.getDataSource();
    }

    @NotNull
    @Override
    public String getPlayerLanguage(UUID playerUUID) {
        if (!isRegisteredPlayer(playerUUID)) {
            this.registerPlayer(playerUUID);
        }
        if (languageCache.getIfPresent(playerUUID) != null) {
            return Objects.requireNonNull(languageCache.getIfPresent(playerUUID));
        }
        try (Connection connection = this.mySQL.getDataSource().getConnection()) {
            ResultSet rs = connection.createStatement().executeQuery("SELECT language FROM playerlanguage WHERE uuid='" + playerUUID.toString() + "';");
            if (rs.next()) {
                String language = rs.getString("language").toLowerCase();
                languageCache.put(playerUUID, language);
                return language;
            }
        } catch (SQLException throwables) {
            return languageAPI.getDefaultLanguage();
        }
        return languageAPI.getDefaultLanguage();
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
        if(!this.isRegisteredPlayer(playerUUID)) {
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
        if(this.isPlayersLanguage(playerUUID, newLanguage)) {
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
        languageCache.put(playerUUID, newLanguage.toLowerCase());
    }

    @Override
    public void registerPlayer(UUID playerUUID) {
        this.registerPlayer(playerUUID, languageAPI.getDefaultLanguage());
    }

    @Override
    public void registerPlayer(UUID playerUUID, String language) {
        String validLanguage = this.validateLanguage(language);
        if (!this.isRegisteredPlayer(playerUUID)) {
            this.setPlayerLanguage(playerUUID, validLanguage);
            this.logInfo("Creating user: " + playerUUID.toString() + " with language " + validLanguage);
        } else {
            if (!languageAPI.isLanguage(this.getPlayerLanguage(playerUUID))) {
                this.setPlayerLanguage(playerUUID, languageAPI.getDefaultLanguage());
                this.logInfo("Updating players selected language");
            }
        }
    }

    @Override
    public boolean isRegisteredPlayer(UUID playerUUID) {
        return this.mySQL.exists("SELECT * FROM playerlanguage WHERE uuid='" + playerUUID.toString() + "';");
    }

    @Override
    public void broadcastMessage(Translation translation) {
        this.getOnlineLanguagePlayers().forEach(languagePlayer -> languagePlayer.sendMessage(translation));
    }

    @Override
    public void kickAll(Translation translation) {
        this.getOnlineLanguagePlayers().forEach(languagePlayer -> languagePlayer.kickPlayer(translation));
    }

    private String validateLanguage(String language) {
        if (!languageAPI.isLanguage(language)) {
            return languageAPI.getDefaultLanguage();
        }
        return language;
    }

    private void logInfo(String message) {
        this.languageConfig.getLogger().log(Level.INFO, message);
    }
}
