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

import de.tentact.languageapi.cache.CacheProvider
import de.tentact.languageapi.cache.CacheType
import de.tentact.languageapi.cache.DatabaseType
import de.tentact.languageapi.cache.provider.LocalCacheProvider
import de.tentact.languageapi.cache.provider.RedisCacheProvider
import de.tentact.languageapi.config.LanguageConfiguration
import de.tentact.languageapi.database.MySQLDatabaseProvider
import de.tentact.languageapi.database.RedisDatabaseProvider
import de.tentact.languageapi.entity.EntityHandler
import de.tentact.languageapi.entity.MySQLEntityHandler
import de.tentact.languageapi.file.DefaultFileHandler
import de.tentact.languageapi.file.FileHandler
import de.tentact.languageapi.language.LocaleHandler
import de.tentact.languageapi.language.MySQLLocaleHandler
import de.tentact.languageapi.message.MessageHandler
import de.tentact.languageapi.message.MySQLMessageHandler
import de.tentact.languageapi.registry.DefaultServiceRegistry
import de.tentact.languageapi.registry.ServiceRegistry
import java.util.concurrent.Executors

class DefaultLanguageAPI(languageConfiguration: LanguageConfiguration) : LanguageAPI() {
  private val executorService = Executors.newCachedThreadPool()
  private var languageConfiguration: LanguageConfiguration
  private var messageHandler: MessageHandler
  private val fileHandler: FileHandler
  private var localeHandler: LocaleHandler
  private var entityHandler: EntityHandler
  private var cacheProvider: CacheProvider
  private val serviceRegistry: ServiceRegistry
  override fun getMessageHandler(): MessageHandler {
    return messageHandler
  }

  override fun getFileHandler(): FileHandler {
    return fileHandler
  }

  override fun getLocaleHandler(): LocaleHandler {
    return localeHandler
  }

  override fun getEntityHandler(): EntityHandler {
    return entityHandler
  }

  override fun getCacheProvider(): CacheProvider {
    return cacheProvider
  }

  override fun getServiceRegistry(): ServiceRegistry {
    return serviceRegistry
  }

  override fun getLanguageConfiguration(): LanguageConfiguration {
    return languageConfiguration
  }

  override fun setLanguageConfiguration(languageConfiguration: LanguageConfiguration) {
    this.languageConfiguration = languageConfiguration
  }

  override fun executeAsync(runnable: Runnable) {
    executorService.execute(runnable)
  }

  init {
    fileHandler = DefaultFileHandler()
    this.languageConfiguration = languageConfiguration
    serviceRegistry = DefaultServiceRegistry()
    cacheProvider = when (this.languageConfiguration.cacheType) {
      CacheType.REDIS -> RedisCacheProvider(RedisDatabaseProvider(this.languageConfiguration.cacheConfiguration))
      CacheType.LOCAL -> LocalCacheProvider()
      else -> LocalCacheProvider()
    }
    when (this.languageConfiguration.databaseType) {
      DatabaseType.MYSQL -> {
        val databaseProvider = MySQLDatabaseProvider(this.languageConfiguration.databaseConfiguration)
        databaseProvider.init(this)
        localeHandler = MySQLLocaleHandler(cacheProvider, databaseProvider)
        messageHandler = MySQLMessageHandler(localeHandler, databaseProvider, cacheProvider)
        entityHandler = MySQLEntityHandler(cacheProvider, localeHandler, databaseProvider)
      }
      else -> {
        val databaseProvider = MySQLDatabaseProvider(this.languageConfiguration.databaseConfiguration)
        databaseProvider.init(this)
        localeHandler = MySQLLocaleHandler(cacheProvider, databaseProvider)
        messageHandler = MySQLMessageHandler(localeHandler, databaseProvider, cacheProvider)
        entityHandler = MySQLEntityHandler(cacheProvider, localeHandler, databaseProvider)
      }
    }
  }
}
