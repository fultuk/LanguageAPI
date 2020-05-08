package de.tentact.languageapi;
/*  Created in the IntelliJ IDEA.
    Created by 0utplay | Aldin Sijamhodzic
    Datum: 25.04.2020
    Uhrzeit: 17:20
*/

import de.tentact.languageapi.api.LanguageAPI;
import de.tentact.languageapi.util.DefaultMessages;
import de.tentact.languageapi.util.Source;
import net.md_5.bungee.api.plugin.Plugin;

public class LanguageBungeecord extends Plugin {

    private static LanguageBungeecord languageBungeecord;

    @Override
    public void onEnable() {
        languageBungeecord = this;
        Source.bungeeCordMode = true;
        Source.createBungeeCordMySQLConfig();
        LanguageAPI.mySQL.connect();
        LanguageAPI.mySQL.createDefaultTable();
        LanguageAPI.getInstance().createLanguage(Source.getDefaultLanguage());
        DefaultMessages.createDefaultPluginMessages();


    }

    @Override
    public void onDisable() {

    }
    public static LanguageBungeecord getLanguageBungeecord() {
        return languageBungeecord;
    }
}
