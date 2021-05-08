/*
 * MIT License
 *
 * Copyright (c) 2020 0utplay (Aldin Sijamhodzic)
 * Copyright (c) 2020 contributors
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

import com.google.inject.Inject;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.PluginDescription;
import com.velocitypowered.api.proxy.ProxyServer;
import de.tentact.languageapi.api.VelocityLanguageAPI;
import de.tentact.languageapi.configuration.Configuration;
import de.tentact.languageapi.configuration.DatabaseProvider;
import de.tentact.languageapi.configuration.LanguageConfig;
import de.tentact.languageapi.util.UpdateNotifier;

import java.util.logging.Logger;

@Plugin(id = "languageapi", name = "LanguageAPI", version = "2.0-SNAPSHOT-1111111", authors = {"0utplay"})
public class LanguageVelocity {

    @Inject
    public LanguageVelocity(ProxyServer proxyServer, Logger logger, PluginDescription pluginDescription) {
        Configuration configuration = new Configuration(logger);
        LanguageConfig languageConfig = configuration.getLanguageConfig();

        DatabaseProvider databaseProvider = configuration.getLanguageConfig().getDatabaseProvider();
        databaseProvider.connect();
        LanguageAPI.setInstance(new VelocityLanguageAPI(proxyServer, languageConfig));

        databaseProvider.createDefaultTable();
        LanguageAPI.getInstance().createLanguage(languageConfig.getLanguageSetting().getDefaultLanguage());

        this.checkForUpdates(new UpdateNotifier(), pluginDescription, logger);
    }

    private void checkForUpdates(UpdateNotifier updateNotifier, PluginDescription pluginDescription, Logger logger) {
        pluginDescription.getVersion().ifPresent(version ->
                updateNotifier.checkForUpdates(version, pluginDescription.getId(), logger));
    }

}
