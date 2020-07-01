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
import de.tentact.languageapi.util.Source;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class LanguageAPI extends AbstractLanguageAPI {

    private final MySQL mySQL = Source.getMySQL();

    private final Cache<String, HashMap<String, String>> translationCache = CacheBuilder.newBuilder().expireAfterWrite(5L, TimeUnit.MINUTES).build();

    @Override
    public void createLanguage(final String language) {
        if (!this.isLanguage(language)) {
            this.mySQL.createTable(language.replace(" ", "").toLowerCase());
            this.mySQL.update("INSERT INTO languages(language) VALUES ('" + language.toLowerCase() + "')");
        }

    }

    @Override
    public void deleteLanguage(String language) {
        if (!this.getDefaultLanguage().equalsIgnoreCase(language) && this.isLanguage(language)) {
            this.mySQL.update("DROP TABLE " + language.toLowerCase());
            this.mySQL.update("DELETE FROM languages WHERE language='" + language.toLowerCase() + "'");
        }

    }

    @Override
    public void setPlayerLanguage(UUID playerUUID, String newLanguage, boolean orElseDefault) {
        this.createPlayer(playerUUID);
        if (!this.isLanguage(newLanguage)) {
            this.setPlayerLanguage(playerUUID, this.getDefaultLanguage());
            return;
        }
        this.mySQL.update("UPDATE choosenlang WHERE uuid='" + playerUUID.toString() + "' SET language='" + newLanguage.toLowerCase() + "';");
    }

    @Override
    public void setPlayerLanguage(UUID playerUUID, String newLanguage) {
        this.createPlayer(playerUUID);
        if (!this.isLanguage(newLanguage)) {
            throw new IllegalArgumentException("Language " + newLanguage + " was not found!");
        }
        this.mySQL.update("UPDATE choosenlang WHERE uuid='" + playerUUID.toString() + "' SET language='" + newLanguage.toLowerCase() + "';");
    }

    @Override
    public void createPlayer(UUID playerUUID) {
        if (!this.isRegisteredPlayer(playerUUID)) {
            String language = getDefaultLanguage();
            if (!Source.isBungeeCordMode) {
                Player player = Bukkit.getPlayer(playerUUID);
                if (player != null) {
                    String localLanguage = player.getLocale().toLowerCase();
                    if (this.isLanguage(localLanguage)) {
                        language = localLanguage;
                    }
                }

            }
            String finalLanguage = language;
            new Thread(() -> this.mySQL.update("INSERT INTO choosenlang(uuid, language) VALUES ('" + playerUUID.toString() + "', '" + finalLanguage + "');")).start();

        } else {
            if (!this.isLanguage(this.getPlayerLanguage(playerUUID))) {
                new Thread(() -> this.mySQL.update("UPDATE choosenlang SET language='" + this.getDefaultLanguage() + "' WHERE uuid='" + playerUUID.toString() + "';")).start();
            }
        }

    }

    @Override
    public boolean isRegisteredPlayer(UUID playerUUID) {
        ResultSet rs = mySQL.getResult("SELECT * FROM choosenlang WHERE uuid='" + playerUUID.toString() + "';");
        try {
            if (rs.next()) {
                return true;
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return false;
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
        if (this.isLanguage(language)) {
            if (!Source.isBungeeCordMode) {
                new Thread(() -> this.mySQL.update("INSERT INTO " + language.toLowerCase() + "(transkey, translation) VALUES ('" + transkey.toLowerCase() + "', '" + ChatColor.translateAlternateColorCodes('&', message) + "');")).start();
            } else {
                new Thread(() -> this.mySQL.update("INSERT INTO " + language.toLowerCase() + "(transkey, translation) VALUES ('" + transkey.toLowerCase() + "', '" + net.md_5.bungee.api.ChatColor.translateAlternateColorCodes('&', message) + "');")).start();
            }

        }
    }

    @Override
    public void addParameter(final String transkey, final String param) {
        new Thread(() -> this.mySQL.update("INSERT INTO Parameter (transkey, param) VALUES ('" + transkey.toLowerCase() + "', '" + param + "');")).start();

    }

    @Override
    public void deleteParameter(final String transkey, final String param) {
        if (!this.hasParameter(transkey)) {
            return;
        }
        if (!this.getParameter(transkey).contains(param)) {
            return;
        }
        new Thread(() -> this.mySQL.update("UPDATE Parameter SET param='" + getParameter(transkey).replace(param, "") + "' WHERE transkey='" + transkey + "';")).start();

    }

    @Override
    public void deleteAllParameter(final String transkey) {
        if (!this.hasParameter(transkey)) {
            return;
        }
        new Thread(() -> this.mySQL.update("DELETE FROM Parameter WHERE transkey='" + transkey + "';")).start();

    }

    @Override
    public void addMessage(final String transkey, final String language) {
        if (!this.isLanguage(language)) {
            return;
        }
        if (this.isKey(transkey, language)) {
            return;
        }
        new Thread(() -> this.mySQL.update("INSERT INTO " + language.toLowerCase() + "(transkey, translation) VALUES ('" + transkey.toLowerCase() + "', '" + transkey + "');")).start();

    }

    @Override
    public void addMessage(final String transkey) {
        if (this.isKey(transkey, this.getDefaultLanguage().toLowerCase())) {
            return;
        }
        new Thread(() -> this.mySQL.update("INSERT INTO " + Source.getDefaultLanguage().toLowerCase() + "(transkey, translation) VALUES ('" + transkey.toLowerCase() + "', '" + transkey + "');")).start();

    }

    @Override
    public void addMessageToDefault(final String transkey, final String translation) {
        if (this.isKey(transkey, this.getDefaultLanguage().toLowerCase())) {
            return;
        }
        new Thread(() -> this.mySQL.update("INSERT INTO " + Source.getDefaultLanguage().toLowerCase()
                + "(transkey, translation) VALUES ('" + transkey.toLowerCase() + "', '" + ChatColor.translateAlternateColorCodes('&', translation) + "');")).start();

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
        ResultSet resultSet = this.mySQL.getResult("SELECT keys FROM MultipleTranslation WHERE transkey='" + multipleTranslation.toLowerCase() + "'");
        String[] translationKeys = new String[]{};
        try {
            if (resultSet.next()) {
                translationKeys = resultSet.getString("keys").split(",");
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
        if (!this.isLanguage(langfrom) || !this.isLanguage(langto)) {
            throw new IllegalArgumentException("Language " + langfrom + " or " + langto + " was not found!");
        }
        this.mySQL.update("INSERT INTO " + langto + " SELECT * FROM " + langfrom + ";");
    }

    @Override
    public boolean hasParameter(String translationKey) {
        ResultSet rs = this.mySQL.getResult("SELECT param FROM Parameter WHERE transkey='" + translationKey + "';");
        try {
            if (rs.next()) {
                return true;
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return false;
    }

    @Override
    public String getParameter(String translationKey) {
        if (!this.hasParameter(translationKey)) {
            throw new IllegalArgumentException(translationKey + " has no parameter");
        }
        ResultSet rs = this.mySQL.getResult("SELECT param FROM Parameter WHERE transkey='" + translationKey.toLowerCase() + "';");
        try {
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
        //new Thread(() -> this.mySQL.update("UPDATE " + language.toLowerCase() + " SET translation='" + ChatColor.translateAlternateColorCodes('&', message) + "' WHERE transkey='" + transkey.toLowerCase() + "';")).start();
        new Thread(() -> {
            try {
                PreparedStatement preparedStatement = this.mySQL.createStatement("UPDATE ? SET translation=? WHERE transkey=?");
                preparedStatement.setString(0, language.toLowerCase());
                preparedStatement.setString(1, ChatColor.translateAlternateColorCodes('&', message));
                preparedStatement.setString(2, transkey.toLowerCase());
                preparedStatement.execute();
            } catch (SQLException ex) {

                ex.printStackTrace();
            }
        }).start();
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
        new Thread(() -> this.mySQL.update("INSERT INTO MultipleTranslation(transkey, keys) VALUES ('" + multipleTranslation.toLowerCase() + "','" + stringBuilder.toString() + "')")).start();
    }

    @Override
    public void removeMultipleTranslation(final String multipleTranslation) {
        if (!isMultipleTranslation(multipleTranslation)) {
            //THROW
            return;
        }
        new Thread(() -> this.mySQL.update("DELETE FROM MultipleTranslation WHERE transkey='" + multipleTranslation + "'")).start();
    }

    @Override
    public void removeSingleTranslationFromMultipleTranslation(final String multipleTranslation, final String transkey) {
        ResultSet resultSet = this.mySQL.getResult("SELECT keys FROM MultipleTranslation WHERE transkey='" + multipleTranslation.toLowerCase() + "'");
        ArrayList<String> translationKeysAsArrayList = null;
        try {
            if (resultSet.next()) {
                translationKeysAsArrayList = new ArrayList<>(Arrays.asList(resultSet.getString("keys").split(",")));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        assert translationKeysAsArrayList != null;
        translationKeysAsArrayList.remove(transkey);
        this.setMultipleTranslation(multipleTranslation, translationKeysAsArrayList, true);
    }

    @Override
    public boolean isMultipleTranslation(final String multipleTranslation) {
        ResultSet rs = this.mySQL.getResult("SELECT * FROM MultipleTranslation WHERE transkey='" + multipleTranslation.toLowerCase() + "';");
        try {
            if (rs.next()) {
                return true;
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return false;
    }

    @Override
    public void deleteMessage(String transkey, String language) {
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
    public String getPlayerLanguage(UUID playerUUID) {
        this.createPlayer(playerUUID);
        ResultSet rs = this.mySQL.getResult("SELECT language FROM choosenlang WHERE uuid='" + playerUUID.toString() + "';");
        try {
            if (rs.next()) {
                return rs.getString("language").toLowerCase();
            }
        } catch (SQLException throwables) {
            return this.getDefaultLanguage();
        }
        return this.getDefaultLanguage();
    }

    @Override
    public boolean isKey(String transkey, String lang) {
        ResultSet rs = this.mySQL.getResult("SELECT * FROM " + lang.toLowerCase() + " WHERE transkey='" + transkey.toLowerCase() + "';");
        try {
            if (rs.next()) {
                return true;
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return false;
    }

    @NotNull
    @Override
    public String getMessage(String transkey, UUID playerUUID, boolean usePrefix) {
        return usePrefix ? this.getPrefix(this.getPlayerLanguage(playerUUID)) + this.getMessage(transkey, playerUUID) : this.getMessage(transkey, playerUUID);
    }


    @NotNull
    @Override
    public String getMessage(String transkey, UUID playerUUID) {
        return this.getMessage(transkey, this.getPlayerLanguage(playerUUID));
    }

    @NotNull
    @Override
    public ArrayList<String> getMultipleMessages(String transkey) {
        return this.getMultipleMessages(transkey, this.getDefaultLanguage());
    }

    @NotNull
    @Override
    public ArrayList<String> getMultipleMessages(String transkey, UUID playerUUID) {
        return this.getMultipleMessages(transkey, this.getPlayerLanguage(playerUUID));
    }

    @NotNull
    @Override
    public ArrayList<String> getMultipleMessages(String transkey, String language) {
        ResultSet resultSet = this.mySQL.getResult("SELECT keys FROM MultipleTranslation WHERE transkey='" + transkey.toLowerCase() + "'");
        ArrayList<String> resolvedMessages = new ArrayList<>();
        String[] translationKeys = new String[]{};
        try {
            if (resultSet.next()) {
                translationKeys = resultSet.getString("keys").split(",");
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
            throw new IllegalArgumentException(transkey + " not found for language " + lang);
        }
        if (this.translationCache.getIfPresent(transkey) != null && Objects.requireNonNull(this.translationCache.getIfPresent(transkey)).containsKey(lang)) {
            return Objects.requireNonNull(this.translationCache.getIfPresent(transkey)).get(lang);
        }
        ResultSet rs = this.mySQL.getResult("SELECT translation FROM " + lang.toLowerCase() + " WHERE transkey='" + transkey.toLowerCase() + "';");
        try {
            if (rs.next()) {
                String translation;
                if (!Source.isBungeeCordMode) {
                    translation = ChatColor.translateAlternateColorCodes('&', rs.getString("translation"));
                } else {
                    translation = net.md_5.bungee.api.ChatColor.translateAlternateColorCodes('&', rs.getString("translation"));
                }

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
        ArrayList<String> langs = new ArrayList<>();
        ResultSet rs = this.mySQL.getResult("SELECT language FROM languages");
        try {
            while (rs.next()) {
                langs.add(rs.getString("language").toLowerCase());
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return langs;
    }

    @Override
    public ArrayList<String> getAllTranslationKeys(String language) {
        ArrayList<String> keys = new ArrayList<>();
        if (this.isLanguage(language)) {
            ResultSet rs = this.mySQL.getResult("SELECT transkey FROM " + language);
            try {
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
    public ArrayList<String> getAllTranslations(String language) {
        ArrayList<String> messages = new ArrayList<>();
        if (this.isLanguage(language)) {
            ResultSet rs = mySQL.getResult("SELECT translation FROM " + language);
            try {
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
    public String getDefaultLanguage() {
        return Source.getDefaultLanguage().toLowerCase();
    }

    @Override
    public String getPrefix() {
        return this.getMessage("languageapi-prefix", this.getDefaultLanguage());
    }

    @Override
    public String getPrefix(String language) {
        return this.getMessage("languageapi-prefix", language);
    }

}
