package de.tentact.languageapi.event;
/*  Created in the IntelliJ IDEA.
    Copyright(c) 2020
    Created by 0utplay | Aldin Sijamhodzic
    Datum: 05.07.2020
    Uhrzeit: 10:42
*/

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class LanguageDeleteEvent extends Event implements Cancellable {

    private final String language;
    private boolean cancel;
    private final HandlerList handlerList = new HandlerList();

    public LanguageDeleteEvent(@NotNull String language) {
        this.language = language;
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
        return this.language;
    }
}
