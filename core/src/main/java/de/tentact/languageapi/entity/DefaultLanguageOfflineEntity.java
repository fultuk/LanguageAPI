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

import com.google.common.base.Preconditions;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

public class DefaultLanguageOfflineEntity implements LanguageOfflineEntity {

  private final UUID entityId;
  private Locale locale;

  public DefaultLanguageOfflineEntity(UUID entityId, Locale locale) {
    this.entityId = entityId;
    this.locale = locale;
  }

  @Override
  public @NotNull UUID getEntityId() {
    return this.entityId;
  }

  @Override
  public @NotNull Locale getLocale() {
    return this.locale;
  }

  @Override
  public void setLocale(@NotNull Locale locale) {
    Preconditions.checkNotNull(locale, "locale");
    this.locale = locale;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof DefaultLanguageOfflineEntity)) return false;
    DefaultLanguageOfflineEntity that = (DefaultLanguageOfflineEntity) o;
    return this.entityId.equals(that.entityId) && this.locale.equals(that.locale);
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.entityId, this.locale);
  }
}
