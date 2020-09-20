package de.tentact.languageapi.player;

import com.velocitypowered.api.proxy.ProxyServer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.UUID;
import java.util.stream.Collectors;

public class PlayerManagerImpl implements PlayerManager {

    private final ProxyServer proxyServer;

    public PlayerManagerImpl(ProxyServer proxyServer) {
        this.proxyServer = proxyServer;
    }

    @Override
    public @Nullable LanguagePlayer getLanguagePlayer(UUID playerId) {
        if (!this.proxyServer.getPlayer(playerId).isPresent()) {
            return null;
        }
        return new LanguagePlayerImpl(proxyServer, playerId);
    }

    @Override
    public @NotNull LanguageOfflinePlayer getLanguageOfflinePlayer(UUID playerId) {
        return new LanguageOfflinePlayerImpl(playerId);
    }

    @Override
    public @NotNull Collection<LanguagePlayer> getOnlineLanguagePlayers() {
        return this.proxyServer.getAllPlayers().stream().map(player -> this.getLanguagePlayer(player.getUniqueId())).collect(Collectors.toList());
    }

}
