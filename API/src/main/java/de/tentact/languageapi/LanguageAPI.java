/*
 * MIT License
 *
 * Copyright (c) 2020 0utplay
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package de.tentact.languageapi;

import de.tentact.languageapi.file.FileHandler;
import de.tentact.languageapi.i18n.Translation;
import de.tentact.languageapi.player.PlayerExecutor;
import de.tentact.languageapi.player.SpecificPlayerExecutor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * An API to the LanguageAPI, which aims to make the translation of messages into different languages efficient and easy.
 * Everything works with a unique key that returns the translation in the correct language.
 */
public abstract class LanguageAPI {

    private static LanguageAPI languageAPI;

    /**
     * @return returns a reference to this interface
     * @since 1.8
     */
    @NotNull
    public static LanguageAPI getInstance() {
        return languageAPI;
    }

    /**
     * @param languageAPI instance of the interface
     * sets the instance of the interface - set by the implementation
     * @since 1.8
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
     * @since 1.8
     */
    public abstract void createLanguage(final String language);

    /**
     * @param language Language that should be deleted
     *                 Deletes the language table and removes it from 'languages'
     * @since 1.8
     */
    public abstract void deleteLanguage(String language);

    /**
     * @param translationKey the translationkey to find the translation
     * @param message  the translation to that translationkey
     * @param language the language of the translation
     * @param param    the parameters that are used in the translation (ex. %KEY%) - seperate them by ',' (ex. %PARAM1%,%PARAM2%)
     *                 adds an translation to the given language with a proper translation and parameters
     * @since 1.8
     */
    public abstract boolean addMessage(final String translationKey, final String message, final String language, String param);

    /**
     * @param translationKey the translationkey to find the translation
     * @param message  the translation to that translationkey
     * @param language the language of the translation
     * @since 1.8
     */
    public abstract boolean addMessage(final String translationKey, final String message, final String language);

    /**
     * @param translationKey the translationkey to the translation (the translation is the key)
     * @param language the language to the translationkey
     *                 adds a translation without an proper translation, it just uses the translationkey as translation
     * @since 1.8
     */
    public abstract boolean addMessage(final String translationKey, final String language);

    /**
     * @param translationKey the translationkey to the translation
     *                 adds a translation without an proper translation to the default language, it just uses the translationkey as translation
     * @since 1.8
     */
    public abstract boolean addMessage(final String translationKey);

    /**
     * @param transkey    translationkey to the translation
     * @param translation the translation to the translationkey
     *                    adds a translation to the default language
     * @since 1.8
     */
    public abstract boolean addMessageToDefault(final String transkey, final String translation);

    /**
     * Adds a translation to the default language with the parameters
     * @param translationKey    translationkey to the translation
     * @param translation the translation to the translationkey
     * @param param       the parameters to the translation
     *
     * @return if the translation was added
     * @since 1.8
     */
    public abstract boolean addMessageToDefault(final String translationKey, final String translation, final String param);

    /**
     * @param multipleTranslation the translationkey to the set of translations
     * @param translationKey      the translationkey that should be added to the set
     *                            adds an single translationkey to an set of keys
     * @since 1.8
     */

    public abstract void addTranslationKeyToMultipleTranslation(final String multipleTranslation, final String translationKey);

    /**
     * @param translationKey the translationkey to find the parameters
     * @param param  the parameters to the translationkey (ex. %KEY%,%KEY2%)
     * @since 1.9 (updated how it works)
     */
    public abstract void addParameter(final String translationKey, final String param);

    /**
     * Sets the parameters (overrides)
     * @param translationKey the translationkey to find the parameters
     * @param param    the parameters to the translationkey (ex. %KEY%)
     * @since 1.9
     */
    public abstract void setParameter(final String translationKey, final String param);

    /**
     * @param translationKey the translationkey to find the parameters
     * @param param    the parameters to the translationkey (ex. %KEY%)
     * @since 1.8
     */
    public abstract void deleteParameter(final String translationKey, final String param);

    /**
     * @param translationKey the translationkey to find the parameters
     *                 deletes all parameter to a translationkey
     * @since 1.8
     */
    public abstract void deleteAllParameter(final String translationKey);


    /**
     * @param langfrom the language from which the data should be copied
     * @param langto   the language to  which the data should be copied
     *                 copys all the data from one language to an other
     * @since 1.8
     */
    public abstract void copyLanguage(String langfrom, String langto);

    /**
     * @param translationKey the translationkey to check the parameters for
     * @return returns if the translationkey has any parameters
     * @since 1.8
     */
    public abstract boolean hasParameter(String translationKey);

    /**
     * @param translationKey the translationkey to get the parameters for
     * @return returns all the parameters to the translationKey, null if the key does not have any parameters
     * @since 1.8
     */
    @Nullable
    public abstract String getParameter(String translationKey);

    /**
     * @param translationKey the translationkey to check the parameters for
     * @param param          the parameter to check for
     * @return returns if {@param param} is a parameter of the given translationkey
     * @since 1.8
     */
    public abstract boolean isParameter(String translationKey, String param);

    /**
     * @param translationKey the translationkey to delete in every language
     * @since 1.8
     */
    public abstract void deleteMessageInEveryLang(String translationKey);

    /**
     * @param transkey the translationkey to update the translation
     * @param language the language to the translationkey
     * @param message  the new translation to the translationkey
     * @since 1.8
     */
    public abstract void updateMessage(String transkey, String language, String message);

    /**
     * @param multipleTranslation the translationkey to the set of translations
     * @param translationKeys     a list of all translationskey to add in the set
     * @param overwrite           decides whether it will overwrite a current set if it already exists
     * @since 1.8
     */
    public abstract void setMultipleTranslation(final String multipleTranslation, List<String> translationKeys, boolean overwrite);

    /**
     * @param multipleTranslation the translationkey to the set of translations
     *                            Deletes a set of translations
     * @since 1.8
     */
    public abstract void removeMultipleTranslation(final String multipleTranslation);

    /**
     * @param multipleTranslation the translationkey to the set of translations
     * @param translationKey      the translationkey that should be removed from the set
     * @since 1.8
     */
    public abstract void removeSingleTranslationFromMultipleTranslation(final String multipleTranslation, final String translationKey);

    /**
     * @param multipleTranslation the translationkey to the set of translations
     * @return returns if the translationkey is a set of translations
     * @since 1.8
     */
    public abstract boolean isMultipleTranslation(final String multipleTranslation);

    /**
     * @param translationkey the translationkey to delete the translation from
     * @param language       the language to the translationkey
     * @since 1.8
     */
    public abstract void deleteMessage(String translationkey, String language);

    /**
     * @param translationkey the translationkey to check if it is one
     * @param language       the language to the translationkey
     * @return returns if the translationkey is in the database for that language
     * @since 1.8
     */
    public abstract boolean isKey(String translationkey, String language);

    /**
     * @param transkey the translationkey which holds the other keys
     * @return returns a {@link List<String>} with the translated messages in the default language
     * @since 1.8
     */
    @NotNull
    public abstract List<String> getMultipleMessages(String transkey);

    /**
     * @param transkey   the translationkey which holds the other keys
     * @param playerUUID the player UUID is needed to select the language
     * @return returns a {@link List<String>} with the translated messages
     * @since 1.8
     */
    @NotNull
    public abstract List<String> getMultipleMessages(String transkey, UUID playerUUID);

    /**
     * @param transkey the translationkey which holds the other keys
     * @param language the language to get the translation in
     * @return returns a {@link List<String>} with the translated messages
     * @since 1.8
     */
    @NotNull
    public abstract List<String> getMultipleMessages(String transkey, String language);

    /**
     * @param transkey the translationkey which holds the other keys
     * @param language the language to get the translation in
     * @return returns a {@link List<String>} with the translated messages
     * @since 1.8
     */
    @NotNull
    public abstract List<String> getMultipleMessages(String transkey, String language, String prefixKey);


    /**
     * @param translationkey the translationkey to get the translation from
     * @param playerUUID     the player uuid to get the language from
     * @return returns the translation for a given player
     * @since 1.8
     */
    @NotNull
    public abstract String getMessage(String translationkey, UUID playerUUID);

    /**
     * @param translationkey translationkey to get the translation from
     * @param language       the language of the translation
     * @return returns the translation to the key and language
     * @throws IllegalArgumentException if the language was not found
     * @throws IllegalArgumentException if the translationKey was not found for the language
     * @since 1.8
     */
    @NotNull
    public abstract String getMessage(String translationkey, String language);

    /**
     * @param language the language to check if it is a language
     * @return returns if the given language is a valid language
     * @since 1.8
     */
    public abstract boolean isLanguage(@Nullable String language);

    /**
     * @return returns all created languages
     * @since 1.8
     */
    @NotNull
    public abstract List<String> getAvailableLanguages();

    /**
     * @param language the loanguage to get the keys from
     * @return returns all the translationkeys for the language
     * @throws IllegalArgumentException if the language was not found
     * @since 1.8
     */
    @NotNull
    public abstract List<String> getAllTranslationKeys(String language);

    /**
     * @param language the language to get the translations from
     * @return returns all the translations for that language
     * @throws IllegalArgumentException if the language was not found
     * @since 1.8
     */
    @NotNull
    public abstract List<String> getAllTranslations(String language);

    /**
     *
     * @param language the language to get the keys and translations from
     * @return returns a {@link Map} with every key and its translation in the given language
     * @since 1.8
     */
    @NotNull
    public abstract Map<String, String> getKeysAndTranslations(String language);

    /**
     * @return returns the default language from the config
     * @since 1.8
     */
    @NotNull
    public abstract String getDefaultLanguage();

    /**
     * @return returns the prefix of the api (languageapi-prefix) in the default language {@link LanguageAPI#getLanguageAPIPrefix(String)}
     * @since 1.8
     */
    @NotNull
    public abstract String getLanguageAPIPrefix();

    /**
     * @param language the language of the prefix
     * @return returns the prefix to the language
     * @since 1.8
     */
    @NotNull
    public abstract String getLanguageAPIPrefix(String language);

    /**
     * Gets a {@link Translation} by its key
     * @param translationKey the translationKey to fetch the translation from
     * @return returns an {@link Translation}
     * @since 1.8
     */
    @NotNull
    public abstract Translation getTranslation(@NotNull String translationKey);

    /**
     *
     * @param prefixTranslation the prefixTranslation %PREFIX% will be replaced with
     * @param translationKey the translationkey to fetch the translation from
     * @return returns a {@link Translation} with a prefixTranslation set
     * @since 1.8
     */
    @NotNull
    public abstract Translation getTranslationWithPrefix(Translation prefixTranslation, String translationKey);

    /**
     * Gets a {@link PlayerExecutor} without a specific player
     * @return returns a {@link PlayerExecutor}
     * @since 1.8
     */
    @NotNull
    public abstract PlayerExecutor getPlayerExecutor();

    /**
     * Gets a {@link SpecificPlayerExecutor} to do updates for a specific player
     * @param playerId the uniqueId to identify the player with
     * @return returns a {@link SpecificPlayerExecutor} for the given playerId
     * @since 1.8
     */
    @NotNull
    public abstract SpecificPlayerExecutor getSpecificPlayerExecutor(@NotNull UUID playerId);

    /**
     * Updates an {@link Translation} in the HashMap
     * @param translation the translation to update
     *                    @since 1.8
     */
    public abstract void updateTranslation(Translation translation);

    /**
     * @return returns the {@link FileHandler}
     * @since 1.8
     */
    public abstract FileHandler getFileHandler();

    /**
     * execute a {@link Runnable} async using the LanguageAPI {@link java.util.concurrent.ExecutorService}
     * @param command the command to run
     * @since 1.8
     */
    public abstract void executeAsync(Runnable command);
}
