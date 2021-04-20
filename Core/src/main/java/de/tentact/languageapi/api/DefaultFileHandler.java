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

package de.tentact.languageapi.api;

import com.github.derrop.documents.DefaultDocument;
import com.github.derrop.documents.Document;
import com.github.derrop.documents.Documents;
import com.google.gson.reflect.TypeToken;
import de.tentact.languageapi.LanguageAPI;
import de.tentact.languageapi.file.FileHandler;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * @since 1.9
 */
public class DefaultFileHandler implements FileHandler {

    private static final Type translationType = new TypeToken<Map<String, String>>() {
    }.getType();

    @Override
    public boolean loadFile(@NotNull File file, boolean doOverwrite) {
        Document document = Documents.yamlStorage().read(file);
        Map<String, String> map = document.get("languageapi", translationType);

        String language = map.get("language");
        map.remove("language");
        if (language == null || language.isEmpty()) {
            return false;
        }
        map.forEach((key, value) -> {
            if (!LanguageAPI.getInstance().addMessage(key, value, language) && doOverwrite) {
                LanguageAPI.getInstance().updateMessage(key, value, language);
            }
        });
        return true;
    }

    @Override
    public CompletableFuture<Boolean> loadFileAsync(@NotNull File file, boolean doOverwrite) {
        return CompletableFuture.supplyAsync(() -> this.loadFile(file, doOverwrite));
    }

    @Override
    public CompletableFuture<Boolean> exportAll() {
        return CompletableFuture.supplyAsync(() -> {
            boolean passed = true;
            for (String language : LanguageAPI.getInstance().getAvailableLanguages()) {
                if (!this.exportLanguageToFile(language).join()) {
                    passed = false;
                }
            }
            return passed;
        });
    }

    @Override
    public CompletableFuture<Boolean> exportLanguageToFile(@NotNull String language) {
        return this.exportLanguageToFile(language, new File("plugins/LanguageAPI/export"));
    }

    @Override
    public CompletableFuture<Boolean> exportLanguageToFile(@NotNull String language, File file) {
        return CompletableFuture.supplyAsync(() -> {
            if (!LanguageAPI.getInstance().isLanguage(language)) {
                return false;
            }
            Map<String, String> keysAndTranslations = new HashMap<>();
            keysAndTranslations.put("language", language);
            keysAndTranslations.putAll(LanguageAPI.getInstance().getKeysAndTranslations(language));

            Document document = new DefaultDocument("languageapi", keysAndTranslations);
            File outputFile = new File(file, language.toLowerCase() + ".yml");
            try {
                Files.createDirectories(outputFile.getParentFile().toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
            document.yaml().write(outputFile);
            return true;
        });
    }
}
