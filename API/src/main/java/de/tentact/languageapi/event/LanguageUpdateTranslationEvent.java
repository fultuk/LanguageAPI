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
    private final String translation;
    private final String oldMessage;
    private final String newMessage;

    public LanguageUpdateTranslationEvent(@NotNull String language,
                                          @NotNull String translation,
                                          @NotNull String oldMessage,
                                          @NotNull String newMessage) {
        this.language = language;
        this.translation = translation;
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


    public String getTranslation() {
        return this.translation;
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
