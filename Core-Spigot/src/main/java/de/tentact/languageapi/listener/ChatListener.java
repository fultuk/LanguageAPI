package de.tentact.languageapi.listener;
/*  Created in the IntelliJ IDEA.
    Created by 0utplay | Aldin Sijamhodzic
    Datum: 06.05.2020
    Uhrzeit: 15:29
*/

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import de.tentact.languageapi.LanguageAPI;
import de.tentact.languageapi.command.LanguageCommand;
import de.tentact.languageapi.i18n.I18N;
import de.tentact.languageapi.player.LanguagePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.TimeUnit;


@SuppressWarnings("UnstableApiUsage")
public class ChatListener implements Listener {

    private final Cache<LanguagePlayer, ArrayList<String>> editedMessage = CacheBuilder.newBuilder().expireAfterWrite(15, TimeUnit.MINUTES).build();
    private final LanguageAPI languageAPI = LanguageAPI.getInstance();

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        LanguagePlayer languagePlayer = languageAPI.getPlayerManager().getLanguagePlayer(player.getUniqueId());
        if (languagePlayer == null) {
            return;
        }
        if (LanguageCommand.editingMessage.contains(player)) {
            if (!event.getMessage().equalsIgnoreCase("finish")) {
                ArrayList<String> currentArray = editedMessage.getIfPresent(languagePlayer);
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
                String transkey = LanguageCommand.givenParameter.get(player).get(0);
                String language = LanguageCommand.givenParameter.get(player).get(1);
                LanguageCommand.editingMessage.remove(player);
                languageAPI.updateMessage(transkey, language, result.toString());

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
