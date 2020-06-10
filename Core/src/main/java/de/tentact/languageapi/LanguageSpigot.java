package de.tentact.languageapi;
/*  Created in the IntelliJ IDEA.
    Created by 0utplay | Aldin Sijamhodzic
    Datum: 25.04.2020
    Uhrzeit: 17:01
*/

import de.tentact.languageapi.mysql.MySQL;
import de.tentact.languageapi.spigot.command.LanguageCommand;
import de.tentact.languageapi.spigot.listener.ChatListener;
import de.tentact.languageapi.spigot.listener.JoinListener;
import de.tentact.languageapi.util.DefaultMessages;
import de.tentact.languageapi.util.Source;
import de.tentact.languageapi.util.Updater;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public class LanguageSpigot extends JavaPlugin {

    private Updater updater;
    private MySQL mySQL;

    @Override
    public void onEnable() {

        Source.bungeeCordMode = false;
        Source.createSpigotMySQLConfig();
        Source.initSpigot();

        this.mySQL = Source.getMySQL();
        this.mySQL.connect();
        this.mySQL.createDefaultTable();

        ILanguageAPI.getInstance().createLanguage(Source.getDefaultLanguage());
        DefaultMessages.createDefaultPluginMessages();
        this.updater = new Updater(this);

        Objects.requireNonNull(this.getCommand("languageapi")).setExecutor(new LanguageCommand());
        Objects.requireNonNull(this.getCommand("languageapi")).setTabCompleter(new LanguageCommand());

        PluginManager pm = Bukkit.getPluginManager();
        pm.registerEvents(new JoinListener(), this);
        pm.registerEvents(new ChatListener(), this);

    }

    @Override
    public void onDisable() {
        mySQL.close();
    }

    public Updater getUpdater() {
        return this.updater;
    }
}
