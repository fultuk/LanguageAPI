package de.tentact.languageapi.player;

import de.tentact.languageapi.i18n.Translation;
import org.jetbrains.annotations.NotNull;

public interface ILanguagePlayer {
    /**
     *
     * @param translation
     */

    void sendMessage(@NotNull Translation translation);

    /**
     *
     * @param transkey
     */
    void sendMessageByKey(@NotNull String transkey);

    /**
     *
     * @param language
     */
    void setLanguage(@NotNull String language);

    /**
     *
     * @param language
     * @param orElseDefault
     */
    void setLanguage(@NotNull String language, boolean orElseDefault);

    /**
     *
     * @param multipleTranslationKey
     */
    void sendMultipleTranslation(@NotNull String multipleTranslationKey);

    /**
     *
     * @param multipleTranslationKey
     * @param language
     */
    void sendMultipleTranslation(@NotNull String multipleTranslationKey, @NotNull String language);

    /**
     *
     * @return
     */
    String getLanguage();

}
