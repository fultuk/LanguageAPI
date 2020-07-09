package de.tentact.languageapi.event;
/*  Created in the IntelliJ IDEA.
    Copyright(c) 2020
    Created by 0utplay | Aldin Sijamhodzic
    Datum: 09.07.2020
    Uhrzeit: 16:06
*/

import org.bukkit.event.Event;

public abstract class AbstractLanguageEvent extends Event {

    public abstract String getLanguage();
}
