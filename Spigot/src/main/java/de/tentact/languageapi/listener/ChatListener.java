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

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import de.tentact.languageapi.LanguageAPI;
import de.tentact.languageapi.LanguageSpigot;
import de.tentact.languageapi.i18n.I18N;
import de.tentact.languageapi.player.LanguagePlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.metadata.MetadataValue;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@SuppressWarnings("UnstableApiUsage")
public class ChatListener implements Listener {

    private final Cache<UUID, List<String>> editedMessage = CacheBuilder.newBuilder().expireAfterWrite(5, TimeUnit.MINUTES).build();
    private final LanguageSpigot languageSpigot;

    public ChatListener(LanguageSpigot languageSpigot) {
        this.languageSpigot = languageSpigot;
        Bukkit.getPluginManager().registerEvents(this, languageSpigot);
    }

    @EventHandler
    public void handlePlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        LanguagePlayer languagePlayer = LanguageAPI.getInstance().getPlayerExecutor().getLanguagePlayer(player.getUniqueId());
        if (languagePlayer == null) {
            return;
        }
        if (player.hasMetadata("editMessage")) {
            if (!event.getMessage().equalsIgnoreCase("finish")) {
                List<String> currentArray = this.editedMessage.getIfPresent(languagePlayer.getUniqueId());
                if (currentArray == null) {
                    currentArray = new ArrayList<>();
                }
                currentArray.add(event.getMessage());
                this.editedMessage.put(languagePlayer.getUniqueId(), currentArray);
                event.setCancelled(true);
            } else {
                List<String> messages = this.editedMessage.getIfPresent(languagePlayer.getUniqueId());
                if (messages == null) {
                    languagePlayer.sendMessage(I18N.LANGUAGEAPI_UPDATE_SAME.get());
                    event.setCancelled(true);
                    return;
                }
                MetadataValue metadataValue = player.getMetadata("editParameter").get(0);
                List<String> parameterList = (List<String>) metadataValue.value();

                String transKey = parameterList.get(0);
                String language = parameterList.get(1);

                player.removeMetadata("editMessage", this.languageSpigot);
                player.removeMetadata("editParamter", this.languageSpigot);

                LanguageAPI.getInstance().updateMessage(transKey, String.join(" ", messages), language);
                event.setCancelled(true);
                languagePlayer.sendMessage(I18N.LANGUAGEAPI_UPDATE_SUCCESS.get()
                        .replace("%KEY%", transKey)
                        .replace("%LANG%", language)
                        .replace("%MSG%", String.join(" ", messages)));
                this.editedMessage.invalidate(languagePlayer.getUniqueId());
            }
        }
    }
}
