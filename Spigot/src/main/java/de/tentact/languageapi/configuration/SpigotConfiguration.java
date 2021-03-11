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

package de.tentact.languageapi.configuration;

import com.github.derrop.documents.DefaultDocument;
import com.github.derrop.documents.Document;
import com.github.derrop.documents.Documents;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Collections;
import java.util.logging.Logger;

public class SpigotConfiguration extends Configuration {

    private Document inventoryDocument = new DefaultDocument();
    private final File inventoryFile = new File("plugins/LanguageAPI", "languages.json");

    private final File importDir = new File("plugins/LanguageAPI/import");
    private final File exportDir = new File("plugins/LanguageAPI/export");

    private LanguageInventory languageInventory;

    public SpigotConfiguration(Logger logger) {
        super(logger);
        try {
            if (!this.importDir.exists()) {
                Files.createDirectories(this.importDir.getParentFile().toPath());
            }
            if (!this.exportDir.exists()) {
                Files.createDirectories(this.exportDir.getParentFile().toPath());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (this.inventoryFile.exists()) {
            this.inventoryDocument = Documents.jsonStorage().read(this.inventoryFile);
        } else {
            try {
                Files.createDirectories(this.inventoryFile.getParentFile().toPath());
                Files.createFile(this.inventoryFile.toPath());
                this.inventoryDocument.append("config",
                        new LanguageInventory(
                                new LanguageInventoryConfiguration(
                                        true,
                                        "Choose a language:",
                                        "BLACK_STAINED_GLASS_PANE",
                                        (byte) -1,
                                        Arrays.asList(
                                                new LanguageItem(
                                                        "PLAYER_HEAD",
                                                        "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNWU3ODk5YjQ4MDY4NTg2OTdlMjgzZjA4NGQ5MTczZmU0ODc4ODY0NTM3NzQ2MjZiMjRiZDhjZmVjYzc3YjNmIn19fQ==",
                                                        "Deutsch (Deutschland)",
                                                        "de_de",
                                                        1,
                                                        Collections.singletonList("Klicke um Deutsch auszuwählen.")
                                                ),
                                                new LanguageItem(
                                                        "PLAYER_HEAD",
                                                        "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTllZGNkZDdiMDYxNzNkN2QyMjFjNzI3NGM4NmNiYTM1NzMwMTcwNzg4YmI2YTFkYjA5Y2M2ODEwNDM1YjkyYyJ9fX0=",
                                                        "English (UK)",
                                                        "en_en",
                                                        0,
                                                        Collections.singletonList("Click to select english.")
                                                )
                                        )
                                ))).json().write(this.inventoryFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public LanguageInventory getLanguageInventory() {
        if (this.languageInventory != null) {
            return this.languageInventory;
        }
        this.languageInventory = this.inventoryDocument.get("config", LanguageInventory.class);
        return this.languageInventory;
    }

}

