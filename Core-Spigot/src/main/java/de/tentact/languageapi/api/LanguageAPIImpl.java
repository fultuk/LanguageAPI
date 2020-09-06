package de.tentact.languageapi.api;
/*  Created in the IntelliJ IDEA.
    Created by 0utplay | Aldin Sijamhodzic
    Datum: 25.04.2020
    Uhrzeit: 16:52
*/

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.zaxxer.hikari.HikariDataSource;
import de.tentact.languageapi.LanguageAPI;
import de.tentact.languageapi.configuration.LanguageConfig;
import de.tentact.languageapi.configuration.MySQL;
import de.tentact.languageapi.i18n.Translation;
import de.tentact.languageapi.player.*;
import net.md_5.bungee.api.ChatColor;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

public class LanguageAPIImpl extends LanguageAPI {

    private final MySQL mySQL;
    private final LanguageConfig languageConfig;

    private final Cache<String, HashMap<String, String>> translationCache;
    private final HashMap<String, Translation> translationMap = new HashMap<>();
    private final PlayerManager playerManager = new PlayerManagerImpl();
    private final PlayerExecutor playerExecutor;
    private final ExecutorService executorService = Executors.newCachedThreadPool(new ThreadFactoryBuilder().build());

    public LanguageAPIImpl(LanguageConfig languageConfig) {
        this.languageConfig = languageConfig;
        this.playerExecutor = new PlayerExecutorImpl(this, languageConfig);
        this.mySQL = languageConfig.getMySQL();
        this.translationCache = CacheBuilder.newBuilder().expireAfterWrite(languageConfig.getLanguageSetting().getCachedTime(), TimeUnit.MINUTES).build();
    }

    @Override
    public void createLanguage(final String language) {
        if (this.getAvailableLanguages().isEmpty() || !this.isLanguage(language)) {
            this.mySQL.createTable(language.replace(" ", "").toLowerCase());
            try (Connection connection = this.getDataSource().getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO languages(language) VALUES (?)")) {
                preparedStatement.setString(1, language.toLowerCase());
                preparedStatement.execute();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            logInfo("Creating new language:" + language);
        }
    }

    @Override
    public void deleteLanguage(String language) {
        if (!this.getDefaultLanguage().equalsIgnoreCase(language) && this.isLanguage(language)) {
            executorService.execute(() -> {
                try (Connection connection = this.getDataSource().getConnection()) {
                    try (PreparedStatement preparedStatement = connection.prepareStatement("DROP TABLE ?;")) {
                        preparedStatement.setString(1, language.toLowerCase());
                        preparedStatement.execute();
                    }
                    try (PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM languages WHERE language=?;")) {
                        preparedStatement.setString(1, language.toLowerCase());
                        preparedStatement.execute();
                    }
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
                logInfo("Deleting language:" + language);
            });
        }
    }


    @Override
    public void addMessage(final String transkey, final String message, final String language, String param) {
        if (!this.isLanguage(language)) {
            throw new IllegalArgumentException("Language " + language + " was not found!");
        }
        this.addMessage(transkey, message, language);
        this.addParameter(transkey, param);
    }

    @Override
    public void addMessage(final String transkey, final String message, final String language) {
        if (!this.isLanguage(language)) {
            return;
        }
        if (this.isKey(transkey, language)) {
            return;
        }
        try (Connection connection = this.getDataSource().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO " + language.toLowerCase() + " (transkey, translation) VALUES (?,?);")) {
            preparedStatement.setString(1, transkey.toLowerCase());
            preparedStatement.setString(2, ChatColor.translateAlternateColorCodes('&', message));
            preparedStatement.execute();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    @Override
    public void addParameter(final String transkey, final String param) {
        if (this.hasParameter(transkey)) {
            return;
        }
        if(param == null || param.isEmpty()) {
            return;
        }
        try (Connection connection = this.getDataSource().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO Parameter(transkey, param) VALUES (?,?);")) {
            preparedStatement.setString(1, transkey.toLowerCase());
            preparedStatement.setString(2, param.replace(" ", ""));
            preparedStatement.execute();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    @Override
    public void deleteParameter(final String transkey, final String param) {
        if (!this.hasParameter(transkey)) {
            return;
        }
        if (!this.getParameter(transkey).contains(param)) {
            return;
        }
        try (Connection connection = this.getDataSource().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("UPDATE Parameter SET param=? WHERE transkey=?;")) {
            preparedStatement.setString(1, this.getParameter(transkey).replace(param, ""));
            preparedStatement.setString(2, transkey);
            preparedStatement.execute();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    @Override
    public void deleteAllParameter(final String transkey) {
        if (!this.hasParameter(transkey)) {
            return;
        }
        try (Connection connection = this.getDataSource().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM Parameter WHERE transkey=?;")) {
            preparedStatement.setString(1, transkey);
            preparedStatement.execute();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    @Override
    public void addMessage(final String transkey, final String language) {
        if (!this.isLanguage(language)) {
            return;
        }
        if (this.isKey(transkey, language)) {
            return;
        }
        this.addMessage(transkey, transkey, language);
    }

    @Override
    public void addMessage(final String transkey) {
        if (this.isKey(transkey, this.getDefaultLanguage().toLowerCase())) {
            return;
        }
        this.addMessage(transkey, transkey, this.getDefaultLanguage());
    }

    @Override
    public void addMessageToDefault(final String transkey, final String translation) {
        if (this.isKey(transkey, this.getDefaultLanguage().toLowerCase())) {
            return;
        }
        this.addMessage(transkey, translation, this.getDefaultLanguage());
    }

    @Override
    public void addMessageToDefault(final String transkey, final String translation, final String param) {
        if (this.isKey(transkey, this.getDefaultLanguage().toLowerCase())) {
            return;
        }
        this.addMessageToDefault(transkey, translation);
        this.addParameter(transkey, param);
    }

    @Override
    public void addTranslationKeyToMultipleTranslation(final String multipleTranslation, final String transkey) {
        String[] translationKeys = new String[]{};
        try (Connection connection = this.mySQL.getDataSource().getConnection();
             ResultSet resultSet = connection.createStatement().executeQuery("SELECT transkeys FROM MultipleTranslation WHERE multipleKey='" + multipleTranslation.toLowerCase() + "'");) {
            if (resultSet.next()) {
                translationKeys = resultSet.getString("transkeys").split(",");
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        ArrayList<String> translationKeysAsArrayList = new ArrayList<>(Arrays.asList(translationKeys));
        translationKeysAsArrayList.add(transkey);
        this.setMultipleTranslation(multipleTranslation, translationKeysAsArrayList, true);
    }

    @Override
    public void copyLanguage(String langfrom, String langto) {
        if (!this.isLanguage(langfrom.toLowerCase()) || !this.isLanguage(langto.toLowerCase())) {
            throw new IllegalArgumentException("Language " + langfrom + " or " + langto + " was not found!");
        }
        try (Connection connection = this.getDataSource().getConnection()) {
            connection.createStatement().execute("INSERT IGNORE " + langto + " SELECT * FROM " + langfrom + ";");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    @Override
    public boolean hasParameter(String translationKey) {
        return this.mySQL.exists("SELECT param FROM Parameter WHERE transkey='" + translationKey + "';");
    }

    @Override
    public String getParameter(String translationKey) {
        if (!this.hasParameter(translationKey)) {
            throw new IllegalArgumentException(translationKey + " has no parameter");
        }
        try (Connection connection = this.mySQL.getDataSource().getConnection()) {
            ResultSet rs = connection.createStatement().executeQuery("SELECT param FROM Parameter WHERE transkey='" + translationKey.toLowerCase() + "';");
            if (rs.next()) {
                return rs.getString("param");
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        throw new IllegalArgumentException(translationKey + " has no parameter");
    }

    @Override
    public boolean isParameter(String translationKey, String param) {
        return this.getParameter(translationKey).contains(param);
    }

    @Override
    public void deleteMessageInEveryLang(String transkey) {
        for (String langs : this.getAvailableLanguages()) {
            if (this.isKey(transkey, langs)) {
                this.deleteMessage(transkey, langs);
            }
        }
    }

    @Override
    public void updateMessage(String transkey, String language, String message) {
        if (!this.isLanguage(language)) {
            throw new IllegalArgumentException("Language " + language + " was not found!");
        }
        if (!this.isKey(transkey, language)) {
            throw new IllegalArgumentException("Translationkey " + transkey + " was not found!");
        }
        try (Connection connection = this.getDataSource().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("UPDATE ? SET translation= ? WHERE transkey=?;")) {
            preparedStatement.setString(1, language.toLowerCase());
            preparedStatement.setString(2, ChatColor.translateAlternateColorCodes('&', message));
            preparedStatement.execute();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        translationCache.invalidate(transkey.toLowerCase());
    }

    @Override
    public void setMultipleTranslation(String multipleTranslation, List<String> translationKeys, boolean overwrite) {
        if (isMultipleTranslation(multipleTranslation) && overwrite) {
            this.removeMultipleTranslation(multipleTranslation);
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (String translationKey : translationKeys) {
            stringBuilder.append(translationKey.toLowerCase()).append(",");
        }
        try (Connection connection = this.getDataSource().getConnection();
             PreparedStatement preparedStatement =
                     connection.prepareStatement("INSERT INTO MultipleTranslation(multipleKey, transkeys) VALUES (?,?)")) {
            preparedStatement.setString(1, multipleTranslation);
            preparedStatement.setString(2, stringBuilder.toString());
            preparedStatement.execute();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }


    @Override
    public void removeMultipleTranslation(final String multipleTranslation) {
        if (!isMultipleTranslation(multipleTranslation)) {
            throw new IllegalArgumentException(multipleTranslation + " was not found");
        }
        try (Connection connection = this.getDataSource().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM MultipleTranslation WHERE multipleKey=?;")) {
            preparedStatement.setString(1, multipleTranslation);
            preparedStatement.execute();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    @Override
    public void removeSingleTranslationFromMultipleTranslation(final String multipleTranslation, final String transkey) {
        if (!isMultipleTranslation(multipleTranslation)) {
            throw new IllegalArgumentException(multipleTranslation + " was not found");
        }
        ArrayList<String> translationKeysAsArrayList = null;
        try (Connection connection = this.mySQL.getDataSource().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT transkeys FROM MultipleTranslation WHERE multipleKey=?")) {
            preparedStatement.setString(1, multipleTranslation.toLowerCase());
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                translationKeysAsArrayList = new ArrayList<>(Arrays.asList(resultSet.getString("transkeys").split(",")));
            }
            resultSet.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        assert translationKeysAsArrayList != null;
        translationKeysAsArrayList.remove(transkey);
        this.setMultipleTranslation(multipleTranslation, translationKeysAsArrayList, true);
    }

    @Override
    public boolean isMultipleTranslation(final String multipleTranslation) {
        return this.mySQL.exists("SELECT * FROM MultipleTranslation WHERE multipleKey='" + multipleTranslation.toLowerCase() + "';");
    }

    @Override
    public void deleteMessage(String transkey, String language) {
        if (!this.isLanguage(language)) {
            throw new IllegalArgumentException("Language " + language + " was not found!");
        }
        if (!this.isKey(transkey, language)) {
            throw new IllegalArgumentException("Translationkey " + transkey + " was not found!");
        }
        try (Connection connection = this.getDataSource().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM ? WHERE transkey=?;")) {
            preparedStatement.setString(1, language.toLowerCase());
            preparedStatement.setString(2, transkey.toLowerCase());
            preparedStatement.execute();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    @Override
    public boolean isKey(String transkey, String lang) {
        return this.mySQL.exists("SELECT * FROM " + lang.toLowerCase() + " WHERE transkey='" + transkey.toLowerCase() + "';");
    }

    @NotNull
    @Override
    public String getMessage(String transkey, UUID playerUUID) {
        return this.getMessage(transkey, this.playerExecutor.getPlayerLanguage(playerUUID));
    }

    @NotNull
    @Override
    public ArrayList<String> getMultipleMessages(String transkey) {
        return this.getMultipleMessages(transkey, this.getDefaultLanguage());
    }

    @NotNull
    @Override
    public ArrayList<String> getMultipleMessages(String transkey, UUID playerUUID) {
        return this.getMultipleMessages(transkey, this.playerExecutor.getPlayerLanguage(playerUUID));
    }

    @NotNull
    @Override
    public ArrayList<String> getMultipleMessages(String transkey, String language) {
        ArrayList<String> resolvedMessages = new ArrayList<>();
        String[] translationKeys = new String[]{};
        try (Connection connection = this.mySQL.getDataSource().getConnection();
             ResultSet resultSet = connection.createStatement().executeQuery("SELECT transkeys FROM MultipleTranslation WHERE multipleKey='" + transkey.toLowerCase() + "'");) {
            if (resultSet.next()) {
                String mysqlString = resultSet.getString("transkeys");
                translationKeys = mysqlString.split(",");
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        for (String translationKey : translationKeys) {
            resolvedMessages.add(this.getMessage(translationKey, language));
        }
        return resolvedMessages;
    }

    @NotNull
    @Override
    public String getMessage(String transkey, String lang) {
        if (!this.isLanguage(lang)) {
            throw new IllegalArgumentException(lang + " was not found");
        }
        if (!this.isKey(transkey, lang)) {
            this.languageConfig.getLogger().log(Level.WARNING, "Translationkey '"+transkey+"' not found in language '"+lang+"'");
            this.languageConfig.getLogger().log(Level.WARNING, "As result you will get the translationkey as translation");
            return transkey;
        }
        if (this.translationCache.getIfPresent(transkey) != null && Objects.requireNonNull(this.translationCache.getIfPresent(transkey)).containsKey(lang)) {
            return Objects.requireNonNull(this.translationCache.getIfPresent(transkey)).get(lang);
        }
        try (Connection connection = this.mySQL.getDataSource().getConnection();
             ResultSet rs = connection.createStatement().executeQuery("SELECT translation FROM " + lang.toLowerCase() + " WHERE transkey='" + transkey.toLowerCase() + "';")) {
            if (rs.next()) {
                String translation = ChatColor.translateAlternateColorCodes('&', rs.getString("translation"));

                HashMap<String, String> cacheMap = new HashMap<>();
                cacheMap.put(lang, translation);
                translationCache.put(transkey, cacheMap);
                return translation;
            }
        } catch (SQLException throwables) {
            return transkey;
        }
        return transkey;
    }

    @Override
    public boolean isLanguage(String language) {
        if (this.getAvailableLanguages().isEmpty()) {
            throw new UnsupportedOperationException("There are no languages available");
        }
        return this.getAvailableLanguages().contains(language.toLowerCase());
    }

    @NotNull
    @Override
    public ArrayList<String> getAvailableLanguages() {
        ArrayList<String> languages = new ArrayList<>();
        try (Connection connection = this.mySQL.getDataSource().getConnection();
             ResultSet rs = connection.createStatement().executeQuery("SELECT language FROM languages");) {
            while (rs.next()) {
                languages.add(rs.getString("language").toLowerCase());
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return languages;
    }

    @Override
    public @NotNull ArrayList<String> getAllTranslationKeys(String language) {
        ArrayList<String> keys = new ArrayList<>();
        if (this.isLanguage(language)) {
            try (Connection connection = this.mySQL.getDataSource().getConnection();
                 ResultSet rs = connection.createStatement().executeQuery("SELECT transkey FROM " + language)) {
                while (rs.next()) {
                    keys.add(rs.getString("transkey"));
                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            return keys;
        }
        throw new IllegalArgumentException(language + " was not found");
    }

    @Override
    public @NotNull ArrayList<String> getAllTranslations(String language) {
        ArrayList<String> messages = new ArrayList<>();
        if (this.isLanguage(language)) {
            try (Connection connection = this.mySQL.getDataSource().getConnection();
                 ResultSet rs = connection.createStatement().executeQuery("SELECT translation FROM " + language)) {
                while (rs.next()) {
                    messages.add(rs.getString("translation"));
                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            return messages;
        }
        throw new IllegalArgumentException(language + " was not found");
    }

    @Override
    public @NotNull String getDefaultLanguage() {
        return this.languageConfig.getLanguageSetting().getDefaultLanguage().toLowerCase();
    }

    @Override
    public @NotNull String getPrefix() {
        return this.getMessage("languageapi-prefix", this.getDefaultLanguage());
    }

    @Override
    public @NotNull String getPrefix(String language) {
        return this.getMessage("languageapi-prefix", language);
    }

    @Override
    public @NotNull PlayerManager getPlayerManager() {
        return this.playerManager;
    }

    @Override
    public @NotNull Translation getTranslation(String translationkey) {
        if(this.translationMap.containsKey(translationkey)) {
            return this.translationMap.get(translationkey);
        }
        Translation translation = new TranslationImpl(translationkey);
        this.translationMap.put(translationkey, translation);
        return translation;
    }

    @Override
    public @NotNull Translation getTranslationWithPrefix(Translation prefixTranslation, String translationKey) {
        return this.getTranslation(translationKey).setPrefixTranslation(prefixTranslation);
    }

    @Override
    public @NotNull PlayerExecutor getPlayerExecutor() {
        return this.playerExecutor;
    }

    @Override
    public @NotNull SpecificPlayerExecutor getSpecificPlayerExecutor(@NotNull UUID playerId) {
        return new SpecificPlayerExecutorImpl(playerId);
    }

    @Override
    public void updateTranslation(Translation translation) {
        this.translationMap.put(translation.getTranslationKey(), translation);
    }

    private HikariDataSource getDataSource() {
        return this.mySQL.getDataSource();
    }

    private void logInfo(String message) {
        this.languageConfig.getLogger().log(Level.INFO, message);
    }


}
