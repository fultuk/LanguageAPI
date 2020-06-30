package de.tentact.languageapi;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/*  Created in the IntelliJ IDEA.
    Copyright(c) 2020
    Created by 0utplay | Aldin Sijamhodzic
    Datum: 09.06.2020
    Uhrzeit: 23:29
*/
public abstract class AbstractLanguageAPI {

    private static AbstractLanguageAPI abstractLanguageAPI;

    /**
     * @return returns a reference to this interface
     */
    public static AbstractLanguageAPI getInstance() {
        return abstractLanguageAPI;
    }

    /**
     *
     * @param abstractLanguageAPI instance of the interface
     * sets the instance of the interface - set by the implementation
     */
    public static void setInstance(AbstractLanguageAPI abstractLanguageAPI) {
        AbstractLanguageAPI.abstractLanguageAPI = abstractLanguageAPI;
    }

    /**
     *
     * @param language language that should be created
     * creates a table with a language and adds the language in the 'languages' table
     */
    public abstract void createLanguage(final String language);

    /**
     *
     * @param language Language that should be deleted
     * Deletes the language table and removes it from 'languages'
     */
    public abstract void deleteLanguage(String language);

    /**
     *
     * @param playerUUID player uuid for whom the language should be changed
     * @param newLanguage the new language of the player
     * @param orElseDefault should set default if the language was not found {@link AbstractLanguageAPI#getDefaultLanguage()}
     * Sets the player specific language
     */
    public abstract void setPlayerLanguage(UUID playerUUID, String newLanguage, boolean orElseDefault);
    /**
     *
     * @param playerUUID player uuid for whom the language should be changed
     * @param newLanguage the new language of the player
     * Sets the player specific language, if the language exists
     */
    public abstract void setPlayerLanguage(UUID playerUUID, String newLanguage);

    /**
     *
     * @param playerUUID player uuid the player is created with
     * creates the player in the database
     */
    public abstract void createPlayer(UUID playerUUID);

    /**
     *
     * @param playerUUID player uuid the player was created with
     * @return returns if a player is in the database
     */
    public abstract boolean isRegisteredPlayer(UUID playerUUID);

    /**
     *
     * @param transkey the translationkey to find the translation
     * @param message the translation to that translationkey
     * @param language the language of the translation
     * @param param the parameters that are used in the translation (ex. %KEY%)
     */
    public abstract void addMessage(final String transkey, final String message, final String language, String param);

    /**
     *
     * @param transkey the translationkey to find the translation
     * @param message the translation to that translationkey
     * @param language the language of the translation
     */
    public abstract void addMessage(final String transkey, final String message, final String language);

    /**
     *
     * @param transkey the translationkey to the translation (the translation is the key)
     * @param language the language to the translationkey
     * adds a translation without an proper translation, it just uses the translationkey to translate
     */
    public abstract void addMessage(final String transkey, final String language);

    /**
     *
     * @param transkey the translationkey to the translation
     * adds a translation without an proper translation to the default language, it just uses the translationkey to translate
     */
    public abstract void addMessage(final String transkey);

    /**
     *
     * @param transkey translationkey to the translation
     * @param translation the translation to the translationkey
     * adds a translation to the default language
     */
    public abstract void addMessageToDefault(final String transkey, final String translation);
    /**
     *
     * @param transkey translationkey to the translation
     * @param translation the translation to the translationkey
     * @param param the parameters to the translation
     * adds a translation to the default language with the parameters
     */
    public abstract void addMessageToDefault(final String transkey, final String translation, final String param);

    public abstract void addTranslationKeyToMultipleTranslation(final String multipleTranslation, final String translationKey);



    /**
     *
     * @param transkey the translationkey to find the parameters
     * @param param the parameters to the translationkey (ex. %KEY%)
     */
    public abstract void addParameter(final String transkey, final String param);

    /**
     *
     * @param transkey the translationkey to find the parameters
     * @param param the parameters to the translationkey (ex. %KEY%)
     */
    public abstract void deleteParameter(final String transkey, final String param);

    /**
     *
     * @param transkey the translationkey to find the parameters
     * deletes all parameter to a translationkey
     */
    public abstract void deleteAllParameter(final String transkey);


    /**
     *
     * @param langfrom the language from which the data should be copied
     * @param langto the language to  which the data should be copied
     * copys all the data from one language to an other
     */
    public abstract void copyLanguage(String langfrom, String langto);

    /**
     *
     * @param translationKey the translationkey to check the parameters for
     * @return returns if the translationkey has any parameters
     */
    public abstract boolean hasParameter(String translationKey);

    /**
     *
     * @param translationKey the translationkey to get the parameters for
     * @return returns all the parameters to the translationkey
     * @throws IllegalArgumentException if the translationkey was not found
     */
    public abstract String getParameter(String translationKey);

    /**
     *
     * @param translationKey the translationkey to delete in every language
     */
    public abstract void deleteMessageInEveryLang(String translationKey);

    /**
     *
     * @param transkey the translationkey to update the translation
     * @param language the language to the translationkey
     * @param message the new translation to the translationkey
     */
    public abstract void updateMessage(String transkey, String language, String message);

    /**
     *
     * @param multipleTranslation the translationkey to the set of translations
     * @param translationKeys a list of all translationskey to add in the set
     * @param overwrite decides whether it will overwrite a current set if it already exists
     *
     */
    public abstract void setMultipleTranslation(final String multipleTranslation, List<String> translationKeys, boolean overwrite);

    /**
     *
     * @param multipleTranslation the translationkey to the set of translations
     * Deletes a set of translations
     */

    public abstract void removeMultipleTranslation(final String multipleTranslation);

    /**
     *
     * @param translationkey the translationkey to delete the translation from
     * @param language the language to the translationkey
     */
    public abstract void deleteMessage(String translationkey, String language);

    /**
     *
     * @param playerUUID the player uuid to specify the player
     * @return returns a String with the language of the player in the database
     */
    public abstract String getPlayerLanguage(UUID playerUUID);

    /**
     *
     * @param translationkey the translationkey to check if it is one
     * @param language the language to the translationkey
     * @return returns if the translationkey is in the database for that language
     */

    public abstract boolean isKey(String translationkey, String language);
    /**
     *
     * @param transkey the translationkey which holds the other keys
     * @return returns a {@link ArrayList<String>} with the translated messages in the default language
     */

    public abstract ArrayList<String> getMultipleMessages(String transkey);
    /**
     *
     * @param transkey the translationkey which holds the other keys
     * @param playerUUID the player UUID is needed to select the language
     * @return returns a {@link ArrayList<String>} with the translated messages
     */

    public abstract ArrayList<String> getMultipleMessages(String transkey, UUID playerUUID);

    /**
     *
     * @param transkey the translationkey which holds the other keys
     * @param language the language to get the translation in
     * @return returns a {@link ArrayList<String>} with the translated messages
     */

    public abstract ArrayList<String> getMultipleMessages(String transkey, String language);

    /**
     *
     * @param translationkey the translationkey to get the translation from
     * @param playerUUID the player uuid to get the language from
     * @param usePrefix specify if the prefix should be returnt with the translation
     * @return returns the translation for a given player
     */

    public abstract String getMessage(String translationkey, UUID playerUUID, boolean usePrefix);

    /**
     *
     * @param translationkey the translationkey to get the translation from
     * @param playerUUID the player uuid to get the language from
     * @return returns the translation for a given player
     */
    public abstract String getMessage(String translationkey, UUID playerUUID);


    /**
     *
     * @param translationkey translationkey to get the translation from
     * @param language the language of the translation
     * @return returns the translation to the key and language
     * @throws IllegalArgumentException if the language was not found
     * @throws IllegalArgumentException if the translationKey was not found for the language
     */

    public abstract String getMessage(String translationkey, String language);

    /**
     *
     * @param language the language to check if it is a language
     * @return returns if it is a language
     */
    public abstract boolean isLanguage(String language);

    /**
     *
     * @return returns all created languages
     */
    public abstract ArrayList<String> getAvailableLanguages();

    /**
     *
     * @param language the loanguage to get the keys from
     * @return returns all the translationkeys for the language
     * @throws IllegalArgumentException if the language was not found
     */

    public abstract ArrayList<String> getAllTranslationKeys(String language);

    /**
     *
     * @param language the language to get the translations from
     * @return returns all the translations for that language
     * @throws IllegalArgumentException if the language was not found
     */
    public abstract ArrayList<String> getAllTranslations(String language);

    /**
     *
     * @return returns the default language from the config
     */
    public abstract String getDefaultLanguage();

    /**
     *
     * @return returns the prefix of the api (languageapi-prefix) in the default language {@link AbstractLanguageAPI#getPrefix(String)}
     */
    public abstract String getPrefix();

    /**
     *
     * @param language the language of the prefix
     * @return returns the prefix to the language
     */
    public abstract String getPrefix(String language);
}
