package de.tentact.languageapi.file;

import java.io.File;

/*  Created in the IntelliJ IDEA.
    Copyright(c) 2020
    Created by 0utplay | Aldin Sijamhodzic
    Datum: 08.09.2020
    Uhrzeit: 19:25
*/

/**
 * This is used to import files and insert the values into the database
 * This feature is currently only available for spigot.
 */
public interface FileHandler {

    default boolean loadFile(File file) {
        return this.loadFile(file, false);
    }

    default boolean loadFiles(File[] files) {
       return this.loadFiles(files, false);
    }

    /**
     *
     * @param file the file that should be read
     * @param doOverwrite whether to overwrite old translations
     * @return returns if it imported correctly
     */
    boolean loadFile(File file, boolean doOverwrite);

    /**
     *
     * @param files the files that should be read
     * @param doOverwrite whether to overwrite old translations
     * @return returns if all files were imported correctly
     */
    default boolean loadFiles(File[] files, boolean doOverwrite) {
        boolean passed = false;
        for (File file : files) {
            passed = this.loadFile(file, doOverwrite);
        }
        return passed;
    }


}
