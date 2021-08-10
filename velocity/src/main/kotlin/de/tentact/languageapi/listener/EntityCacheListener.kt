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
package de.tentact.languageapi.listener

import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.connection.DisconnectEvent
import com.velocitypowered.api.event.connection.LoginEvent
import de.tentact.languageapi.LanguageAPI
import de.tentact.languageapi.VelocityLanguageAPIPlugin
import de.tentact.languageapi.entity.EntityHandler
import de.tentact.languageapi.entity.LanguageOfflineEntity
import de.tentact.languageapi.entity.VelocityLanguageEntity

class EntityCacheListener(velocityLanguageAPIPlugin: VelocityLanguageAPIPlugin) {
  private val entityHandler: EntityHandler

  @Subscribe
  fun handlePlayerJoin(loginEvent: LoginEvent) {
    val player = loginEvent.player
    entityHandler.getOfflineLanguageEntity(player.uniqueId).thenAccept { offlineEntity: LanguageOfflineEntity? ->
      val entity = offlineEntity ?: entityHandler.registerEntity(player.uniqueId)
      entityHandler.loginEntity(VelocityLanguageEntity(entity, player))
    }
  }

  @Subscribe
  fun handlePlayerQuit(disconnectEvent: DisconnectEvent) {
    entityHandler.logoutEntity(disconnectEvent.player.uniqueId)
  }

  init {
    velocityLanguageAPIPlugin.proxyServer.eventManager.register(velocityLanguageAPIPlugin, this)
    entityHandler = LanguageAPI.getInstance().entityHandler
  }
}
