package de.tentact.languageapi.translation;

import java.util.Locale;
import java.util.concurrent.CompletableFuture;

public interface Translation {

    /**
     * Retrieves the translation for the given language
     * @param locale the locale to get the translation for
     * @return the translation for the given language
     */
    String getMessage(Locale locale);

    /**
     * Retrieves the translation for the given language async
     * @param locale the locale to get the translation for
     * @return the translation for the given language async
     */
    CompletableFuture<String> getMessageAsync(Locale locale);

}
