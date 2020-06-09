package de.tentact.languageapi;
/*  Created in the IntelliJ IDEA.
    Created by 0utplay | Aldin Sijamhodzic
    Datum: 25.04.2020
    Uhrzeit: 17:20
*/

import de.tentact.languageapi.api.LanguageImpl;
import de.tentact.languageapi.mysql.MySQL;
import de.tentact.languageapi.util.DefaultMessages;
import de.tentact.languageapi.util.Source;
import de.tentact.languageapi.util.Updater;
import net.md_5.bungee.api.plugin.Plugin;

public class LanguageBungeecord extends Plugin {

    private static LanguageBungeecord languageBungeecord;
    private static MySQL mySQL;

    @Override
    public void onEnable() {
        languageBungeecord = this;
        Source.bungeeCordMode = true;
        Source.setLogger(this.getLogger());
        Source.createBungeeCordMySQLConfig();
        Source.initMySQLBungeecord();
        mySQL = Source.getMySQL();
        mySQL.connect();
        mySQL.createDefaultTable();
        LanguageImpl.getInstance().createLanguage(Source.getDefaultLanguage());
        DefaultMessages.createDefaultPluginMessages();

        new Updater(this);
    }

    @Override
    public void onDisable() {
        mySQL.close();
    }
    public static LanguageBungeecord getLanguageBungeecord() {
        return languageBungeecord;
    }
}
