package de.tentact.languageapi.util;
/*  Created in the IntelliJ IDEA.
    Copyright(c) 2020
    Created by 0utplay | Aldin Sijamhodzic
    Datum: 07.08.2020
    Uhrzeit: 11:49
*/

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;
import java.util.List;
import java.util.UUID;

public class ItemBuilder {

    private final ItemStack itemStack;

    public ItemBuilder(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    public ItemBuilder(Material material, int amount) {
        this(new ItemStack(material, amount));
    }

    public ItemBuilder(Material material) {
        this(material, 1);
    }

    public ItemBuilder setSubId(byte subId) {
        this.itemStack.setDurability(subId);
        return this;
    }

    public ItemStack build() {
        return this.itemStack;
    }

    public static ItemStack buildSkull(String value, String displayName, List<String> lore) {
        ItemStack itemStack = new ItemStack(Material.LEGACY_SKULL_ITEM, 1, (byte) 3);
        itemStack.setLore(lore);
        SkullMeta skullMeta = (SkullMeta) itemStack.getItemMeta();
        skullMeta.setDisplayName(displayName);


        GameProfile gameProfile = new GameProfile(UUID.randomUUID(), "");
        gameProfile.getProperties().put("textures", new Property("textures", value));

        Field profile;
        try {
            profile = skullMeta.getClass().getDeclaredField("profile");
            profile.setAccessible(true);
            profile.set(skullMeta, gameProfile);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }

        itemStack.setItemMeta(skullMeta);

        return itemStack;
    }
}
