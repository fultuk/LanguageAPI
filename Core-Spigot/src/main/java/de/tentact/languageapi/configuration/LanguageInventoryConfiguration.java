package de.tentact.languageapi.configuration;
/*  Created in the IntelliJ IDEA.
    Copyright(c) 2020
    Created by 0utplay | Aldin Sijamhodzic
    Datum: 07.08.2020
    Uhrzeit: 11:24
*/


import org.bukkit.Material;

import java.util.Collection;

public class LanguageInventoryConfiguration {

    private final String name;
    private final String fillItemMaterial;
    private final byte subid;
    private final Collection<LanguageItem> languages;
    private final boolean useInventory;

    public LanguageInventoryConfiguration(boolean useInventory, String name, String fillItemMaterial, byte subid, Collection<LanguageItem> languages) {
        this.useInventory = useInventory;
        this.name = name;
        this.fillItemMaterial = fillItemMaterial;
        this.subid = subid;
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

    public byte getSubid() {
        return this.subid;
    }

    public Collection<LanguageItem> getLanguages() {
        return this.languages;
    }

    private int calculateRows() {
        return (int) Math.ceil(languages.size() / 9.0);
    }

    public int getInventorySize() {
        return this.calculateRows() * 9;
    }
}
