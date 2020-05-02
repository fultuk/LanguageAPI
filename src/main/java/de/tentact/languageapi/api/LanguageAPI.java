package de.tentact.languageapi.api;
/*  Created in the IntelliJ IDEA.
    Created by 0utplay | Aldin Sijamhodzic
    Datum: 25.04.2020
    Uhrzeit: 16:52
*/

import de.tentact.languageapi.mysql.MySQL;
import de.tentact.languageapi.util.Source;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
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
            new Thread(new Runnable() {
                @Override
                public void run() {
                    mySQL.update("INSERT INTO languages(language) VALUES ('" + langName.toLowerCase() + "')");
                }
            }).start();


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
            if(!Source.bungeeCordMode) {
                Player player = Bukkit.getPlayer(playerUUID);
                if(getAvailableLanguages().contains(player.getLocale().toLowerCase())) {
                    language = player.getLocale().toLowerCase();
                }
            }
            mySQL.update("INSERT INTO choosenlang(uuid, language) VALUES ('" + playerUUID.toString() + "', '" + language + "');");
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

    public void addMessage(final String transkey, final String message, final String lang) {
        if (getAvailableLanguages().contains(lang.toLowerCase())) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    mySQL.update("INSERT INTO " + lang.toLowerCase() + "(transkey, translation) VALUES ('" + transkey.toLowerCase() + "', '" + message + "');");
                }
            }).start();
        }


    }


    public void addMessage(final String transkey, final String lang) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                mySQL.update("INSERT INTO " + lang.toLowerCase() + "(transkey, translation) VALUES ('" + transkey.toLowerCase() + "', '" + transkey + "');");
            }
        }).start();

    }

    public void addMessage(final String transkey) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                mySQL.update("INSERT INTO " + Source.getDefaultLanguage().toLowerCase() + "(transkey, translation) VALUES ('" + transkey.toLowerCase() + "', '" + transkey + "');");
            }
        }).start();

    }
    public void copyLanguage(String langfrom, String langto) {
        if(getAvailableLanguages().contains(langfrom) && getAvailableLanguages().contains(langto)) {
            mySQL.update("INSERT INTO "+langto+" SELECT * FROM "+langfrom+";");
            Bukkit.broadcastMessage("MYSQL DOING");
        }
        Bukkit.broadcastMessage("MYSQL FAILED");
    }

    public void updateMessage(String transkey, String lang, String message) {
        mySQL.update("UPDATE " + lang.toLowerCase() + " SET translation='" + message.replace('§', '&') + "' WHERE transkey='" + transkey.toLowerCase() + "';");
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

    public String getMessage(String transkey, UUID playerUUID) {
        return getMessage(transkey, getPlayerLanguage(playerUUID));
    }

    public String getMessage(String transkey, String lang) {
        if(!getAvailableLanguages().contains(lang)) {
            return "Language not found";
        }
        if(!isKey(transkey, lang)) {
            return transkey+" not found for language "+lang;
        }
        ResultSet rs = mySQL.getResult("SELECT translation FROM " + lang.toLowerCase() + " WHERE transkey='" + transkey.toLowerCase() + "';");
        try {
            if (rs.next()) {
                return rs.getString("translation").replace('&', '§');
            }
        } catch (SQLException throwables) {
            return transkey;
        }
        return transkey;
    }

    public ArrayList<String> getAvailableLanguages() {
        if (languageCache.isEmpty()) {
            Bukkit.broadcastMessage("CACHE EMPTY");
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
        } else {
            if (lastupdatedCache + 5 * 60000 <= System.currentTimeMillis()) {
                Bukkit.broadcastMessage("CACHE TO OLD");
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
            } else {
                Bukkit.broadcastMessage("CACHE OKAY");
                return languageCache;
            }
        }

    }

    public String getDefaultLanguage() {
        return Source.getDefaultLanguage().toLowerCase();
    }
}
