package de.tentact.languageapi.player;


import de.tentact.languageapi.LanguageAPI;
import de.tentact.languageapi.i18n.Translation;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class DefaultLanguagePlayer extends DefaultLanguageOfflinePlayer implements LanguagePlayer {

    private final UUID playerID;
    private Player player;
    private final LanguageAPI languageAPI = LanguageAPI.getInstance();

    public DefaultLanguagePlayer(UUID playerID) {
        super(playerID);
        this.playerID = playerID;
    }

    @Override
    public void sendMessage(@NotNull Translation translation) {
        Player player = this.getPlayer();
        if(player == null) {
            return;
        }
        player.sendMessage(translation.getMessage(this.getLanguage()));
    }


    @Override
    public void sendMessageByKey(@NotNull String translationKey) {
        this.sendMessage(this.languageAPI.getTranslation(translationKey));
    }

    @Override
    public void sendMultipleTranslation(@NotNull String multipleTranslationKey) {
        this.sendMultipleTranslation(multipleTranslationKey, this.getLanguage());
    }

    @Override
    public void sendMultipleTranslation(@NotNull Translation multipleTranslation) {
        this.sendMultipleTranslation(multipleTranslation.getTranslationKey());
    }

    @Override
    public void sendMultipleTranslation(@NotNull String multipleTranslationKey, @NotNull String language) {
        if (!this.languageAPI.isMultipleTranslation(multipleTranslationKey)) {
            throw new IllegalArgumentException(multipleTranslationKey + " was not found");
        }
        Player player = this.getPlayer();
        if(player == null) {
            return;
        }
        this.languageAPI.getMultipleMessages(multipleTranslationKey, language).forEach(player::sendMessage);
    }

    @Override
    public void kickPlayer(Translation translation) {
        Player player = this.getPlayer();
        if(player == null) {
            return;
        }
        player.kickPlayer(translation.getMessage(this.getLanguage()));
    }

    private Player getPlayer() {
        if (this.player != null) {
            return this.player;
        }
        this.player = Bukkit.getPlayer(this.playerID);
        return this.player;
    }

}
