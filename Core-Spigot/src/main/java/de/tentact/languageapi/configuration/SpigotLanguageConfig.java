package de.tentact.languageapi.configuration;
/*  Created in the IntelliJ IDEA.
    Copyright(c) 2020
    Created by 0utplay | Aldin Sijamhodzic
    Datum: 16.10.2020
    Uhrzeit: 13:02
*/

public class SpigotLanguageConfig extends LanguageConfig {

    private final SpigotLanguageSetting languageSetting;

    public SpigotLanguageConfig(MySQL mySQL, SpigotLanguageSetting languageSetting) {
        super(mySQL, languageSetting);
        this.languageSetting = languageSetting;
    }

    @Override
    public SpigotLanguageSetting getLanguageSetting() {
        return this.languageSetting;
    }

}
