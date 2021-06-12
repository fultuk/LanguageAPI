package de.tentact.languageapi.entity;

import de.tentact.languageapi.LanguageAPI;
import de.tentact.languageapi.message.Identifier;
import de.tentact.languageapi.message.Message;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public interface ConsoleEntity {

  /**
   * Translates the given translation and sends it to the console
   *
   * @param translation the translation to be sent
   * @param parameters  the replacement for the parameters
   */
  void sendMessage(@NotNull Message translation, @NotNull Object... parameters);

  /**
   * Retrieves a translation by its translationKey and sends it to the console
   *
   * @param translationKey the translationKey a translation belongs to
   * @param parameters     the replacement for the parameters
   */
  default void sendMessage(@NotNull String translationKey, @NotNull Object... parameters) {
    Objects.requireNonNull(translationKey, "translationKey");
    Objects.requireNonNull(parameters, "parameters");

    Identifier identifier = Identifier.of(translationKey);
    Message message = LanguageAPI.getInstance().getMessageHandler().getMessage(identifier);
    this.sendMessage(message, parameters);
  }

}
