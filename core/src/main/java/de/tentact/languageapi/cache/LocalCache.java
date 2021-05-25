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

package de.tentact.languageapi.cache;


import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import java.time.Duration;
import java.util.Collection;
import java.util.Map;

public class LocalCache<K, V> implements LanguageCache<K, V> {

  private final Cache<K, V> localCache;

  public LocalCache(Cache<K, V> localCache) {
    this.localCache = localCache;
  }

  public LocalCache() {
    this(CacheBuilder.newBuilder().expireAfterWrite(Duration.ofMinutes(5L)).build());
  }

  @Override
  public void put(K key, V value) {
    this.localCache.put(key, value);
  }

  @Override
  public V getIfPresent(K key) {
    return this.localCache.getIfPresent(key);
  }

  @Override
  public void invalidate(K key) {
    this.localCache.invalidate(key);
  }

  @Override
  public Collection<V> getValues() {
    return this.localCache.asMap().values();
  }

  @Override
  public Map<K, V> asMap() {
    return this.localCache.asMap();
  }
}
