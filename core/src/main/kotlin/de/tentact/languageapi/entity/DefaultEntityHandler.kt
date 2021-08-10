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
package de.tentact.languageapi.entity

import de.tentact.languageapi.LanguageAPI
import de.tentact.languageapi.cache.CacheProvider
import de.tentact.languageapi.cache.LanguageCache
import de.tentact.languageapi.registry.ServiceRegistry
import java.util.*
import java.util.concurrent.CompletableFuture

abstract class DefaultEntityHandler(cacheProvider: CacheProvider) : EntityHandler {

  private val offlineEntityCache: LanguageCache<UUID, LanguageOfflineEntity>
  private val entityCache: LanguageCache<UUID, LanguageEntity>
  override fun getLanguageEntity(entityId: UUID): LanguageEntity? {
    return entityCache.getIfPresent(entityId)
  }

  override fun loginEntity(languageEntity: LanguageEntity) {
    entityCache.put(languageEntity.entityId, languageEntity)
  }

  override fun logoutEntity(entityId: UUID) {
    val cachedEntity = entityCache.getIfPresent(entityId)
    if (cachedEntity != null) {
      updateLanguageEntity(cachedEntity).thenAccept { entityCache.invalidate(entityId) }
    } else {
      entityCache.invalidate(entityId)
    }
  }

  abstract override fun getOfflineLanguageEntity(entityId: UUID): CompletableFuture<LanguageOfflineEntity>
  abstract override fun updateLanguageEntity(languageOfflineEntity: LanguageOfflineEntity): CompletableFuture<Void>
  override fun registerEntity(entityId: UUID): LanguageOfflineEntity {
    return this.registerEntity(entityId, LanguageAPI.getInstance().languageConfiguration.defaultLocale)
  }

  override fun registerEntity(entityId: UUID, locale: Locale): LanguageOfflineEntity {
    val languageOfflineEntity: LanguageOfflineEntity = DefaultLanguageOfflineEntity(entityId, locale)
    updateLanguageEntity(languageOfflineEntity)
    return languageOfflineEntity
  }

  override fun getConsoleEntity(): ConsoleEntity {
    return ServiceRegistry.getService(ConsoleEntity::class.java)
  }

  protected fun cacheLanguageEntity(languageOfflineEntity: LanguageOfflineEntity) {
    if (languageOfflineEntity is LanguageEntity) {
      entityCache.put(languageOfflineEntity.getEntityId(), languageOfflineEntity)
    } else {
      offlineEntityCache.put(languageOfflineEntity.entityId, languageOfflineEntity)
    }
  }

  protected fun getCachedEntity(entityId: UUID): LanguageOfflineEntity? {
    val cachedEntity = offlineEntityCache.getIfPresent(entityId)
    return cachedEntity ?: entityCache.getIfPresent(entityId)
  }

  init {
    offlineEntityCache = cacheProvider.newCache()
    entityCache = cacheProvider.newPersistentCache()
  }
}
