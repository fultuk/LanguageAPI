package de.tentact.languageapi.message;

import org.jetbrains.annotations.NotNull;

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
  @NotNull String build(@NotNull Locale locale, @NotNull Object... parameters);

  /**
   * Retrieves the translation and replaces parameters
   *
   * @param locale     the locale of the translated message
   * @param parameters the parameters as replacement
   * @return the translation with the parameters replaced
   */
  @NotNull CompletableFuture<String> buildAsync(@NotNull Locale locale, @NotNull Object... parameters);

  /**
   * The identifier associated with this message
   *
   * @return the identifier associated with this message
   */
  @NotNull Identifier getIdentifier();

}
