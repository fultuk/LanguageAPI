package de.tentact.languageapi;

import de.tentact.languageapi.cache.CacheProvider;
import de.tentact.languageapi.config.LanguageConfiguration;
import de.tentact.languageapi.entity.EntityHandler;
import de.tentact.languageapi.file.FileHandler;
import de.tentact.languageapi.language.LocaleHandler;
import de.tentact.languageapi.message.MessageHandler;
import de.tentact.languageapi.registry.ServiceRegistry;
import org.jetbrains.annotations.ApiStatus;

public abstract class LanguageAPI {

  private static LanguageAPI languageAPI;

  @ApiStatus.Internal
  public static void setLanguageAPI(LanguageAPI languageAPI) {
    if (LanguageAPI.languageAPI != null) {
      throw new UnsupportedOperationException("Cannot redefine singleton LanguageAPI");
    }
    LanguageAPI.languageAPI = languageAPI;
  }

  /**
   * @return the instance set by the implementation
   */
  public static LanguageAPI getInstance() {
    return LanguageAPI.languageAPI;
  }

  public abstract MessageHandler getMessageHandler();

  public abstract FileHandler getFileHandler();

  public abstract LocaleHandler getLocaleHandler();

  public abstract EntityHandler getEntityHandler();

  public abstract ServiceRegistry getServiceRegistry();

  public abstract CacheProvider getCacheProvider();

  public abstract LanguageConfiguration getLanguageConfiguration();

  public abstract void setLanguageConfiguration(LanguageConfiguration languageConfiguration);

  public abstract void executeAsync(Runnable runnable);
}
