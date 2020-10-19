package de.tentact.languageapi.file;

import org.jetbrains.annotations.NotNull;

import java.io.File;

/*  Created in the IntelliJ IDEA.
    Copyright(c) 2020
    Created by 0utplay | Aldin Sijamhodzic
    Datum: 08.09.2020
    Uhrzeit: 19:25
*/

/**
 * This is used to import files and insert the values into the database
 * The BungeeCord & Spigot FileHandler will produce a YAML-File, the Velocity one will produce a JSON-File
 */

public interface FileHandler {

    /**
     * Loads a file and inserts the content to the database
     * @param file the file that should be read
     * @return returns if it imported correctly
     */
    default boolean loadFile(@NotNull File file) {
        return this.loadFile(file, false);
    }

    /**
     * Loads files and inserts the content to the database
     * @param files the files that should be read
     * @return returns if all files were imported correctly
     */
    default boolean loadFiles(@NotNull File[] files) {
       return this.loadFiles(files, false);
    }

    /**
     * Loads a file and inserts the content to the database
     * @param file the file that should be read
     * @param doOverwrite whether to overwrite old translations
     * @return returns if it imported correctly
     */
    boolean loadFile(@NotNull File file, boolean doOverwrite);

    /**
     * Loads files and inserts the content to the database
     * @param files the files that should be read
     * @param doOverwrite whether to overwrite old translations
     * @return returns if all files were imported correctly
     */
    default boolean loadFiles(@NotNull File[] files, boolean doOverwrite) {
        boolean passed = false;
        for (File file : files) {
            passed = this.loadFile(file, doOverwrite);
        }
        return passed;
    }

    /**
     * Export all languages to files
     * @since 1.9
     * @return whether all languages were exported or not
     */
    boolean exportAll();

    /**
     * Exports all translationKeys and translations into a file
     * @since 1.9
     * @param language the language to export
     * @return whether the language was exported or not
     */
    boolean exportLanguageToFile(@NotNull String language);

}
