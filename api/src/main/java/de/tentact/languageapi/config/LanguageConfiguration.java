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

package de.tentact.languageapi.config;

import de.tentact.languageapi.cache.CacheType;
import de.tentact.languageapi.cache.DatabaseType;
import de.tentact.languageapi.config.database.DatabaseConfiguration;

import java.util.Locale;

public class LanguageConfiguration {

  public static final LanguageConfiguration DEFAULT_LANGUAGE_CONFIGURATION =
      new LanguageConfiguration(
          DatabaseConfiguration.DEFAULT_DATABASE_CONFIGURATION,
          DatabaseConfiguration.DEFAULT_DATABASE_CONFIGURATION,
          CacheType.REDIS,
          DatabaseType.MYSQL,
          Locale.GERMAN.toLanguageTag()
      );

  private DatabaseConfiguration databaseConfiguration;
  private DatabaseConfiguration cacheConfiguration;
  private CacheType cacheType;
  private DatabaseType databaseType;
  private String defaultLocale;
  private transient Locale locale;

  public LanguageConfiguration(DatabaseConfiguration databaseConfiguration, DatabaseConfiguration cacheConfiguration, CacheType cacheType, DatabaseType databaseType, String localeTag) {
    this.databaseConfiguration = databaseConfiguration;
    this.cacheConfiguration = cacheConfiguration;
    this.cacheType = cacheType;
    this.databaseType = databaseType;
    this.defaultLocale = localeTag;
    this.locale = Locale.forLanguageTag(localeTag);
  }

  public DatabaseConfiguration getDatabaseConfiguration() {
    return this.databaseConfiguration;
  }

  public void setDatabaseConfiguration(DatabaseConfiguration databaseConfiguration) {
    this.databaseConfiguration = databaseConfiguration;
  }

  public DatabaseConfiguration getCacheConfiguration() {
    return this.cacheConfiguration;
  }

  public void setCacheConfiguration(DatabaseConfiguration cacheConfiguration) {
    this.cacheConfiguration = cacheConfiguration;
  }

  public CacheType getCacheType() {
    return this.cacheType;
  }

  public void setCacheType(CacheType cacheType) {
    this.cacheType = cacheType;
  }

  public DatabaseType getDatabaseType() {
    return this.databaseType;
  }

  public void setDatabaseType(DatabaseType databaseType) {
    this.databaseType = databaseType;
  }

  public Locale getDefaultLocale() {
    return this.locale;
  }

  public void setDefaultLocale(Locale locale) {
    this.locale = locale;
    this.defaultLocale = locale.toLanguageTag();
  }
}
