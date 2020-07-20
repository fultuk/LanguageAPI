package de.tentact.languageapi.spigot.listener;
/*  Created in the IntelliJ IDEA.
    Created by 0utplay | Aldin Sijamhodzic
    Datum: 06.05.2020
    Uhrzeit: 15:29
*/

import de.tentact.languageapi.AbstractLanguageAPI;
import de.tentact.languageapi.event.LanguageUpdateTranslationEvent;
import de.tentact.languageapi.spigot.command.LanguageCommand;
import de.tentact.languageapi.util.ChatColorTranslator;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.ArrayList;
import java.util.HashMap;

public class ChatListener implements Listener {

    private final HashMap<Player, ArrayList<String>> editedMessage = new HashMap<>();
    private final AbstractLanguageAPI abstractLanguageAPI = AbstractLanguageAPI.getInstance();

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        if(LanguageCommand.editingMessage.contains(player)) {
            if(!event.getMessage().equalsIgnoreCase("finish")) {
                ArrayList<String> currentArray = editedMessage.get(player);
                if(currentArray != null) {
                    editedMessage.remove(player, currentArray);
                }else {
                    currentArray = new ArrayList<>();
                }
                currentArray.add(event.getMessage());
                editedMessage.put(player, currentArray);
                event.setCancelled(true);
            }else{
                if(editedMessage.get(player) == null) {
                    player.sendMessage(abstractLanguageAPI.getPrefix()+ abstractLanguageAPI.getMessage("languageapi-update-same", player.getUniqueId()));
                    event.setCancelled(true);
                    return;
                }
                StringBuilder result = new StringBuilder();
                for(String message : editedMessage.get(player)) {
                    result.append(message).append(" ");
                }
                String transkey = LanguageCommand.givenParameter.get(player).get(0);
                String language = LanguageCommand.givenParameter.get(player).get(1);
                LanguageCommand.editingMessage.remove(player);
                LanguageUpdateTranslationEvent languageUpdateTranslationEvent = new LanguageUpdateTranslationEvent(language, transkey, abstractLanguageAPI.getMessage(transkey, language), result.toString());
                if (!languageUpdateTranslationEvent.isCancelled()) {
                    Bukkit.getPluginManager().callEvent(languageUpdateTranslationEvent);
                }
                abstractLanguageAPI.updateMessage(transkey, language, result.toString());

                event.setCancelled(true);
                player.sendMessage(ChatColorTranslator.translateAlternateColorCodes('&', abstractLanguageAPI.getMessage("languageapi-update-success", player.getUniqueId())
                        .replace("%KEY%", transkey)
                        .replace("%LANG%", language)
                        .replace("%MSG%", result.toString())));
                editedMessage.remove(player);
            }
        }
    }
}
