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
package de.tentact.languageapi.file

import com.github.derrop.documents.Documents
import de.tentact.languageapi.LanguageAPI
import de.tentact.languageapi.message.Identifier
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*
import java.util.concurrent.CompletableFuture

class DefaultFileHandler : FileHandler {
  override fun loadFile(path: Path): CompletableFuture<Void> {
    return this.loadFile(path, false)
  }

  override fun loadFile(path: Path, overwrite: Boolean): CompletableFuture<Void> {
    return CompletableFuture.supplyAsync {
      val inputDocument = Documents.yamlStorage().read(path)
      val uncheckedMessages = inputDocument["languageAPI"] as Map<Identifier, String>
      val localeIdentifier = Identifier.of("locale")
      val locale = Locale.forLanguageTag(uncheckedMessages[localeIdentifier])
      LanguageAPI.getInstance().messageHandler.translateMessage(uncheckedMessages, locale, overwrite)
      null
    }
  }

  override fun exportLanguage(locale: Locale): CompletableFuture<Boolean> {
    return this.exportLanguage(locale, Paths.get("plugins/LanguageAPI/export"))
  }

  override fun exportLanguage(locale: Locale, path: Path): CompletableFuture<Boolean> {
    return CompletableFuture.supplyAsync {
      if (!LanguageAPI.getInstance().localeHandler.isAvailable(locale)) {
        return@supplyAsync false
      }
      val translations: MutableMap<Identifier, String> = HashMap()
      val languageTag = locale.toLanguageTag().uppercase(Locale.getDefault())
      translations[Identifier.of("locale")] = languageTag
      translations.putAll(LanguageAPI.getInstance().messageHandler.getMessages(locale, false).join())
      val outputDocument = Documents.newDocument("languageAPI", translations)
      val outputPath = path.resolve("$languageTag.yml")
      try {
        Files.createDirectories(path)
      } catch (exception: IOException) {
        exception.printStackTrace()
        return@supplyAsync false
      }
      outputDocument.yaml().write(outputPath)
      true
    }
  }
}
