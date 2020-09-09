package de.tentact.languageapi.player;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.UUID;

/**
 * An interface to get the {@link LanguagePlayer} & {@link LanguageOfflinePlayer}
 */
public interface PlayerManager {

    /**
     * Gets an {@link LanguagePlayer} - null when the player is not online
     * @param playerId the uniqueId to fetch the player from
     * @return returns a {@link LanguagePlayer}
     */
    @Nullable
    LanguagePlayer getLanguagePlayer(UUID playerId);

    /**
     * Gets an {@link LanguageOfflinePlayer}
     * @param playerId the uniqueId to fetch the player from
     * @return returns a LanguageOfflinePlayer
     */
    @NotNull
    LanguageOfflinePlayer getLanguageOfflinePlayer(UUID playerId);

    /**
     * Get all online players
     * @return a {@link Collection<LanguagePlayer>} with all online players
     */
    @NotNull
    Collection<LanguagePlayer> getOnlineLanguagePlayers();


}
