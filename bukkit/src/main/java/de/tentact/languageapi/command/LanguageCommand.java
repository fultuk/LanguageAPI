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

package de.tentact.languageapi.command;

import de.tentact.languageapi.LanguageAPI;
import de.tentact.languageapi.entity.ConsoleEntity;
import de.tentact.languageapi.i18n.I18N;
import de.tentact.languageapi.registry.ServiceRegistry;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class LanguageCommand implements TabExecutor {

  private final LanguageAPI languageAPI = LanguageAPI.getInstance();

  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    ConsoleEntity languageEntity = ServiceRegistry.getService(ConsoleEntity.class);
    if (sender instanceof Player) {
      languageEntity = this.languageAPI.getEntityHandler().getLanguageEntity(((Player) sender).getUniqueId());
    }
    if (languageEntity == null) {
      return false;
    }
    if (!sender.hasPermission("system.languageapi")) {
      languageEntity.sendMessage(I18N.LANGUAGEAPI_NOPERMS.get());
      return false;
    }
    if (args.length >= 1) {
      switch (args[0].toLowerCase()) {
        case "": {

        }
        break;
      }
    }

    return true;
  }

  private boolean checkPermission(CommandSender commandSender, ConsoleEntity consoleEntity, String[] args) {
    if (commandSender.hasPermission("system.languageapi." + args[0])) {
      return false;
    }
    consoleEntity.sendMessage(I18N.LANGUAGEAPI_NOPERMS.get());
    return true;
  }

  @Override
  public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
    return Collections.emptyList();
  }
}
