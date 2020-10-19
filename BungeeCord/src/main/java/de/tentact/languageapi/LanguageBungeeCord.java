package de.tentact.languageapi;
/*  Created in the IntelliJ IDEA.
    Created by 0utplay | Aldin Sijamhodzic
    Datum: 25.04.2020
    Uhrzeit: 17:20
*/

import de.tentact.languageapi.api.BungeeCordLanguageAPI;
import de.tentact.languageapi.configuration.Configuration;
import de.tentact.languageapi.configuration.LanguageConfig;
import de.tentact.languageapi.configuration.MySQL;
import de.tentact.languageapi.util.Updater;
import net.md_5.bungee.api.plugin.Plugin;

public class LanguageBungeeCord extends Plugin {

    private MySQL mySQL;

    @Override
    public void onEnable() {
        Configuration configuration = new Configuration(this.getLogger());
        LanguageConfig languageConfig = configuration.getLanguageConfig();
        mySQL = configuration.getLanguageConfig().getMySQL();
        mySQL.connect();
        LanguageAPI.setInstance(new BungeeCordLanguageAPI(languageConfig));
        mySQL.createDefaultTable();
        LanguageAPI.getInstance().createLanguage(languageConfig.getLanguageSetting().getDefaultLanguage());
        new Updater(this);
    }

    @Override
    public void onDisable() {
        mySQL.closeConnection();
    }
}
