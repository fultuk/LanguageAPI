package de.tentact.languageapi.configuration;
/*  Created in the IntelliJ IDEA.
    Copyright(c) 2020
    Created by 0utplay | Aldin Sijamhodzic
    Datum: 07.08.2020
    Uhrzeit: 11:20
*/

import com.github.derrop.documents.DefaultDocument;
import com.github.derrop.documents.Document;
import com.github.derrop.documents.Documents;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Collections;
import java.util.logging.Logger;

public class Configuration {

    Document inventoryDocument = new DefaultDocument();
    File inventoryFile = new File("plugins/LanguageAPI", "languages.json");

    Document settingsDocument = new DefaultDocument();
    File settingsFile = new File("plugins/LanguageAPI", "config.json");

    File importDir = new File("plugins/LanguageAPI/import");

    private LanguageInventory languageInventory;
    private LanguageConfig languageConfig;

    public Configuration(Logger logger) {
        if (!importDir.exists()) {
            importDir.mkdirs();
        }
        if (inventoryFile.exists()) {
            inventoryDocument = Documents.jsonStorage().read(inventoryFile);
        } else {
            try {
                Files.createDirectories(inventoryFile.getParentFile().toPath());
                Files.createFile(inventoryFile.toPath());
                inventoryDocument.append("config",
                        new LanguageInventory(
                                new LanguageInventoryConfiguration(
                                        true,
                                        "Choose a language:",
                                        "BLACK_STAINED_GLASS_PANE",
                                        (byte) -1,
                                        Arrays.asList(
                                                new LanguageItem(
                                                        "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNWU3ODk5YjQ4MDY4NTg2OTdlMjgzZjA4NGQ5MTczZmU0ODc4ODY0NTM3NzQ2MjZiMjRiZDhjZmVjYzc3YjNmIn19fQ==",
                                                        "Deutsch (Deutschland)",
                                                        "de_de",
                                                        1,
                                                        Collections.singletonList("Klicke um Deutsch auszuwählen.")
                                                ),
                                                new LanguageItem(
                                                        "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTllZGNkZDdiMDYxNzNkN2QyMjFjNzI3NGM4NmNiYTM1NzMwMTcwNzg4YmI2YTFkYjA5Y2M2ODEwNDM1YjkyYyJ9fX0=",
                                                        "English (UK)",
                                                        "en_en",
                                                        0,
                                                        Collections.singletonList("Click to select english.")
                                                )
                                        )
                                ))).json().write(inventoryFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (settingsFile.exists()) {
            settingsDocument = Documents.jsonStorage().read(settingsFile);
        } else {
            try {
                Files.createDirectories(settingsFile.getParentFile().toPath());
                Files.createFile(settingsFile.toPath());
                settingsDocument.append("config",
                        new LanguageConfig(
                                new MySQL(
                                        "hostname",
                                        "languagapi",
                                        "languagapi",
                                        "password",
                                        3306
                                ),
                                new LanguageSetting(
                                        "de_de",
                                        5,
                                        true
                                )
                        )
                ).json().write(settingsFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        this.getLanguageConfig().setLogger(logger);
        this.getLanguageConfig().getMySQL().setLogger(logger);
    }

    public LanguageInventory getLanguageInventory() {
        if (this.languageInventory != null) {
            return this.languageInventory;
        }
        this.languageInventory = this.inventoryDocument.get("config", LanguageInventory.class);
        return this.languageInventory;
    }

    public LanguageConfig getLanguageConfig() {
        if (this.languageConfig != null) {
            return this.languageConfig;
        }
        this.languageConfig = this.settingsDocument.get("config", LanguageConfig.class);
        return this.languageConfig;
    }

}

