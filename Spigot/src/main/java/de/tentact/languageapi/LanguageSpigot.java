package de.tentact.languageapi;
/*  Created in the IntelliJ IDEA.
    Created by 0utplay | Aldin Sijamhodzic
    Datum: 25.04.2020
    Uhrzeit: 17:01
*/

import de.tentact.languageapi.api.SpigotLanguageAPI;
import de.tentact.languageapi.command.LanguageCommand;
import de.tentact.languageapi.configuration.LanguageConfig;
import de.tentact.languageapi.configuration.MySQL;
import de.tentact.languageapi.configuration.SpigotConfiguration;
import de.tentact.languageapi.listener.ChatListener;
import de.tentact.languageapi.listener.InventoryClickListener;
import de.tentact.languageapi.listener.JoinListener;
import de.tentact.languageapi.util.Updater;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;
import java.util.logging.Level;

public class LanguageSpigot extends JavaPlugin {

    private Updater updater;
    private MySQL mySQL;
    public SpigotConfiguration spigotConfiguration;

    @Override
    public void onEnable() {

        this.getLogger().log(Level.INFO, "Starting LanguageAPI");

        this.spigotConfiguration = new SpigotConfiguration(this.getLogger());
        LanguageConfig languageConfig = this.spigotConfiguration.getLanguageConfig();

        this.mySQL = languageConfig.getMySQL();
        this.mySQL.connect();
        LanguageAPI.setInstance(new SpigotLanguageAPI(languageConfig));
        this.mySQL.createDefaultTable();

        LanguageAPI.getInstance().createLanguage(languageConfig.getLanguageSetting().getDefaultLanguage());
        this.updater = new Updater(this);

        LanguageCommand languageCommand = new LanguageCommand(this);
        Objects.requireNonNull(this.getCommand("languageapi")).setExecutor(languageCommand);
        Objects.requireNonNull(this.getCommand("languageapi")).setTabCompleter(new LanguageCommand(this));

        new JoinListener(this);
        new ChatListener(this, languageCommand);
        new InventoryClickListener(this, this.spigotConfiguration.getLanguageInventory());
    }

    @Override
    public void onDisable() {
        mySQL.closeConnection();
    }

    public String getVersion() {
        return "1.9-SNAPSHOT-1157-1910";
    }

    public Updater getUpdater() {
        return this.updater;
    }
}
