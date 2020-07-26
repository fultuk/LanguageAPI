package de.tentact.languageapi.player;

import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class PlayerManagerImpl implements PlayerManager{
    @Override
    public @Nullable LanguagePlayer getLanguagePlayer(UUID playerId) {
        if(Bukkit.getPlayer(playerId) == null) {
            return null;
        }
        return new LanguagePlayerImpl(playerId);
    }

    @Override
    public @NotNull LanguageOfflinePlayer getLanguageOfflinePlayer(UUID playerId) {
        return new LanguageOfflinePlayerImpl(playerId);
    }
}