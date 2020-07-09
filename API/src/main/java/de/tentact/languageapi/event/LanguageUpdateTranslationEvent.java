package de.tentact.languageapi.event;
/*  Created in the IntelliJ IDEA.
    Copyright(c) 2020
    Created by 0utplay | Aldin Sijamhodzic
    Datum: 09.07.2020
    Uhrzeit: 16:05
*/

import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class LanguageUpdateTranslationEvent extends AbstractLanguageEvent implements Cancellable {

    private final String language;
    private final String translation;
    private final String oldMessage;
    private final String newMessage;

    public LanguageUpdateTranslationEvent(String language, String translation, String oldMessage, String newMessage) {
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
    public String getLanguage() {
        return this.language;
    }

    public String getOldMessage() {
        return this.oldMessage;
    }

    public String getNewMessage() {
        return this.newMessage;
    }
}
