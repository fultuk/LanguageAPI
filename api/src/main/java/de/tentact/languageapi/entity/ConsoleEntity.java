package de.tentact.languageapi.entity;

import de.tentact.languageapi.LanguageAPI;
import de.tentact.languageapi.message.Identifier;
import de.tentact.languageapi.message.Message;

public interface ConsoleEntity {

  /**
   * Translates the given translation and sends it to the console
   *
   * @param translation the translation to be sent
   * @param parameters  the replacement for the parameters
   */
  void sendMessage(Message translation, Object... parameters);

  /**
   * Retrieves a translation by its translationKey and sends it to the console
   *
   * @param translationKey the translationKey a translation belongs to
   * @param parameters  the replacement for the parameters
   */
  default void sendMessage(String translationKey, Object... parameters) {
    Identifier identifier = Identifier.of(translationKey);
    Message message = LanguageAPI.getInstance().getMessageHandler().getMessage(identifier);
    this.sendMessage(message, parameters);
  }

}
