package de.tentact.languageapi.listener;
/*  Created in the IntelliJ IDEA.
    Created by 0utplay | Aldin Sijamhodzic
    Datum: 06.05.2020
    Uhrzeit: 15:29
*/

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import de.tentact.languageapi.LanguageAPI;
import de.tentact.languageapi.LanguageSpigot;
import de.tentact.languageapi.command.LanguageCommand;
import de.tentact.languageapi.i18n.I18N;
import de.tentact.languageapi.player.LanguagePlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;


@SuppressWarnings("UnstableApiUsage")
public class ChatListener implements Listener {

    private final Cache<LanguagePlayer, List<String>> editedMessage = CacheBuilder.newBuilder().expireAfterWrite(15, TimeUnit.MINUTES).build();
    private final LanguageAPI languageAPI = LanguageAPI.getInstance();
    private final LanguageCommand languageCommand;

    public ChatListener(LanguageSpigot languageSpigot, LanguageCommand languageCommand) {
        this.languageCommand = languageCommand;
        Bukkit.getPluginManager().registerEvents(this, languageSpigot);
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        LanguagePlayer languagePlayer = languageAPI.getPlayerManager().getLanguagePlayer(player.getUniqueId());
        if (languagePlayer == null) {
            return;
        }
        if (this.languageCommand.editingMessage.contains(player)) {
            if (!event.getMessage().equalsIgnoreCase("finish")) {
                List<String> currentArray = editedMessage.getIfPresent(languagePlayer);
                if (currentArray != null) {
                    editedMessage.invalidate(languagePlayer);
                } else {
                    currentArray = new ArrayList<>();
                }
                currentArray.add(event.getMessage());
                editedMessage.put(languagePlayer, currentArray);
                event.setCancelled(true);
            } else {
                if (editedMessage.getIfPresent(languagePlayer) == null) {
                    languagePlayer.sendMessage(I18N.LANGUAGEAPI_UPDATE_SAME.get());
                    event.setCancelled(true);
                    return;
                }
                StringBuilder result = new StringBuilder();
                for (String message : Objects.requireNonNull(editedMessage.getIfPresent(languagePlayer))) {
                    result.append(message).append(" ");
                }
                String transkey = this.languageCommand.givenParameter.get(player).get(0);
                String language = this.languageCommand.givenParameter.get(player).get(1);
                this.languageCommand.editingMessage.remove(player);
                languageAPI.updateMessage(transkey, result.toString(), language);

                event.setCancelled(true);
                languagePlayer.sendMessage(I18N.LANGUAGEAPI_UPDATE_SUCCESS.get()
                        .replace("%KEY%", transkey)
                        .replace("%LANG%", language)
                        .replace("%MSG%", result.toString()));
                editedMessage.invalidate(languagePlayer);
            }
        }
    }
}
