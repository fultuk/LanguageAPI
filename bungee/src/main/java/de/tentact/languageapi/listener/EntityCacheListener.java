/*
 * MIT License
 *
 * Copyright (c) 2021 0utplay (Aldin Sijamhodzic)
 * Copyright (c) 2021 contributors
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

import de.tentact.languageapi.BungeeLanguageAPIPlugin;
import de.tentact.languageapi.LanguageAPI;
import de.tentact.languageapi.entity.BungeeLanguageEntity;
import de.tentact.languageapi.entity.EntityHandler;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class EntityCacheListener implements Listener {

  private final EntityHandler entityHandler;

  public EntityCacheListener(BungeeLanguageAPIPlugin bungeeLanguageAPIPlugin) {
    ProxyServer.getInstance().getPluginManager().registerListener(bungeeLanguageAPIPlugin, this);
    this.entityHandler = LanguageAPI.getInstance().getEntityHandler();
  }

  @EventHandler
  public void handlePlayerJoin(PostLoginEvent playerJoinEvent) {
    ProxiedPlayer player = playerJoinEvent.getPlayer();
    this.entityHandler.getOfflineLanguageEntity(player.getUniqueId()).thenAccept(offlineEntity ->
        this.entityHandler.loginEntity(new BungeeLanguageEntity(offlineEntity, player)));
  }

  @EventHandler
  public void handlePlayerQuit(PlayerDisconnectEvent playerQuitEvent) {
    this.entityHandler.logoutEntity(playerQuitEvent.getPlayer().getUniqueId());
  }
}
