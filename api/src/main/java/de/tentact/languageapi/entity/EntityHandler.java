package de.tentact.languageapi.entity;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface EntityHandler {

  /**
   * Retrieves the entity from the online entity cache
   *
   * @param entityId the entityId that belongs to the entity
   * @return the retrieved entity
   */
  @Nullable LanguageEntity getLanguageEntity(@NotNull UUID entityId);

  /**
   * Caches the entity in the online entity cache
   *
   * @param languageEntity the entity to be cached
   */
  @ApiStatus.Internal
  void loginEntity(@NotNull LanguageEntity languageEntity);

  /**
   * Removes the entity from the cache
   *
   * @param entityId the entity to be removed from cache
   */
  @ApiStatus.Internal
  void logoutEntity(@NotNull UUID entityId);

  /**
   * Retrieves the entity from the cache if present or loads it from the database and caches it
   *
   * @param entityId the entityId that belongs to the entity
   * @return the found entity - null if no entity was found
   */
  CompletableFuture<LanguageOfflineEntity> getOfflineLanguageEntity(@NotNull UUID entityId);

  /**
   * Validates the locale of the given entity and updates it in caches and databases
   *
   * @param languageOfflineEntity the entity to update
   * @return a future when the update is written to the database
   */
  CompletableFuture<Void> updateLanguageEntity(@NotNull LanguageOfflineEntity languageOfflineEntity);

  /**
   * Registers a entity with the given id in the database and caches it
   *
   * @param entityId the entityId that belongs to the entity
   * @return the new entity
   */
  LanguageOfflineEntity registerEntity(@NotNull UUID entityId);

  /**
   * Registers a entity with the given id in the database and caches it
   *
   * @param entityId the entityId that belongs to the entity
   * @param locale   the locale that is set for the entity
   * @return the new entity
   */
  LanguageOfflineEntity registerEntity(@NotNull UUID entityId,@NotNull Locale locale);

  /**
   * @return the console entity to send messages to a console
   */
  ConsoleEntity getConsoleEntity();

}
