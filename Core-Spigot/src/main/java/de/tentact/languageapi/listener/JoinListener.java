package de.tentact.languageapi.listener;
/*  Created in the IntelliJ IDEA.
    Created by 0utplay | Aldin Sijamhodzic
    Datum: 25.04.2020
    Uhrzeit: 19:03
*/

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
            player.performCommand("languageapi");
        }
        if (!this.languageSpigot.spigotConfiguration.getLanguageConfig().getLanguageSetting().isNotify()) {
            return;
        }
        if (!updater.hasUpdate()) {
            return;
        }
        if (!player.hasPermission("languageapi.notify")) {
            return;
        }
        player.sendMessage(LanguageAPI.getInstance().getLanguageAPIPrefix() + "Es ist ein neues Update verfügbar. Aktuelle Version: §6" + updater.getLocalVersion()
                + "§7. Neuste Version: §c" + updater.getOnlineVersion() + "");
    }
}
