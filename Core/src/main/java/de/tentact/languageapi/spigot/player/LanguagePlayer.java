package de.tentact.languageapi.spigot.player;


import de.tentact.languageapi.AbstractLanguageAPI;
import de.tentact.languageapi.i18n.Translation;
import de.tentact.languageapi.player.ILanguagePlayer;
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
        getPlayer().sendMessage(translation.getMessage());

    }

    @Override
    public void sendMessageByKey(@NotNull String transkey) {
        this.sendMessage(new Translation(transkey));

    }

    @Override
    public void setLanguage(@NotNull String language) {
        this.abstractLanguageAPI.setPlayerLanguage(this.playerID, language, true);
    }

    @Override
    public String getLanguage() {
        return this.abstractLanguageAPI.getPlayerLanguage(this.playerID);
    }

    @Nullable
    private Player getPlayer() {
        this.player = Bukkit.getPlayer(this.playerID);
        return this.player;

    }

}
