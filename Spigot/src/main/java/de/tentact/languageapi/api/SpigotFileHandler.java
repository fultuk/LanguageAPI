/*
 * MIT License
 *
 * Copyright (c) 2020 0utplay
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

package de.tentact.languageapi.api;

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
