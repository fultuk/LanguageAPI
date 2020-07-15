package de.tentact.languageapi.api;
/*  Created in the IntelliJ IDEA.
    Created by 0utplay | Aldin Sijamhodzic
    Datum: 25.04.2020
    Uhrzeit: 16:52
*/

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import de.tentact.languageapi.AbstractLanguageAPI;
import de.tentact.languageapi.mysql.MySQL;
import de.tentact.languageapi.util.ChatColorTranslator;
import de.tentact.languageapi.util.Source;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

public class LanguageAPI extends AbstractLanguageAPI {

    private final MySQL mySQL = Source.getMySQL();

    private final Cache<String, HashMap<String, String>> translationCache = CacheBuilder.newBuilder().expireAfterWrite(5L, TimeUnit.MINUTES).build();

    @Override
    public void createLanguage(@NotNull final String language) {
        if (this.getAvailableLanguages().isEmpty() || !this.isLanguage(language)) {
            this.mySQL.createTable(language.replace(" ", "").toLowerCase());
            this.mySQL.update("INSERT INTO languages(language) VALUES ('" + language.toLowerCase() + "')");
            logInfo("Creating new language:" + language);


        }

    }

    @Override
    public void deleteLanguage(@NotNull String language) {
        if (!this.getDefaultLanguage().equalsIgnoreCase(language) && this.isLanguage(language)) {
            this.mySQL.update("DROP TABLE " + language.toLowerCase());
            this.mySQL.update("DELETE FROM languages WHERE language='" + language.toLowerCase() + "'");
            logInfo("Deleting language:" + language);

        }

    }

    @Override
    public void setPlayerLanguage(@NotNull UUID playerUUID, @NotNull String newLanguage, boolean orElseDefault) {
        this.registerPlayer(playerUUID);
        if (!this.isLanguage(newLanguage)) {
            this.setPlayerLanguage(playerUUID, this.getDefaultLanguage());
            return;
        }
        this.mySQL.update("UPDATE choosenlang WHERE uuid='" + playerUUID.toString() + "' SET language='" + newLanguage.toLowerCase() + "';");
    }

    @Override
    public void setPlayerLanguage(@NotNull UUID playerUUID, @NotNull String newLanguage) {
        this.registerPlayer(playerUUID);
        if (!this.isLanguage(newLanguage)) {
            throw new IllegalArgumentException("Language " + newLanguage + " was not found!");
        }
        this.mySQL.update("UPDATE choosenlang WHERE uuid='" + playerUUID.toString() + "' SET language='" + newLanguage.toLowerCase() + "';");
    }

    @Override
    public void registerPlayer(@NotNull UUID playerUUID) {
        this.registerPlayer(playerUUID, this.getDefaultLanguage());
    }

    @Override
    public void registerPlayer(@NotNull UUID playerUUID, @NotNull String language) {
        if (!this.isRegisteredPlayer(playerUUID)) {
            if (!this.isLanguage(language)) {
                logInfo("Registering player with default language (" + this.getDefaultLanguage() + ")");
                new Thread(() -> this.mySQL.update("INSERT INTO choosenlang(uuid, language) VALUES ('" + playerUUID.toString() + "', '" + this.getDefaultLanguage() + "');")).start();
                return;
            }
            new Thread(() -> this.mySQL.update("INSERT INTO choosenlang(uuid, language) VALUES ('" + playerUUID.toString() + "', '" + language.toLowerCase() + "');")).start();
            logInfo("Registering player with language: " + language);
        } else {
            if (!this.isLanguage(this.getPlayerLanguage(playerUUID))) {
                new Thread(() -> this.mySQL.update("UPDATE choosenlang SET language='" + this.getDefaultLanguage() + "' WHERE uuid='" + playerUUID.toString() + "';")).start();
                logInfo("Updating players selected language");
            }
        }
    }

    @Override
    public boolean isRegisteredPlayer(@NotNull UUID playerUUID) {
        return this.mySQL.exists("SELECT * FROM choosenlang WHERE uuid='" + playerUUID.toString() + "';");
    }

    @Override
    public void addMessage(@NotNull final String transkey,
                           @NotNull final String message,
                           @NotNull final String language,
                           @NotNull final String param) {
        if (!this.isLanguage(language)) {
            throw new IllegalArgumentException("Language " + language + " was not found!");
        }
        this.addMessage(transkey, message, language);
        this.addParameter(transkey, param);
    }

    @Override
    public void addMessage(@NotNull final String transkey,
                           @NotNull final String message,
                           @NotNull final String language) {
        if (this.isLanguage(language)) {
            new Thread(()
                    -> this.mySQL.update("INSERT INTO " + language.toLowerCase() + "(transkey, translation) VALUES ('" + transkey.toLowerCase() +
                    "', '" + ChatColorTranslator.translateAlternateColorCodes('&', message) + "');")).start();
        }
    }

    @Override
    public void addParameter(@NotNull final String transkey,
                             @NotNull final String param) {
        new Thread(() -> this.mySQL.update("INSERT INTO Parameter (transkey, param) VALUES ('" + transkey.toLowerCase() + "', '" + param + "');")).start();

    }

    @Override
    public void deleteParameter(@NotNull final String transkey,
                                @NotNull final String param) {
        if (!this.hasParameter(transkey)) {
            return;
        }
        if (!this.getParameter(transkey).contains(param)) {
            return;
        }
        new Thread(() -> this.mySQL.update("UPDATE Parameter SET param='" + getParameter(transkey).replace(param, "") + "' WHERE transkey='" + transkey + "';")).start();

    }

    @Override
    public void deleteAllParameter(@NotNull final String transkey) {
        if (!this.hasParameter(transkey)) {
            return;
        }
        new Thread(() -> this.mySQL.update("DELETE FROM Parameter WHERE transkey='" + transkey + "';")).start();

    }

    @Override
    public void addMessage(@NotNull final String transkey,
                           @NotNull final String language) {
        if (!this.isLanguage(language)) {
            return;
        }
        if (this.isKey(transkey, language)) {
            return;
        }
        this.addMessage(transkey, transkey, language);

    }

    @Override
    public void addMessage(@NotNull final String transkey) {
        if (this.isKey(transkey, this.getDefaultLanguage().toLowerCase())) {
            return;
        }
        this.addMessage(transkey, transkey, this.getDefaultLanguage());

    }

    @Override
    public void addMessageToDefault(@NotNull final String transkey,
                                    @NotNull final String translation) {
        if (this.isKey(transkey, this.getDefaultLanguage().toLowerCase())) {
            return;
        }
        this.addMessage(transkey, translation, this.getDefaultLanguage());

    }

    @Override
    public void addMessageToDefault(@NotNull final String transkey,
                                    @NotNull final String translation,
                                    @NotNull final String param) {
        if (this.isKey(transkey, this.getDefaultLanguage().toLowerCase())) {
            return;
        }
        this.addMessageToDefault(transkey, translation);
        this.addParameter(transkey, param);

    }

    @Override
    public void addTranslationKeyToMultipleTranslation(@NotNull final String multipleTranslation,
                                                       @NotNull final String transkey) {

        String[] translationKeys = new String[]{};
        try (Connection connection = this.mySQL.dataSource.getConnection()) {
            ResultSet resultSet = connection.createStatement().executeQuery("SELECT transkeys FROM MultipleTranslation WHERE multipleKey='" + multipleTranslation.toLowerCase() + "'");
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
    public void copyLanguage(@NotNull String langfrom,
                             @NotNull String langto) {
        if (!this.isLanguage(langfrom.toLowerCase()) || !this.isLanguage(langto.toLowerCase())) {
            throw new IllegalArgumentException("Language " + langfrom + " or " + langto + " was not found!");
        }
        this.mySQL.update("INSERT INTO " + langto.toLowerCase() + " SELECT * FROM " + langfrom.toLowerCase() + ";");

    }

    @Override
    public boolean hasParameter(@NotNull String translationKey) {
        return this.mySQL.exists("SELECT param FROM Parameter WHERE transkey='" + translationKey + "';");
    }

    @Override
    public String getParameter(@NotNull String translationKey) {
        if (!this.hasParameter(translationKey)) {
            throw new IllegalArgumentException(translationKey + " has no parameter");
        }
        try (Connection connection = this.mySQL.dataSource.getConnection()) {
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
    public boolean isParameter(@NotNull String translationKey,
                               @NotNull String param) {
        return this.getParameter(translationKey).contains(param);
    }

    @Override
    public void deleteMessageInEveryLang(@NotNull String transkey) {
        for (String langs : this.getAvailableLanguages()) {
            if (this.isKey(transkey, langs)) {
                this.deleteMessage(transkey, langs);
            }
        }
    }

    @Override
    public void updateMessage(@NotNull String transkey,
                              @NotNull String language,
                              @NotNull String message) {
        if (!this.isLanguage(language)) {
            throw new IllegalArgumentException("Language " + language + " was not found!");
        }
        if (!this.isKey(transkey, language)) {
            throw new IllegalArgumentException("Translationkey " + transkey + " was not found!");
        }

        new Thread(() -> this.mySQL.update("UPDATE " + language.toLowerCase() + " SET translation='" + ChatColorTranslator.translateAlternateColorCodes('&', message) + "' WHERE transkey='" + transkey.toLowerCase() + "';")).start();
        translationCache.invalidate(transkey.toLowerCase());

    }

    @Override
    public void setMultipleTranslation(@NotNull String multipleTranslation,
                                       @NotNull List<String> translationKeys,
                                       boolean overwrite) {
        if (isMultipleTranslation(multipleTranslation) && overwrite) {
            this.removeMultipleTranslation(multipleTranslation);
        }
        StringBuilder stringBuilder = new StringBuilder();

        for (String translationKey : translationKeys) {
            stringBuilder.append(translationKey.toLowerCase()).append(",");
        }
        new Thread(() -> this.mySQL.update("INSERT INTO MultipleTranslation(multipleKey, transkeys) VALUES ('" + multipleTranslation.toLowerCase() + "','" + stringBuilder.toString() + "');")).start();
    }

    @Override
    public void removeMultipleTranslation(@NotNull final String multipleTranslation) {
        if (!isMultipleTranslation(multipleTranslation)) {
            throw new IllegalArgumentException(multipleTranslation + " was not found");
        }
        new Thread(() -> this.mySQL.update("DELETE FROM MultipleTranslation WHERE multipleKey='" + multipleTranslation + "';")).start();
    }

    @Override
    public void removeSingleTranslationFromMultipleTranslation(@NotNull final String multipleTranslation,
                                                               @NotNull final String transkey) {
        if (!isMultipleTranslation(multipleTranslation)) {
            throw new IllegalArgumentException(multipleTranslation + " was not found");
        }

        ArrayList<String> translationKeysAsArrayList = null;
        try (Connection connection = this.mySQL.dataSource.getConnection()) {
            ResultSet resultSet = connection.createStatement().executeQuery("SELECT transkeys FROM MultipleTranslation WHERE multipleKey='" + multipleTranslation.toLowerCase() + "'");
            if (resultSet.next()) {
                translationKeysAsArrayList = new ArrayList<>(Arrays.asList(resultSet.getString("transkeys").split(",")));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        assert translationKeysAsArrayList != null;
        translationKeysAsArrayList.remove(transkey);
        this.setMultipleTranslation(multipleTranslation, translationKeysAsArrayList, true);
    }

    @Override
    public boolean isMultipleTranslation(@NotNull final String multipleTranslation) {
        return this.mySQL.exists("SELECT * FROM MultipleTranslation WHERE multipleKey='" + multipleTranslation.toLowerCase() + "';");
    }

    @Override
    public void deleteMessage(@NotNull String transkey,
                              @NotNull String language) {
        if (!this.isLanguage(language)) {
            throw new IllegalArgumentException("Language " + language + " was not found!");
        }
        if (!this.isKey(transkey, language)) {
            throw new IllegalArgumentException("Translationkey " + transkey + " was not found!");
        }
        new Thread(() -> this.mySQL.update("DELETE FROM " + language.toLowerCase() + " WHERE transkey='" + transkey.toLowerCase() + "';")).start();
    }

    @NotNull
    @Override
    public String getPlayerLanguage(@NotNull UUID playerUUID) {
        if (!isRegisteredPlayer(playerUUID)) {
            this.registerPlayer(playerUUID);
        }

        try (Connection connection = this.mySQL.dataSource.getConnection()) {
            ResultSet rs = connection.createStatement().executeQuery("SELECT language FROM choosenlang WHERE uuid='" + playerUUID.toString() + "';");
            if (rs.next()) {

                return rs.getString("language").toLowerCase();
            }
        } catch (SQLException throwables) {
            return this.getDefaultLanguage();
        }
        return this.getDefaultLanguage();
    }

    @Override
    public boolean isKey(@NotNull String transkey,
                         @NotNull String lang) {
        return this.mySQL.exists("SELECT * FROM " + lang.toLowerCase() + " WHERE transkey='" + transkey.toLowerCase() + "';");
    }

    @NotNull
    @Override
    public String getMessage(@NotNull String transkey,
                             @NotNull UUID playerUUID,
                             boolean usePrefix) {
        return this.getMessage(transkey, this.getPlayerLanguage(playerUUID), usePrefix);
    }

    @Override
    public String getMessage(@NotNull String translationkey,
                             @NotNull String language,
                             boolean usePrefix) {
        return usePrefix ? this.getPrefix(language) + this.getMessage(translationkey, language) : this.getMessage(translationkey, language);
    }


    @NotNull
    @Override
    public String getMessage(@NotNull String transkey,
                             @NotNull UUID playerUUID) {
        return this.getMessage(transkey, this.getPlayerLanguage(playerUUID));
    }

    @NotNull
    @Override
    public ArrayList<String> getMultipleMessages(@NotNull String transkey) {
        return this.getMultipleMessages(transkey, this.getDefaultLanguage());
    }

    @NotNull
    @Override
    public ArrayList<String> getMultipleMessages(@NotNull String transkey,
                                                 @NotNull UUID playerUUID) {
        return this.getMultipleMessages(transkey, this.getPlayerLanguage(playerUUID));
    }

    @Override
    public ArrayList<String> getMultipleMessages(@NotNull String transkey,
                                                 @NotNull String language) {
        return this.getMultipleMessages(transkey, language, false);
    }

    @Override
    public ArrayList<String> getMultipleMessages(@NotNull String transkey,
                                                 @NotNull UUID playerUUID,
                                                 boolean usePrefix) {
        return this.getMultipleMessages(transkey, this.getPlayerLanguage(playerUUID), usePrefix);
    }

    @NotNull
    @Override
    public ArrayList<String> getMultipleMessages(@NotNull String transkey,
                                                 @NotNull String language,
                                                 boolean usePrefix) {
        ArrayList<String> resolvedMessages = new ArrayList<>();
        String[] translationKeys = new String[]{};
        try (Connection connection = this.mySQL.dataSource.getConnection()) {
            ResultSet resultSet = connection.createStatement().executeQuery("SELECT transkeys FROM MultipleTranslation WHERE multipleKey='" + transkey.toLowerCase() + "'");
            if (resultSet.next()) {
                String mysqlString = resultSet.getString("transkeys");
                translationKeys = mysqlString.split(",");
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        for (String translationKey : translationKeys) {
            resolvedMessages.add(this.getMessage(translationKey, language, usePrefix));
        }

        return resolvedMessages;
    }

    @NotNull
    @Override
    public String getMessage(@NotNull String transkey, @NotNull String lang) {
        if (!this.isLanguage(lang)) {
            throw new IllegalArgumentException(lang + " was not found");
        }
        if (!this.isKey(transkey, lang)) {
            throw new IllegalArgumentException(transkey + " not found for language " + lang);
        }
        if (this.translationCache.getIfPresent(transkey) != null && Objects.requireNonNull(this.translationCache.getIfPresent(transkey)).containsKey(lang)) {
            return Objects.requireNonNull(this.translationCache.getIfPresent(transkey)).get(lang);
        }

        try (Connection connection = this.mySQL.dataSource.getConnection()) {
            ResultSet rs = connection.createStatement().executeQuery("SELECT translation FROM " + lang.toLowerCase() + " WHERE transkey='" + transkey.toLowerCase() + "';");
            if (rs.next()) {
                String translation = ChatColorTranslator.translateAlternateColorCodes('&', rs.getString("translation"));

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
    public boolean isLanguage(@NotNull String language) {
        if (this.getAvailableLanguages().isEmpty()) {
            throw new UnsupportedOperationException("There are no languages available");
        }
        return this.getAvailableLanguages().contains(language.toLowerCase());
    }

    @NotNull
    @Override
    public ArrayList<String> getAvailableLanguages() {
        ArrayList<String> languages = new ArrayList<>();

        try (Connection connection = this.mySQL.dataSource.getConnection()) {
            ResultSet rs = connection.createStatement().executeQuery("SELECT language FROM languages");
            while (rs.next()) {
                languages.add(rs.getString("language").toLowerCase());
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return languages;
    }

    @Override
    public ArrayList<String> getAllTranslationKeys(@NotNull String language) {
        ArrayList<String> keys = new ArrayList<>();
        if (this.isLanguage(language)) {
            try (Connection connection = this.mySQL.dataSource.getConnection()) {
                ResultSet rs = connection.createStatement().executeQuery("SELECT transkey FROM " + language);
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
    public ArrayList<String> getAllTranslations(@NotNull String language) {
        ArrayList<String> messages = new ArrayList<>();
        if (this.isLanguage(language)) {
            try (Connection connection = this.mySQL.dataSource.getConnection()) {
                ResultSet rs = connection.createStatement().executeQuery("SELECT translation FROM " + language);
                while (rs.next()) {
                    messages.add(rs.getString("translation"));
                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();

            }
            //AH
            return messages;
        }
        throw new IllegalArgumentException(language + " was not found");
    }

    @Override
    public String getDefaultLanguage() {
        return Source.getDefaultLanguage().toLowerCase();
    }

    @Override
    public String getPrefix() {
        return this.getMessage("languageapi-prefix", this.getDefaultLanguage());
    }

    @Override
    public String getPrefix(@NotNull String language) {
        return this.getMessage("languageapi-prefix", language);
    }

    private void logInfo(@NotNull String message) {
        Source.log(message, Level.INFO);
    }

}
