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
import java.util.logging.Logger;

public class Configuration {

    Document settingsDocument = new DefaultDocument();
    File settingsFile = new File("plugins/LanguageAPI", "config.json");
    private LanguageConfig languageConfig;

    public Configuration(Logger logger) {
        if (settingsFile.exists()) {
            settingsDocument = Documents.jsonStorage().read(settingsFile);
        } else {
            try {
                Files.createFile(settingsFile.toPath());
                settingsDocument.append("config",
                        new LanguageConfig(
                                new MySQL(
                                        "hostname",
                                        "languagapi",
                                        "languagapi",
                                        "password",
                                        3306
                                ),
                                new LanguageSetting(
                                        "de_de",
                                        5,
                                        true
                                )
                        )
                ).json().write(settingsFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        this.getLanguageConfig().setLogger(logger);
        this.getLanguageConfig().getMySQL().setLogger(logger);
    }

    public LanguageConfig getLanguageConfig() {
        if (this.languageConfig != null) {
            return this.languageConfig;
        }
        this.languageConfig = this.settingsDocument.get("config", LanguageConfig.class);
        return this.languageConfig;
    }

}

