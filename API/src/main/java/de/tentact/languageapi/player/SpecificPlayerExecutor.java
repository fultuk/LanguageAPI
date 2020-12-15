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

/**
 * This is the {@link PlayerExecutor} but for an specific player
 * @since 1.8
 */
public interface SpecificPlayerExecutor extends PlayerManager {

    /**
     * @return returns a String with the language of the player in the database
     */
    @NotNull
    String getPlayerLanguage();

    /**
     * @param language the language to check
     * @return returns if the given language is the players set language
     */

    boolean isPlayersLanguage(@NotNull String language);

    /**
     * @param newLanguage   the new language of the player
     * @param orElseDefault should set default if the language was not found {@link LanguageAPI#getDefaultLanguage()}
     *                      Sets the player specific language
     */
    void setPlayerLanguage(@NotNull String newLanguage, boolean orElseDefault);

    /**
     * @param newLanguage the new language of the player
     *                    Sets the player specific language, if the language exists
     */
    void setPlayerLanguage(@NotNull String newLanguage);

    /**
     * creates the player in the database
     */
    void registerPlayer();

    /**
     * @param language the language that the player has on creation
     * creates the player in the database
     */

    void registerPlayer(@NotNull String language);

    /**
     * @return returns if a player is in the database
     */
    boolean isRegisteredPlayer();

    /**
     * Gets an {@link LanguagePlayer} - null if the player is not online
     *
     * @return returns a {@link LanguagePlayer}
     */
    @Nullable
    LanguagePlayer getLanguagePlayer();

    /**
     * Gets an {@link LanguageOfflinePlayer}
     *
     * @return returns a LanguageOfflinePlayer
     */
    @NotNull
    LanguageOfflinePlayer getLanguageOfflinePlayer();

}
