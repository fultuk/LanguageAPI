package de.tentact.languageapi.event;
/*  Created in the IntelliJ IDEA.
    Copyright(c) 2020
    Created by 0utplay | Aldin Sijamhodzic
    Datum: 05.07.2020
    Uhrzeit: 10:45
*/

import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class LanguageCopyEvent extends AbstractLanguageEvent {

    private final String language;
    private final String newLanguage;
    private boolean cancel;
    private final HandlerList handlerList = new HandlerList();

    /**
     * This event is called when {@link de.tentact.languageapi.AbstractLanguageAPI#copyLanguage(String, String)} is called and the given languages are correct
     *
     * @param language    the old language to copy from
     * @param newLanguage the language to copy to
     */

    public LanguageCopyEvent(@NotNull String language,
                             @NotNull String newLanguage) {
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

    public String getNewLanguage() {
        return this.newLanguage;
    }

    @Override
    public @NotNull String getLanguage() {
        return this.language;
    }
}
