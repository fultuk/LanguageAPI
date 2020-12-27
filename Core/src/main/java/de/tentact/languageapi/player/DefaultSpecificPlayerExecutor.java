/*
 * MIT License
 *
 * Copyright (c) 2020 0utplay (Aldin Sijamhodzic)
 * Copyright (c) 2020 contributors
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

public class DefaultSpecificPlayerExecutor implements SpecificPlayerExecutor {

    private final UUID playerId;
    private final LanguageAPI languageAPI = LanguageAPI.getInstance();
    private final PlayerExecutor playerExecutor;

    public DefaultSpecificPlayerExecutor(UUID playerId) {
        this.playerExecutor = languageAPI.getPlayerExecutor();
        this.playerId = playerId;
    }

    @Override
    public @NotNull String getPlayerLanguage() {
        return this.playerExecutor.getPlayerLanguage(this.playerId);
    }

    @Override
    public boolean isPlayersLanguage(@NotNull String language) {
        if (!this.languageAPI.isLanguage(language)) {
            return false;
        }
        return this.playerExecutor.getPlayerLanguage(this.playerId).equalsIgnoreCase(language);
    }

    @Override
    public void setPlayerLanguage(@NotNull String newLanguage, boolean orElseDefault) {
        this.playerExecutor.setPlayerLanguage(this.playerId, newLanguage, orElseDefault);
    }

    @Override
    public void setPlayerLanguage(@NotNull String newLanguage) {
        this.playerExecutor.setPlayerLanguage(this.playerId, newLanguage);
    }

    @Override
    public void registerPlayer() {
        this.playerExecutor.registerPlayer(this.playerId);
    }

    @Override
    public void registerPlayer(@NotNull String language) {
        this.playerExecutor.registerPlayer(this.playerId, language);
    }

    @Override
    public boolean isRegisteredPlayer() {
        return this.playerExecutor.isRegisteredPlayer(this.playerId);
    }

    @Override
    public @Nullable LanguagePlayer getLanguagePlayer() {
        return this.playerExecutor.getLanguagePlayer(this.playerId);
    }

    @Override
    public @NotNull LanguageOfflinePlayer getLanguageOfflinePlayer() {
        return this.playerExecutor.getLanguageOfflinePlayer(this.playerId);
    }
}
