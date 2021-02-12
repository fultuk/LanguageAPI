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
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class LanguageInventory {

    //TODO: merge this with LanguageInventoryConfiguration
    private final LanguageInventoryConfiguration languageInventoryConfiguration;
    private transient Inventory inventory;

    public LanguageInventory(LanguageInventoryConfiguration languageInventoryConfiguration) {
        this.languageInventoryConfiguration = languageInventoryConfiguration;
    }

    public Inventory getLanguageInventory() {
        if(this.inventory != null) {
            return this.inventory;
        }
        if (this.languageInventoryConfiguration == null) {
            return null;
        }
        if (!this.languageInventoryConfiguration.isUseInventory()) {
            return null;
        }
        Inventory inventory = Bukkit.createInventory(null, this.languageInventoryConfiguration.getInventorySize(), this.languageInventoryConfiguration.getName());

        ItemBuilder builder = new ItemBuilder(this.languageInventoryConfiguration.getFillItemMaterial());
        byte subId = this.languageInventoryConfiguration.getSubId();
        if(subId != -1) {
             builder.setSubId(subId);
        }
        for (int i = 0; i < inventory.getSize(); i++) {
            inventory.setItem(i, builder.build());
        }
        for (LanguageItem languageItem : this.languageInventoryConfiguration.getLanguages()) {
            ItemStack itemStack = ItemBuilder.buildSkull(languageItem.getHeadValue(), languageItem.getDisplayName(), languageItem.getLore());
            inventory.setItem(languageItem.getInventorySlot(), itemStack);
        }
        this.inventory = inventory;
        return inventory;
    }

    public LanguageInventoryConfiguration getLanguageInventoryConfiguration() {
        return this.languageInventoryConfiguration;
    }
}
