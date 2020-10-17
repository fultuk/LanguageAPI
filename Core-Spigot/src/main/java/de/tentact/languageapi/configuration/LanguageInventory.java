package de.tentact.languageapi.configuration;
/*  Created in the IntelliJ IDEA.
    Copyright(c) 2020
    Created by 0utplay | Aldin Sijamhodzic
    Datum: 07.08.2020
    Uhrzeit: 15:44
*/

import de.tentact.languageapi.util.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class LanguageInventory {

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
