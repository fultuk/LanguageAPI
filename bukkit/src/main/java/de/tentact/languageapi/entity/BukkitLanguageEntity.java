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
import de.tentact.languageapi.message.Message;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;
import java.util.UUID;

public class BukkitLanguageEntity extends DefaultLanguageOfflineEntity implements LanguageEntity {

  private final Player bukkitPlayer;

  public BukkitLanguageEntity(LanguageOfflineEntity languageOfflineEntity, Player player) {
    this(languageOfflineEntity.getEntityId(), languageOfflineEntity.getLocale(), player);
  }

  public BukkitLanguageEntity(UUID entityId, Locale locale, Player player) {
    super(entityId, locale);
    this.bukkitPlayer = player;
  }

  @Override
  public void sendMessage(@NotNull Message translation, Object... parameters) {
    Preconditions.checkNotNull(translation, "translation");
    Preconditions.checkNotNull(parameters, "parameters");

    translation.buildAsync(super.locale, parameters).thenAccept(this.bukkitPlayer::sendMessage);
  }
}
