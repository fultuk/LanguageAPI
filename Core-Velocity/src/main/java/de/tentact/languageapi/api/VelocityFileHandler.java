package de.tentact.languageapi.api;
/*  Created in the IntelliJ IDEA.
    Copyright(c) 2020
    Created by 0utplay | Aldin Sijamhodzic
    Datum: 19.10.2020
    Uhrzeit: 11:19
*/

import com.github.derrop.documents.DefaultDocument;
import com.github.derrop.documents.Document;
import com.github.derrop.documents.Documents;
import de.tentact.languageapi.LanguageAPI;
import de.tentact.languageapi.file.FileHandler;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class VelocityFileHandler implements FileHandler {

    LanguageAPI languageAPI = LanguageAPI.getInstance();

    @Override
    public boolean loadFile(@NotNull File file, boolean doOverwrite) {
        Document document = Documents.jsonStorage().read(file);
        Map<String, String> map = document.get("languageapi", HashMap.class);
        String language = map.get("language");
        map.remove("language");
        if (language == null || language.isEmpty()) {
            return false;
        }
        map.forEach((key, value) -> {
            if (!this.languageAPI.addMessage(key, value, language) && doOverwrite) {
                this.languageAPI.updateMessage(key, value, language);
            }
        });
        return true;
    }

    @Override
    public boolean exportAll() {
        boolean passed = true;
        for (String language : this.languageAPI.getAvailableLanguages()) {
            if (!this.exportLanguageToFile(language)) {
                passed = false;
            }
        }
        return passed;
    }

    @Override
    public boolean exportLanguageToFile(@NotNull String language) {
        if (!this.languageAPI.isLanguage(language)) {
            return false;
        }
        Map<String, String> keysAndTranslations = new HashMap<>();

        keysAndTranslations.put("language", language);
        keysAndTranslations.putAll(this.languageAPI.getKeysAndTranslations(language));

        Document document = new DefaultDocument("languageapi", keysAndTranslations);
        document.json().write(new File(language.toLowerCase() + ".json"));
        return true;
    }
}
