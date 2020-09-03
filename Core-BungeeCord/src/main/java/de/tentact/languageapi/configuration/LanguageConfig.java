package de.tentact.languageapi.configuration;
/*  Created in the IntelliJ IDEA.
    Copyright(c) 2020
    Created by 0utplay | Aldin Sijamhodzic
    Datum: 03.09.2020
    Uhrzeit: 08:57
*/

import java.util.logging.Logger;

public class LanguageConfig {

    private final MySQL mySQL;
    private final LanguageSetting languageSetting;
    private final Logger logger;

    public LanguageConfig(MySQL mySQL, LanguageSetting languageSetting, Logger logger) {
        this.mySQL = mySQL;
        this.languageSetting = languageSetting;
        this.logger = logger;
    }

    public MySQL getMySQL() {
        return this.mySQL;
    }

    public LanguageSetting getLanguageSetting() {
        return this.languageSetting;
    }

    public Logger getLogger() {
        return this.logger;
    }
}
