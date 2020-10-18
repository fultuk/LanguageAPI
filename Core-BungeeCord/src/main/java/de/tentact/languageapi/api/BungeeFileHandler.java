package de.tentact.languageapi.api;
/*  Created in the IntelliJ IDEA.
    Copyright(c) 2020
    Created by 0utplay | Aldin Sijamhodzic
    Datum: 06.10.2020
    Uhrzeit: 17:40
*/

import de.tentact.languageapi.LanguageAPI;
import de.tentact.languageapi.file.FileHandler;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

public class BungeeFileHandler implements FileHandler {
    private final LanguageAPI languageAPI;

    public BungeeFileHandler(LanguageAPI languageAPI) {
        this.languageAPI = languageAPI;
    }

    @Override
    public boolean loadFile(@NotNull File file, boolean doOverwrite) {
        try {
            Configuration configuration = YamlConfiguration.getProvider(YamlConfiguration.class).load(file);
            Collection<String> keys = configuration.getKeys();
            String languageName = configuration.getString("language");
            if (languageName == null || languageName.isEmpty()) {
                return false;
            }
            if (!this.languageAPI.isLanguage(languageName)) {
                return false;
            }
            keys.remove("language");
            keys.forEach(key -> {
                if (!this.languageAPI.addMessage(key, configuration.getString(key), languageName) && doOverwrite) {
                    this.languageAPI.updateMessage(key, configuration.getString(key), languageName);
                }
            });
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean exportAll() {
        boolean passed = false;
        for (String language : this.languageAPI.getAvailableLanguages()) {
            passed = this.exportLanguageToFile(language);
        }
        return passed;
    }

    @Override
    public boolean exportLanguageToFile(@NotNull String language) {
        if (!this.languageAPI.isLanguage(language)) {
            return false;
        }
        File exportFile = new File("plugins/LanguageAPI/export", language + ".yml");
        ConfigurationProvider provider = YamlConfiguration.getProvider(YamlConfiguration.class);
        try {
            Configuration configuration = provider.load(exportFile);
            configuration.set("language", language.toLowerCase());
            this.languageAPI.getKeysAndTranslations(language).forEach(configuration::set);

            provider.save(configuration, exportFile);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
