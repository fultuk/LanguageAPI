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
     * @param language the language to set to the player
     */
    void setLanguage(@NotNull String language);

    /**
     * @param language the language to set to the player
     * @param orElseDefault whether to use the default language if the given language was not found
     */
    void setLanguage(@NotNull String language, boolean orElseDefault);

    /**
     * @return returns the players language
     */
    @NotNull String getLanguage();

    /**
     * Gets the players uniqueId
     * @return returns the player uniqueId
     */
    UUID getUniqueId();

    /**
     * Gets a {@link SpecificPlayerExecutor} for the specific player
     * @return returns the {@link SpecificPlayerExecutor} for the {@link LanguageOfflinePlayer}
     */
    SpecificPlayerExecutor getSpecificPlayerExecutor();

}
