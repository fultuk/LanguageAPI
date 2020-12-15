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

package de.tentact.languageapi;

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

        /*
         * A LanguagePlayer is null, if the player is not online.
         */
        if (languagePlayer != null) {
            languagePlayer.sendMessage(translation);
        }

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

    /**
     * Use this if you want to export a language to a file (ingame command /lang export <Language>)
     */
    public void export() {
        FileHandler fileHandler = this.languageAPI.getFileHandler();

        boolean passed = fileHandler.exportLanguageToFile("languageName");

        boolean allFilesPassed = fileHandler.exportAll();
    }

}
