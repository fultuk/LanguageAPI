package de.tentact.languageapi;


import de.tentact.languageapi.i18n.Translation;
import de.tentact.languageapi.player.PlayerExecutor;
import de.tentact.languageapi.player.PlayerManager;
import de.tentact.languageapi.player.SpecificPlayerExecutor;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/*  Created in the IntelliJ IDEA.
    Copyright(c) 2020
    Created by 0utplay | Aldin Sijamhodzic
    Datum: 09.06.2020
    Uhrzeit: 23:29
*/

/**
 * An API to the LanguageAPI, which aims to make the translation of messages into different languages efficient and easy.
 * Everything works with a unique key that returns the translation in the correct language. A key can not only lead to a translation,
 * but can lead also to a collection of keys that lead back to the translations.
 */
public abstract class LanguageAPI {

    private static LanguageAPI languageAPI;

    /**
     * @return returns a reference to this interface
     */
    @NotNull
    public static LanguageAPI getInstance() {
        return languageAPI;
    }

    /**
     * @param languageAPI instance of the interface
     *                            sets the instance of the interface - set by the implementation
     */
    public static void setInstance(@NotNull LanguageAPI languageAPI) {
        if (LanguageAPI.languageAPI != null) {
            throw new UnsupportedOperationException("Cannot redefine singleton LanguageAPI");
        }
        LanguageAPI.languageAPI = languageAPI;
    }

    /**
     * @param language language that should be created
     *                 creates a table with a language and adds the language in the 'languages' table
     */
    public abstract void createLanguage(final String language);

    /**
     * @param language Language that should be deleted
     *                 Deletes the language table and removes it from 'languages'
     */
    public abstract void deleteLanguage(String language);



    /**
     * @param transkey the translationkey to find the translation
     * @param message  the translation to that translationkey
     * @param language the language of the translation
     * @param param    the parameters that are used in the translation (ex. %KEY%) - seperate them by ',' (ex. %PARAM1%,%PARAM2%)
     *                 adds an translation to the given language with a proper translation and parameters
     */
    public abstract void addMessage(final String transkey, final String message, final String language, String param);

    /**
     * @param transkey the translationkey to find the translation
     * @param message  the translation to that translationkey
     * @param language the language of the translation
     */
    public abstract void addMessage(final String transkey, final String message, final String language);

    /**
     * @param transkey the translationkey to the translation (the translation is the key)
     * @param language the language to the translationkey
     *                 adds a translation without an proper translation, it just uses the translationkey as translation
     */
    public abstract void addMessage(final String transkey, final String language);

    /**
     * @param transkey the translationkey to the translation
     *                 adds a translation without an proper translation to the default language, it just uses the translationkey as translation
     */
    public abstract void addMessage(final String transkey);

    /**
     * @param transkey    translationkey to the translation
     * @param translation the translation to the translationkey
     *                    adds a translation to the default language
     */
    public abstract void addMessageToDefault(final String transkey, final String translation);

    /**
     * @param transkey    translationkey to the translation
     * @param translation the translation to the translationkey
     * @param param       the parameters to the translation
     *                    adds a translation to the default language with the parameters
     */
    public abstract void addMessageToDefault(final String transkey, final String translation, final String param);

    /**
     * @param multipleTranslation the translationkey to the set of translations
     * @param translationKey      the translationkey that should be added to the set
     *                            adds an single translationkey to an set of keys
     */

    public abstract void addTranslationKeyToMultipleTranslation(final String multipleTranslation, final String translationKey);

    /**
     * @param transkey the translationkey to find the parameters
     * @param param    the parameters to the translationkey (ex. %KEY%)
     */
    public abstract void addParameter(final String transkey, final String param);

    /**
     * @param transkey the translationkey to find the parameters
     * @param param    the parameters to the translationkey (ex. %KEY%)
     */
    public abstract void deleteParameter(final String transkey, final String param);

    /**
     * @param transkey the translationkey to find the parameters
     *                 deletes all parameter to a translationkey
     */
    public abstract void deleteAllParameter(final String transkey);


    /**
     * @param langfrom the language from which the data should be copied
     * @param langto   the language to  which the data should be copied
     *                 copys all the data from one language to an other
     */
    public abstract void copyLanguage(String langfrom, String langto);

    /**
     * @param translationKey the translationkey to check the parameters for
     * @return returns if the translationkey has any parameters
     */
    public abstract boolean hasParameter(String translationKey);

    /**
     * @param translationKey the translationkey to get the parameters for
     * @return returns all the parameters to the translationkey
     * @throws IllegalArgumentException if the translationkey was not found
     */
    public abstract String getParameter(String translationKey);

    /**
     * @param translationKey the translationkey to check the parameters for
     * @param param          the parameter to check for
     * @return returns if {@param param} is a parameter of the given translationkey
     */

    public abstract boolean isParameter(String translationKey, String param);

    /**
     * @param translationKey the translationkey to delete in every language
     */
    public abstract void deleteMessageInEveryLang(String translationKey);

    /**
     * @param transkey the translationkey to update the translation
     * @param language the language to the translationkey
     * @param message  the new translation to the translationkey
     */
    public abstract void updateMessage(String transkey, String language, String message);

    /**
     * @param multipleTranslation the translationkey to the set of translations
     * @param translationKeys     a list of all translationskey to add in the set
     * @param overwrite           decides whether it will overwrite a current set if it already exists
     */
    public abstract void setMultipleTranslation(final String multipleTranslation, List<String> translationKeys, boolean overwrite);

    /**
     * @param multipleTranslation the translationkey to the set of translations
     *                            Deletes a set of translations
     */

    public abstract void removeMultipleTranslation(final String multipleTranslation);

    /**
     * @param multipleTranslation the translationkey to the set of translations
     * @param translationKey      the translationkey that should be removed from the set
     */

    public abstract void removeSingleTranslationFromMultipleTranslation(final String multipleTranslation, final String translationKey);

    /**
     * @param multipleTranslation the translationkey to the set of translations
     * @return returns if the translationkey is a set of translations
     */
    public abstract boolean isMultipleTranslation(final String multipleTranslation);

    /**
     * @param translationkey the translationkey to delete the translation from
     * @param language       the language to the translationkey
     */
    public abstract void deleteMessage(String translationkey, String language);



    /**
     * @param translationkey the translationkey to check if it is one
     * @param language       the language to the translationkey
     * @return returns if the translationkey is in the database for that language
     */

    public abstract boolean isKey(String translationkey, String language);

    /**
     * @param transkey the translationkey which holds the other keys
     * @return returns a {@link ArrayList<String>} with the translated messages in the default language
     */
    @NotNull
    public abstract ArrayList<String> getMultipleMessages(String transkey);

    /**
     * @param transkey   the translationkey which holds the other keys
     * @param playerUUID the player UUID is needed to select the language
     * @return returns a {@link ArrayList<String>} with the translated messages
     */
    @NotNull
    public abstract ArrayList<String> getMultipleMessages(String transkey, UUID playerUUID);

    /**
     * @param transkey the translationkey which holds the other keys
     * @param language the language to get the translation in
     * @return returns a {@link ArrayList<String>} with the translated messages
     */
    @NotNull
    public abstract ArrayList<String> getMultipleMessages(String transkey, String language);

    /**
     * @param transkey  the translationkey which holds the other keys
     * @param language  the language to get the translation in
     * @param usePrefix specify if the prefix should be returned with the translation
     * @return returns a {@link ArrayList<String>} with the translated messages
     */
    @NotNull
    public abstract ArrayList<String> getMultipleMessages(String transkey, String language, boolean usePrefix);

    /**
     * @param transkey   the translationkey which holds the other keys
     * @param playerUUID the player uuid to get the language from
     * @param usePrefix  specify if the prefix should be returned with the translation
     * @return returns a {@link ArrayList<String>} with the translated messages
     */
    @NotNull
    public abstract ArrayList<String> getMultipleMessages(String transkey, UUID playerUUID, boolean usePrefix);

    /**
     * @param translationkey the translationkey to get the translation from
     * @param playerUUID     the player uuid to get the language from
     * @param usePrefix      specify if the prefix should be returned with the translation
     * @return returns the translation for a given player
     */
    @NotNull
    public abstract String getMessage(String translationkey, UUID playerUUID, boolean usePrefix);

    /**
     * @param translationkey the translationkey to get the translation from
     * @param language       the language of the translation
     * @param usePrefix      specify if the prefix should be returned with the translation
     * @return returns the translation for a given language
     */

    @NotNull
    public abstract String getMessage(String translationkey, String language, boolean usePrefix);

    /**
     * @param translationkey the translationkey to get the translation from
     * @param playerUUID     the player uuid to get the language from
     * @return returns the translation for a given player
     */
    @NotNull
    public abstract String getMessage(String translationkey, UUID playerUUID);


    /**
     * @param translationkey translationkey to get the translation from
     * @param language       the language of the translation
     * @return returns the translation to the key and language
     * @throws IllegalArgumentException if the language was not found
     * @throws IllegalArgumentException if the translationKey was not found for the language
     */
    @NotNull
    public abstract String getMessage(String translationkey, String language);

    /**
     * @param language the language to check if it is a language
     * @return returns if it is a language
     */

    public abstract boolean isLanguage(String language);

    /**
     * @return returns all created languages
     */
    @NotNull
    public abstract ArrayList<String> getAvailableLanguages();

    /**
     * @param language the loanguage to get the keys from
     * @return returns all the translationkeys for the language
     * @throws IllegalArgumentException if the language was not found
     */
    @NotNull
    public abstract ArrayList<String> getAllTranslationKeys(String language);

    /**
     * @param language the language to get the translations from
     * @return returns all the translations for that language
     * @throws IllegalArgumentException if the language was not found
     */
    @NotNull
    public abstract ArrayList<String> getAllTranslations(String language);

    /**
     * @return returns the default language from the config
     */
    @NotNull
    public abstract String getDefaultLanguage();

    /**
     * @return returns the prefix of the api (languageapi-prefix) in the default language {@link LanguageAPI#getPrefix(String)}
     */
    @NotNull
    public abstract String getPrefix();

    /**
     * @param language the language of the prefix
     * @return returns the prefix to the language
     */
    @NotNull
    public abstract String getPrefix(String language);

    /**
     * Gets an {@link PlayerManager} to get {@link de.tentact.languageapi.player.LanguagePlayer} & {@link de.tentact.languageapi.player.LanguageOfflinePlayer}
     * @return returns a {@link PlayerManager}
     */
    @NotNull
    public abstract PlayerManager getPlayerManager();

    /**
     * Gets a {@link Translation} by its key
     * @param translationkey the translationkey to fetch the translation from
     * @return returns an {@link Translation}
     */
    @NotNull
    public abstract Translation getTranslation(String translationkey);

    /**
     * Gets a {@link Translation} by its key - with the option to use the prefix
     * @param translationkey the translationkey to fetch the translation from
     * @param usePrefix whether use the prefix in the translation
     * @return returns an {@link Translation} with usePrefix set
     */
    @NotNull
    public abstract Translation getTranslation(String translationkey, boolean usePrefix);

    /**
     * Gets a {@link PlayerExecutor} without a specific player
     * @return returns a {@link PlayerExecutor}
     */
    @NotNull
    public abstract PlayerExecutor getPlayerExecutor();

    /**
     * Gets a {@link SpecificPlayerExecutor} to do updates for a specific player
     * @param playerId the uniqueId to identify the player with
     * @return returns a {@link SpecificPlayerExecutor} for the given playerId
     */
    @NotNull
    public abstract SpecificPlayerExecutor getSpecificPlayerExecutor(@NotNull UUID playerId);



}
