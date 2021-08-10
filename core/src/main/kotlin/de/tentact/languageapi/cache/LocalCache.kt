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

import com.google.common.cache.Cache
import com.google.common.cache.CacheBuilder
import java.time.Duration

class LocalCache<K, V> @JvmOverloads constructor(
  private val localCache: Cache<K, V> = CacheBuilder.newBuilder()
    .concurrencyLevel(4)
    .expireAfterWrite(Duration.ofMinutes(5L))
    .build()
) : LanguageCache<K, V> {
  override fun put(key: K, value: V) {
    localCache.put(key, value)
  }

  override fun putAll(map: Map<K, V>) {
    localCache.putAll(map)
  }

  override fun getIfPresent(key: K): V? {
    return localCache.getIfPresent(key)
  }

  override fun invalidate(key: K) {
    localCache.invalidate(key)
  }

  override fun getValues(): Collection<V> {
    return localCache.asMap().values
  }

  override fun asMap(): Map<K, V> {
    return localCache.asMap()
  }

  override fun clear() {
    localCache.cleanUp()
  }
}
