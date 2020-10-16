package de.tentact.languageapi.player;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.UUID;

public class DefaultPlayerManager implements PlayerManager {

    @Override
    public @Nullable LanguagePlayer getLanguagePlayer(UUID playerId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public @NotNull LanguageOfflinePlayer getLanguageOfflinePlayer(UUID playerId) {
        return new DefaultLanguageOfflinePlayer(playerId);
    }

    @Override
    public @NotNull Collection<LanguagePlayer> getOnlineLanguagePlayers() {
        throw new UnsupportedOperationException();
    }
}
