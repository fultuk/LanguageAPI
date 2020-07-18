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
     * @param translationkey the translationkey to fetch the translation from
     */
    public Translation(@NotNull String translationkey) {
        this.translationkey = translationkey;
    }

    /**
     *
     * @param translationkey the translationkey to fetch the translation from
     * @param usePrefix wether to use the prefix of the languageapi or not
     */
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
     * @param usePrefix wether to use the languageapi prefix in the translation or not
     * @return returns a {@link Translation}
     */
    public Translation setPrefix(boolean usePrefix) {
        this.usePrefix = usePrefix;
        return this;
    }

    /**
     * a method to replace parameter in the specific translation for a player - this is reset after {@link Translation#getMessage()}
     * @param old the old String to replace
     * @param replacement the replacement for the paramater
     * @return returns {@link Translation} after inserting the parameter
     */
    public Translation replace(String old, String replacement) {
        params.put(old, replacement);
        return this;
    }

    /**
     *
     * @return returns the translationkey which was given
     */
    public String getTranslationKey() {
        return this.translationkey;
    }
}
