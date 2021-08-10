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
package de.tentact.languageapi.message

import de.tentact.languageapi.LanguageAPI
import de.tentact.languageapi.cache.CacheProvider
import de.tentact.languageapi.cache.LanguageCache
import de.tentact.languageapi.language.LocaleHandler
import java.util.*
import java.util.concurrent.CompletableFuture

abstract class DefaultMessageHandler(protected val localeHandler: LocaleHandler, cacheProvider: CacheProvider) : MessageHandler {
  protected val translationCache: LanguageCache<Locale, MutableMap<Identifier, String>>
  protected val identifierCache: LanguageCache<String, Identifier>

  override fun loadIdentifierAsync(identifier: Identifier): CompletableFuture<Identifier> {
    return CompletableFuture.supplyAsync { loadIdentifier(identifier) }
  }

  abstract override fun writeIdentifier(identifier: Identifier)
  override fun getMessage(identifier: Identifier): Message {
    return DefaultMessage(identifier)
  }

  override fun getMessage(identifier: Identifier, prefixMessage: Message): Message {
    return DefaultMessage(identifier, prefixMessage)
  }

  override fun getMessage(identifier: Identifier, prefixIdentifier: Identifier): Message {
    return DefaultMessage(identifier, prefixIdentifier)
  }

  override fun getMessageAsync(identifier: Identifier, locale: Locale): CompletableFuture<String> {
    return CompletableFuture.supplyAsync { this.getMessage(identifier, locale) }
  }

  override fun translateMessage(identifier: Identifier, translation: String, replaceIfExists: Boolean) {
    this.translateMessage(identifier, LanguageAPI.getInstance().languageConfiguration.defaultLocale, translation, replaceIfExists)
  }

  protected fun cacheTranslation(identifier: Identifier, locale: Locale, translation: String) {
    val cacheMap: MutableMap<Identifier, String> = translationCache.getIfPresent(locale) ?: HashMap(1)
    cacheMap[identifier] = translation
    translationCache.put(locale, cacheMap)
  }

  protected fun cacheIdentifier(identifier: Identifier) {
    identifierCache.put(identifier.translationKey, identifier)
  }

  protected fun cacheIdentifier(identifiers: Set<Identifier>) {
    identifierCache.putAll(identifiers.associateBy { it.translationKey })

  }

  protected fun translateColor(message: String): String {
    val b = message.toCharArray()
    for (i in 0 until b.size - 1) {
      if (b[i] == '&' && "0123456789AaBbCcDdEeFfKkLlMmNnOoRr".indexOf(b[i + 1]) > -1) {
        b[i] = '§'
        b[i + 1] = b[i + 1].lowercaseChar()
      }
    }
    return String(b)
  }

  init {
    translationCache = cacheProvider.newCache()
    identifierCache = cacheProvider.newCache()
  }
}
