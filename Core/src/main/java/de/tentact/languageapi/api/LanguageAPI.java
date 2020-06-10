package de.tentact.languageapi.api;
/*  Created in the IntelliJ IDEA.
    Created by 0utplay | Aldin Sijamhodzic
    Datum: 25.04.2020
    Uhrzeit: 16:52
*/

import de.tentact.languageapi.ILanguageAPI;
import de.tentact.languageapi.mysql.MySQL;
import de.tentact.languageapi.util.Source;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.UUID;

public class LanguageAPI extends ILanguageAPI {

    private final MySQL mySQL = Source.getMySQL();

    public ArrayList<String> languageCache = new ArrayList<>();

    public long lastupdatedCache = System.currentTimeMillis();

    public LanguageAPI() {

    }

    public void createLanguage(final String langName) {
        if (!isLanguage(langName)) {
            this.mySQL.createTable(langName.replace(" ", "").toLowerCase());
            this.languageCache.add(langName.toLowerCase());
            this.mySQL.update("INSERT INTO languages(language) VALUES ('" + langName.toLowerCase() + "')");
        }

    }

    public void deleteLanguage(String langName) {
        if (!getDefaultLanguage().equalsIgnoreCase(langName) && isLanguage(langName)) {
            this.mySQL.update("DROP TABLE " + langName.toLowerCase());
            this.mySQL.update("DELETE FROM languages WHERE language='" + langName.toLowerCase() + "'");
        }

    }

    public void changePlayerLanguage(UUID playerUUID, String newLang) {
        this.createPlayer(playerUUID);
        if (!this.isLanguage(newLang)) {
            return;
        }
        this.mySQL.update("UPDATE choosenlang WHERE uuid='" + playerUUID.toString() + "' SET language='" + newLang.toLowerCase() + "';");
    }

    public void createPlayer(UUID playerUUID) {
        if (!this.playerExists(playerUUID)) {
            String language = getDefaultLanguage();
            if (!Source.bungeeCordMode) {
                Player player = Bukkit.getPlayer(playerUUID);
                if(player != null) {
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

    public boolean playerExists(UUID playerUUID) {
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

    public void addMessage(final String transkey, final String message, final String lang, String param) {
        if (this.isLanguage(lang)) {
            new Thread(() -> {
                this.mySQL.update("INSERT INTO " + lang.toLowerCase() + "(transkey, translation) VALUES ('" + transkey.toLowerCase() + "', '" + message + "');");
                this.addParameter(transkey, param);
            }).start();
        }
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

    public void copyLanguage(String langfrom, String langto) {
        if (this.isLanguage(langfrom) && this.isLanguage(langto)) {
            this.mySQL.update("INSERT INTO " + langto + " SELECT * FROM " + langfrom + ";");
        }
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

    public String getParameter(String translationKey) {
        if (!this.hasParameter(translationKey)) {
            throw new IllegalArgumentException(translationKey + " was not found");
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


    public void updateMessage(String transkey, String lang, String message) {
        new Thread(() -> this.mySQL.update("UPDATE " + lang.toLowerCase() + " SET translation='" + ChatColor.translateAlternateColorCodes('&', message) + "' WHERE transkey='" + transkey.toLowerCase() + "';")).start();

    }

    public void deleteMessage(String transkey, String lang) {
        new Thread(() -> this.mySQL.update("DELETE FROM " + lang.toLowerCase() + " WHERE transkey='" + transkey.toLowerCase() + "';")).start();
    }

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
    public String getMessage(String transkey, UUID playerUUID, boolean usePrefix) {
        return usePrefix ? this.getPrefix(this.getPlayerLanguage(playerUUID))+this.getMessage(transkey, playerUUID) : this.getMessage(transkey, playerUUID);
    }

    public String getMessage(String transkey, UUID playerUUID) {
        return this.getMessage(transkey, this.getPlayerLanguage(playerUUID));
    }

    public String getMessage(String transkey, String lang) {
        if (!this.isLanguage(lang)) {
            return "Language not found";
        }
        if (!this.isKey(transkey, lang)) {
            return transkey + " not found for language " + lang;
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

    public boolean isLanguage(String lang) {
        return this.getAvailableLanguages() != null && this.getAvailableLanguages().contains(lang.toLowerCase());
    }

    public ArrayList<String> getAvailableLanguages() {
        if (this.languageCache.isEmpty()) {
            return this.getLangUpdate();
        } else {
            if (this.lastupdatedCache + 5 * 60000 <= System.currentTimeMillis()) {
                return this.getLangUpdate();
            } else {
                return this.languageCache;
            }
        }
    }

    public ArrayList<String> getLangUpdate() {
        ArrayList<String> langs = new ArrayList<>();
        ResultSet rs = this.mySQL.getResult("SELECT language FROM languages");
        try {
            while (rs.next()) {
                langs.add(rs.getString("language").toLowerCase());
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        this.languageCache = langs;
        this.lastupdatedCache = System.currentTimeMillis();
        return langs;
    }

    public ArrayList<String> getAllKeys(String lang) {
        ArrayList<String> keys = new ArrayList<>();
        if (this.isLanguage(lang)) {
            ResultSet rs = this.mySQL.getResult("SELECT transkey FROM " + lang);
            try {
                while (rs.next()) {
                    keys.add(rs.getString("transkey"));
                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            return keys;
        }
        keys.add("Language not found");
        return keys;
    }

    public ArrayList<String> getAllMessages(String lang) {
        ArrayList<String> messages = new ArrayList<>();
        if (this.isLanguage(lang)) {
            ResultSet rs = mySQL.getResult("SELECT translation FROM " + lang);
            try {
                while (rs.next()) {
                    messages.add(rs.getString("translation"));
                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            return messages;
        }
        messages.add("Language not found");
        return messages;
    }

    public String getDefaultLanguage() {
        return Source.getDefaultLanguage().toLowerCase();
    }

    public String getPrefix() {
        return this.getMessage("languageapi-prefix", this.getDefaultLanguage());
    }
    public String getPrefix(String langName) {
        return this.getMessage("languageapi-prefix", langName);
    }
}
