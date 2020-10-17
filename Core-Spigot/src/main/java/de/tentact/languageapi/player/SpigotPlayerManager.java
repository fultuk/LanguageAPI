package de.tentact.languageapi.player;

import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.UUID;
import java.util.stream.Collectors;

public class SpigotPlayerManager extends DefaultPlayerManager {

    @Override
    public @Nullable LanguagePlayer getLanguagePlayer(UUID playerId) {
        if(Bukkit.getPlayer(playerId) == null) {
            return null;
        }
        return new DefaultLanguagePlayer(playerId);
    }

    @Override
    public @NotNull Collection<LanguagePlayer> getOnlineLanguagePlayers() {
        return Bukkit.getOnlinePlayers().stream().map(player -> this.getLanguagePlayer(player.getUniqueId())).collect(Collectors.toList());
    }
}
