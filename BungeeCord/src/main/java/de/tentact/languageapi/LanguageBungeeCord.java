/*
 * MIT License
 *
 * Copyright (c) 2020 0utplay
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package de.tentact.languageapi;

import de.tentact.languageapi.api.BungeeCordLanguageAPI;
import de.tentact.languageapi.configuration.Configuration;
import de.tentact.languageapi.configuration.LanguageConfig;
import de.tentact.languageapi.configuration.MySQL;
import de.tentact.languageapi.util.Updater;
import net.md_5.bungee.api.plugin.Plugin;

public class LanguageBungeeCord extends Plugin {

    private MySQL mySQL;

    @Override
    public void onEnable() {
        Configuration configuration = new Configuration(this.getLogger());
        LanguageConfig languageConfig = configuration.getLanguageConfig();
        mySQL = configuration.getLanguageConfig().getMySQL();
        mySQL.connect();
        LanguageAPI.setInstance(new BungeeCordLanguageAPI(languageConfig));
        mySQL.createDefaultTable();
        LanguageAPI.getInstance().createLanguage(languageConfig.getLanguageSetting().getDefaultLanguage());
        new Updater(this);
    }

    @Override
    public void onDisable() {
        mySQL.closeConnection();
    }
}
