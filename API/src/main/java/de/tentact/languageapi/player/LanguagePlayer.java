package de.tentact.languageapi.player;

import de.tentact.languageapi.i18n.Translation;
import org.jetbrains.annotations.NotNull;

public interface LanguagePlayer extends LanguageOfflinePlayer {

    /**
     * @param translation
     */

    void sendMessage(@NotNull Translation translation);

    /**
     * @param transkey
     */
    void sendMessageByKey(@NotNull String transkey);

    void sendMessageByKey(@NotNull String transkey, boolean usePrefix);


    /**
     * @param multipleTranslationKey
     */
    void sendMultipleTranslation(@NotNull String multipleTranslationKey);

    /**
     * @param multipleTranslationKey
     * @param language
     */
    void sendMultipleTranslation(@NotNull String multipleTranslationKey, @NotNull String language);

    /**
     * @return
     */
    boolean isOnline();


}
