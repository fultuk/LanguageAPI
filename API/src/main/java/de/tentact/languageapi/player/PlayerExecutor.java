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
import de.tentact.languageapi.i18n.Translation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.UUID;

/**
 * An interface to set and get properties of an player by its uniqueId
 * @since 1.8
 */
public interface PlayerExecutor {

    /**
     * @param playerUUID the player uuid to specify the player
     * @return returns a String with the language of the player in the database - registers the player if he not exists
     */
    @NotNull
    String getPlayerLanguage(UUID playerUUID);

    /**
     * Checks if the given language equals the set language of the player
     * @param playerUUID the player uuid
     * @param language the language to check against
     * @return if the language is the same
     */
    boolean isPlayersLanguage(UUID playerUUID, String language);

    /**
     * @param playerUUID    player uuid for whom the language should be changed
     * @param newLanguage   the new language of the player
     * @param orElseDefault should set default if the language was not found {@link LanguageAPI#getDefaultLanguage()}
     *                      Sets the player specific language
     */
    void setPlayerLanguage(UUID playerUUID, String newLanguage, boolean orElseDefault);

    /**
     * @param playerUUID  player uuid for whom the language should be changed
     * @param newLanguage the new language of the player
     *                    Sets the player specific language, if the language exists
     */
     void setPlayerLanguage(UUID playerUUID, String newLanguage);

    /**
     * @param playerUUID player uuid the player is created with
     *                   creates the player in the database
     */
     void registerPlayer(UUID playerUUID);

    /**
     * creates the player in the database
     * @param playerUUID player uuid the player is created with
     * @param language   the language that the player has on creation
     */

     void registerPlayer(UUID playerUUID, String language);

    /**
     * @param playerUUID player uuid the player was created with
     * @return returns if a player is in the database
     */
     boolean isRegisteredPlayer(UUID playerUUID);

    /**
     * Broadcast a message
     * @param translation the {@link Translation} to get the translated message from
     */
    void broadcastMessage(Translation translation);

    /**
     * Kick every player with a {@link Translation} as reason
     * @param translation the {@link Translation} to get the translated message from
     */
    void kickAll(Translation translation);

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
