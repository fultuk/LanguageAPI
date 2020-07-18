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
     * set the selected language of a player
     * @param language the language to set
     */
    void setLanguage(@NotNull String language);

    /**
     * set the selected language of a player with an option to use default
     * @param language the language to set
     * @param orElseDefault wether use default if the language was not found
     */
    void setLanguage(@NotNull String language, boolean orElseDefault);

    /**
     * @return returns a players selected language
     */
    @NotNull String getLanguage();

    /**
     * @return returns a players uniqueId
     */
    UUID getUniqueId();

}
