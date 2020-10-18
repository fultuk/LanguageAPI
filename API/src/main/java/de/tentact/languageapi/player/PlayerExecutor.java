package de.tentact.languageapi.player;

import de.tentact.languageapi.LanguageAPI;
import de.tentact.languageapi.i18n.Translation;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * An interface to set and get properties of an player by its uniqueId
 */
public interface PlayerExecutor extends PlayerManager {

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
}
