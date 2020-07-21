package de.tentact.languageapi.i18n;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * This interface can only be accessed via {@link de.tentact.languageapi.AbstractLanguageAPI#getTranslation(String)}
 */
public interface Translation {


    /**
     * @return returns a translation of the key in the default language
     */
    @NotNull
    String getMessage();

    /**
     * @param playerUUID the player's uniqueid to fetch the language from
     * @return returns a translation of the key in the language fetched by @link UUID
     */
    @NotNull
    String getMessage(@NotNull UUID playerUUID);


    /**
     * @param language the language to get the translation in
     * @return returns a translation of the key in the given language
     */
    @NotNull
    String getMessage(@NotNull String language);


    /**
     * @return returns the parameter for a key
     */

    String getParameter();


    /**
     * @param usePrefix wether to use the languageapi prefix in the translation or not
     * @return returns a @link TranslationImpl
     */
    Translation setPrefix(boolean usePrefix);

    /**
     * a method to replace parameter in the specific translation for a player - this is reset after @link TranslationImpl#getMessage()
     *
     * @param old         the old String to replace
     * @param replacement the replacement for the paramater
     * @return returns @link TranslationImpl after inserting the parameter
     */
    Translation replace(String old, String replacement);


    /**
     * @return returns the translationkey which was given
     */
    String getTranslationKey();


}
