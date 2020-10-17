package de.tentact.languageapi.player;

import de.tentact.languageapi.i18n.Translation;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * You can get this by {@link PlayerManager#getLanguagePlayer(UUID)} or {@link PlayerExecutor#getLanguagePlayer(UUID)}
 */
public interface LanguagePlayer extends LanguageOfflinePlayer {

    /**
     * Sends a message to the player by a {@link Translation}
     * @param translation the {@link Translation} to get the translated message from
     */
    void sendMessage(@NotNull Translation translation);

    /**
     * Sends a message to the player by a translationkey
     * @param translationKey the translationkey to get the translation from
     */
    void sendMessageByKey(@NotNull String translationKey);

    /**
     * Sends multiple messages to the player by a single multipleTranslationKey
     *
     * @param multipleTranslationKey the multipleTranslationKey to get the Collection of translationkeys
     */
    default void sendMultipleTranslation(@NotNull String multipleTranslationKey) {
        this.sendMultipleTranslation(multipleTranslationKey, this.getLanguage());
    }

    /**
     * Sends multiple messages to the player by a single multipleTranslationKey
     * @param multipleTranslationKey the multipleTranslationKey to get the Collection of translationkeys
     */
    default void sendMultipleTranslationWithPrefix(@NotNull String multipleTranslationKey, String prefixKey) {
        this.sendMultipleTranslation(multipleTranslationKey, this.getLanguage(), prefixKey);
    }

    /**
     * Sends multiple messages to the player by a single {@link Translation}
     * @param multipleTranslation the multipleTranslation to get the Collection of translationkeys
     */
    default void sendMultipleTranslation(@NotNull Translation multipleTranslation) {
        if (multipleTranslation.getPrefixTranslation() != null) {
            this.sendMultipleTranslationWithPrefix(multipleTranslation.getTranslationKey(), multipleTranslation.getPrefixTranslation().getTranslationKey());
        } else {
            this.sendMultipleTranslation(multipleTranslation.getTranslationKey());
        }
    }



    /**
     * Sends multiple messages to the player by a single multipleTranslationKey
     * @param multipleTranslationKey the multipleTranslationKey to get the Collection of translationkeys
     * @param language the language to get the translation in
     */
    default void sendMultipleTranslation(@NotNull String multipleTranslationKey, @NotNull String language) {
        this.sendMultipleTranslation(multipleTranslationKey, language, null);
    }

    /**
     * Sends multiple messages to the player by a single multipleTranslationKey
     * @param multipleTranslationKey the multipleTranslationKey to get the Collection of translationkeys
     * @param language the language to get the translation in
     */
    void sendMultipleTranslation(@NotNull String multipleTranslationKey, @NotNull String language, String prefixKey);

    /**
     * Kick a player with a {@link Translation} as reason
     * @param translation the {@link Translation} to get the translated message from
     */
    void kickPlayer(Translation translation);


}
