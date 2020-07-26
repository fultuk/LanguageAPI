package de.tentact.languageapi.player;

import de.tentact.languageapi.AbstractLanguageAPI;
import org.jetbrains.annotations.NotNull;

public interface SpecificPlayerExecutor {

    /**
     * @return returns a String with the language of the player in the database
     */
    @NotNull
    String getPlayerLanguage();

    /**
     * @param newLanguage   the new language of the player
     * @param orElseDefault should set default if the language was not found {@link AbstractLanguageAPI#getDefaultLanguage()}
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

    LanguagePlayer getLanguagePlayer();

    LanguageOfflinePlayer getLanguageOfflinePlayer();

}
