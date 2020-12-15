/*
 * MIT License
 *
 * Copyright (c) 2020 0utplay
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package de.tentact.languageapi.player;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.UUID;

/**
 * An interface to get the {@link LanguagePlayer} & {@link LanguageOfflinePlayer}
 * @since 1.8
 */
public interface PlayerManager {

    /**
     * Gets an {@link LanguagePlayer} - null if the player is offline
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
