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

import de.tentact.languageapi.LanguageAPI;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

final class DefaultIdentifier implements Identifier {

  private final String translationKey;
  private final Map<Integer, String> parameters;

  public DefaultIdentifier(String translationKey) {
    this.translationKey = translationKey.toLowerCase();
    this.parameters = new HashMap<>();
  }

  public DefaultIdentifier(String translationKey, String... parameters) {
    this(translationKey);
    this.parameters(parameters);
  }

  @Override
  public @NotNull Identifier parameters(String... parameters) {
    for (int i = this.parameters.size(); i < parameters.length; i++) {
      this.parameters.put(i, parameters[i]);
    }
    return this;
  }

  @Override
  public @NotNull Identifier load() {
    return LanguageAPI.getInstance().getMessageHandler().loadIdentifier(this);
  }

  @Override
  public @NotNull Identifier write() {
    LanguageAPI.getInstance().getMessageHandler().writeIdentifier(this);
    return this;
  }

  @Override
  public @NotNull Map<Integer, String> getParameters() {
    return this.parameters;
  }

  @Override
  public boolean hasParameters() {
    return !this.parameters.isEmpty();
  }

  @Override
  public @NotNull String getTranslationKey() {
    return this.translationKey;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof DefaultIdentifier)) return false;
    DefaultIdentifier that = (DefaultIdentifier) o;
    return translationKey.equals(that.translationKey);
  }

  @Override
  public int hashCode() {
    return Objects.hash(translationKey);
  }
}
