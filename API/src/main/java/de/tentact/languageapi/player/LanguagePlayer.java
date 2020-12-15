/*
 * MIT License
 *
 * Copyright (c) 2020 0utplay
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
