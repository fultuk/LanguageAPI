package de.tentact.languageapi.player;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class SpecificPlayerExecutorImpl implements SpecificPlayerExecutor {

    private final UUID playerId;
    private final PlayerExecutor playerExecutor = new PlayerExecutorImpl();

    public SpecificPlayerExecutorImpl(UUID playerId) {
        this.playerId = playerId;
    }

    @Override
    public @NotNull String getPlayerLanguage() {
        return this.playerExecutor.getPlayerLanguage(this.playerId);
    }

    @Override
    public void setPlayerLanguage(String newLanguage, boolean orElseDefault) {
        this.playerExecutor.setPlayerLanguage(this.playerId, newLanguage, orElseDefault);

    }

    @Override
    public void setPlayerLanguage(String newLanguage) {
        this.playerExecutor.setPlayerLanguage(this.playerId, newLanguage);
    }

    @Override
    public void registerPlayer() {
        this.playerExecutor.registerPlayer(this.playerId);
    }

    @Override
    public void registerPlayer(String language) {
        this.playerExecutor.registerPlayer(this.playerId, language);
    }

    @Override
    public boolean isRegisteredPlayer() {
        return this.playerExecutor.isRegisteredPlayer(this.playerId);
    }
}
