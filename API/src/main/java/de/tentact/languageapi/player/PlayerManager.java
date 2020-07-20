package de.tentact.languageapi.player;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public interface PlayerManager {

    @Nullable
    LanguagePlayer getLanguagePlayer(UUID playerId);

    @NotNull
    LanguageOfflinePlayer getLanguageOfflinePlayer(UUID playerId);

}
