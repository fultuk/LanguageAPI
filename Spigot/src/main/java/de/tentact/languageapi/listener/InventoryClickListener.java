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

package de.tentact.languageapi.listener;

import de.tentact.languageapi.LanguageAPI;
import de.tentact.languageapi.LanguageSpigot;
import de.tentact.languageapi.configuration.LanguageInventory;
import de.tentact.languageapi.configuration.LanguageInventoryConfiguration;
import de.tentact.languageapi.i18n.I18N;
import de.tentact.languageapi.player.LanguagePlayer;
import de.tentact.languageapi.player.PlayerExecutor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class InventoryClickListener implements Listener {

    private final LanguageInventory languageInventory;
    private final LanguageInventoryConfiguration languageInventoryConfiguration;
    private final PlayerExecutor playerExecutor = LanguageAPI.getInstance().getPlayerExecutor();

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
                    .filter(languageItem -> LanguageAPI.getInstance().isLanguage(languageItem.getLanguageName().toLowerCase()))
                    .findFirst()
                    .ifPresent(languageItem -> {
                        Player player = (Player) event.getWhoClicked();
                        player.closeInventory();
                        this.playerExecutor.setPlayerLanguage(player.getUniqueId(), languageItem.getLanguageName());
                        LanguagePlayer languagePlayer = this.playerExecutor.getLanguagePlayer(player.getUniqueId());
                        //Even if it should never be null here
                        if (languagePlayer != null) {
                            languagePlayer.sendMessage(I18N.LANGUAGEAPI_PLAYER_SELECTED_LANGUAGE.get().replace("%LANGUAGE%", languageItem.getLanguageName()));
                        }
                    });
        }
    }
}
