package de.tentact.languageapi.player;

import de.tentact.languageapi.AbstractLanguageAPI;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public interface SpecificPlayerExecutor {

    /**
     * @param playerUUID the player uuid to specify the player
     * @return returns a String with the language of the player in the database
     */
    @NotNull
    String getPlayerLanguage();

    /**
     * @param playerUUID    player uuid for whom the language should be changed
     * @param newLanguage   the new language of the player
     * @param orElseDefault should set default if the language was not found {@link AbstractLanguageAPI#getDefaultLanguage()}
     *                      Sets the player specific language
     */
    void setPlayerLanguage(String newLanguage, boolean orElseDefault);

    /**
     * @param playerUUID  player uuid for whom the language should be changed
     * @param newLanguage the new language of the player
     *                    Sets the player specific language, if the language exists
     */
    void setPlayerLanguage(String newLanguage);

    /**
     * @param playerUUID player uuid the player is created with
     *                   creates the player in the database
     */
    void registerPlayer();

    /**
     * @param playerUUID player uuid the player is created with
     * @param language   the language that the player has on creation
     *                   creates the player in the database
     */

    void registerPlayer(String language);

    /**
     * @param playerUUID player uuid the player was created with
     * @return returns if a player is in the database
     */
    boolean isRegisteredPlayer();

}
