package de.tentact.languageapi.api;
/*  Created in the IntelliJ IDEA.
    Created by 0utplay | Aldin Sijamhodzic
    Datum: 25.04.2020
    Uhrzeit: 16:52
*/

import de.tentact.languageapi.AbstractLanguageAPI;
import de.tentact.languageapi.mysql.MySQL;
import de.tentact.languageapi.util.Source;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class LanguageAPI extends AbstractLanguageAPI {

    private final MySQL mySQL = Source.getMySQL();


    public void createLanguage(final String langName) {
        if (!this.isLanguage(langName)) {
            this.mySQL.createTable(langName.replace(" ", "").toLowerCase());
            this.mySQL.update("INSERT INTO languages(language) VALUES ('" + langName.toLowerCase() + "')");
        }

    }

    public void deleteLanguage(String langName) {
        if (!this.getDefaultLanguage().equalsIgnoreCase(langName) && this.isLanguage(langName)) {
            this.mySQL.update("DROP TABLE " + langName.toLowerCase());
            this.mySQL.update("DELETE FROM languages WHERE language='" + langName.toLowerCase() + "'");
        }

    }

    public void setPlayerLanguage(UUID playerUUID, String newLang, boolean orElseDefault) {
        this.createPlayer(playerUUID);
        if (!this.isLanguage(newLang)) {
            this.setPlayerLanguage(playerUUID, this.getDefaultLanguage());
            return;
        }
        this.mySQL.update("UPDATE choosenlang WHERE uuid='" + playerUUID.toString() + "' SET language='" + newLang.toLowerCase() + "';");
    }

    public void setPlayerLanguage(UUID playerUUID, String newLang) {
        this.createPlayer(playerUUID);
        if (!this.isLanguage(newLang)) {
            throw new IllegalArgumentException("Language " + newLang + " was not found!");
        }
        this.mySQL.update("UPDATE choosenlang WHERE uuid='" + playerUUID.toString() + "' SET language='" + newLang.toLowerCase() + "';");
    }

    public void createPlayer(UUID playerUUID) {
        if (!this.isRegisteredPlayer(playerUUID)) {
            String language = getDefaultLanguage();
            if (!Source.isBungeeCordMode) {
                Player player = Bukkit.getPlayer(playerUUID);
                if (player != null) {
                    if (this.getAvailableLanguages().contains(player.getLocale().toLowerCase())) {
                        language = player.getLocale().toLowerCase();
                    }
                }

            }
            this.mySQL.update("INSERT INTO choosenlang(uuid, language) VALUES ('" + playerUUID.toString() + "', '" + language + "');");
        } else {
            if (!this.getAvailableLanguages().contains(this.getPlayerLanguage(playerUUID))) {
                this.mySQL.update("UPDATE choosenlang SET language='" + this.getDefaultLanguage() + "' WHERE uuid='" + playerUUID.toString() + "';");
            }
        }

    }

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

    public void addMessage(final String transkey, final String message, final String language, String param) {
        if (!this.isLanguage(language)) {
            throw new IllegalArgumentException("Language " + language + " was not found!");
        }
        this.addMessage(transkey, message, language);
        this.addParameter(transkey, param);
    }

    public void addMessage(final String transkey, final String message, final String lang) {
        if (this.isLanguage(lang)) {
            new Thread(() -> this.mySQL.update("INSERT INTO " + lang.toLowerCase() + "(transkey, translation) VALUES ('" + transkey.toLowerCase() + "', '" + ChatColor.translateAlternateColorCodes('&', message) + "');")).start();
        }
    }

    public void addParameter(final String transkey, final String param) {
        new Thread(() -> this.mySQL.update("INSERT INTO Parameter (transkey, param) VALUES ('" + transkey.toLowerCase() + "', '" + param + "');")).start();

    }

    public void deleteParameter(final String transkey, final String param) {
        if (!this.getParameter(transkey).contains(param)) {
            return;
        }
        new Thread(() -> this.mySQL.update("UPDATE Parameter SET param='" + getParameter(transkey).replace(param, "") + "' WHERE transkey='" + transkey + "';")).start();

    }

    public void deleteAllParameter(final String transkey) {
        if (!this.hasParameter(transkey)) {
            return;
        }
        new Thread(() -> this.mySQL.update("DELETE FROM Parameter WHERE transkey='" + transkey + "';")).start();

    }

    public void addMessage(final String transkey, final String lang) {
        if (!this.isLanguage(lang)) {
            return;
        }
        if (this.isKey(transkey, lang)) {
            return;
        }
        new Thread(() -> this.mySQL.update("INSERT INTO " + lang.toLowerCase() + "(transkey, translation) VALUES ('" + transkey.toLowerCase() + "', '" + transkey + "');")).start();

    }

    public void addMessage(final String transkey) {
        if (this.isKey(transkey, this.getDefaultLanguage().toLowerCase())) {
            return;
        }
        new Thread(() -> this.mySQL.update("INSERT INTO " + Source.getDefaultLanguage().toLowerCase() + "(transkey, translation) VALUES ('" + transkey.toLowerCase() + "', '" + transkey + "');")).start();

    }

    public void addMessageToDefault(final String transkey, final String translation) {
        if (this.isKey(transkey, this.getDefaultLanguage().toLowerCase())) {
            return;
        }
        new Thread(() -> this.mySQL.update("INSERT INTO " + Source.getDefaultLanguage().toLowerCase()
                + "(transkey, translation) VALUES ('" + transkey.toLowerCase() + "', '" + ChatColor.translateAlternateColorCodes('&', translation) + "');")).start();

    }

    public void addMessageToDefault(final String transkey, final String translation, final String param) {
        if (this.isKey(transkey, this.getDefaultLanguage().toLowerCase())) {
            return;
        }
        this.addMessageToDefault(transkey, translation);
        this.addParameter(transkey, param);

    }

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
        ArrayList<String> translationKeysAsArrayList = new ArrayList<>();
        for (String translationKey : translationKeys) {
            translationKeysAsArrayList.add(translationKey);
        }
        translationKeysAsArrayList.add(transkey);
        this.setMultipleTranslation(multipleTranslation, translationKeysAsArrayList, true);
    }

    public void copyLanguage(String langfrom, String langto) {
        if (!this.isLanguage(langfrom) || !this.isLanguage(langto)) {
            throw new IllegalArgumentException("Language " + langfrom + " or " + langto + " was not found!");
        }
        this.mySQL.update("INSERT INTO " + langto + " SELECT * FROM " + langfrom + ";");
    }

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

    @Nullable
    public String getParameter(String translationKey) {
        if (!this.hasParameter(translationKey)) {
            throw new IllegalArgumentException(translationKey + " has no parameter");
        }
        ResultSet rs = this.mySQL.getResult("SELECT param FROM Parameter WHERE transkey='" + translationKey + "';");
        try {
            if (rs.next()) {
                return rs.getString("param");
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;

    }

    public void deleteMessageInEveryLang(String transkey) {
        for (String langs : this.getAvailableLanguages()) {
            if (this.isKey(transkey, langs)) {
                this.deleteMessage(transkey, langs);
            }
        }
    }


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

    public void setMultipleTranslation(String multipleTranslation, List<String> translationKeys, boolean overwrite) {
        if(isMultipleTranslation(multipleTranslation) && overwrite) {
            this.removeMultipleTranslation(multipleTranslation);
        }
        StringBuilder stringBuilder = new StringBuilder();

        for (String translationKey : translationKeys) {
            stringBuilder.append(translationKey.toLowerCase()).append(",");
        }
        new Thread(() -> this.mySQL.update("INSERT INTO MultipleTranslation(transkey, keys) VALUES ('" + multipleTranslation.toLowerCase() + "','" + stringBuilder.toString() + "')")).start();
    }

    public void removeMultipleTranslation(final String multipleTranslation) {
        if(!isMultipleTranslation(multipleTranslation)) {
            //THROW
            return;
        }
        new Thread(() -> this.mySQL.update("DELETE FROM MultipleTranslation WHERE transkey='" + multipleTranslation + "'")).start();
    }

    public void removeSingleTranslationFromMultipleTranslation(final String multipleTranslation, final String transkey) {
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
        translationKeysAsArrayList.remove(transkey);
        this.setMultipleTranslation(multipleTranslation, translationKeysAsArrayList, true);
    }
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
    public String getMessage(String transkey, UUID playerUUID, boolean usePrefix) {
        return usePrefix ? this.getPrefix(this.getPlayerLanguage(playerUUID)) + this.getMessage(transkey, playerUUID) : this.getMessage(transkey, playerUUID);
    }

    @NotNull
    public String getMessage(String transkey, Player player, boolean usePrefix) {
        return usePrefix ? this.getPrefix(this.getPlayerLanguage(player.getUniqueId())) + this.getMessage(transkey, player.getUniqueId()) : this.getMessage(transkey, player.getUniqueId());
    }

    @NotNull
    public String getMessage(String transkey, UUID playerUUID) {
        return this.getMessage(transkey, this.getPlayerLanguage(playerUUID));
    }

    @NotNull
    public String getMessage(String transkey, Player player) {
        return this.getMessage(transkey, this.getPlayerLanguage(player.getUniqueId()));
    }

    @NotNull
    public ArrayList<String> getMultipleMessages(String transkey) {
        return this.getMultipleMessages(transkey, this.getDefaultLanguage());
    }

    @NotNull
    public ArrayList<String> getMultipleMessages(String transkey, UUID playerUUID) {
        return this.getMultipleMessages(transkey, this.getPlayerLanguage(playerUUID));
    }

    @NotNull
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
    public String getMessage(String transkey, String lang) {
        if (!this.isLanguage(lang)) {
            throw new IllegalArgumentException(lang + " was not found");
        }
        if (!this.isKey(transkey, lang)) {
            throw new IllegalArgumentException(transkey + " not found for language " + lang);
        }
        ResultSet rs = this.mySQL.getResult("SELECT translation FROM " + lang.toLowerCase() + " WHERE transkey='" + transkey.toLowerCase() + "';");
        try {
            if (rs.next()) {
                return ChatColor.translateAlternateColorCodes('&', rs.getString("translation"));
            }
        } catch (SQLException throwables) {
            return transkey;
        }
        return transkey;
    }

    public boolean isLanguage(String language) {
        if (this.getAvailableLanguages().isEmpty()) {
            throw new UnsupportedOperationException("There are no languages available");
        }
        return this.getAvailableLanguages().contains(language.toLowerCase());
    }

    @NotNull
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

    public String getDefaultLanguage() {
        return Source.getDefaultLanguage().toLowerCase();
    }

    public String getPrefix() {
        return this.getMessage("languageapi-prefix", this.getDefaultLanguage());
    }

    public String getPrefix(String language) {
        return this.getMessage("languageapi-prefix", language);

    }

}
