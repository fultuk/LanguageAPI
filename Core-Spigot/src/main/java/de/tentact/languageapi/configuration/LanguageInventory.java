package de.tentact.languageapi.configuration;
/*  Created in the IntelliJ IDEA.
    Copyright(c) 2020
    Created by 0utplay | Aldin Sijamhodzic
    Datum: 07.08.2020
    Uhrzeit: 15:44
*/

import de.tentact.languageapi.util.I18N;
import de.tentact.languageapi.util.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class LanguageInventory {

    private final LanguageInventoryConfiguration languageInventoryConfiguration;

    public LanguageInventory(LanguageInventoryConfiguration languageInventoryConfiguration) {
        this.languageInventoryConfiguration = languageInventoryConfiguration;
    }

    public Inventory getLanguageInventory() {
        if (this.languageInventoryConfiguration == null) {
            return null;
        }
        if (!this.languageInventoryConfiguration.isUseInventory()) {
            return null;
        }
        Inventory inventory = Bukkit.createInventory(null, this.languageInventoryConfiguration.getInventorySize(), this.languageInventoryConfiguration.getName());

        ItemStack fillItem = new ItemBuilder(this.languageInventoryConfiguration.getFillItemMaterial()).build();
        for (int i = 0; i < inventory.getSize(); i++) {
            inventory.setItem(i, fillItem);
        }

        for (LanguageItem languageItem : this.languageInventoryConfiguration.getLanguages()) {
            ItemStack itemStack = ItemBuilder.buildSkull(languageItem.getHeadValue(), languageItem.getDisplayName());
            inventory.setItem(languageItem.getInventorySlot(), itemStack);
        }

        return inventory;

    }

    public LanguageInventoryConfiguration getLanguageInventoryConfiguration() {
        return this.languageInventoryConfiguration;
    }
}
