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

import com.github.derrop.documents.Documents;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Collections;
import java.util.logging.Logger;

public class SpigotConfiguration extends Configuration {

    private LanguageInventoryConfiguration languageInventory;

    public SpigotConfiguration(Logger logger) {
        super(logger);
        try {
            File importDir = new File("plugins/LanguageAPI/import");
            if (!importDir.exists()) {
                Files.createDirectories(importDir.getParentFile().toPath());
            }
            File exportDir = new File("plugins/LanguageAPI/export");
            if (!exportDir.exists()) {
                Files.createDirectories(exportDir.getParentFile().toPath());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        File inventoryFile = new File("plugins/LanguageAPI", "languages.json");
        if (inventoryFile.exists()) {
            this.languageInventory = Documents.jsonStorage().read(inventoryFile).get("config", LanguageInventoryConfiguration.class);
        } else {
            try {
                Files.createDirectories(inventoryFile.getParentFile().toPath());
                Files.createFile(inventoryFile.toPath());

                Documents.newDocument().append("config",
                        new LanguageInventoryConfiguration(
                                true,
                                "Choose a language:",
                                "BLACK_STAINED_GLASS_PANE",
                                (byte) -1,
                                Arrays.asList(
                                        new LanguageItem(
                                                "PLAYER_HEAD",
                                                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0" +
                                                        "dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNWU3ODk5YjQ4MDY4NTg" +
                                                        "2OTdlMjgzZjA4NGQ5MTczZmU0ODc4ODY0NTM3NzQ2MjZiMjRiZDhjZm" +
                                                        "VjYzc3YjNmIn19fQ==",
                                                "Deutsch (Deutschland)",
                                                "de_de",
                                                1,
                                                Collections.singletonList("Klicke um Deutsch auszuw??hlen.")
                                        ),
                                        new LanguageItem(
                                                "PLAYER_HEAD",
                                                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0" +
                                                        "dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTllZGNkZDdiMDYxNz" +
                                                        "NkN2QyMjFjNzI3NGM4NmNiYTM1NzMwMTcwNzg4YmI2YTFkYjA5Y2M2O" +
                                                        "DEwNDM1YjkyYyJ9fX0=",
                                                "English (UK)",
                                                "en_en",
                                                0,
                                                Collections.singletonList("Click to select english.")
                                        )
                                )
                        )).json().write(inventoryFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public LanguageInventoryConfiguration getLanguageInventory() {
        return this.languageInventory;
    }

}

