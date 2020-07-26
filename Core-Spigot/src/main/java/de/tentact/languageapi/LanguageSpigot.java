package de.tentact.languageapi;
/*  Created in the IntelliJ IDEA.
    Created by 0utplay | Aldin Sijamhodzic
    Datum: 25.04.2020
    Uhrzeit: 17:01
*/

import de.tentact.languageapi.command.LanguageCommand;
import de.tentact.languageapi.listener.ChatListener;
import de.tentact.languageapi.listener.JoinListener;
import de.tentact.languageapi.mysql.MySQL;
import de.tentact.languageapi.util.ConfigUtil;
import de.tentact.languageapi.util.I18N;
import de.tentact.languageapi.util.Updater;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;
import java.util.logging.Level;

public class LanguageSpigot extends JavaPlugin {

    private Updater updater;
    private MySQL mySQL;

    @Override
    public void onEnable() {
        getLogger().log(Level.INFO, "Starting LanguageAPI");
        ConfigUtil.createSpigotMySQLConfig();
        ConfigUtil.init();
        ConfigUtil.initLogger(this.getLogger());

        this.mySQL = ConfigUtil.getMySQL();
        this.mySQL.connect();
        this.mySQL.createDefaultTable();

        AbstractLanguageAPI.getInstance().createLanguage(ConfigUtil.getDefaultLanguage());
        I18N.createDefaultPluginMessages();
        this.updater = new Updater(this);

        Objects.requireNonNull(this.getCommand("languageapi")).setExecutor(new LanguageCommand(this));
        Objects.requireNonNull(this.getCommand("languageapi")).setTabCompleter(new LanguageCommand(this));

        PluginManager pm = Bukkit.getPluginManager();
        pm.registerEvents(new JoinListener(), this);
        pm.registerEvents(new ChatListener(), this);

    }

    @Override
    public void onDisable() {
        mySQL.closeConnection();
    }

    public Updater getUpdater() {
        return this.updater;
    }
}
