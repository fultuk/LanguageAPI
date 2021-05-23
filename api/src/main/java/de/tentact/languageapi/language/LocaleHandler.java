package de.tentact.languageapi.language;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;

public interface LocaleHandler {

    /**
     * Creates a new language from the given locale in the database
     * @param locale the new locale
     */
    void createLocale(Locale locale);

    /**
     * Checks whether a language is available for the given locale
     * @param locale the locale to check for
     * @return whether a language is available for the given locale
     */
    boolean isAvailable(Locale locale);

    /**
     * Deletes the language corresponding to the locale from the database
     * @param locale the locale to be deleted
     */
    void deleteLocale(Locale locale);

    /**
     * Retrieves all locales from the cache
     * @return all locales from the cache
     */
    CompletableFuture<List<Locale>> getAvailableLocales();

    /**
     * Retrieves all locales from the cache or loads them from the database
     * @param fromCache whether the cache should be used or not
     * @return all locales from the cache or loads them from the database
     */
    CompletableFuture<List<Locale>> getAvailableLocales(boolean fromCache);

    /**
     * Copies every translationKey and the translation from one to the other locale
     * @param from the locale to be copied from
     * @param to the locale to be copied to
     */
    void copyLocale(Locale from, Locale to);

    /**
     * Copies every translationKey and the translation from one to the other locale and optional creates the locale
     * @param from from the locale to be copied from
     * @param to the locale to be copied to
     * @param createIfNotExists whether the locale should be created or not
     */
    void copyLocale(Locale from, Locale to, boolean createIfNotExists);

}
