package de.tentact.languageapi.file;

import java.io.File;

/*  Created in the IntelliJ IDEA.
    Copyright(c) 2020
    Created by 0utplay | Aldin Sijamhodzic
    Datum: 08.09.2020
    Uhrzeit: 19:25
*/
public interface FileHandler {

    default boolean loadFile(File file) {
        return this.loadFile(file, false);
    }

    default boolean loadFiles(File[] files) {
       return this.loadFiles(files, false);
    }

    boolean loadFile(File file, boolean doOverwrite);

    boolean loadFiles(File[] files, boolean doOverwrite);


}
