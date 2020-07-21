package de.tentact.languageapi.listener;
/*  Created in the IntelliJ IDEA.
    Created by 0utplay | Aldin Sijamhodzic
    Datum: 25.04.2020
    Uhrzeit: 19:03
*/

import de.tentact.languageapi.AbstractLanguageAPI;
import de.tentact.languageapi.LanguageSpigot;
import de.tentact.languageapi.util.ConfigUtil;
import de.tentact.languageapi.util.Updater;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinListener implements Listener {


    LanguageSpigot languageSpigot = LanguageSpigot.getPlugin(LanguageSpigot.class);
    private final Updater updater = languageSpigot.getUpdater();
    private final AbstractLanguageAPI abstractLanguageAPI = AbstractLanguageAPI.getInstance();

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        abstractLanguageAPI.getSpecificPlayerExecutor(player.getUniqueId())
                .registerPlayer(player.getLocale().toLowerCase());

        if(!ConfigUtil.getUpdateNotfication()) {
            return;
        }
        if (!updater.isEnabled()) {
            return;
        }
        if (!updater.hasUpdate()) {
            return;
        }
        if (!player.hasPermission("languageapi.notify")) {
            return;
        }
        player.sendMessage(AbstractLanguageAPI.getInstance().getPrefix() + "Es ist ein neues Update verfügbar. Aktuelle Version: §6" + updater.getLocalVersion()
                + "§7. Neuste Version: §c" + updater.getOnlineVersion() + "");


    }
}
