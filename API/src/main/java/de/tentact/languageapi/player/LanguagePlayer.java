package de.tentact.languageapi.player;

import de.tentact.languageapi.i18n.Translation;
import org.jetbrains.annotations.NotNull;

public interface LanguagePlayer extends LanguageOfflinePlayer {

    /**
     * send a translated message using a {@link Translation} to a player
     * @param translation the translation to get the message from
     */

    void sendMessage(@NotNull Translation translation);

    /**
     * send a message in the players language by the translationkey
     * @param transkey the translationkey to fetch the translation from
     */
    void sendMessageByKey(@NotNull String transkey);

    /**
     * send a message in the players language by the translationkey
     * @param transkey the translationkey to fetch the translation from
     * @param usePrefix wether use the prefix of the languageapi or not
     */

    void sendMessageByKey(@NotNull String transkey, boolean usePrefix);


    /**
     * send a multipletranslation which relates to other translationkeys and it's translations
     * @param multipleTranslationKey the multipletranslationkey to get other translations from
     */
    void sendMultipleTranslation(@NotNull String multipleTranslationKey);

    /**
     * send a multipletranslation which relates to other translationkeys and it's translations in a specific language
     * @param multipleTranslationKey the multipletranslationkey to get other translations from
     * @param language the language to get the translations in
     */
    void sendMultipleTranslation(@NotNull String multipleTranslationKey, @NotNull String language);

    /**
     * @return returns wether the player is online or not, if false the Player is null
     */
    boolean isOnline();


}
