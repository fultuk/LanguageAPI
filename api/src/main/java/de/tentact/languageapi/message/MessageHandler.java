package de.tentact.languageapi.message;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

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
  @NotNull Identifier loadIdentifier(@NotNull Identifier identifier);

  /**
   * Loads the parameters of an identifier from the cache or the database
   *
   * @param identifier the identifier to be loaded
   * @return the loaded identifier
   */
  @NotNull CompletableFuture<Identifier> loadIdentifierAsync(@NotNull Identifier identifier);

  /**
   * Updates the parameters of the identifier in the cache & database
   *
   * @param identifier the identifier to be updated
   */
  void writeIdentifier(@NotNull Identifier identifier);

  /**
   * Retrieves the translation associated with the identifier
   *
   * @param identifier the identifier to search for
   * @param locale     the locale of the translation
   * @return the translation associated with the identifier, if no translation is found the identifier-key is returned
   */
  @NotNull String getMessage(@NotNull Identifier identifier, @NotNull Locale locale);

  /**
   * Retrieves the translation associated with the identifier
   *
   * @param identifier the identifier to search for
   * @param locale     the locale of the translation
   * @return the translation associated with the identifier
   */
  @NotNull CompletableFuture<String> getMessageAsync(@NotNull Identifier identifier, @NotNull Locale locale);

  /**
   * @param identifier the identifier for the message
   * @return the message associated with the given identifier
   */
  @NotNull Message getMessage(@NotNull Identifier identifier);

  /**
   * @param identifier the identifier for the message
   * @param prefixIdentifier the identifier of the prefix for the message
   * @return the message associated with the given identifier & prefixIdentifier
   */
  @NotNull Message getMessage(@NotNull Identifier identifier, @NotNull Identifier prefixIdentifier);

  /**
   * @param identifier the identifier for the message
   * @param prefixMessage the prefixMessage used for resolving the prefix of the message
   * @return the message associated with the given identifier & the prefixMessage
   */
  @NotNull Message getMessage(@NotNull Identifier identifier, @NotNull Message prefixMessage);

  /**
   * Retrieves every identifier and it's translation in the given locale
   *
   * @param locale the locale to retrieve the translation in
   * @return every identifier and it's translation in the given locale
   */
  @NotNull CompletableFuture<Map<Identifier, String>> getMessages(@NotNull Locale locale, boolean fromCache);

  /**
   * Retrieves every identifier for a given locale
   *
   * @param locale    the locale to retrieve the identifiers in
   * @return every identifier for the given locale
   */
  @NotNull CompletableFuture<Set<Identifier>> getIdentifier(@NotNull Locale locale, boolean fromCache);

  /**
   * Retrieves every identifier that was written to the database
   *
   * @param fromCache whether to use the cache only
   * @return every identifier that was written to the database
   */
  @NotNull CompletableFuture<Set<Identifier>> getGlobalIdentifier(boolean fromCache);

  /**
   * Adds or updates a translation for the given identifier in the given locale
   *
   * @param identifier      the identifier for the translation
   * @param locale          the locale of the translation
   * @param translation     the translation to the identifier and the locale
   * @param replaceIfExists whether to replace an existing translation
   */
  void translateMessage(@NotNull Identifier identifier, @NotNull Locale locale, @NotNull String translation, boolean replaceIfExists);

  /**
   * Adds or updates a translation for the given identifier in the given locale,
   * if a translation exists this will overwrite it
   *
   * @param identifier  the identifier for the translation
   * @param locale      the locale of the translation
   * @param translation the translation to the identifier and the locale
   */
  default void translateMessage(@NotNull Identifier identifier, @NotNull Locale locale, @NotNull String translation) {
    this.translateMessage(identifier, locale, translation, true);
  }

  /**
   * Adds or updates every translation from the given map in the given locale
   *
   * @param translations    the identifier and translations as map
   * @param locale          the locale of the translation
   * @param replaceIfExists whether to replace an existing translation
   */
  void translateMessage(@NotNull Map<Identifier, String> translations, @NotNull Locale locale, boolean replaceIfExists);

  /**
   * Adds or updates every translation from the given map in the given locale,
   * if a translation exists this will overwrite it
   *
   * @param translations the identifier and translations as map
   * @param locale       the locale of the translation
   */
  default void translateMessage(@NotNull Map<Identifier, String> translations, @NotNull Locale locale) {
    this.translateMessage(translations, locale, true);
  }

  /**
   * Adds or updates a translation for the given identifier in the default locale
   *
   * @param identifier      the identifier for the translation
   * @param translation     the translation to the identifier and the locale
   * @param replaceIfExists whether to replace an existing translation
   */
  void translateMessage(@NotNull Identifier identifier, @NotNull String translation, boolean replaceIfExists);

  /**
   * Adds or updates a translation for the given identifier in the default locale,
   * if a translation exists this will overwrite it
   *
   * @param identifier  the identifier for the translation
   * @param translation the translation to the identifier and the locale
   */
  default void translateMessage(@NotNull Identifier identifier, @NotNull String translation) {
    this.translateMessage(identifier, translation, true);
  }

}
