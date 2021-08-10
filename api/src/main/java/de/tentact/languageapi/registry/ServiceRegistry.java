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

package de.tentact.languageapi.registry;

import de.tentact.languageapi.LanguageAPI;
import org.jetbrains.annotations.Nullable;

public interface ServiceRegistry {

  /**
   * @param service the corresponding class to the searched service
   * @param <T>     the type of the service
   * @return the searched service
   */
  static <T> T getService(Class<T> service) {
    return LanguageAPI.getInstance().getServiceRegistry().getProvider(service);
  }

  /**
   * @param service the corresponding class to the searched service
   * @param <T>     the type of the service
   * @return the instance of the searched service
   */
  @Nullable <T> T getProvider(Class<T> service);

  /**
   * Registers a service in the registry
   *
   * @param service  the corresponding class to the searched service
   * @param provider the instance of the given service
   */
  <T> void setProvider(Class<T> service, T provider);

  /**
   * Unregisters the instance of the given service
   *
   * @param service the service to be unregistered
   */
  <T> void unregister(Class<T> service);

}
