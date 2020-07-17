package de.tentact.languageapi.i18n;
/*  Created in the IntelliJ IDEA.
    Copyright(c) 2020
    Created by 0utplay | Aldin Sijamhodzic
    Datum: 07.07.2020
    Uhrzeit: 15:47
*/

import de.tentact.languageapi.AbstractLanguageAPI;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.UUID;

/**
 *
 */

public class Translation {

    private final String translationkey;
    private final AbstractLanguageAPI abstractLanguageAPI = AbstractLanguageAPI.getInstance();
    private boolean usePrefix = false;
    private final HashMap<String, String> params = new HashMap<>();
    private String message;

    /**
     * @param translationkey
     */
    public Translation(@NotNull String translationkey) {
        this.translationkey = translationkey;
    }
    public Translation(String translationkey, boolean usePrefix) {
        this(translationkey);
        this.setPrefix(usePrefix);
    }

    /**
     * @return returns a translation of the key in the default language
     */
    @NotNull
    public String getMessage() {
        return this.getMessage(this.abstractLanguageAPI.getDefaultLanguage());
    }

    /**
     * @param playerUUID the player's uniqueid to fetch the language from
     * @return returns a translation of the key in the language fetched by {@link UUID}
     */
    @NotNull
    public String getMessage(@NotNull UUID playerUUID) {
        message = this.abstractLanguageAPI.getMessage(this.translationkey, playerUUID, usePrefix);
        params.forEach((key, value) -> message = message.replace(key, value));
        params.clear();
        return message;
    }

    /**
     * @param language the language to get the translation in
     * @return returns a translation of the key in the given language
     */
    @NotNull
    public String getMessage(@NotNull String language) {
        message = this.abstractLanguageAPI.getMessage(this.translationkey, language, usePrefix);
        params.forEach((key, value) -> message = message.replace(key, value));
        params.clear();
        return message;
    }

    /**
     * @return returns the parameter for a key
     */

    public String getParameter() {
        return this.abstractLanguageAPI.getParameter(this.translationkey);
    }

    /**
     * @param usePrefix
     * @return returns a {@link Translation}
     */
    public Translation setPrefix(boolean usePrefix) {
        this.usePrefix = usePrefix;
        return this;
    }

    public Translation replace(String old, String replacement) {
        params.put(old, replacement);
        return this;
    }

    public String getTranslationKey() {
        return this.translationkey;
    }
}
