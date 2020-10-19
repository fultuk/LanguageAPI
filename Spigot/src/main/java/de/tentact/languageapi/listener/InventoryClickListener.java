package de.tentact.languageapi.listener;
/*  Created in the IntelliJ IDEA.
    Copyright(c) 2020
    Created by 0utplay | Aldin Sijamhodzic
    Datum: 07.08.2020
    Uhrzeit: 15:36
*/

import de.tentact.languageapi.LanguageAPI;
import de.tentact.languageapi.LanguageSpigot;
import de.tentact.languageapi.configuration.LanguageInventory;
import de.tentact.languageapi.configuration.LanguageInventoryConfiguration;
import de.tentact.languageapi.i18n.I18N;
import de.tentact.languageapi.player.PlayerExecutor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

public class InventoryClickListener implements Listener {

    private final LanguageInventory languageInventory;
    private final LanguageInventoryConfiguration languageInventoryConfiguration;
    private final LanguageAPI languageAPI = LanguageAPI.getInstance();
    private final PlayerExecutor playerExecutor = languageAPI.getPlayerExecutor();

    public InventoryClickListener(LanguageSpigot languageSpigot, LanguageInventory languageInventory) {
        this.languageInventory = languageInventory;
        this.languageInventoryConfiguration = languageInventory.getLanguageInventoryConfiguration();
        Bukkit.getPluginManager().registerEvents(this, languageSpigot);
    }

    @EventHandler
    public void handleInventoryClick(InventoryClickEvent event) {
        Inventory inventory = event.getInventory();
        ItemStack currentItem = event.getCurrentItem();

        if(currentItem != null && event.getWhoClicked() instanceof Player) {
            int clickedSlot = event.getSlot();
            if(!this.languageInventory.getLanguageInventory().equals(inventory)) {
                return;
            }
            event.setCancelled(true);
            this.languageInventoryConfiguration.getLanguages()
                    .stream()
                    .filter(languageItem -> languageItem.getInventorySlot() == clickedSlot)
                    .filter(languageItem -> languageAPI.isLanguage(languageItem.getLanguageName().toLowerCase()))
                    .findFirst()
                    .ifPresent(languageItem -> {
                        Player player = (Player) event.getWhoClicked();
                        playerExecutor.setPlayerLanguage(player.getUniqueId(), languageItem.getLanguageName());
                        Objects.requireNonNull(playerExecutor.getLanguagePlayer(player.getUniqueId())).sendMessage(I18N.LANGUAGEAPI_PLAYER_SELECTED_LANGUAGE.get().replace("%LANGUAGE%", languageItem.getLanguageName()));
                        player.closeInventory();
                    });
        }
    }
}
