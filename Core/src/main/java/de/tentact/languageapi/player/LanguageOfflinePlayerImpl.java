package de.tentact.languageapi.player;
/*  Created in the IntelliJ IDEA.
    Copyright(c) 2020
    Created by 0utplay | Aldin Sijamhodzic
    Datum: 16.07.2020
    Uhrzeit: 23:12
*/

import de.tentact.languageapi.AbstractLanguageAPI;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class LanguageOfflinePlayerImpl implements LanguageOfflinePlayer {

    private final AbstractLanguageAPI abstractLanguageAPI = AbstractLanguageAPI.getInstance();

    private final PlayerExecutor playerExecutor = this.abstractLanguageAPI.getPlayerExecutor();

    private final UUID playerID;

    private final String language;

    public LanguageOfflinePlayerImpl(@NotNull UUID playerID) {
        this.playerID = playerID;
        this.language = this.playerExecutor.getPlayerLanguage(playerID);

    }

    @Override
    public void setLanguage(@NotNull String language) {
        this.setLanguage(language, false);
    }

    @Override
    public void setLanguage(@NotNull String language, boolean orElseDefault) {
        if (!this.abstractLanguageAPI.isLanguage(language) && !orElseDefault) {
            throw new IllegalArgumentException(language + " was not found");
        }
        this.playerExecutor.setPlayerLanguage(this.playerID, language, orElseDefault);
    }

    @Override
    public @NotNull String getLanguage() {
        return this.language;
    }

    @Override
    public UUID getUniqueId() {
        return this.playerID;
    }

    @Override
    public SpecificPlayerExecutor getSpecificPlayerExecutor() {
        return new SpecificPlayerExecutorImpl(this.playerID);
    }


}
