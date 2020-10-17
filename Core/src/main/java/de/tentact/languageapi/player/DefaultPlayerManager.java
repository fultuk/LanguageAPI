package de.tentact.languageapi.player;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.UUID;

public abstract class DefaultPlayerManager implements PlayerManager {

    @Override
    public abstract @Nullable LanguagePlayer getLanguagePlayer(UUID playerId);

    @Override
    public @NotNull LanguageOfflinePlayer getLanguageOfflinePlayer(UUID playerId) {
        return new DefaultLanguageOfflinePlayer(playerId);
    }

    @Override
    public abstract @NotNull Collection<LanguagePlayer> getOnlineLanguagePlayers();
}
