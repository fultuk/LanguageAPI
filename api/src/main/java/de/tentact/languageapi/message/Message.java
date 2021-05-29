package de.tentact.languageapi.message;

import java.util.Locale;
import java.util.concurrent.CompletableFuture;

public interface Message {

  /**
   * Retrieves the translation and replaces parameters
   *
   * @param locale     the locale of the translated message
   * @param parameters the parameters as replacement
   * @return the translation with the parameters replaced
   */
  String build(Locale locale, Object... parameters);

  /**
   * Retrieves the translation and replaces parameters
   *
   * @param locale     the locale of the translated message
   * @param parameters the parameters as replacement
   * @return the translation with the parameters replaced
   */
  CompletableFuture<String> buildAsync(Locale locale, Object... parameters);

  /**
   * The identifier associated with this message
   *
   * @return the identifier associated with this message
   */
  Identifier getIdentifier();

}
