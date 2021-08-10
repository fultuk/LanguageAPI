/*
 * MIT License
 *
 * Copyright (c) 2021 0utplay (Aldin Sijamhodzic)
 * Copyright (c) 2021 contributors
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

package de.tentact.languageapi.config;

import com.github.derrop.documents.Document;
import com.github.derrop.documents.Documents;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

public class LanguageConfigurationHelper {

  /**
   * Write the given LanguageConfiguration to the file specified by the path
   *
   * @param languageConfiguration the languageConfiguration that should be written to the file
   * @param path                  the path to the file
   */
  public static void writeConfiguration(@NotNull LanguageConfiguration languageConfiguration, @NotNull Path path) {
    Objects.requireNonNull(languageConfiguration, "languageConfiguration");
    Objects.requireNonNull(path, "path");

    try {
      Files.createDirectories(path.getParent());
      Files.createFile(path);
    } catch (IOException exception) {
      exception.printStackTrace();
    }
    Document document = Documents.newDocument("config", languageConfiguration);
    document.json().write(path);
  }

  /**
   * Reads the configuration from the given path
   *
   * @param path              the path to the file
   * @param createIfNotExists whether to create a configuration
   * @return the configuration from the given path
   */
  @Nullable
  public static LanguageConfiguration readConfiguration(Path path, boolean createIfNotExists) {
    Objects.requireNonNull(path, "path");

    if (Files.notExists(path)) {
      if (createIfNotExists) {
        writeConfiguration(LanguageConfiguration.DEFAULT_LANGUAGE_CONFIGURATION, path);
        return LanguageConfiguration.DEFAULT_LANGUAGE_CONFIGURATION.clone();
      }
      return null;
    }
    Document document = Documents.jsonStorage().read(path);
    if (!document.contains("config")) {
      if (createIfNotExists) {
        writeConfiguration(LanguageConfiguration.DEFAULT_LANGUAGE_CONFIGURATION, path);
        return LanguageConfiguration.DEFAULT_LANGUAGE_CONFIGURATION.clone();
      }
      return null;
    }
    return document.get("config", LanguageConfiguration.class);
  }

}
