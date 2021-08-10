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
package de.tentact.languageapi.cache

import de.tentact.languageapi.database.RedisDatabaseProvider
import org.redisson.api.RMap
import java.util.*
import java.util.concurrent.TimeUnit

open class RedisCache<K, V>(redisDatabaseProvider: RedisDatabaseProvider) : LanguageCache<K, V> {
  protected val cacheName = UUID.randomUUID().toString().split("-").first()
  protected var backingMap: RMap<K, V>

  override fun put(key: K, value: V) {
    backingMap.fastPut(key, value)
  }

  override fun putAll(map: Map<K, V>) {
    backingMap.putAll(map)
  }

  override fun getIfPresent(key: K): V? {
    return backingMap[key]
  }

  override fun invalidate(key: K) {
    backingMap.fastRemove(key)
  }

  override fun getValues(): Collection<V> {
    return backingMap.values
  }

  override fun asMap(): Map<K, V> {
    return backingMap.readAllMap()
  }

  override fun clear() {
    backingMap.clear()
  }

  class PersistentRedisCache<K, V>(redisDatabaseProvider: RedisDatabaseProvider) : RedisCache<K, V>(redisDatabaseProvider), LanguageCache<K, V> {
    init {
      super.backingMap = redisDatabaseProvider.redissonClient.getMap(super.cacheName)
    }
  }

  init {
    val cache = redisDatabaseProvider.redissonClient.getMapCache<K, V>(cacheName)
    cache.expire(1L, TimeUnit.HOURS)
    backingMap = cache
  }
}
