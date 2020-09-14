package de.tentact.languageapi.configuration;
/*  Created in the IntelliJ IDEA.
    Copyright(c) 2020
    Created by 0utplay | Aldin Sijamhodzic
    Datum: 03.09.2020
    Uhrzeit: 08:56
*/

import com.github.derrop.documents.DefaultDocument;
import com.github.derrop.documents.Document;
import com.github.derrop.documents.Documents;
import de.tentact.languageapi.LanguageBungeecord;

import java.io.File;

public class Configuration {

    private Document document = new DefaultDocument();

    private final File configFile = new File("plugins/LanguageAPI", "config.json");

    public Configuration(LanguageBungeecord languageBungeecord) {
        if (configFile.exists()) {
            languageBungeecord.logInfo("Found config.json. Reading config.json...");
            document = Documents.jsonStorage().read(configFile);
        }else {
            configFile.getParentFile().mkdirs();
            languageBungeecord.logInfo("No config.json found...");
            languageBungeecord.logInfo("Creating new config.json");
            document.append("config", new LanguageConfig(
                    new MySQL(
                            "hostname",
                            "languageapi",
                            "languageapi",
                            "password",
                            3306
                    ),
                    new LanguageSetting(
                            "de_de",
                            5
                    )
            )).json().write(configFile);
        }
        this.getLanguageConfig().setLogger(languageBungeecord.getLogger());
        this.getLanguageConfig().getMySQL().setLogger(languageBungeecord.getLogger());
    }

    public LanguageConfig getLanguageConfig() {
        return document.get("config", LanguageConfig.class);
    }

    /**
     * Rewrites the current {@link Configuration}
     *
     * @param languageConfig the config to set
     */
    public void writeConfiguration(LanguageConfig languageConfig) {
        document = new DefaultDocument("config", languageConfig);
        document.json().write(configFile);
    }

}
