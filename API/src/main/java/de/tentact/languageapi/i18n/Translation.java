package de.tentact.languageapi.i18n;
/*  Created in the IntelliJ IDEA.
    Copyright(c) 2020
    Created by 0utplay | Aldin Sijamhodzic
    Datum: 07.07.2020
    Uhrzeit: 15:47
*/

import de.tentact.languageapi.AbstractLanguageAPI;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 *
 */

public class Translation {

    private final String translationkey;
    private final AbstractLanguageAPI abstractLanguageAPI = AbstractLanguageAPI.getInstance();
    private boolean usePrefix = false;

    /**
     *
     * @param translationkey
     */
    public Translation(@NotNull String translationkey) {
        this.translationkey = translationkey;
    }

    /**
     *
     * @return returns a translation of the key in the default language
     */
    @NotNull
    public String getMessage() {
        return this.getMessage(this.abstractLanguageAPI.getDefaultLanguage());
    }

    /**
     *
     * @param playerUUID the player's uniqueid to fetch the language from
     * @return returns a translation of the key in the language fetched by {@link UUID}
     */
    @NotNull
    public String getMessage(@NotNull UUID playerUUID) {
        return this.abstractLanguageAPI.getMessage(this.translationkey, playerUUID, usePrefix);
    }

    /**
     *
     * @param language the language to get the translation in
     * @return returns a translation of the key in the given language
     */
    @NotNull
    public String getMessage(@NotNull String language) {
        return this.abstractLanguageAPI.getMessage(this.translationkey, language, usePrefix);
    }

    /**
     *
     * @return returns the parameter for a key
     */

    public String getParameter() {
       return this.abstractLanguageAPI.getParameter(this.translationkey);
    }

    /**
     *
     * @param usePrefix
     * @return returns a {@link Translation}
     */
    public Translation setPrefix(boolean usePrefix) {
        this.usePrefix = usePrefix;
        return this;
    }
}
