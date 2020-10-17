package de.tentact.languageapi.player;
/*  Created in the IntelliJ IDEA.
    Copyright(c) 2020
    Created by 0utplay | Aldin Sijamhodzic
    Datum: 17.10.2020
    Uhrzeit: 14:14
*/

import com.velocitypowered.api.proxy.ProxyServer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.UUID;
import java.util.stream.Collectors;

public class VelocitySpecificPlayerExecutor extends DefaultSpecificPlayerExecutor {

    private final ProxyServer proxyServer;

    public VelocitySpecificPlayerExecutor(ProxyServer proxyServer, UUID playerId) {
        super(playerId);
        this.proxyServer = proxyServer;
    }

    @Override
    public @Nullable LanguagePlayer getLanguagePlayer(UUID playerId) {
        return new DefaultLanguagePlayer(this.proxyServer, playerId);
    }

    @Override
    public @NotNull Collection<LanguagePlayer> getOnlineLanguagePlayers() {
        return this.proxyServer.getAllPlayers().stream().map(player -> this.getLanguagePlayer(player.getUniqueId())).collect(Collectors.toList());
    }
}
