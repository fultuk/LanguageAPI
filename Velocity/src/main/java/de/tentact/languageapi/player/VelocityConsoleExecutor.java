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

package de.tentact.languageapi.player;

import com.velocitypowered.api.proxy.ConsoleCommandSource;
import de.tentact.languageapi.LanguageAPI;
import de.tentact.languageapi.console.ConsoleExecutor;
import de.tentact.languageapi.i18n.Translation;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;

public class VelocityConsoleExecutor implements ConsoleExecutor {

    private final LanguageAPI languageAPI;
    private final ConsoleCommandSource consoleCommandSource;

    public VelocityConsoleExecutor(LanguageAPI languageAPI, ConsoleCommandSource consoleCommandSource) {
        this.languageAPI = languageAPI;
        this.consoleCommandSource = consoleCommandSource;
    }

    @Override
    public void sendMessage(Translation translation) {
        translation.getMessageAsync().thenAccept(message -> consoleCommandSource.sendMessage(GsonComponentSerializer.colorDownsamplingGson().deserialize(message)));
    }

    @Override
    public void sendMessage(String translationKey) {
        this.sendMessage(this.languageAPI.getTranslation(translationKey));
    }
}
