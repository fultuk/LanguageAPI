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

import java.util.List;

public class LanguageItem {

    private final String material;
    private final String headValue;
    private final String displayName;
    private final String languageName;
    private final int inventorySlot;
    private final List<String> lore;

    public LanguageItem(String material,String headValue, String displayName, String languageName, int inventorySlot, List<String> lore) {
        this.headValue = headValue;
        this.displayName = displayName;
        this.languageName = languageName;
        this.inventorySlot = inventorySlot;
        this.lore = lore;
        this.material = material;
    }

    public String getHeadValue() {
        return this.headValue;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public String getLanguageName() {
        return this.languageName;
    }

    public List<String> getLore() {
        return this.lore;
    }

    public int getInventorySlot() {
        return this.inventorySlot;
    }

    public String getMaterial() {
        return this.material;
    }
}
