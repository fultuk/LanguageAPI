package de.tentact.languageapi.api;
/*  Created in the IntelliJ IDEA.
    Created by 0utplay | Aldin Sijamhodzic
    Datum: 25.04.2020
    Uhrzeit: 16:52
*/

import com.velocitypowered.api.proxy.ProxyServer;
import de.tentact.languageapi.configuration.LanguageConfig;
import de.tentact.languageapi.file.FileHandler;
import de.tentact.languageapi.player.*;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class VelocityLanguageAPI extends DefaultLanguageAPI {

    private final ProxyServer proxyServer;
    private final PlayerManager playerManager;
    private final PlayerExecutor playerExecutor;
    private final FileHandler fileHandler;

    public VelocityLanguageAPI(ProxyServer proxyServer, LanguageConfig languageConfig) {
        super(languageConfig);
        this.proxyServer = proxyServer;
        this.playerManager = new VelocityPlayerManager(proxyServer);
        this.playerExecutor = new VelocityPlayerExecutor(proxyServer, this, languageConfig);
        this.fileHandler = new VelocityFileHandler();
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
        return new VelocitySpecificPlayerExecutor(this.proxyServer, playerId);
    }

    @Override
    public FileHandler getFileHandler() {
        return this.fileHandler;
    }
}
