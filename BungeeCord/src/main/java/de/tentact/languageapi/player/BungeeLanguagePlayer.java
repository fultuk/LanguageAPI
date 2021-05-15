/*
 * MIT License
 *
 * Copyright (c) 2020 0utplay (Aldin Sijamhodzic)
 * Copyright (c) 2020 contributors
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

package de.tentact.languageapi.player;

import de.tentact.languageapi.LanguageAPI;
import de.tentact.languageapi.i18n.Translation;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.jetbrains.annotations.NotNull;

public class BungeeLanguagePlayer extends DefaultLanguageOfflinePlayer implements LanguagePlayer {

    private final ProxiedPlayer proxiedPlayer;
    private final LanguageAPI languageAPI = LanguageAPI.getInstance();

    public BungeeLanguagePlayer(ProxiedPlayer proxiedPlayer) {
        super(proxiedPlayer.getUniqueId());
        this.proxiedPlayer = proxiedPlayer;
    }

    @Override
    public void sendMessage(@NotNull Translation translation) {
        if (this.proxiedPlayer == null) {
            return;
        }
        super.getLanguageAsync().thenCompose(translation::getMessageAsync)
                .thenAccept(message -> this.proxiedPlayer.sendMessage(TextComponent.fromLegacyText(message)));
    }

    @Override
    public void sendMessage(String translationKey) {
        this.sendMessage(this.languageAPI.getTranslation(translationKey));
    }

    @Override
    public void sendMultipleTranslation(@NotNull String multipleTranslationKey, String prefixKey) {
        if (this.proxiedPlayer == null) {
            return;
        }
        super.getLanguageAsync().thenCompose(language ->
                this.languageAPI.getMultipleMessagesAsync(multipleTranslationKey, language, prefixKey))
                .thenAccept(messages -> messages
                        .forEach(message -> this.proxiedPlayer.sendMessage(TextComponent.fromLegacyText(message))));
    }

    @Override
    public void kickPlayer(Translation translation) {
        if (this.proxiedPlayer == null) {
            return;
        }
        this.proxiedPlayer.disconnect(TextComponent.fromLegacyText(translation.getMessage(this.getLanguage())));
    }
}
