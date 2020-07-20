package de.tentact.languageapi.player;


import de.tentact.languageapi.AbstractLanguageAPI;
import de.tentact.languageapi.i18n.Translation;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.UUID;

public class LanguagePlayerImpl extends LanguageOfflinePlayerImpl implements LanguagePlayer {

    private final UUID playerID;
    private ProxiedPlayer proxiedPlayer;
    private final AbstractLanguageAPI abstractLanguageAPI = AbstractLanguageAPI.getInstance();


    public LanguagePlayerImpl(UUID playerID) {
        super(playerID);
        this.playerID = playerID;
    }

    @Override
    public void sendMessage(@NotNull Translation translation) {
        this.getProxiedPlayer().sendMessage(translation.getMessage(this.getLanguage()));

    }

    @Override
    public void sendMessageByKey(@NotNull String transkey) {
        this.sendMessageByKey(transkey, false);

    }

    @Override
    public void sendMessageByKey(@NotNull String transkey, boolean usePrefix) {
        this.sendMessage(new Translation(transkey).setPrefix(usePrefix));
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
        this.abstractLanguageAPI.getMultipleMessages(multipleTranslationKey, language).forEach(Objects.requireNonNull(this.getProxiedPlayer())::sendMessage);

    }

    @Override
    public boolean isOnline() {
        return this.getProxiedPlayer() != null;
    }

    private ProxiedPlayer getProxiedPlayer() {
        if (this.proxiedPlayer != null) {
            return this.proxiedPlayer;
        }
        this.proxiedPlayer = ProxyServer.getInstance().getPlayer(this.playerID);
        if (this.proxiedPlayer == null) {
            throw new NullPointerException();
        }
        return this.proxiedPlayer;
    }
}
