package de.tentact.languageapi.player;

import de.tentact.languageapi.i18n.Translation;
import org.jetbrains.annotations.NotNull;

public interface ILanguagePlayer {

    void sendMessage(@NotNull Translation translation);

    void sendMessageByKey(@NotNull String transkey);

    void setLanguage(@NotNull String language);

    String getLanguage();

}
