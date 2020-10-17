package de.tentact.languageapi.player;
/*  Created in the IntelliJ IDEA.
    Copyright(c) 2020
    Created by 0utplay | Aldin Sijamhodzic
    Datum: 17.10.2020
    Uhrzeit: 14:14
*/

import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.UUID;
import java.util.stream.Collectors;

public class SpigotSpecificPlayerExecutor extends DefaultSpecificPlayerExecutor {

    public SpigotSpecificPlayerExecutor(UUID playerId) {
        super(playerId);
    }

    @Override
    public @Nullable LanguagePlayer getLanguagePlayer(UUID playerId) {
        return new DefaultLanguagePlayer(playerId);
    }

    @Override
    public @NotNull Collection<LanguagePlayer> getOnlineLanguagePlayers() {
        return Bukkit.getOnlinePlayers().stream().map(player -> this.getLanguagePlayer(player.getUniqueId())).collect(Collectors.toList());
    }
}
