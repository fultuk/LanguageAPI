package de.tentact.languageapi.player;

import de.tentact.languageapi.i18n.Translation;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface LanguagePlayer extends LanguageOfflinePlayer {

    /**
     * @param translation
     */

    void sendMessage(@NotNull Translation translation);

    /**
     * @param transkey
     */
    void sendMessageByKey(@NotNull String transkey);


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

    /**
     * @return
     */
    @Nullable Player getPlayer();

}
