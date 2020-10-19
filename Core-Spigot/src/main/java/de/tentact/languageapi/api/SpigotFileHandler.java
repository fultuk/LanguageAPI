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
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.Set;

public class SpigotFileHandler implements FileHandler {

    private final LanguageAPI languageAPI;

    public SpigotFileHandler(LanguageAPI languageAPI) {
        this.languageAPI = languageAPI;
    }

    @Override
    public boolean loadFile(@NotNull File file, boolean doOverwrite) {
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
    public boolean exportAll() {
        boolean passed = true;
        for(String language : this.languageAPI.getAvailableLanguages()) {
            if(!this.exportLanguageToFile(language)) {
                passed = false;
            }
        }
        return passed;
    }

    @Override
    public boolean exportLanguageToFile(@NotNull String language) {
        if(!this.languageAPI.isLanguage(language)) {
            return false;
        }
        File exportFile = new File("plugins/LanguageAPI/export", language+".yml");
        YamlConfiguration configuration = YamlConfiguration.loadConfiguration(exportFile);

        configuration.set("language", language.toLowerCase());
        this.languageAPI.getKeysAndTranslations(language).forEach(configuration::set);

        try {
            configuration.save(exportFile);
        } catch (IOException e) {
            return false;
        }
        return true;
    }
}
