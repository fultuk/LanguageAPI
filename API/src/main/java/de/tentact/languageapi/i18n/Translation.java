package de.tentact.languageapi.i18n;

import de.tentact.languageapi.LanguageAPI;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * This interface can only be accessed via {@link LanguageAPI#getTranslation(String)}
 * Used to store translationkey and send them directly to a player instead of fetching
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
    default String getMessage(@NotNull String language) {
        return this.getMessage(language, false);
    }

    /**
     * @param language the language to get the translation in
     * @param orElseDefault whether to use the default language if the given one was not found
     * @return returns a translation of the key in the given language if found, else uses default language if orElseDefault is <code>true<code/>
     */
    @NotNull
    String getMessage(@NotNull String language, boolean orElseDefault);

    /**
     * @return returns all parameters for the key in the {@link Translation#getTranslationKey()}
     */

    String getParameter();

    /**
     *
     * @param prefixTranslation the prefix translation to get the prefix from
     * @return returns a {@link Translation} after setting the prefixTranslation
     */
    Translation setPrefixTranslation(Translation prefixTranslation);

    /**
     * a method to replace parameter in the specific translation for a player - this is reset after {@link Translation#getMessage()}
     *
     * @param old         the old String to replace
     * @param replacement the replacement for the paramater
     * @return returns {@link Translation} after replacing the parameter
     */
    Translation replace(String old, String replacement);


    /**
     * @return returns the translationkey which was given
     */
    String getTranslationKey();

    /**
     * Create the default translation for the {@link Translation}
     * @param message the default translation
     * @return the {@link Translation} after setting the default translation
     */

    Translation createDefaults(String message);

    /**
     *Create the default translation for the {@link Translation}
     * @param message the default translation
     * @param param the parameter of the translation
     * @return the {@link Translation} after setting the default translation
     */
    Translation createDefaults(String message, String param);

}
