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
package de.tentact.languageapi.command

import de.tentact.languageapi.LanguageAPI
import de.tentact.languageapi.entity.ConsoleEntity
import de.tentact.languageapi.i18n.I18N
import de.tentact.languageapi.registry.ServiceRegistry
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor
import org.bukkit.entity.Player
import java.util.*

class LanguageCommand : TabExecutor {
  private val languageAPI = LanguageAPI.getInstance()
  override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
    var languageEntity = ServiceRegistry.getService(ConsoleEntity::class.java)
    if (sender is Player) {
      languageEntity = languageAPI.entityHandler.getLanguageEntity(sender.uniqueId)
    }
    if (languageEntity == null) {
      return false
    }
    if (!sender.hasPermission("system.languageapi")) {
      languageEntity.sendMessage(I18N.LANGUAGEAPI_NOPERMS.get())
      return false
    }
    if (args.isNotEmpty()) {
      when (args[0].lowercase(Locale.getDefault())) {
        "" -> {
        }
      }
    }
    return true
  }

  private fun checkPermission(commandSender: CommandSender, consoleEntity: ConsoleEntity, args: Array<String>): Boolean {
    if (commandSender.hasPermission("system.languageapi." + args[0])) {
      return false
    }
    consoleEntity.sendMessage(I18N.LANGUAGEAPI_NOPERMS.get())
    return true
  }

  override fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<String>): List<String> {
    return emptyList()
  }
}
