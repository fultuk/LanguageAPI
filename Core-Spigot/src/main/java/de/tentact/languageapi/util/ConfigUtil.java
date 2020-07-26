package de.tentact.languageapi.util;
/*  Created in the IntelliJ IDEA.
    Created by 0utplay | Aldin Sijamhodzic
    Datum: 25.04.2020
    Uhrzeit: 17:05
*/

import de.tentact.languageapi.LanguageAPI;
import de.tentact.languageapi.api.LanguageAPIImpl;
import de.tentact.languageapi.mysql.MySQL;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConfigUtil {

    private static YamlConfiguration mySQLConfiguration;


    private static MySQL mySQL;

    private static Logger logger;
    public static String defaultLanguage;

    public static void createSpigotMySQLConfig() {
        File mySQLConfigFile = new File("plugins/LanguageAPI", "config.yml");
        mySQLConfiguration = YamlConfiguration.loadConfiguration(mySQLConfigFile);

        mySQLConfiguration.addDefault("mysql.hostname", "hostname");
        mySQLConfiguration.addDefault("mysql.database", "de/tentact/languageapi");
        mySQLConfiguration.addDefault("mysql.username", "de/tentact/languageapi");

        mySQLConfiguration.addDefault("mysql.password", "password");
        mySQLConfiguration.addDefault("mysql.port", 3306);
        mySQLConfiguration.addDefault("languageapi.defaultlang", "de_de");
        mySQLConfiguration.addDefault("languageapi.notify", true);
        


        mySQLConfiguration.options().copyDefaults(true);

        try {
            mySQLConfiguration.save(mySQLConfigFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void init() {
        mySQL = new MySQL(mySQLConfiguration.getString("mysql.hostname"),
                mySQLConfiguration.getString("mysql.database"),
                mySQLConfiguration.getString("mysql.username"),
                mySQLConfiguration.getString("mysql.password"),
                mySQLConfiguration.getInt("mysql.port"));
        LanguageAPI.setInstance(new LanguageAPIImpl());
    }

    @NotNull
    public static String getDefaultLanguage() {
        if(defaultLanguage == null) {
            defaultLanguage = mySQLConfiguration.getString("language.defaultlang");
            if(defaultLanguage == null) {
                defaultLanguage = "en_en";
            }
        }
        return defaultLanguage;
    }

    public static boolean getUpdateNotfication() {
        return mySQLConfiguration.getBoolean("language.notify");
    }
    @NotNull
    public static MySQL getMySQL() {
        return mySQL;
    }
    public static void initLogger(Logger initLogger) {
        logger = initLogger;
    }
    public static void log(String message, Level logLevel) {
        logger.log(logLevel, message);
    }
}
