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

import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class DefaultLanguageOfflinePlayer implements LanguageOfflinePlayer {

    private final PlayerExecutor playerExecutor;
    private final SpecificPlayerExecutor specificPlayerExecutor;
    private final UUID playerID;

    public DefaultLanguageOfflinePlayer(@NotNull UUID playerID) {
        this.playerID = playerID;
        this.playerExecutor = LanguageAPI.getInstance().getPlayerExecutor();
        this.specificPlayerExecutor = LanguageAPI.getInstance().getSpecificPlayerExecutor(this.playerID);
    }

    @Override
    public void setLanguage(@Nullable String language) {
        this.setLanguage(language, true);
    }

    @Override
    public void setLanguage(@Nullable String language, boolean orElseDefault) {
        this.playerExecutor.setPlayerLanguage(this.playerID, language, orElseDefault);
    }

    @Override
    public @NotNull String getLanguage() {
        return this.playerExecutor.getPlayerLanguage(this.playerID);
    }

    @Override
    public @NotNull CompletableFuture<String> getLanguageAsync() {
        return this.playerExecutor.getPlayerLanguageAsync(this.playerID);
    }

    @Override
    public @NotNull UUID getUniqueId() {
        return this.playerID;
    }

    @Override
    public SpecificPlayerExecutor getSpecificPlayerExecutor() {
        return this.specificPlayerExecutor;
    }

    @Override
    public boolean isOnline() {
        return this.playerExecutor.getLanguagePlayer(this.playerID) != null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DefaultLanguageOfflinePlayer)) return false;
        DefaultLanguageOfflinePlayer that = (DefaultLanguageOfflinePlayer) o;
        return this.playerID.equals(that.playerID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.playerID);
    }
}
