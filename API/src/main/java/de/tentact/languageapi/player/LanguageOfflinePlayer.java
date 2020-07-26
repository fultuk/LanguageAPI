package de.tentact.languageapi.player;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/*  Created in the IntelliJ IDEA.
    Copyright(c) 2020
    Created by 0utplay | Aldin Sijamhodzic
    Datum: 16.07.2020
    Uhrzeit: 23:11
*/
public interface LanguageOfflinePlayer {

    /**
     * @param language
     */
    void setLanguage(@NotNull String language);

    /**
     * @param language
     * @param orElseDefault
     */
    void setLanguage(@NotNull String language, boolean orElseDefault);

    /**
     * @return
     */
    @NotNull String getLanguage();

    UUID getUniqueId();

    SpecificPlayerExecutor getSpecificPlayerExecutor();

}
