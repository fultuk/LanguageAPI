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

import de.tentact.languageapi.database.RedisDatabaseProvider;
import org.jetbrains.annotations.NotNull;
import org.redisson.api.RMap;
import org.redisson.api.RMapCache;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class RedisCache<K, V> implements LanguageCache<K, V> {

  protected final RedisDatabaseProvider redisDatabaseProvider;
  protected final String cacheName = UUID.randomUUID().toString().split("-")[0];
  protected RMap<K, V> backingMap;

  public RedisCache(RedisDatabaseProvider redisDatabaseProvider) {
    this.redisDatabaseProvider = redisDatabaseProvider;

    RMapCache<K, V> cache = redisDatabaseProvider.getRedissonClient().getMapCache(this.cacheName);
    cache.expire(1L, TimeUnit.HOURS);

    this.backingMap = cache;
  }

  @Override
  public void put(K key, V value) {
    this.backingMap.fastPut(key, value);
  }

  @Override
  public void putAll(Map<K, V> map) {
    this.backingMap.putAll(map);
  }

  @Override
  public V getIfPresent(K key) {
    return this.backingMap.get(key);
  }

  @Override
  public void invalidate(K key) {
    this.backingMap.remove(key);
  }

  @Override
  public @NotNull Collection<V> getValues() {
    return this.backingMap.values();
  }

  @Override
  public @NotNull Map<K, V> asMap() {
    return this.backingMap.readAllMap();
  }

  @Override
  public void clear() {
    this.backingMap.clear();
  }

  public static class PersistentRedisCache<K, V> extends RedisCache<K, V> implements LanguageCache<K, V> {

    public PersistentRedisCache(RedisDatabaseProvider redisDatabaseProvider) {
      super(redisDatabaseProvider);
      super.backingMap = redisDatabaseProvider.getRedissonClient().getMap(super.cacheName);
    }
  }
}
