package de.tentact.languageapi.player;


import de.tentact.languageapi.AbstractLanguageAPI;
import de.tentact.languageapi.i18n.Translation;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.UUID;

public class LanguagePlayerImpl extends LanguageOfflinePlayerImpl implements LanguagePlayer {

    private final UUID playerID;
    private Player player;
    private final AbstractLanguageAPI abstractLanguageAPI = AbstractLanguageAPI.getInstance();

    public LanguagePlayerImpl(UUID playerID) {
        super(playerID);
        this.playerID = playerID;
    }

    @Override
    public void sendMessage(@NotNull Translation translation) {
        Objects.requireNonNull(this.getPlayer()).sendMessage(translation.getMessage());

    }

    @Override
    public void sendMessageByKey(@NotNull String transkey) {
        this.sendMessage(new Translation(transkey));

    }


    @Override
    public void sendMultipleTranslation(@NotNull String multipleTranslationKey) {
        this.sendMultipleTranslation(multipleTranslationKey, this.getLanguage());
    }

    @Override
    public void sendMultipleTranslation(@NotNull String multipleTranslationKey, @NotNull String language) {
        if (!this.abstractLanguageAPI.isMultipleTranslation(multipleTranslationKey)) {
            throw new IllegalArgumentException(multipleTranslationKey + " was not found");
        }
        this.abstractLanguageAPI.getMultipleMessages(multipleTranslationKey, language).forEach(Objects.requireNonNull(this.getPlayer())::sendMessage);
    }

    @Override
    public boolean isOnline() {
        return this.getPlayer() != null;
    }


    @Nullable
    public Player getPlayer() {
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
