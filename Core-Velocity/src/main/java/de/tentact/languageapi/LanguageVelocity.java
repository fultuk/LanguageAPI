package de.tentact.languageapi;
/*  Created in the IntelliJ IDEA.
    Created by 0utplay | Aldin Sijamhodzic
    Datum: 25.04.2020
    Uhrzeit: 17:20
*/

import com.google.inject.Inject;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import de.tentact.languageapi.api.VelocityLanguageAPI;
import de.tentact.languageapi.configuration.Configuration;
import de.tentact.languageapi.configuration.LanguageConfig;
import de.tentact.languageapi.configuration.MySQL;
import de.tentact.languageapi.util.Updater;

import java.util.logging.Logger;

@Plugin(id = "languageapi", name = "LanguageAPI", version = "1.9", authors = {"0utplay"})
public class LanguageVelocity {

    private final Logger logger;
    private final ProxyServer proxyServer;

    @Inject
    public LanguageVelocity(ProxyServer proxyServer, Logger logger) {
        this.logger = logger;
        this.proxyServer = proxyServer;
        Configuration configuration = new Configuration(this.getLogger());
        LanguageConfig languageConfig = configuration.getLanguageConfig();

        MySQL mySQL = configuration.getLanguageConfig().getMySQL();
        mySQL.connect();
        LanguageAPI.setInstance(new VelocityLanguageAPI(proxyServer, languageConfig));

        mySQL.createDefaultTable();

        LanguageAPI.getInstance().createLanguage(languageConfig.getLanguageSetting().getDefaultLanguage());

        new Updater(this.proxyServer, this.logger);
    }

    public Logger getLogger() {
        return this.logger;
    }
}
