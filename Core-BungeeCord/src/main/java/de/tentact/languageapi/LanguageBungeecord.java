package de.tentact.languageapi;
/*  Created in the IntelliJ IDEA.
    Created by 0utplay | Aldin Sijamhodzic
    Datum: 25.04.2020
    Uhrzeit: 17:20
*/

import de.tentact.languageapi.api.LanguageAPIImpl;
import de.tentact.languageapi.configuration.Configuration;
import de.tentact.languageapi.configuration.LanguageConfig;
import de.tentact.languageapi.configuration.MySQL;
import de.tentact.languageapi.util.Updater;
import net.md_5.bungee.api.plugin.Plugin;

import java.util.logging.Level;

public class LanguageBungeecord extends Plugin {

    private MySQL mySQL;
    private Configuration configuration;
    private LanguageConfig languageConfig;

    @Override
    public void onEnable() {
        this.configuration = new Configuration(this);
        this.languageConfig = this.configuration.getLanguageConfig();
        mySQL = this.configuration.getLanguageConfig().getMySQL();
        mySQL.connect();
        LanguageAPI.setInstance(new LanguageAPIImpl(this.languageConfig));
        mySQL.createDefaultTable();
        LanguageAPI.getInstance().createLanguage(this.languageConfig.getLanguageSetting().getDefaultLanguage());
        new Updater(this);
    }

    @Override
    public void onDisable() {
        mySQL.closeConnection();
    }

    public void logInfo(String info) {
        this.getLogger().log(Level.INFO, info);
    }
}
