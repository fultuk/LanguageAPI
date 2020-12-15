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

import de.tentact.languageapi.LanguageAPI;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * You can get this interface by {@link PlayerManager#getLanguageOfflinePlayer(UUID)} or {@link PlayerExecutor#getLanguageOfflinePlayer(UUID)}
 */
public interface LanguageOfflinePlayer {

    /**
     * @param language the language to set to the player
     */
    void setLanguage(@NotNull String language);

    /**
     * @param language the language to set to the player
     * @param orElseDefault whether to use the default language if the given language was not found
     */
    void setLanguage(@NotNull String language, boolean orElseDefault);

    /**
     * @return returns the players language
     */
    @NotNull String getLanguage();

    /**
     * Gets the players uniqueId
     * @return returns the player uniqueId
     */
    UUID getUniqueId();

    /**
     * Gets a {@link SpecificPlayerExecutor} for the specific player
     * @return returns the {@link SpecificPlayerExecutor} for the {@link LanguageOfflinePlayer}
     */
    default SpecificPlayerExecutor getSpecificPlayerExecutor() {
        return LanguageAPI.getInstance().getSpecificPlayerExecutor(this.getUniqueId());
    }

    /**
     * Get a {@link LanguagePlayer}
     * @return returns a {@link LanguagePlayer} - null if the player is offline
     */
    @Nullable
    default LanguagePlayer getLanguagePlayer() {
        return this.getSpecificPlayerExecutor().getLanguagePlayer();
    }

    /**
     * @return returns if the player is online
     */
    boolean isOnline();
}
