package de.tentact.languageapi.player;
/*  Created in the IntelliJ IDEA.
    Copyright(c) 2020
    Created by 0utplay | Aldin Sijamhodzic
    Datum: 17.10.2020
    Uhrzeit: 14:17
*/

import de.tentact.languageapi.LanguageAPI;
import de.tentact.languageapi.configuration.LanguageConfig;
import net.md_5.bungee.api.ProxyServer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.UUID;
import java.util.stream.Collectors;

public class BungeePlayerExecutor extends DefaultPlayerExecutor {
    public BungeePlayerExecutor(LanguageAPI languageAPI, LanguageConfig languageConfig) {
        super(languageAPI, languageConfig);
    }

    @Override
    public @Nullable LanguagePlayer getLanguagePlayer(UUID playerId) {
        return new DefaultLanguagePlayer(playerId);
    }

    @Override
    public @NotNull Collection<LanguagePlayer> getOnlineLanguagePlayers() {
        return ProxyServer.getInstance().getPlayers().stream().map(player -> this.getLanguagePlayer(player.getUniqueId())).collect(Collectors.toList());
    }
}
