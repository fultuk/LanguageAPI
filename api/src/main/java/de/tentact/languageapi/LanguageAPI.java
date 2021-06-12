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

  /**
   * @return an instance to the {@link MessageHandler}
   */
  public abstract MessageHandler getMessageHandler();

  /**
   * @return an instance to the {@link FileHandler}
   */
  public abstract FileHandler getFileHandler();

  /**
   * @return an instance to the {@link LocaleHandler}
   */
  public abstract LocaleHandler getLocaleHandler();

  /**
   * @return an instance to the {@link EntityHandler}
   */
  public abstract EntityHandler getEntityHandler();

  /**
   * @return an instance to the {@link ServiceRegistry}
   */
  public abstract ServiceRegistry getServiceRegistry();

  /**
   * @return an instance to the {@link CacheProvider}
   */
  public abstract CacheProvider getCacheProvider();

  /**
   * @return the loaded {@link LanguageConfiguration}
   */
  public abstract LanguageConfiguration getLanguageConfiguration();

  /**
   * Sets the current {@link LanguageConfiguration} to the given one
   * Use the {@link de.tentact.languageapi.config.LanguageConfigurationWriter} to write the config in the file
   * @param languageConfiguration the new configuration
   */
  public abstract void setLanguageConfiguration(LanguageConfiguration languageConfiguration);

  /**
   * Executes the given runnable on another thread using an {@link java.util.concurrent.ExecutorService}
   * @param runnable the runnable to be executed
   */
  public abstract void executeAsync(Runnable runnable);
}
