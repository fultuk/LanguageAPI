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
import java.util.Arrays;
import java.util.Collections;

public class Configuration {

    Document document = new DefaultDocument();
    File file = new File("plugins/LanguageAPI", "languages.json");

    private LanguageInventory languageInventory;

    public Configuration() {
        if (file.exists()) {
            document = Documents.jsonStorage().read(file);
        } else {
            try {
                file.createNewFile();
                document.append("config",
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
                                                        Collections.singletonList("Klicke um Deutsch auszuwählen."))
                                        )
                                ))).json().write(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public LanguageInventory getLanguageInventory() {
        if(languageInventory != null) {
            return this.languageInventory;
        }
        this.languageInventory = this.document.get("config", LanguageInventory.class);
        return this.languageInventory;
    }


}
