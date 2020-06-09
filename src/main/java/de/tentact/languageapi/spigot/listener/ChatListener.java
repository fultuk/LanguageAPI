package de.tentact.languageapi.spigot.listener;
/*  Created in the IntelliJ IDEA.
    Created by 0utplay | Aldin Sijamhodzic
    Datum: 06.05.2020
    Uhrzeit: 15:29
*/

import de.tentact.languageapi.api.LanguageAPI;
import de.tentact.languageapi.spigot.command.LanguageCommand;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.ArrayList;
import java.util.HashMap;

public class ChatListener implements Listener {

    private HashMap<Player, ArrayList<String>> editedMessage = new HashMap<>();



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
                    player.sendMessage(LanguageAPI.getInstance().getPrefix()+LanguageAPI.getInstance().getMessage("languageapi-update-same", player.getUniqueId()));
                    event.setCancelled(true);
                    return;
                }
                StringBuilder result = new StringBuilder();
                for(String message : editedMessage.get(player)) {
                    result.append(message).append(" ");
                }
                LanguageCommand.editingMessage.remove(player);
                LanguageAPI.getInstance().updateMessage(LanguageCommand.givenParameter.get(player).get(0), LanguageCommand.givenParameter.get(player).get(1), result.toString());
                event.setCancelled(true);
                player.sendMessage(ChatColor.translateAlternateColorCodes('&',LanguageAPI.getInstance().getMessage("languageapi-update-success", player.getUniqueId())
                        .replace("%KEY%", LanguageCommand.givenParameter.get(player).get(0))
                        .replace("%LANG%", LanguageCommand.givenParameter.get(player).get(1))
                        .replace("%MSG%", result.toString())));
                editedMessage.remove(player);
            }
        }
    }
}
