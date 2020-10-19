package de.tentact.languageapi.configuration;
/*  Created in the IntelliJ IDEA.
    Copyright(c) 2020
    Created by 0utplay | Aldin Sijamhodzic
    Datum: 07.08.2020
    Uhrzeit: 11:26
*/

import java.util.List;

public class LanguageItem {

    private final String headValue;
    private final String displayName;
    private final String languageName;
    private final int inventorySlot;
    private final List<String> lore;

    public LanguageItem(String headValue, String displayName, String languageName, int inventorySlot, List<String> lore) {
        this.headValue = headValue;
        this.displayName = displayName;
        this.languageName = languageName;
        this.inventorySlot = inventorySlot;
        this.lore = lore;
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
}
