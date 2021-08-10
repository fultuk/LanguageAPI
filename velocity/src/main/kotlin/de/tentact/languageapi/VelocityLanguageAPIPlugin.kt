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
package de.tentact.languageapi

import com.google.inject.Inject
import com.velocitypowered.api.plugin.annotation.DataDirectory
import com.velocitypowered.api.proxy.ProxyServer
import de.tentact.languageapi.config.LanguageConfigurationHelper
import de.tentact.languageapi.entity.ConsoleEntity
import de.tentact.languageapi.entity.VelocityConsoleEntity
import de.tentact.languageapi.listener.EntityCacheListener
import java.nio.file.Path

class VelocityLanguageAPIPlugin @Inject constructor(val proxyServer: ProxyServer, @DataDirectory dataDirectory: Path) {

  init {
    val languageConfiguration = LanguageConfigurationHelper.readConfiguration(
      dataDirectory.resolve("config.json"),
      true
    )!!
    LanguageAPI.setLanguageAPI(DefaultLanguageAPI(languageConfiguration))
    LanguageAPI.getInstance().serviceRegistry.setProvider(
      ConsoleEntity::class.java,
      VelocityConsoleEntity(languageConfiguration, proxyServer)
    )
    EntityCacheListener(this)
  }
}
