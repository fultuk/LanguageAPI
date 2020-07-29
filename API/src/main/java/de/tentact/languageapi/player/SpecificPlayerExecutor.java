package de.tentact.languageapi.player;

import de.tentact.languageapi.LanguageAPI;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * This is the {@link PlayerExecutor} but for an specific player
 */
public interface SpecificPlayerExecutor extends PlayerManager {



    /**
     * @return returns a String with the language of the player in the database
     */
    @NotNull
    String getPlayerLanguage();

    /**
     *
     * @param language the language to check
     * @return returns if the given language is the players set language
     */

    boolean isPlayersLanguage(String language);

    /**
     * @param newLanguage   the new language of the player
     * @param orElseDefault should set default if the language was not found {@link LanguageAPI#getDefaultLanguage()}
     *                      Sets the player specific language
     */
    void setPlayerLanguage(String newLanguage, boolean orElseDefault);

    /**
     * @param newLanguage the new language of the player
     *                    Sets the player specific language, if the language exists
     */
    void setPlayerLanguage(String newLanguage);

    /**
     * creates the player in the database
     */
    void registerPlayer();

    /**
     * @param language the language that the player has on creation
     *                 creates the player in the database
     */

    void registerPlayer(String language);

    /**
     * @return returns if a player is in the database
     */
    boolean isRegisteredPlayer();

    /**
     * Gets an {@link LanguagePlayer} - null when the player is not online
     * @return returns a {@link LanguagePlayer}
     */
    LanguagePlayer getLanguagePlayer();

    /**
     *Gets an {@link LanguageOfflinePlayer}
     * @return returns a LanguageOfflinePlayer
     */
    LanguageOfflinePlayer getLanguageOfflinePlayer();

}
