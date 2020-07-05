package de.tentact.languageapi.event;
/*  Created in the IntelliJ IDEA.
    Copyright(c) 2020
    Created by 0utplay | Aldin Sijamhodzic
    Datum: 05.07.2020
    Uhrzeit: 10:45
*/

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class LanguageCopyEvent extends Event implements Cancellable {

    private final String language;
    private String newLanguage;
    private boolean cancel;
    private final HandlerList handlerList = new HandlerList();

    public LanguageCopyEvent(@NotNull String language, @NotNull String newLanguage) {
        this.language = language;
        this.newLanguage = newLanguage;
    }


    @Override
    public @NotNull HandlerList getHandlers() {
        return this.handlerList;
    }

    @Override
    public boolean isCancelled() {
        return this.cancel;
    }

    @Override
    public void setCancelled(boolean b) {
        this.cancel = b;
    }

    public String getLanguage() {
        return language;
    }

    public String getNewLanguage() {
        return newLanguage;
    }
}
