package de.tentact.languageapi.event;
/*  Created in the IntelliJ IDEA.
    Copyright(c) 2020
    Created by 0utplay | Aldin Sijamhodzic
    Datum: 09.07.2020
    Uhrzeit: 16:06
*/

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractLanguageEvent extends Event implements Cancellable {

    public abstract @NotNull String getLanguage();
}
