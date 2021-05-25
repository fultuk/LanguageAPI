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

package de.tentact.languageapi.entity;

import de.tentact.languageapi.cache.CacheProvider;
import de.tentact.languageapi.cache.LanguageCache;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public abstract class DefaultEntityHandler implements EntityHandler {

  protected final LanguageCache<UUID, LanguageOfflineEntity> offlineEntityCache;
  protected final LanguageCache<UUID, LanguageEntity> entityCache;

  public DefaultEntityHandler(CacheProvider cacheProvider) {
    this.offlineEntityCache = cacheProvider.newCache();
    this.entityCache = cacheProvider.newPersistenceCache();
  }

  @Override
  public @Nullable LanguageEntity getLanguageEntity(UUID entityId) {
    return this.entityCache.getIfPresent(entityId);
  }

  @Override
  public CompletableFuture<LanguageOfflineEntity> getOfflineLanguageEntity(UUID entityId) {
    return null;
  }

  @Override
  public void updateLanguageEntity(LanguageOfflineEntity languageOfflineEntity) {

  }

  @Override
  public LanguageOfflineEntity registerEntity(UUID entityId) {
    return null;
  }

  @Override
  public LanguageOfflineEntity registerEntity(UUID entityId, Locale language) {
    return null;
  }

  @Override
  public abstract ConsoleEntity getConsoleEntity();
}
