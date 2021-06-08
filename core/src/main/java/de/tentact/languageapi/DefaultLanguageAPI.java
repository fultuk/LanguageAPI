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

package de.tentact.languageapi;

import de.tentact.languageapi.cache.CacheProvider;
import de.tentact.languageapi.cache.provider.LocalCacheProvider;
import de.tentact.languageapi.cache.provider.RedisCacheProvider;
import de.tentact.languageapi.config.LanguageConfiguration;
import de.tentact.languageapi.database.MySQLDatabaseProvider;
import de.tentact.languageapi.database.RedisDatabaseProvider;
import de.tentact.languageapi.entity.EntityHandler;
import de.tentact.languageapi.entity.MySQLEntityHandler;
import de.tentact.languageapi.file.DefaultFileHandler;
import de.tentact.languageapi.file.FileHandler;
import de.tentact.languageapi.language.LocaleHandler;
import de.tentact.languageapi.language.MySQLLocaleHandler;
import de.tentact.languageapi.message.MessageHandler;
import de.tentact.languageapi.message.MySQLMessageHandler;
import de.tentact.languageapi.registry.DefaultServiceRegistry;
import de.tentact.languageapi.registry.ServiceRegistry;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DefaultLanguageAPI extends LanguageAPI {

  private final ExecutorService executorService = Executors.newCachedThreadPool();
  private LanguageConfiguration languageConfiguration;
  private final MessageHandler messageHandler;
  private final FileHandler fileHandler;
  private final LocaleHandler localeHandler;
  private final EntityHandler entityHandler;
  private final CacheProvider cacheProvider;
  private final ServiceRegistry serviceRegistry;

  public DefaultLanguageAPI(LanguageConfiguration languageConfiguration) {
    this.fileHandler = new DefaultFileHandler();
    this.languageConfiguration = languageConfiguration;
    this.serviceRegistry = new DefaultServiceRegistry();

    switch (this.languageConfiguration.getCacheType()) {
      case REDIS:
        this.cacheProvider = new RedisCacheProvider(new RedisDatabaseProvider(this.languageConfiguration.getCacheConfiguration()));
        break;
      case LOCAL:
      default:
        this.cacheProvider = new LocalCacheProvider();
        break;
    }


    switch (this.languageConfiguration.getDatabaseType()) {
      case MYSQL:
      default:
        MySQLDatabaseProvider databaseProvider = new MySQLDatabaseProvider(this.languageConfiguration.getDatabaseConfiguration());
        databaseProvider.init(this);
        this.localeHandler = new MySQLLocaleHandler(this.cacheProvider, databaseProvider);
        this.messageHandler = new MySQLMessageHandler(this.localeHandler, databaseProvider, this.cacheProvider);
        this.entityHandler = new MySQLEntityHandler(this.cacheProvider, this.localeHandler, databaseProvider);
    }

  }

  @Override
  public MessageHandler getMessageHandler() {
    return this.messageHandler;
  }

  @Override
  public FileHandler getFileHandler() {
    return this.fileHandler;
  }

  @Override
  public LocaleHandler getLocaleHandler() {
    return this.localeHandler;
  }

  @Override
  public EntityHandler getEntityHandler() {
    return this.entityHandler;
  }

  @Override
  public CacheProvider getCacheProvider() {
    return this.cacheProvider;
  }

  @Override
  public ServiceRegistry getServiceRegistry() {
    return this.serviceRegistry;
  }

  @Override
  public LanguageConfiguration getLanguageConfiguration() {
    return this.languageConfiguration;
  }

  @Override
  public void setLanguageConfiguration(LanguageConfiguration languageConfiguration) {
    this.languageConfiguration = languageConfiguration;
  }

  @Override
  public void executeAsync(Runnable runnable) {
    this.executorService.execute(runnable);
  }
}
