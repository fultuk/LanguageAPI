package de.tentact.languageapi.player;

import com.zaxxer.hikari.HikariDataSource;
import de.tentact.languageapi.LanguageAPI;
import de.tentact.languageapi.mysql.MySQL;
import de.tentact.languageapi.util.ConfigUtil;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import java.util.logging.Level;

public class PlayerExecutorImpl extends PlayerManagerImpl implements PlayerExecutor {

    private final MySQL mySQL = ConfigUtil.getMySQL();
    private final LanguageAPI languageAPI = LanguageAPI.getInstance();
    private final HikariDataSource dataSource = mySQL.getDataSource();

    @NotNull
    @Override
    public String getPlayerLanguage(UUID playerUUID) {
        if (!isRegisteredPlayer(playerUUID)) {
            this.registerPlayer(playerUUID);
        }

        try (Connection connection = this.mySQL.getDataSource().getConnection()) {
            ResultSet rs = connection.createStatement().executeQuery("SELECT language FROM choosenlang WHERE uuid='" + playerUUID.toString() + "';");
            if (rs.next()) {

                return rs.getString("language").toLowerCase();
            }
        } catch (SQLException throwables) {
            return languageAPI.getDefaultLanguage();
        }
        return languageAPI.getDefaultLanguage();
    }

    @Override
    public boolean isPlayersLanguage(UUID playerUUID, String language) {
        if(!this.languageAPI.isLanguage(language)) {
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
        if (!this.isRegisteredPlayer(playerUUID)) {
            throw new UnsupportedOperationException();
        }
        if (!languageAPI.isLanguage(newLanguage)) {
            throw new IllegalArgumentException("Language " + newLanguage + " was not found!");
        }
        try (Connection connection = this.dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("UPDATE choosenlang WHERE uuid=? SET language=?;")) {
            preparedStatement.setString(1, playerUUID.toString());
            preparedStatement.setString(2, newLanguage.toLowerCase());

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
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
                logInfo("Updating players selected language");
            }
        }
    }

    @Override
    public boolean isRegisteredPlayer(UUID playerUUID) {
        return this.mySQL.exists("SELECT * FROM choosenlang WHERE uuid='" + playerUUID.toString() + "';");
    }

    private String validateLanguage(String language) {
        if (!languageAPI.isLanguage(language)) {
            return languageAPI.getDefaultLanguage();
        }
        return language;
    }

    private void logInfo(String message) {
        ConfigUtil.log(message, Level.INFO);
    }
}
