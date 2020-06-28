package de.tentact.languageapi;
/*  Created in the IntelliJ IDEA.
    Created by 0utplay | Aldin Sijamhodzic
    Datum: 25.04.2020
    Uhrzeit: 17:20
*/

import de.tentact.languageapi.mysql.MySQL;
import de.tentact.languageapi.util.I18N;
import de.tentact.languageapi.util.Source;
import de.tentact.languageapi.util.Updater;
import net.md_5.bungee.api.plugin.Plugin;

public class LanguageBungeecord extends Plugin {

    private static MySQL mySQL;

    @Override
    public void onEnable() {
        Source.isBungeeCordMode = true;
        Source.createBungeeCordMySQLConfig(this);
        Source.initBungeecord();
        mySQL = Source.getMySQL();
        mySQL.connect();
        mySQL.createDefaultTable();
        AbstractLanguageAPI.getInstance().createLanguage(Source.getDefaultLanguage());
        I18N.createDefaultPluginMessages();

        new Updater(this);
    }

    @Override
    public void onDisable() {
        mySQL.close();
    }
}
