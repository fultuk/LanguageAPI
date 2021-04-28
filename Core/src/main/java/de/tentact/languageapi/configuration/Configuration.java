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
import java.util.logging.Logger;

public class Configuration {

    private Document settingsDocument = new DefaultDocument();
    private LanguageConfig languageConfig;

    public Configuration(Logger logger) {
        File settingsFile = new File("plugins/LanguageAPI", "config.json");
        if (settingsFile.exists()) {
            this.settingsDocument = Documents.jsonStorage().read(settingsFile);
        } else {
            try {
                Files.createDirectories(settingsFile.getParentFile().toPath());
                Files.createFile(settingsFile.toPath());
                this.settingsDocument.append("config",
                        this.getDefaultLanguageConfig()
                ).json().write(settingsFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        this.getLanguageConfig().setLogger(logger);
        this.getLanguageConfig().getDatabaseProvider().setLogger(logger);
    }

    public LanguageConfig getLanguageConfig() {
        if (this.languageConfig == null) {
            this.languageConfig = this.settingsDocument.get("config", LanguageConfig.class);
        }
        return this.languageConfig;
    }

    private LanguageConfig getDefaultLanguageConfig() {
        return new LanguageConfig(
                new DatabaseProvider(
                        "hostname",
                        "languagapi",
                        "languagapi",
                        "password",
                        3306
                ),
                new LanguageSetting(
                        "en_en",
                        5,
                        true,
                        false
                )
        );
    }

}

