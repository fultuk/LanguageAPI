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

package de.tentact.languageapi.file;

import com.github.derrop.documents.Document;
import com.github.derrop.documents.Documents;
import com.google.common.base.Preconditions;
import de.tentact.languageapi.LanguageAPI;
import de.tentact.languageapi.message.Identifier;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class DefaultFileHandler implements FileHandler {

  @Override
  public CompletableFuture<Void> loadFile(@NotNull Path path) {
    return this.loadFile(path, false);
  }

  @Override
  public CompletableFuture<Void> loadFile(@NotNull Path path, boolean overwrite) {
    Preconditions.checkNotNull(path, "path");

    return CompletableFuture.supplyAsync(() -> {
      Document inputDocument = Documents.yamlStorage().read(path);

      Map<Identifier, String> uncheckedMessages = (Map<Identifier, String>) inputDocument.get("languageAPI");

      Identifier localeIdentifier = Identifier.of("locale");
      Locale locale = Locale.forLanguageTag(uncheckedMessages.get(localeIdentifier));

      LanguageAPI.getInstance().getMessageHandler().translateMessage(uncheckedMessages, locale, overwrite);

      return null;
    });
  }

  @Override
  public CompletableFuture<Boolean> exportLanguage(@NotNull Locale locale) {
    Preconditions.checkNotNull(locale, "locale");

    return this.exportLanguage(locale, Paths.get("plugins/LanguageAPI/export"));
  }

  @Override
  public CompletableFuture<Boolean> exportLanguage(@NotNull Locale locale, @NotNull Path path) {
    Preconditions.checkNotNull(path, "path");
    Preconditions.checkNotNull(locale, "locale");

    return CompletableFuture.supplyAsync(() -> {
      if (!LanguageAPI.getInstance().getLocaleHandler().isAvailable(locale)) {
        return false;
      }
      Map<Identifier, String> translations = new HashMap<>();
      String languageTag = locale.toLanguageTag().toUpperCase();

      translations.put(Identifier.of("locale"), languageTag);
      translations.putAll(LanguageAPI.getInstance().getMessageHandler().getMessages(locale, false).join());

      Document outputDocument = Documents.newDocument("languageAPI", translations);
      Path outputPath = path.resolve(languageTag + ".yml");

      try {
        Files.createDirectories(path);
      } catch (IOException exception) {
        exception.printStackTrace();
        return false;
      }
      outputDocument.yaml().write(outputPath);
      return true;
    });
  }
}
