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
   * @param identifier
   * @param prefixIdentifier
   * @return
   */
  Message getMessage(Identifier identifier, Identifier prefixIdentifier);

  /**
   * @param identifier
   * @param prefixMessage
   * @return
   */
  Message getMessage(Identifier identifier, Message prefixMessage);

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
   * @param identifier      the identifier for the translation
   * @param locale          the locale of the translation
   * @param translation     the translation to the identifier and the locale
   * @param replaceIfExists whether to replace an existing translation
   */
  void translateMessage(Identifier identifier, Locale locale, String translation, boolean replaceIfExists);

  /**
   * Adds or updates a translation for the given identifier in the given locale,
   * if a translation exists this will overwrite it
   *
   * @param identifier  the identifier for the translation
   * @param locale      the locale of the translation
   * @param translation the translation to the identifier and the locale
   */
  default void translateMessage(Identifier identifier, Locale locale, String translation) {
    this.translateMessage(identifier, locale, translation, true);
  }

  /**
   * Adds or updates every translation from the given map in the given locale
   *
   * @param translations    the identifier and translations as map
   * @param locale          the locale of the translation
   * @param replaceIfExists whether to replace an existing translation
   */
  void translateMessage(Map<Identifier, String> translations, Locale locale, boolean replaceIfExists);

  /**
   * Adds or updates every translation from the given map in the given locale,
   * if a translation exists this will overwrite it
   *
   * @param translations the identifier and translations as map
   * @param locale       the locale of the translation
   */
  default void translateMessage(Map<Identifier, String> translations, Locale locale) {
    this.translateMessage(translations, locale, true);
  }

  /**
   * Adds or updates a translation for the given identifier in the default locale
   *
   * @param identifier      the identifier for the translation
   * @param translation     the translation to the identifier and the locale
   * @param replaceIfExists whether to replace an existing translation
   */
  void translateMessage(Identifier identifier, String translation, boolean replaceIfExists);

  /**
   * Adds or updates a translation for the given identifier in the default locale,
   * if a translation exists this will overwrite it
   *
   * @param identifier  the identifier for the translation
   * @param translation the translation to the identifier and the locale
   */
  default void translateMessage(Identifier identifier, String translation) {
    this.translateMessage(identifier, translation, true);
  }

}
