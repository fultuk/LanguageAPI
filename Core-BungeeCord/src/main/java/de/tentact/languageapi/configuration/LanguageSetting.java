package de.tentact.languageapi.configuration;
/*  Created in the IntelliJ IDEA.
    Copyright(c) 2020
    Created by 0utplay | Aldin Sijamhodzic
    Datum: 03.09.2020
    Uhrzeit: 09:02
*/

public class LanguageSetting {

    private final String defaultLanguage;
    private final int cachedTime;

    public LanguageSetting(String defaultLanguage, int cacheTime) {
        this.defaultLanguage = defaultLanguage;
        this.cachedTime = cacheTime;
    }

    public String getDefaultLanguage() {
        return this.defaultLanguage;
    }

    public int getCachedTime() {
        return this.cachedTime;
    }
}
