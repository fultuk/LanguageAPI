package de.tentact.languageapi.player;

import net.md_5.bungee.api.ProxyServer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.UUID;
import java.util.stream.Collectors;

public class BungeePlayerManager extends DefaultPlayerManager {

    @Override
    public @Nullable LanguagePlayer getLanguagePlayer(UUID playerId) {
        if(ProxyServer.getInstance().getPlayer(playerId) == null) {
            return null;
        }
        return new DefaultLanguagePlayer(playerId);
    }

    @Override
    public @NotNull Collection<LanguagePlayer> getOnlineLanguagePlayers() {
        return ProxyServer.getInstance().getPlayers().stream().map(player -> this.getLanguagePlayer(player.getUniqueId())).collect(Collectors.toList());
    }
}
