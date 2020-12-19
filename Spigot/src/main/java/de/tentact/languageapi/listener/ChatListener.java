/*
 * MIT License
 *
 * Copyright (c) 2020 0utplay
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
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class ChatListener implements Listener {

    private final Cache<UUID, List<String>> editedMessage = CacheBuilder.newBuilder().expireAfterWrite(5, TimeUnit.MINUTES).build();
    private final LanguageCommand languageCommand;

    public ChatListener(LanguageSpigot languageSpigot, LanguageCommand languageCommand) {
        this.languageCommand = languageCommand;
        Bukkit.getPluginManager().registerEvents(this, languageSpigot);
    }

    @EventHandler
    public void handlePlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        LanguagePlayer languagePlayer = LanguageAPI.getInstance().getPlayerExecutor().getLanguagePlayer(player.getUniqueId());
        if (languagePlayer == null) {
            return;
        }
        if (this.languageCommand.editingMessage.contains(player)) {
            if (!event.getMessage().equalsIgnoreCase("finish")) {
                List<String> currentArray = editedMessage.getIfPresent(languagePlayer.getUniqueId());
                if (currentArray != null) {
                    editedMessage.invalidate(languagePlayer.getUniqueId());
                } else {
                    currentArray = new ArrayList<>();
                }
                currentArray.add(event.getMessage());
                editedMessage.put(languagePlayer.getUniqueId(), currentArray);
                event.setCancelled(true);
            } else {
                if (editedMessage.getIfPresent(languagePlayer.getUniqueId()) == null) {
                    languagePlayer.sendMessage(I18N.LANGUAGEAPI_UPDATE_SAME.get());
                    event.setCancelled(true);
                    return;
                }
                StringBuilder result = new StringBuilder();
                for (String message : Objects.requireNonNull(editedMessage.getIfPresent(languagePlayer.getUniqueId()))) {
                    result.append(message).append(" ");
                }
                String transKey = this.languageCommand.givenParameter.get(player).get(0);
                String language = this.languageCommand.givenParameter.get(player).get(1);
                this.languageCommand.editingMessage.remove(player);
                LanguageAPI.getInstance().updateMessage(transKey, result.toString(), language);

                event.setCancelled(true);
                languagePlayer.sendMessage(I18N.LANGUAGEAPI_UPDATE_SUCCESS.get()
                        .replace("%KEY%", transKey)
                        .replace("%LANG%", language)
                        .replace("%MSG%", result.toString()));
                editedMessage.invalidate(languagePlayer.getUniqueId());
            }
        }
    }
}
