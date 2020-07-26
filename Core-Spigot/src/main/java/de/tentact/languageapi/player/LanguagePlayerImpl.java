package de.tentact.languageapi.player;


import de.tentact.languageapi.LanguageAPI;
import de.tentact.languageapi.api.TranslationImpl;
import de.tentact.languageapi.i18n.Translation;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.UUID;

public class LanguagePlayerImpl extends LanguageOfflinePlayerImpl implements LanguagePlayer {

    private final UUID playerID;
    private Player player;
    private final LanguageAPI languageAPI = LanguageAPI.getInstance();


    public LanguagePlayerImpl(UUID playerID) {
        super(playerID);
        this.playerID = playerID;
    }

    @Override
    public void sendMessage(@NotNull Translation translation) {
        this.getPlayer().sendMessage(translation.getMessage(this.getLanguage()));

    }

    @Override
    public void sendMessageByKey(@NotNull String transkey) {
        this.sendMessageByKey(transkey, false);

    }

    @Override
    public void sendMessageByKey(@NotNull String transkey, boolean usePrefix) {
        this.sendMessage(new TranslationImpl(transkey).setPrefix(usePrefix));
    }


    @Override
    public void sendMultipleTranslation(@NotNull String multipleTranslationKey) {
        this.sendMultipleTranslation(multipleTranslationKey, this.getLanguage());
    }

    @Override
    public void sendMultipleTranslation(@NotNull String multipleTranslationKey, @NotNull String language) {
        if (!this.languageAPI.isMultipleTranslation(multipleTranslationKey)) {
            throw new IllegalArgumentException(multipleTranslationKey + " was not found");
        }
        this.languageAPI.getMultipleMessages(multipleTranslationKey, language).forEach(Objects.requireNonNull(this.getPlayer())::sendMessage);

    }

    @Override
    public boolean isOnline() {
        return this.getPlayer() != null;
    }

    private Player getPlayer() {
        if (this.player != null) {
            return this.player;
        }
        this.player = Bukkit.getPlayer(this.playerID);
        if (this.player == null) {
            throw new NullPointerException();
        }
        return this.player;
    }
}
