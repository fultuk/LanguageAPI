package de.tentact.languageapi.player;


import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import de.tentact.languageapi.LanguageAPI;
import de.tentact.languageapi.i18n.Translation;
import net.kyori.text.serializer.legacy.LegacyComponentSerializer;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class DefaultLanguagePlayer extends DefaultLanguageOfflinePlayer implements LanguagePlayer {

    private final UUID playerID;
    private final ProxyServer proxyServer;
    private Player player;
    private final LanguageAPI languageAPI = LanguageAPI.getInstance();

    public DefaultLanguagePlayer(ProxyServer proxyServer, UUID playerID) {
        super(playerID);
        this.playerID = playerID;
        this.proxyServer = proxyServer;
    }

    @Override
    public void sendMessage(@NotNull Translation translation) {
        Player player = this.getPlayer();
        if (player == null) {
            return;
        }
        player.sendMessage(LegacyComponentSerializer.legacyLinking().deserialize(translation.getMessage(this.getLanguage())));
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
        Player player = this.getPlayer();
        if (player == null) {
            return;
        }
        this.languageAPI.getMultipleMessages(multipleTranslationKey, language, prefixKey).forEach(s -> player.sendMessage(LegacyComponentSerializer.legacyLinking().deserialize(s)));
    }


    @Override
    public void kickPlayer(Translation translation) {
        Player player = this.getPlayer();
        if (player == null) {
            return;
        }
        player.disconnect(LegacyComponentSerializer.legacyLinking().deserialize(translation.getMessage(this.getLanguage())));
    }

    private Player getPlayer() {
        if (this.player != null) {
            return this.player;
        }
        this.proxyServer.getPlayer(this.playerID).ifPresent(value -> this.player = value);
        return this.player;
    }
}
