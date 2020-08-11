package de.tentact.languageapi.util;
/*  Created in the IntelliJ IDEA.
    Created by 0utplay | Aldin Sijamhodzic
    Datum: 25.04.2020
    Uhrzeit: 17:05
*/

import de.tentact.languageapi.mysql.MySQL;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConfigUtil {

    private static YamlConfiguration yamlConfiguration;
    private static MySQL mySQL;
    private static Logger logger;
    public static String defaultLanguage;

    public static void createSpigotMySQLConfig() {
        File mySQLConfigFile = new File("plugins/LanguageAPI", "config.yml");
        yamlConfiguration = YamlConfiguration.loadConfiguration(mySQLConfigFile);

        yamlConfiguration.addDefault("mysql.hostname", "hostname");
        yamlConfiguration.addDefault("mysql.database", "de/tentact/languageapi");
        yamlConfiguration.addDefault("mysql.username", "de/tentact/languageapi");

        yamlConfiguration.addDefault("mysql.password", "password");
        yamlConfiguration.addDefault("mysql.port", 3306);
        yamlConfiguration.addDefault("language.defaultlang", "de_de");
        yamlConfiguration.addDefault("language.notify", true);

        yamlConfiguration.options().copyDefaults(true);

        try {
            yamlConfiguration.save(mySQLConfigFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void init() {
        mySQL = new MySQL(yamlConfiguration.getString("mysql.hostname"),
                yamlConfiguration.getString("mysql.database"),
                yamlConfiguration.getString("mysql.username"),
                yamlConfiguration.getString("mysql.password"),
                yamlConfiguration.getInt("mysql.port"));
    }

    @NotNull
    public static String getDefaultLanguage() {
        if(defaultLanguage == null) {
            defaultLanguage = yamlConfiguration.getString("language.defaultlang");
            if(defaultLanguage == null) {
                defaultLanguage = "en_en";
            }
        }
        return defaultLanguage;
    }

    public static boolean getUpdateNotfication() {
        return yamlConfiguration.getBoolean("language.notify");
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
