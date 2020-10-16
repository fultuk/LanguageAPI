package de.tentact.languageapi.configuration;
/*  Created in the IntelliJ IDEA.
    Copyright(c) 2020
    Created by 0utplay | Aldin Sijamhodzic
    Datum: 03.09.2020
    Uhrzeit: 09:02
*/

public class SpigotLanguageSetting extends LanguageSetting {

    private final String defaultLanguage;
    private final int cachedTime;
    private final boolean isNotify;

    public SpigotLanguageSetting(String defaultLanguage, int cacheTime, boolean notify) {
        super(defaultLanguage, cacheTime);
        this.defaultLanguage = defaultLanguage;
        this.cachedTime = cacheTime;
        this.isNotify = notify;
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
