package de.tentact.languageapi.file;

import java.io.File;

/*  Created in the IntelliJ IDEA.
    Copyright(c) 2020
    Created by 0utplay | Aldin Sijamhodzic
    Datum: 08.09.2020
    Uhrzeit: 19:25
*/
public interface FileHandler {

    default void loadFile(File file) {
        this.loadFile(file, false);
    }

    default void loadFiles(File[] files) {
        this.loadFiles(files, false);
    }

    void loadFile(File file, boolean doOverwrite);

    void loadFiles(File[] files, boolean doOverwrite);


}
