package de.tentact.languageapi.player;


import de.tentact.languageapi.AbstractLanguageAPI;
import de.tentact.languageapi.i18n.Translation;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class LanguagePlayer implements ILanguagePlayer {

    private final UUID playerID;
    private Player player;
    private final AbstractLanguageAPI abstractLanguageAPI = AbstractLanguageAPI.getInstance();

    public LanguagePlayer(UUID playerID) {
        this.playerID = playerID;

    }

    @Override
    public void sendMessage(@NotNull Translation translation) {
        if(this.getPlayer() == null) {
            throw new NullPointerException();
        }
        this.getPlayer().sendMessage(translation.getMessage());

    }

    @Override
    public void sendMessageByKey(@NotNull String transkey) {
        this.sendMessage(new Translation(transkey));

    }

    @Override
    public void setLanguage(@NotNull String language) {
        this.setLanguage(language, false);
    }

    @Override
    public void setLanguage(@NotNull String language, boolean orElseDefault) {
        if(!this.abstractLanguageAPI.isLanguage(language) && !orElseDefault) {
            throw new IllegalArgumentException(language+" was not found");
        }
        this.abstractLanguageAPI.setPlayerLanguage(this.playerID, language, orElseDefault);
    }

    @Override
    public void sendMultipleTranslation(@NotNull String multipleTranslationKey) {
        this.sendMultipleTranslation(multipleTranslationKey, this.getLanguage());
    }
    @Override
    public void sendMultipleTranslation(@NotNull String multipleTranslationKey, @NotNull String language) {
        if(this.getPlayer() == null) {
            throw new NullPointerException();
        }
        if(!this.abstractLanguageAPI.isMultipleTranslation(multipleTranslationKey)) {
            throw new IllegalArgumentException(multipleTranslationKey+" was not found");
        }
        this.abstractLanguageAPI.getMultipleMessages(multipleTranslationKey, language).forEach(this.getPlayer() :: sendMessage);
    }

    @Override
    public String getLanguage() {
        return this.abstractLanguageAPI.getPlayerLanguage(this.playerID);
    }

    @Nullable
    private Player getPlayer() {
        if(this.player != null) {
            return this.player;
        }
        this.player = Bukkit.getPlayer(this.playerID);
        return this.player;
    }

}
