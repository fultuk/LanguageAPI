package de.tentact.languageapi.player;


import de.tentact.languageapi.LanguageAPI;
import de.tentact.languageapi.i18n.Translation;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class DefaultLanguagePlayer extends DefaultLanguageOfflinePlayer implements LanguagePlayer {

    private final UUID playerID;
    private ProxiedPlayer proxiedPlayer;
    private final LanguageAPI languageAPI = LanguageAPI.getInstance();

    public DefaultLanguagePlayer(UUID playerID) {
        super(playerID);
        this.playerID = playerID;
    }

    @Override
    public void sendMessage(@NotNull Translation translation) {
        ProxiedPlayer proxiedPlayer = this.getProxiedPlayer();
        if (proxiedPlayer == null) {
            return;
        }
        proxiedPlayer.sendMessage(translation.getMessage(this.getLanguage()));
    }


    @Override
    public void sendMessageByKey(@NotNull String translationKey) {
        this.sendMessage(this.languageAPI.getTranslation(translationKey));
    }

    @Override
    public void sendMultipleTranslation(@NotNull String multipleTranslationKey, @NotNull String language, String prefixKey) {
        if (!this.languageAPI.isMultipleTranslation(multipleTranslationKey)) {
            throw new IllegalArgumentException(multipleTranslationKey + " was not found");
        }
        ProxiedPlayer proxiedPlayer = this.getProxiedPlayer();
        if (proxiedPlayer == null) {
            return;
        }
        this.languageAPI.getMultipleMessages(multipleTranslationKey, language, prefixKey).forEach(proxiedPlayer::sendMessage);
    }


    @Override
    public void kickPlayer(Translation translation) {
        ProxiedPlayer proxiedPlayer = this.getProxiedPlayer();
        if (proxiedPlayer == null) {
            return;
        }
        proxiedPlayer.disconnect(translation.getMessage(this.getLanguage()));
    }

    private ProxiedPlayer getProxiedPlayer() {
        if (this.proxiedPlayer != null) {
            return this.proxiedPlayer;
        }
        this.proxiedPlayer = ProxyServer.getInstance().getPlayer(this.playerID);
        return this.proxiedPlayer;
    }

}
