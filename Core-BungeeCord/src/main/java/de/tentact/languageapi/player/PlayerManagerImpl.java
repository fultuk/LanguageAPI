package de.tentact.languageapi.player;

import de.tentact.languageapi.i18n.Translation;
import net.md_5.bungee.api.ProxyServer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.UUID;
import java.util.stream.Collectors;

public class PlayerManagerImpl implements PlayerManager{
    @Override
    public @Nullable LanguagePlayer getLanguagePlayer(UUID playerId) {
        if(ProxyServer.getInstance().getPlayer(playerId) == null) {
            return null;
        }
        return new LanguagePlayerImpl(playerId);
    }

    @Override
    public @NotNull LanguageOfflinePlayer getLanguageOfflinePlayer(UUID playerId) {
        return new LanguageOfflinePlayerImpl(playerId);
    }

    @Override
    public @NotNull Collection<LanguagePlayer> getOnlineLanguagePlayer() {
        return ProxyServer.getInstance().getPlayers().stream().map(player -> this.getLanguagePlayer(player.getUniqueId())).collect(Collectors.toList());
    }

}
