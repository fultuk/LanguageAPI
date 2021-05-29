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

package de.tentact.languageapi.message;

import de.tentact.languageapi.cache.CacheProvider;
import de.tentact.languageapi.cache.LanguageCache;
import de.tentact.languageapi.language.LocaleHandler;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public abstract class DefaultMessageHandler implements MessageHandler {

  protected final LocaleHandler localeHandler;
  protected final LanguageCache<String, Map<String, String>> translationCache;
  protected final LanguageCache<String, Identifier> identifierCache;

  public DefaultMessageHandler(LocaleHandler localeHandler, CacheProvider cacheProvider) {
    this.localeHandler = localeHandler;
    this.translationCache = cacheProvider.newCache();
    this.identifierCache = cacheProvider.newCache();
  }

  @Override
  public CompletableFuture<Map<Identifier, String>> getMessages(Locale locale) {
    return this.getIdentifier(locale, false).thenApplyAsync(identifiers -> {
      Map<Identifier, String> translations = new HashMap<>();
      for (Identifier identifier : identifiers) {
        translations.put(identifier, this.getMessage(identifier, locale));
      }
      return translations;
    });
  }

  @Override
  public Identifier loadIdentifier(Identifier identifier) {
    return this.identifierCache.getIfPresent(identifier.getTranslationKey());
  }

  @Override
  public CompletableFuture<Identifier> loadIdentifierAsync(Identifier identifier) {
    return CompletableFuture.supplyAsync(() -> this.loadIdentifier(identifier));
  }

  @Override
  public abstract void writeIdentifier(Identifier identifier);

  @Override
  public Message getMessage(Identifier identifier) {
    return new DefaultMessage(identifier);
  }

  @Override
  public String getMessage(Identifier identifier, Locale locale) {
    Map<String, String> cacheMap = this.translationCache.getIfPresent(identifier.getTranslationKey());
    if (cacheMap != null && cacheMap.containsKey(identifier.getTranslationKey())) {
      return cacheMap.get(locale.toLanguageTag().toUpperCase());
    }
    return null;
  }

  @Override
  public CompletableFuture<String> getMessageAsync(Identifier identifier, Locale locale) {
    return CompletableFuture.supplyAsync(() -> this.getMessage(identifier, locale));
  }

  protected void cacheTranslation(Identifier identifier, Locale locale, String translation) {
    Map<String, String> cacheMap = this.translationCache.getIfPresent(identifier.getTranslationKey());
    if (cacheMap == null) {
      cacheMap = new HashMap<>(1);
    }
    cacheMap.put(locale.toLanguageTag().toUpperCase(), translation);
    this.translationCache.put(identifier.getTranslationKey(), cacheMap);
  }

  protected void cacheIdentifier(Identifier identifier) {
    this.identifierCache.put(identifier.getTranslationKey(), identifier);
  }

  protected void cacheIdentifier(Set<Identifier> identifiers) {
    this.identifierCache.putAll(identifiers.stream().collect(Collectors.toMap(Identifier::getTranslationKey,
        identifier -> identifier)));
  }

  protected String translateColor(String message) {
    char[] b = message.toCharArray();
    for (int i = 0; i < b.length - 1; i++) {
      if (b[i] == '&' && "0123456789AaBbCcDdEeFfKkLlMmNnOoRr".indexOf(b[i + 1]) > -1) {
        b[i] = '§';
        b[i + 1] = Character.toLowerCase(b[i + 1]);
      }
    }
    return new String(b);
  }
}
