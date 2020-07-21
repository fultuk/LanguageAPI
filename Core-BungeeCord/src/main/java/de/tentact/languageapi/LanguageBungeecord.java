package de.tentact.languageapi;
/*  Created in the IntelliJ IDEA.
    Created by 0utplay | Aldin Sijamhodzic
    Datum: 25.04.2020
    Uhrzeit: 17:20
*/
import de.tentact.languageapi.mysql.MySQL;
import de.tentact.languageapi.util.ConfigUtil;
import de.tentact.languageapi.util.I18N;
import de.tentact.languageapi.util.Updater;
import net.md_5.bungee.api.plugin.Plugin;

public class LanguageBungeecord extends Plugin {

    private static MySQL mySQL;

    @Override
    public void onEnable() {
        ConfigUtil.createBungeeCordMySQLConfig(this);
        ConfigUtil.initBungeecord();
        ConfigUtil.initLogger(this.getLogger());
        mySQL = ConfigUtil.getMySQL();
        mySQL.connect();
        mySQL.createDefaultTable();
        AbstractLanguageAPI.getInstance().createLanguage(ConfigUtil.getDefaultLanguage());
        I18N.createDefaultPluginMessages();

        new Updater(this);
    }

    @Override
    public void onDisable() {
        mySQL.closeConnection();
    }
}
