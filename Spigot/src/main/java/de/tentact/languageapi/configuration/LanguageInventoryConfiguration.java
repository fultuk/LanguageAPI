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

import de.tentact.languageapi.util.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;

public class LanguageInventoryConfiguration {

    private final String name;
    private final String fillItemMaterial;
    private final byte subId;
    private final Collection<LanguageItem> languages;
    private final boolean useInventory;
    private transient Inventory inventory;

    public LanguageInventoryConfiguration(boolean useInventory, String name, String fillItemMaterial, byte subId, Collection<LanguageItem> languages) {
        this.useInventory = useInventory;
        this.name = name;
        this.fillItemMaterial = fillItemMaterial;
        this.subId = subId;
        this.languages = languages;
    }

    public boolean isUseInventory() {
        return this.useInventory;
    }

    public String getName() {
        return this.name;
    }

    public Material getFillItemMaterial() {
        return Material.getMaterial(this.fillItemMaterial);
    }

    public byte getSubId() {
        return this.subId;
    }

    public Collection<LanguageItem> getLanguages() {
        return this.languages;
    }

    private int calculateRows() {
        return (int) Math.ceil(this.languages.size() / 9.0);
    }

    public int getInventorySize() {
        return this.calculateRows() * 9;
    }

    public Inventory getLanguageInventory() {
        if (!this.useInventory) {
            return null;
        }
        if (this.inventory != null) {
            return this.inventory;
        }
        Inventory inventory = Bukkit.createInventory(null, this.getInventorySize(), this.name);

        ItemBuilder builder = new ItemBuilder(this.getFillItemMaterial());
        if (this.subId != -1) {
            builder.setSubId(this.subId);
        }
        for (int i = 0; i < inventory.getSize(); i++) {
            inventory.setItem(i, builder.build());
        }
        for (LanguageItem languageItem : this.languages) {
            ItemStack itemStack = ItemBuilder.buildSkull(languageItem.getMaterial(), languageItem.getHeadValue(),
                    languageItem.getDisplayName(), languageItem.getLore());
            inventory.setItem(languageItem.getInventorySlot(), itemStack);
        }
        this.inventory = inventory;
        return inventory;
    }
}
