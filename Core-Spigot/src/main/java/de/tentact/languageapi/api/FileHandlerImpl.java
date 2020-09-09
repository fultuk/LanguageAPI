package de.tentact.languageapi.api;
/*  Created in the IntelliJ IDEA.
    Copyright(c) 2020
    Created by 0utplay | Aldin Sijamhodzic
    Datum: 08.09.2020
    Uhrzeit: 19:27
*/

import de.tentact.languageapi.LanguageAPI;
import de.tentact.languageapi.file.FileHandler;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.Set;

public class FileHandlerImpl implements FileHandler {

    private final LanguageAPI languageAPI;

    public FileHandlerImpl(LanguageAPI languageAPI) {
        this.languageAPI = languageAPI;
    }

    @Override
    public boolean loadFile(File file, boolean doOverwrite) {
        YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(file);
        Set<String> keys = yamlConfiguration.getKeys(false);
        String languageName = yamlConfiguration.getString("language");
        if (languageName == null || languageName.isEmpty()) {
            return false;
        }
        if (!this.languageAPI.isLanguage(languageName)) {
            return false;
        }
        keys.remove("language");
        keys.forEach(key -> {
            if (!this.languageAPI.addMessage(key, yamlConfiguration.getString(key), languageName) && doOverwrite) {
                this.languageAPI.updateMessage(key, yamlConfiguration.getString(key), languageName);
            }
        });
        return true;
    }

    @Override
    public boolean loadFiles(File[] files, boolean doOverwrite) {
        boolean passed = false;
        for (File file : files) {
            passed = this.loadFile(file, doOverwrite);
        }
        return passed;
    }
}
