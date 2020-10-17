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
    private final boolean isNotify;

    public LanguageSetting(String defaultLanguage, int cacheTime, boolean isNotify) {
        this.defaultLanguage = defaultLanguage;
        this.cachedTime = cacheTime;
        this.isNotify = isNotify;
    }

    public String getDefaultLanguage() {
        return this.defaultLanguage;
    }

    public int getCachedTime() {
        return this.cachedTime;
    }

    public boolean isNotify() {
        return this.isNotify;
    }
}
