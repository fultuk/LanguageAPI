package de.tentact.languageapi.configuration;
/*  Created in the IntelliJ IDEA.
    Copyright(c) 2020
    Created by 0utplay | Aldin Sijamhodzic
    Datum: 07.08.2020
    Uhrzeit: 11:20
*/

import com.github.derrop.documents.DefaultDocument;
import com.github.derrop.documents.Document;
import com.github.derrop.documents.Documents;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Configuration {

    Document settingsDocument = new DefaultDocument();
    File settingsFile = new File("plugins/LanguageAPI", "config.json");
    private LanguageConfig languageConfig;
    private final Logger logger;

    public Configuration(Logger logger) {
        this.logger = logger;
        if (settingsFile.exists()) {
            this.logInfo("Found config.json. Reading config.json...");
            this.settingsDocument = Documents.jsonStorage().read(settingsFile);
            if(!this.settingsDocument.contains("config")) {
                this.logInfo("Config.json has no entry... Resetting to default...");
                this.writeDefaultConfiguration();
            }
        } else {
            this.logInfo("No config.json found...");
            this.logInfo("Creating new config.json");
            try {
                Files.createDirectories(this.settingsFile.getParentFile().toPath());
                Files.createFile(this.settingsFile.toPath());
            } catch (IOException e) {
                this.logger.log(Level.WARNING, "While creating directories used by the plugin an exception occurred:");
                e.printStackTrace();
            }
            this.writeDefaultConfiguration();
        }
        this.getLanguageConfig().setLogger(logger);
        this.getLanguageConfig().getMySQL().setLogger(logger);
    }

    public LanguageConfig getDefaultServerConfig() {
        return new LanguageConfig(
                new MySQL(
                        "hostname",
                        "languagapi",
                        "languagapi",
                        "password",
                        3306
                ),
                new LanguageSetting(
                        "de_de",
                        5
                )
        );
    }

    public LanguageConfig getLanguageConfig() {
        if (this.languageConfig != null) {
            return this.languageConfig;
        }
        this.languageConfig = this.settingsDocument.get("config", LanguageConfig.class);
        return this.languageConfig;
    }

    private void logInfo(String info) {
        this.logger.log(Level.INFO, info);
    }

    /**
     * Rewrites the current {@link Configuration}
     *
     * @param languageConfig the config to set
     */
    public void writeConfiguration(LanguageConfig languageConfig) {
        this.settingsDocument = new DefaultDocument("config", languageConfig);
        this.settingsDocument.json().write(settingsFile);
    }

    private void writeDefaultConfiguration() {
        this.writeConfiguration(this.getDefaultServerConfig());
    }

}

