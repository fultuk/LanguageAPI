package de.tentact.languageapi.api;
/*  Created in the IntelliJ IDEA.
    Copyright(c) 2020
    Created by 0utplay | Aldin Sijamhodzic
    Datum: 07.07.2020
    Uhrzeit: 15:47
*/

import de.tentact.languageapi.LanguageAPI;
import de.tentact.languageapi.i18n.Translation;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.UUID;

/**
 *
 */

public class TranslationImpl implements Translation {

    private final String translationkey;
    private final LanguageAPI languageAPI = LanguageAPI.getInstance();
    private boolean usePrefix = false;
    private final HashMap<String, String> params = new HashMap<>();
    private String message;

    /**
     * @param translationkey the translationkey to fetch the translation from
     */
    public TranslationImpl(@NotNull String translationkey) {
        this.translationkey = translationkey;
    }

    /**
     * @param translationkey the translationkey to fetch the translation from
     * @param usePrefix      wether to use the prefix of the languageapi or not
     */
    public TranslationImpl(String translationkey, boolean usePrefix) {
        this(translationkey);
        this.setPrefix(usePrefix);
    }

    /**
     * @return returns a translation of the key in the default language
     */
    @NotNull
    @Override
    public String getMessage() {
        return this.getMessage(this.languageAPI.getDefaultLanguage());
    }

    @NotNull
    @Override
    public String getMessage(@NotNull UUID playerUUID) {
        message = this.languageAPI.getMessage(this.translationkey, playerUUID, usePrefix);
        params.forEach((key, value) -> message = message.replace(key, value));
        params.clear();
        return message;
    }

    /**
     * @param language the language to get the translation in
     * @return returns a translation of the key in the given language
     */
    @NotNull
    @Override
    public String getMessage(@NotNull String language) {
        message = this.languageAPI.getMessage(this.translationkey, language, usePrefix);
        params.forEach((key, value) -> message = message.replace(key, value));
        params.clear();
        return message;
    }

    /**
     * @return returns the parameter for a key
     */
    @Override
    public String getParameter() {
        return this.languageAPI.getParameter(this.translationkey);
    }

    /**
     * @param usePrefix wether to use the languageapi prefix in the translation or not
     * @return returns a {@link TranslationImpl}
     */
    @Override
    public TranslationImpl setPrefix(boolean usePrefix) {
        this.usePrefix = usePrefix;
        return this;
    }

    /**
     * a method to replace parameter in the specific translation for a player - this is reset after {@link TranslationImpl#getMessage()}
     *
     * @param old         the old String to replace
     * @param replacement the replacement for the paramater
     * @return returns {@link TranslationImpl} after inserting the parameter
     */
    @Override
    public TranslationImpl replace(String old, String replacement) {
        params.put(old, replacement);
        return this;
    }

    /**
     * @return returns the translationkey which was given
     */
    @Override
    public String getTranslationKey() {
        return this.translationkey;
    }
}
