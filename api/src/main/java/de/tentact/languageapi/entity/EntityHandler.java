package de.tentact.languageapi.entity;

import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface EntityHandler {

    /**
     * Retrieves the entity from the online entity cache
     * @param entityId the entityId that belongs to the entity
     * @return the retrieved entity
     */
    LanguageEntity getLanguageEntity(UUID entityId);

    /**
     * Retrieves the entity from the cache if present or loads it from the database and caches it
     * @param entityId the entityId that belongs to the entity
     * @return the found entity - null if no entity was found
     */
    CompletableFuture<LanguageOfflineEntity> getOfflineLanguageEntity(UUID entityId);

    /**
     * Validates the locale of the given entity and updates it in caches and databases
     * @param languageOfflineEntity the entity to update
     */
    void updateLanguageEntity(LanguageOfflineEntity languageOfflineEntity);

    /**
     * Registers a entity with the given id in the database and caches it
     * @param entityId the entityId that belongs to the entity
     * @return the new entity
     */
    LanguageOfflineEntity registerEntity(UUID entityId);

    /**
     * Registers a entity with the given id in the database and caches it
     * @param entityId the entityId that belongs to the entity
     * @param language the locale that is set for the entity
     * @return the new entity
     */
    LanguageOfflineEntity registerEntity(UUID entityId, Locale language);

    /**
     * @return the console entity to send messages to a console
     */
    ConsoleEntity getConsoleEntity();

}
