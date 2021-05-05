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

package de.tentact.languageapi.configuration;

import com.google.gson.annotations.SerializedName;

public class LanguageSetting {

    private final String defaultLanguage;
    private final int cachedTime;
    private final String defaultPrefix;
    @SerializedName("notify")
    private final boolean isNotify;
    private final boolean debugLogging;

    public LanguageSetting(String defaultLanguage, int cacheTime, boolean isNotify, boolean debugLogging) {
        this(defaultLanguage, cacheTime, "&eLanguageAPI x &7", isNotify, debugLogging);
    }

    public LanguageSetting(String defaultLanguage, int cachedTime, String defaultPrefix, boolean isNotify, boolean debugLogging) {
        this.defaultLanguage = defaultLanguage;
        this.cachedTime = cachedTime;
        this.defaultPrefix = defaultPrefix;
        this.isNotify = isNotify;
        this.debugLogging = debugLogging;
    }

    public String getDefaultLanguage() {
        return this.defaultLanguage;
    }

    public int getCachedTime() {
        return this.cachedTime;
    }

    public String getDefaultPrefix() {
        return this.defaultPrefix;
    }

    public boolean isNotify() {
        return this.isNotify;
    }

    public boolean isDebugLogging() {
        return this.debugLogging;
    }
}
