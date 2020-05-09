package de.tentact.languageapi.util;
/*  Created in the IntelliJ IDEA.
    Created by 0utplay | Aldin Sijamhodzic
    Datum: 25.04.2020
    Uhrzeit: 17:05
*/

import de.tentact.languageapi.LanguageBungeecord;
import de.tentact.languageapi.api.LanguageAPI;
import de.tentact.languageapi.mysql.MySQL;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class Source {

    static File mySQLFile;
    static YamlConfiguration mySQLcfg;

    public static boolean bungeeCordMode;

    static File bungeeMySQL;
    static Configuration bungeecfg;

    public static void createSpigotMySQLConfig() {
        mySQLFile = new File("plugins/LanguageAPI", "config.yml");
        mySQLcfg = YamlConfiguration.loadConfiguration(mySQLFile);



        mySQLcfg.addDefault("mysql.hostname", "hostname");
        mySQLcfg.addDefault("mysql.database", "languageapi");
        mySQLcfg.addDefault("mysql.username", "languageapi");
        mySQLcfg.addDefault("mysql.password", "password");
        mySQLcfg.addDefault("mysql.port", 3306);
        mySQLcfg.addDefault("languageapi.defaultlang", "de_de");
        mySQLcfg.addDefault("languageapi.notify", true);

        mySQLcfg.options().copyDefaults(true);

        try {
            mySQLcfg.save(mySQLFile);

        } catch (IOException e) {
            e.printStackTrace();


        }
        LanguageAPI.mySQL = new MySQL(mySQLcfg.getString("mysql.hostname"),
                mySQLcfg.getString("mysql.database"),
                mySQLcfg.getString("mysql.username"),
                mySQLcfg.getString("mysql.password"),
                mySQLcfg.getInt("mysql.port"));
    }

    public static void createBungeeCordMySQLConfig() {

        bungeeMySQL = new File("plugins/LanguageAPI", "config.yml");
        if (!LanguageBungeecord.getLanguageBungeecord().getDataFolder().exists()) {
            LanguageBungeecord.getLanguageBungeecord().getDataFolder().mkdir();
        }

        try {
            bungeecfg = ConfigurationProvider.getProvider(net.md_5.bungee.config.YamlConfiguration.class).load(bungeeMySQL);
            if (!bungeeMySQL.exists()) {
                bungeeMySQL.createNewFile();
                bungeecfg.set("mysql.host", "hostname");
                bungeecfg.set("mysql.username", "languageapi");
                bungeecfg.set("mysql.database", "languageapi");
                bungeecfg.set("mysql.password", "password");
                bungeecfg.set("languageapi.defaultlang", "en_EN");
            }
            ConfigurationProvider.getProvider(net.md_5.bungee.config.YamlConfiguration.class).save(bungeecfg, bungeeMySQL);
        } catch (IOException e) {

        }
        LanguageAPI.mySQL = new MySQL(bungeecfg.getString("mysql.hostname"),
                bungeecfg.getString("mysql.database"),
                bungeecfg.getString("mysql.username"),
                bungeecfg.getString("mysql.password"),
                bungeecfg.getInt("mysql.port"));



    }

    public static String getDefaultLanguage() {
        return bungeeCordMode ? bungeecfg.getString("languageapi.defaultlang") : mySQLcfg.getString("languageapi.defaultlang");
    }
    public static boolean getUpdateNotfication() {
        return bungeeCordMode ? bungeecfg.getBoolean("languageapi.notify") : mySQLcfg.getBoolean("languageapi.notify");
    }

}
