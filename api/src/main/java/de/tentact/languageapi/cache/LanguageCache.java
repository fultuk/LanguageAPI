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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Map;

public interface LanguageCache<K, V> {

  /**
   * Inserts a value in the backing caches
   *
   * @param key   the key to be set
   * @param value the value to be set
   */
  void put(K key, V value);

  /**
   * Inserts a value in the backing caches
   *
   * @param map a map of keys and values that is set in the backing map
   */
  void putAll(Map<K, V> map);

  /**
   * @param key the key to the value
   * @return the value associated with the given key
   */
  @Nullable V getIfPresent(@NotNull K key);

  /**
   * Invalidates the given key in the backing cache
   *
   * @param key the key to be invalided
   */
  void invalidate(K key);

  /**
   * Retrieves all present values from the backing cache
   *
   * @return all present values from the backing cache
   */
  @NotNull Collection<V> getValues();

  /**
   * Retrieves all present keys and values from the backing cache
   *
   * @return all present keys and values from the backing cache
   */
  @NotNull Map<K, V> asMap();

  /**
   * Clears the whole backing cache
   */
  void clear();

}
