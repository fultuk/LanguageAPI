package de.tentact.languageapi.api;
/*  Created in the IntelliJ IDEA.
    Created by 0utplay | Aldin Sijamhodzic
    Datum: 25.04.2020
    Uhrzeit: 16:52
*/

import de.tentact.languageapi.mysql.MySQL;
import de.tentact.languageapi.util.Source;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.UUID;

public class LanguageAPI {

    private static LanguageAPI instance;

    public static MySQL mySQL;

    public ArrayList<String> languageCache = new ArrayList<>();

    public long lastupdatedCache = System.currentTimeMillis();

    private LanguageAPI() {

    }

    public static LanguageAPI getInstance() {
        if (LanguageAPI.instance == null) {
            LanguageAPI.instance = new LanguageAPI();
        }
        return LanguageAPI.instance;
    }

    public void createLanguage(final String langName) {
        if (!getAvailableLanguages().contains(langName.toLowerCase())) {
            mySQL.createTable(langName.replace(" ", "").toLowerCase());
            languageCache.add(langName.toLowerCase());
            new Thread(() -> mySQL.update("INSERT INTO languages(language) VALUES ('" + langName.toLowerCase() + "')")).start();


        }

    }

    public void deleteLanguage(String langName) {
        if (!LanguageAPI.getInstance().getDefaultLanguage().equalsIgnoreCase(langName) && getAvailableLanguages().contains(langName.toLowerCase())) {
            mySQL.update("DROP TABLE " + langName.toLowerCase());
            mySQL.update("DELETE FROM languages WHERE language='" + langName.toLowerCase() + "'");
        }

    }

    public void changePlayerLanguage(UUID playerUUID, String newLang) {
        createPlayer(playerUUID);
        mySQL.update("UPDATE choosenlang WHERE uuid='" + playerUUID.toString() + "' SET language='" + newLang.toLowerCase() + "';");
    }

    public void createPlayer(UUID playerUUID) {
        if (!playerExists(playerUUID)) {
            String language = getDefaultLanguage();
            if (!Source.bungeeCordMode) {
                Player player = Bukkit.getPlayer(playerUUID);
                if (getAvailableLanguages().contains(player.getLocale().toLowerCase())) {
                    language = player.getLocale().toLowerCase();
                }
            }
            mySQL.update("INSERT INTO choosenlang(uuid, language) VALUES ('" + playerUUID.toString() + "', '" + language + "');");
        } else {
            if (!getAvailableLanguages().contains(getPlayerLanguage(playerUUID))) {
                mySQL.update("UPDATE choosenlang SET language='" + getDefaultLanguage() + "' WHERE uuid='" + playerUUID.toString() + "';");
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
        if (getAvailableLanguages().contains(lang.toLowerCase())) {
            new Thread(() -> {
                mySQL.update("INSERT INTO " + lang.toLowerCase() + "(transkey, translation) VALUES ('" + transkey.toLowerCase() + "', '" + message + "');");
                addParameter(transkey, param);
            }).start();
        }
    }

    public void addMessage(final String transkey, final String message, final String lang) {
        if (getAvailableLanguages().contains(lang.toLowerCase())) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    mySQL.update("INSERT INTO " + lang.toLowerCase() + "(transkey, translation) VALUES ('" + transkey.toLowerCase() + "', '" + ChatColor.translateAlternateColorCodes('&', message) + "');");
                }
            }).start();
        }
    }

    public void addParameter(final String transkey, final String param) {
        new Thread(() -> mySQL.update("INSERT INTO Parameter (transkey, param) VALUES ('" + transkey.toLowerCase() + "', '" + param + "');")).start();

    }

    public void deleteParameter(final String transkey, final String param) {
        if (!getParameter(transkey).contains(param)) {
            return;
        }
        new Thread(() -> mySQL.update("UPDATE Parameter SET param='" + getParameter(transkey).replace(param, "") + "' WHERE transkey='" + transkey + "';")).start();

    }

    public void deleteAllParameter(final String transkey) {
        if (!hasParameter(transkey)) {
            return;
        }
        new Thread(() -> mySQL.update("DELETE FROM Parameter WHERE transkey='" + transkey + "';")).start();

    }

    public void addMessage(final String transkey, final String lang) {
        if (!getAvailableLanguages().contains(lang)) {
            return;
        }
        if (isKey(transkey, lang)) {
            return;
        }
        new Thread(() -> mySQL.update("INSERT INTO " + lang.toLowerCase() + "(transkey, translation) VALUES ('" + transkey.toLowerCase() + "', '" + transkey + "');")).start();

    }

    public void addMessage(final String transkey) {
        if (isKey(transkey, getDefaultLanguage().toLowerCase())) {
            return;
        }
        new Thread(() -> mySQL.update("INSERT INTO " + Source.getDefaultLanguage().toLowerCase() + "(transkey, translation) VALUES ('" + transkey.toLowerCase() + "', '" + transkey + "');")).start();

    }

    public void addMessageExtra(final String transkey, final String translation) {
        if (isKey(transkey, getDefaultLanguage().toLowerCase())) {
            return;
        }
        new Thread(() -> mySQL.update("INSERT INTO " + Source.getDefaultLanguage().toLowerCase()
                + "(transkey, translation) VALUES ('" + transkey.toLowerCase() + "', '" + ChatColor.translateAlternateColorCodes('&', translation) + "');")).start();

    }

    public void copyLanguage(String langfrom, String langto) {
        if (getAvailableLanguages().contains(langfrom) && getAvailableLanguages().contains(langto)) {
            mySQL.update("INSERT INTO " + langto + " SELECT * FROM " + langfrom + ";");

        }


    }

    public boolean hasParameter(String translationKey) {
        ResultSet rs = mySQL.getResult("SELECT param FROM Parameter WHERE transkey='" + translationKey + "';");
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
        if (!hasParameter(translationKey))
            return null;
        ResultSet rs = mySQL.getResult("SELECT param FROM Parameter WHERE transkey='" + translationKey + "';");
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
        for (String langs : getAvailableLanguages()) {
            if (isKey(transkey, langs)) {
                deleteMessage(transkey, langs);
            }
        }
    }


    public void updateMessage(String transkey, String lang, String message) {
        mySQL.update("UPDATE " + lang.toLowerCase() + " SET translation='" + ChatColor.translateAlternateColorCodes('&', message) + "' WHERE transkey='" + transkey.toLowerCase() + "';");
    }

    public void deleteMessage(String transkey, String lang) {
        mySQL.update("DELETE FROM " + lang.toLowerCase() + " WHERE transkey='" + transkey.toLowerCase() + "';");
    }

    public String getPlayerLanguage(UUID playerUUID) {
        createPlayer(playerUUID);
        ResultSet rs = mySQL.getResult("SELECT language FROM choosenlang WHERE uuid='" + playerUUID.toString() + "';");
        try {
            if (rs.next()) {
                return rs.getString("language").toLowerCase();
            }
        } catch (SQLException throwables) {
            return Source.getDefaultLanguage();
        }
        return Source.getDefaultLanguage();
    }

    public boolean isKey(String transkey, String lang) {
        ResultSet rs = mySQL.getResult("SELECT * FROM " + lang.toLowerCase() + " WHERE transkey='" + transkey.toLowerCase() + "';");
        try {
            if (rs.next()) {
                return true;
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return false;
    }

    public String getMessage(String transkey, UUID playerUUID, boolean autoCreate) {
        if(autoCreate) {
            addMessage(transkey);
        }
        return getMessage(transkey, getPlayerLanguage(playerUUID));
    }

    public String getMessage(String transkey, UUID playerUUID) {
        return getMessage(transkey, getPlayerLanguage(playerUUID));
    }

    public String getMessage(String transkey, String lang) {
        if (!getAvailableLanguages().contains(lang)) {
            return "Language not found";
        }
        if (!isKey(transkey, lang)) {
            return transkey + " not found for language " + lang;
        }
        ResultSet rs = mySQL.getResult("SELECT translation FROM " + lang.toLowerCase() + " WHERE transkey='" + transkey.toLowerCase() + "';");
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
        return getAvailableLanguages().contains(lang);
    }

    public ArrayList<String> getAvailableLanguages() {
        if (languageCache.isEmpty()) {

            return getLangUpdate();
        } else {
            if (lastupdatedCache + 5 * 60000 <= System.currentTimeMillis()) {

                return getLangUpdate();
            } else {

                return languageCache;
            }
        }

    }

    private ArrayList<String> getLangUpdate() {
        ArrayList<String> langs = new ArrayList<String>();
        ResultSet rs = mySQL.getResult("SELECT language FROM languages");
        try {
            while (rs.next()) {
                langs.add(rs.getString("language").toLowerCase());
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        languageCache = langs;
        lastupdatedCache = System.currentTimeMillis();
        return langs;
    }

    public ArrayList<String> getAllKeys(String lang) {
        ArrayList<String> keys = new ArrayList<>();
        if (getAvailableLanguages().contains(lang)) {
            ResultSet rs = mySQL.getResult("SELECT transkey FROM " + lang);
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
        if (getAvailableLanguages().contains(lang)) {
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
        return getMessage("languageapi-prefix", getDefaultLanguage());
    }
}
