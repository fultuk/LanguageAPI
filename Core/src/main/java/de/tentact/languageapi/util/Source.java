package de.tentact.languageapi.util;
/*  Created in the IntelliJ IDEA.
    Created by 0utplay | Aldin Sijamhodzic
    Datum: 25.04.2020
    Uhrzeit: 17:05
*/

import de.tentact.languageapi.AbstractLanguageAPI;
import de.tentact.languageapi.LanguageBungeecord;
import de.tentact.languageapi.api.LanguageAPI;
import de.tentact.languageapi.mysql.MySQL;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;

public class Source {

    static File mySQLConfigFile;
    static YamlConfiguration mySQLConfiguration;

    public static boolean isBungeeCordMode;

    static File bungeecordmySQLConfigFile;
    static Configuration bungeecordmySQLConfiguration;

    private static MySQL mySQL;

    public static void createSpigotMySQLConfig() {
        mySQLConfigFile = new File("plugins/LanguageAPI", "config.yml");
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

    public static void initSpigot() {
        mySQL = new MySQL(mySQLConfiguration.getString("mysql.hostname"),
                mySQLConfiguration.getString("mysql.database"),
                mySQLConfiguration.getString("mysql.username"),
                mySQLConfiguration.getString("mysql.password"),
                mySQLConfiguration.getInt("mysql.port"));
        AbstractLanguageAPI.setInstance(new LanguageAPI());
    }

    public static void initBungeecord() {
        mySQL = new MySQL(bungeecordmySQLConfiguration.getString("mysql.hostname"),
                bungeecordmySQLConfiguration.getString("mysql.database"),
                bungeecordmySQLConfiguration.getString("mysql.username"),
                bungeecordmySQLConfiguration.getString("mysql.password"),
                bungeecordmySQLConfiguration.getInt("mysql.port"));
        AbstractLanguageAPI.setInstance(new LanguageAPI());

    }

    public static void createBungeeCordMySQLConfig(LanguageBungeecord languageBungeecord) {

        bungeecordmySQLConfigFile = new File("plugins/LanguageAPI", "config.yml");
        if (!languageBungeecord.getDataFolder().exists()) {
            languageBungeecord.getDataFolder().mkdir();
        }

        try {
            bungeecordmySQLConfiguration = ConfigurationProvider.getProvider(net.md_5.bungee.config.YamlConfiguration.class).load(bungeecordmySQLConfigFile);

            if (!bungeecordmySQLConfigFile.exists()) {
                bungeecordmySQLConfigFile.createNewFile();
                bungeecordmySQLConfiguration.set("mysql.host", "hostname");
                bungeecordmySQLConfiguration.set("mysql.username", "de/tentact/languageapi");
                bungeecordmySQLConfiguration.set("mysql.database", "de/tentact/languageapi");
                bungeecordmySQLConfiguration.set("mysql.password", "password");
                bungeecordmySQLConfiguration.set("languageapi.defaultlang", "en_EN");
            }
            ConfigurationProvider.getProvider(net.md_5.bungee.config.YamlConfiguration.class).save(bungeecordmySQLConfiguration, bungeecordmySQLConfigFile);

        } catch (IOException ignored) {

        }
    }
    @Nullable
    public static String getDefaultLanguage() {
        return isBungeeCordMode ? bungeecordmySQLConfiguration.getString("languageapi.defaultlang") : mySQLConfiguration.getString("languageapi.defaultlang");
    }

    public static boolean getUpdateNotfication() {
        return isBungeeCordMode ? bungeecordmySQLConfiguration.getBoolean("languageapi.notify") : mySQLConfiguration.getBoolean("languageapi.notify");
    }
    @NotNull
    public static MySQL getMySQL() {
        return mySQL;
    }
}
