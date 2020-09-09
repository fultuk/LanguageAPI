package de.tentact.languageapi;
/*  Created in the IntelliJ IDEA.
    Copyright(c) 2020
    Created by 0utplay | Aldin Sijamhodzic
    Datum: 06.09.2020
    Uhrzeit: 09:48
*/

import de.tentact.languageapi.file.FileHandler;
import de.tentact.languageapi.i18n.Translation;
import de.tentact.languageapi.player.LanguageOfflinePlayer;
import de.tentact.languageapi.player.LanguagePlayer;
import de.tentact.languageapi.player.PlayerExecutor;

import java.io.File;
import java.util.UUID;

public class ExampleLanguageAPI {

    /**
     * Get an instance of the languageAPI
     */
    private final LanguageAPI languageAPI = LanguageAPI.getInstance();

    /**
     * Get the {@link PlayerExecutor}
     */
    private final PlayerExecutor playerExecutor = languageAPI.getPlayerExecutor();

    /**
     * Examples for sending a message to a player
     */
    public void sendMessage() {

        /*
         * Get a LanguagePlayer by the uuid
         * LanguagePlayer is null if the player is offline!
         */
        LanguagePlayer languagePlayer = this.playerExecutor.getLanguagePlayer(UUID.randomUUID());

        /*
         * Get a LanguageOfflinePlayer by the uuid
         *
         */

        LanguageOfflinePlayer languageOfflinePlayer = this.playerExecutor.getLanguageOfflinePlayer(UUID.randomUUID());

        /*
         * Get a Translation by its key
         */
        Translation translation = this.languageAPI.getTranslation("translation-key");

        /*
         * Send a message to a LanguagePlayer by a Translation
         */

        languagePlayer.sendMessage(translation);

        /*
         * Replace parameter in a translation
         */

        translation.replace("toReplace", "with");
        languagePlayer.sendMessage(translation.replace("%EXAMPLE%", "Replacement"));

        /*
         * Send a message directly by the translationKey
         */

        languagePlayer.sendMessageByKey("translation-key");

        /*
         * Get a message from the api without sending to a player
         */

        String message = this.languageAPI.getMessage("translation-key", "LanguageToGetIn");
        /*
         * This uses the players uuid to fetch the language
         */
        String messageByUUID = this.languageAPI.getMessage("translation-key", UUID.randomUUID());

    }

    /**
     * Use this if you want to load a translation file (ingame command /lang import <File> <Overwrite>)
     */
    public void load() {

        FileHandler fileHandler = this.languageAPI.getFileHandler();

        // This loads one file and overwrites existing translations
        File fileToLoad = new File("pathToFile", "fileName");
        boolean passed = fileHandler.loadFile(fileToLoad, true);

        //This loads multiple files and overwrites existing translations
        File[] filesToLoad = new File[0];
        boolean allFilesPassed = fileHandler.loadFiles(filesToLoad, true);
    }

}
