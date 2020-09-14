package de.tentact.languageapi;
/*  Created in the IntelliJ IDEA.
    Created by 0utplay | Aldin Sijamhodzic
    Datum: 25.04.2020
    Uhrzeit: 17:01
*/

import de.tentact.languageapi.api.LanguageAPIImpl;
import de.tentact.languageapi.command.LanguageCommand;
import de.tentact.languageapi.configuration.Configuration;
import de.tentact.languageapi.configuration.LanguageConfig;
import de.tentact.languageapi.configuration.MySQL;
import de.tentact.languageapi.listener.ChatListener;
import de.tentact.languageapi.listener.InventoryClickListener;
import de.tentact.languageapi.listener.JoinListener;
import de.tentact.languageapi.util.Updater;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;
import java.util.logging.Level;

public class LanguageSpigot extends JavaPlugin {

    private Updater updater;
    private MySQL mySQL;
    public Configuration configuration;

    @Override
    public void onEnable() {

        getLogger().log(Level.INFO, "Starting LanguageAPI");

        this.configuration = new Configuration(this.getLogger());
        LanguageConfig languageConfig = this.configuration.getLanguageConfig();

        this.mySQL = languageConfig.getMySQL();
        this.mySQL.connect();
        LanguageAPI.setInstance(new LanguageAPIImpl(languageConfig));
        this.mySQL.createDefaultTable();

        LanguageAPI.getInstance().createLanguage(languageConfig.getLanguageSetting().getDefaultLanguage());
        this.updater = new Updater(this);

        Objects.requireNonNull(this.getCommand("languageapi")).setExecutor(new LanguageCommand(this));
        Objects.requireNonNull(this.getCommand("languageapi")).setTabCompleter(new LanguageCommand(this));

        PluginManager pm = Bukkit.getPluginManager();
        pm.registerEvents(new JoinListener(this), this);
        pm.registerEvents(new ChatListener(), this);
        pm.registerEvents(new InventoryClickListener(this.configuration.getLanguageInventory()), this);
    }

    @Override
    public void onDisable() {
        mySQL.closeConnection();
    }

    public Updater getUpdater() {
        return this.updater;
    }

}
