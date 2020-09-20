package de.tentact.languageapi.player;


import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import de.tentact.languageapi.LanguageAPI;
import de.tentact.languageapi.i18n.Translation;
import net.kyori.text.serializer.legacy.LegacyComponentSerializer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;

public class LanguagePlayerImpl extends LanguageOfflinePlayerImpl implements LanguagePlayer {

    private final UUID playerID;
    private Player player;
    private final ProxyServer proxyServer;
    private final LanguageAPI languageAPI = LanguageAPI.getInstance();

    public LanguagePlayerImpl(ProxyServer proxyServer, UUID playerID) {
        super(playerID);
        this.proxyServer = proxyServer;
        this.playerID = playerID;
    }

    @Override
    public void sendMessage(@NotNull Translation translation) {
        if(this.getPlayer() != null) {
            this.getPlayer().sendMessage(LegacyComponentSerializer.legacy().deserialize(translation.getMessage(this.getLanguage())));
        }

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
        this.languageAPI.getMultipleMessages(multipleTranslationKey, language).forEach(message -> player.sendMessage(LegacyComponentSerializer.legacy().deserialize(message)));
    }

    @Override
    public void kickPlayer(Translation translation) {
        if(this.getPlayer() == null) {
            return;
        }
        this.getPlayer().disconnect(LegacyComponentSerializer.legacy().deserialize(translation.getMessage(this.getLanguage())));
    }

    @Nullable
    private Player getPlayer() {
        if (this.player != null) {
            return this.player;
        }
        Optional<Player> playerOptional = this.proxyServer.getPlayer(this.playerID);
        playerOptional.ifPresent(value -> this.player = value);
        return this.player;
    }
}
