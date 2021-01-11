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

package de.tentact.languageapi.api;

import de.tentact.languageapi.LanguageAPI;
import de.tentact.languageapi.i18n.Translation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

public class DefaultTranslation implements Translation {

    private final String translationKey;
    private Translation prefixTranslation = null;
    private final LanguageAPI languageAPI = LanguageAPI.getInstance();
    private final Map<String, String> params;

    public DefaultTranslation(@NotNull String translationKey) {
        this.translationKey = translationKey;
        this.params = new HashMap<>();
    }

    @NotNull
    @Override
    public String getMessage() {
        return this.getMessage(this.languageAPI.getDefaultLanguage());
    }

    @NotNull
    @Override
    public String getMessage(@NotNull UUID playerUUID) {
        return this.getMessage(this.languageAPI.getPlayerExecutor().getPlayerLanguage(playerUUID));
    }

    @NotNull
    @Override
    public String getMessage(@NotNull String language, boolean orElseDefault) {
        String prefix = "";

        if (this.hasPrefixTranslation()) {
            prefix = this.prefixTranslation.getMessage(language, orElseDefault);
        }
        AtomicReference<String> message = new AtomicReference<>(this.languageAPI.getMessage(this.translationKey, language));
        this.params.forEach((key, value) -> message.set(message.get().replace(key, value)));
        this.params.clear();
        return prefix + message;
    }

    @Override
    public String getParameter() {
        return this.languageAPI.getParameter(this.translationKey);
    }

    @Override
    public @NotNull Translation setPrefixTranslation(Translation prefixTranslation) {
        this.prefixTranslation = prefixTranslation;
        this.languageAPI.updateTranslation(this);
        return this;
    }

    @Override
    public @Nullable Translation getPrefixTranslation() {
        return this.prefixTranslation;
    }

    @Override
    public boolean hasPrefix() {
        return this.prefixTranslation != null;
    }

    @Override
    public @NotNull Translation replace(String old, String replacement) {
        this.params.put(old, replacement);
        return this;
    }

    @Override
    public @NotNull String getTranslationKey() {
        return this.translationKey;
    }

    @Override
    public @NotNull Translation createDefaults(String message) {
        this.languageAPI.addMessageToDefault(this.translationKey, message);
        return this;
    }

    @Override
    public @NotNull Translation createDefaults(String message, String param) {
        this.languageAPI.addMessageToDefault(this.translationKey, message, param);
        return this;
    }

    @Override
    public @NotNull Translation addTranslation(String language, String message) {
        return this.addTranslation(language, message, null);
    }

    @Override
    public @NotNull Translation addTranslation(String language, String message, String param) {
        this.languageAPI.addMessage(this.translationKey, message, language, param);
        return this;
    }

    private boolean hasPrefixTranslation() {
        return this.prefixTranslation != null;
    }
}
