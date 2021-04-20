/*
 * MIT License
 *
 * Copyright (c) 2020 0utplay (Aldin Sijamhodzic)
 * Copyright (c) 2020 contributors
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

package de.tentact.languageapi.file;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.concurrent.CompletableFuture;

/**
 * This is used to import files and insert the values into the database
 * The {@link FileHandler} will produce a YAML-File
 */
public interface FileHandler {

    /**
     * Loads a file and inserts the content to the database
     * @param file the file that should be read
     * @return returns if it imported correctly
     * @since 1.8
     */
    default boolean loadFile(@NotNull File file) {
        return this.loadFile(file, false);
    }

    /**
     * Loads files and inserts the content to the database
     * @param files the files that should be read
     * @return returns if all files were imported correctly
     * @since 1.8
     */
    default boolean loadFiles(@NotNull File... files) {
       return this.loadFiles(files, false);
    }

    /**
     * Loads a file and inserts the content to the database
     * @param file the file that should be read
     * @param doOverwrite whether to overwrite old translations
     * @return returns if it imported correctly
     * @since 1.8
     *
     */
    boolean loadFile(@NotNull File file, boolean doOverwrite);

    /**
     * Loads a file and inserts the content to the database async
     * @param file the file that should be read
     * @param doOverwrite whether to overwrite old translations
     * @return returns if it imported correctly
     * @since 1.9
     */
    CompletableFuture<Boolean> loadFileAsync(@NotNull File file, boolean doOverwrite);

    /**
     * Loads files and inserts the content to the database
     * @param files the files that should be read
     * @param doOverwrite whether to overwrite old translations
     * @return returns if all files were imported correctly
     * @since 1.8
     */
    default boolean loadFiles(@NotNull File[] files, boolean doOverwrite) {
        boolean passed = true;
        for (File file : files) {
            if(!this.loadFile(file, doOverwrite)) {
                passed = false;
            }
        }
        return passed;
    }

    /**
     * Export all languages to files
     * @since 1.9
     * @return whether all languages were exported or not
     */
    CompletableFuture<Boolean> exportAll();

    /**
     * Exports all translationKeys and translations into a file
     * @since 1.9
     * @param language the language to export
     * @return whether the language was exported or not
     */
    CompletableFuture<Boolean> exportLanguageToFile(@NotNull String language);

    /**
     * Exports all translationKeys and translations into a file
     * @since 1.9
     * @param language the language to export
     * @param file the parent file destination
     * @return whether the language was exported or not
     */
    CompletableFuture<Boolean> exportLanguageToFile(@NotNull String language, File file);

}
