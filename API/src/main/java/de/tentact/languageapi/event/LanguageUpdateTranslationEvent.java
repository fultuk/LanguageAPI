package de.tentact.languageapi.event;
/*  Created in the IntelliJ IDEA.
    Copyright(c) 2020
    Created by 0utplay | Aldin Sijamhodzic
    Datum: 09.07.2020
    Uhrzeit: 16:05
*/

import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class LanguageUpdateTranslationEvent extends AbstractLanguageEvent{

    private final String language;
    private final String translationkey;
    private final String oldMessage;
    private final String newMessage;

    /**
     * this event is called when a player updates an translation by command
     * @param language the language in which the translation was changed
     * @param translationkey the translationkey to the translation which was updated
     * @param oldMessage the old translation
     * @param newMessage the new translation
     */

    public LanguageUpdateTranslationEvent(@NotNull String language,
                                          @NotNull String translationkey,
                                          @NotNull String oldMessage,
                                          @NotNull String newMessage) {
        this.language = language;
        this.translationkey = translationkey;
        this.oldMessage = oldMessage;
        this.newMessage = newMessage;
    }


    @Override
    public boolean isCancelled() {
        return false;
    }

    @Override
    public void setCancelled(boolean cancel) {

    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return null;
    }


    public String getTranslationkey() {
        return this.translationkey;
    }

    @Override
    public @NotNull String getLanguage() {
        return this.language;
    }

    public String getOldMessage() {
        return this.oldMessage;
    }

    public String getNewMessage() {
        return this.newMessage;
    }
}
