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
import de.tentact.languageapi.player.SpecificPlayerExecutor;
import de.tentact.languageapi.util.Updater;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinListener implements Listener {

    private final LanguageSpigot languageSpigot;
    private final Updater updater;
    private final LanguageAPI languageAPI = LanguageAPI.getInstance();

    public JoinListener(LanguageSpigot languageSpigot) {
        this.languageSpigot = languageSpigot;
        this.updater = this.languageSpigot.getUpdater();
        Bukkit.getPluginManager().registerEvents(this, languageSpigot);
    }

    @EventHandler
    public void handlePlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        SpecificPlayerExecutor playerExecutor = languageAPI.getSpecificPlayerExecutor(player.getUniqueId());

        if (playerExecutor.isRegisteredPlayer()) {
            playerExecutor.registerPlayer();
        } else {
            if (this.languageSpigot.spigotConfiguration.getLanguageInventory().getLanguageInventoryConfiguration().isUseInventory()) {
                player.performCommand("languageapi");
            } else {
                playerExecutor.registerPlayer();
            }
        }
        if (!player.hasPermission("languageapi.notify")) {
            return;
        }
        if (!this.languageSpigot.spigotConfiguration.getLanguageConfig().getLanguageSetting().isNotify()) {
            return;
        }
        if (!updater.hasUpdate()) {
            return;
        }
        player.sendMessage(LanguageAPI.getInstance().getLanguageAPIPrefix() + "Es ist ein neues Update verfügbar. Aktuelle Version: §6" + updater.getLocalVersion()
                + "§7. Neuste Version: §c" + updater.getOnlineVersion() + "");
    }
}
