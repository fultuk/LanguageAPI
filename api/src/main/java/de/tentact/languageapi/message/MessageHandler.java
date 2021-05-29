package de.tentact.languageapi.message;

import org.jetbrains.annotations.ApiStatus;

import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

@ApiStatus.Internal
public interface MessageHandler {

  /**
   * Loads the parameters of an identifier from the cache or the database
   *
   * @param identifier the identifier to be loaded
   * @return the loaded identifier
   */
  Identifier loadIdentifier(Identifier identifier);

  /**
   * Loads the parameters of an identifier from the cache or the database
   *
   * @param identifier the identifier to be loaded
   * @return the loaded identifier
   */
  CompletableFuture<Identifier> loadIdentifierAsync(Identifier identifier);

  /**
   * Updates the parameters of the identifier in the cache & database
   *
   * @param identifier the identifier to be updated
   */
  void writeIdentifier(Identifier identifier);

  /**
   * Retrieves the translation associated with the identifier
   *
   * @param identifier the identifier to search for
   * @param locale     the locale of the translation
   * @return the translation associated with the identifier
   */
  String getMessage(Identifier identifier, Locale locale);

  /**
   * Retrieves the translation associated with the identifier
   *
   * @param identifier the identifier to search for
   * @param locale     the locale of the translation
   * @return the translation associated with the identifier
   */
  CompletableFuture<String> getMessageAsync(Identifier identifier, Locale locale);

  /**
   * @param identifier
   * @return
   */
  Message getMessage(Identifier identifier);

  /**
   * Retrieves every identifier and it's translation in the given locale
   *
   * @param locale the locale to retrieve the translation in
   * @return every identifier and it's translation in the given locale
   */
  CompletableFuture<Map<Identifier, String>> getMessages(Locale locale);

  /**
   * Retrieves every identifier for a given locale
   *
   * @param locale    the locale to retrieve the identifiers in
   * @param cacheOnly whether to use the cache only
   * @return every identifier for the given locale
   */
  CompletableFuture<Set<Identifier>> getIdentifier(Locale locale, boolean cacheOnly);

  /**
   * Adds or updates a translation for the given identifier in the given locale
   *
   * @param identifier  the identifier for the translation
   * @param locale      the locale of the translation
   * @param translation the translation to the identifier and the locale
   */
  void translateMessage(Identifier identifier, Locale locale, String translation);

  /**
   * Adds or updates every translation from the given map in the given locale
   * @param translations the identifier and translations as map
   * @param locale the locale of the translation
   */
  void translateMessage(Map<Identifier, String> translations, Locale locale);

}
