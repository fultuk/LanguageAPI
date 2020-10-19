package de.tentact.languageapi.api;
/*  Created in the IntelliJ IDEA.
    Created by 0utplay | Aldin Sijamhodzic
    Datum: 25.04.2020
    Uhrzeit: 16:52
*/

import de.tentact.languageapi.configuration.LanguageConfig;
import de.tentact.languageapi.file.FileHandler;
import de.tentact.languageapi.player.*;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class BungeeCordLanguageAPI extends DefaultLanguageAPI {

    private final PlayerManager playerManager;
    private final FileHandler fileHandler;
    private final PlayerExecutor playerExecutor;

    public BungeeCordLanguageAPI(LanguageConfig languageConfig) {
        super(languageConfig);
        this.playerManager = new BungeePlayerManager();
        this.fileHandler = new BungeeFileHandler(this);
        this.playerExecutor = new BungeePlayerExecutor(this, languageConfig);
    }

    @Override
    public @NotNull PlayerManager getPlayerManager() {
        return this.playerManager;
    }

    @Override
    public @NotNull PlayerExecutor getPlayerExecutor() {
        return this.playerExecutor;
    }

    @Override
    public @NotNull SpecificPlayerExecutor getSpecificPlayerExecutor(@NotNull UUID playerId) {
        return new BungeeSpecificPlayerExecutor(playerId);
    }

    @Override
    public FileHandler getFileHandler() {
        return this.fileHandler;
    }


}
