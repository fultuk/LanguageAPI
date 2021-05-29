package de.tentact.languageapi.entity;

import org.jetbrains.annotations.NotNull;

import java.util.Locale;
import java.util.UUID;

public interface LanguageOfflineEntity {

  /**
   * Retrieves the uniqueId of the entity
   *
   * @return the uniqueId of the entity
   */
  @NotNull UUID getEntityId();

  /**
   * Retrieves the locale of the entity
   *
   * @return the locale of the entity
   */
  @NotNull Locale getLocale();

  /**
   * Set the locale of the entity
   *
   * @param locale the locale of the entity
   * @see EntityHandler#updateLanguageEntity(LanguageOfflineEntity) to update the player
   */
  void setLocale(@NotNull Locale locale);

}
