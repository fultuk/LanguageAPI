package de.tentact.languageapi.entity;

import de.tentact.languageapi.message.Message;

public interface ConsoleEntity {

    /**
     * Translates the given translation and sends it to the console
     * @param translation the translation to be sent
     */
    void sendMessage(Message translation);

    /**
     * Retrieves a translation by its translationKey and sends it to the console
     * @param translationKey the translationKey a translation belongs to
     */
    void sendMessage(String translationKey);

}
