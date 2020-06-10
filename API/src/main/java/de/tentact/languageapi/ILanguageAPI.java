package de.tentact.languageapi;

import java.util.ArrayList;
import java.util.UUID;

/*  Created in the IntelliJ IDEA.
    Copyright(c) 2020
    Created by 0utplay | Aldin Sijamhodzic
    Datum: 09.06.2020
    Uhrzeit: 23:29
*/
public abstract class ILanguageAPI {

    private static ILanguageAPI iLanguageAPI;

    /**
     * @return returns a reference to this interface
     */
    public static ILanguageAPI getInstance() {
        return iLanguageAPI;
    }

    /**
     *
     * @param iLanguageAPI instance of the interface
     *
     */
    public static void setInstance(ILanguageAPI iLanguageAPI) {
        ILanguageAPI.iLanguageAPI = iLanguageAPI;
    }

    /**
     *
     * @param langName language that should be created
     * creates a table with langName and adds the language in the 'languages' table
     */
    public abstract void createLanguage(final String langName);

    /**
     *
     * @param langName Language that should be deleted
     * Deletes the language table and removes it from 'languages'
     */
    public abstract void deleteLanguage(String langName);

    /**
     *
     * @param playerUUID player uuid for whom the language should be changed
     * @param newLang the new language of the player
     */
    public abstract void changePlayerLanguage(UUID playerUUID, String newLang);

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
    public abstract boolean playerExists(UUID playerUUID);

    /**
     *
     * @param transkey the translationkey to find the translation
     * @param message the translation to that translationkey
     * @param lang the language of the translation
     * @param param the parameters that are used in the translation (ex. %KEY%)
     */
    public abstract void addMessage(final String transkey, final String message, final String lang, String param);

    /**
     *
     * @param transkey the translationkey to find the translation
     * @param message the translation to that translationkey
     * @param lang the language of the translation
     */
    public abstract void addMessage(final String transkey, final String message, final String lang);

    /**
     *
     * @param transkey the translationkey to the translation (the translation is the key)
     * @param lang the language to the translationkey
     * adds a translation without an proper translation, it just uses the translationkey to translate
     */
    public abstract void addMessage(final String transkey, final String lang);

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
     * @param lang the language to the translationkey
     * @param message the new translation to the translationkey
     */
    public abstract void updateMessage(String transkey, String lang, String message);

    /**
     *
     * @param translationkey the translationkey to delete the translation from
     * @param lang the language to the translationkey
     */
    public abstract void deleteMessage(String translationkey, String lang);

    /**
     *
     * @param playerUUID the player uuid to specify the player
     * @return returns a String with the language of the player in the database
     */
    public abstract String getPlayerLanguage(UUID playerUUID);

    /**
     *
     * @param translationkey the translationkey to check if it is one
     * @param lang the language to the translationkey
     * @return returns if the translationkey is in the database for that language
     */

    public abstract boolean isKey(String translationkey, String lang);

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
     * @param lang the language of the translation
     * @return returns the translation to the key and language
     */

    public abstract String getMessage(String translationkey, String lang);

    /**
     *
     * @param lang the language to check if it is a language
     * @return returns if it is a language
     */
    public abstract boolean isLanguage(String lang);

    /**
     *
     * @return returns all the created languages cached
     */
    public abstract ArrayList<String> getAvailableLanguages();

    /**
     *
     * @return forces to update all created languages
     * dont use this method
     */
    public abstract ArrayList<String> getLangUpdate();

    /**
     *
     * @param lang the loanguage to get the keys from
     * @return returns all the translationkeys for the language
     */

    public abstract ArrayList<String> getAllKeys(String lang);

    /**
     *
     * @param lang the language to get the translations from
     * @return returns all the translations for that language
     */
    public abstract ArrayList<String> getAllMessages(String lang);

    /**
     *
     * @return returns the default language from the config
     */
    public abstract String getDefaultLanguage();

    /**
     *
     * @return returns the prefix of the api (languageapi-prefix)
     */
    public abstract String getPrefix();

    /**
     *
     * @param langName the language of the prefix
     * @return returns the prefix to the language
     */
    public abstract String getPrefix(String langName);
}
