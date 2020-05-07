package de.tentact.languageapi;
/*  Created in the IntelliJ IDEA.
    Created by 0utplay | Aldin Sijamhodzic
    Datum: 25.04.2020
    Uhrzeit: 17:01
*/

import de.tentact.languageapi.api.LanguageAPI;
import de.tentact.languageapi.spigot.command.LanguageCommand;
import de.tentact.languageapi.spigot.listener.ChatListener;
import de.tentact.languageapi.spigot.listener.JoinListener;
import de.tentact.languageapi.util.DefaultMessages;
import de.tentact.languageapi.util.Source;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class LanguageSpigot extends JavaPlugin {

    static LanguageSpigot languageSpigot;

    @Override
    public void onEnable() {
        languageSpigot = this;
        Source.bungeeCordMode = false;
        Source.createSpigotMySQLConfig();
        LanguageAPI.mySQL.connect();
        LanguageAPI.mySQL.createDefaultTable();
        LanguageAPI.getInstance().createLanguage(Source.getDefaultLanguage());
        DefaultMessages.createDefaultPluginMessages();


        this.getCommand("languageapi").setExecutor(new LanguageCommand());
        this.getCommand("languageapi").setTabCompleter(new LanguageCommand());

        PluginManager pm = Bukkit.getPluginManager();
        pm.registerEvents(new JoinListener(), this);
        pm.registerEvents(new ChatListener(), this);
    }

    public static LanguageSpigot getLanguageSpigot() {
        return languageSpigot;
    }
}
