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

package de.tentact.languageapi.util;

import com.velocitypowered.api.plugin.PluginDescription;
import com.velocitypowered.api.proxy.ProxyServer;

import java.io.IOException;
import java.net.URL;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Updater {

    private final Logger logger;

    public Updater(ProxyServer proxyServer, Logger logger) {
        this.logger = logger;
        proxyServer.getPluginManager().getPlugin("languageapi").ifPresent(pluginContainer -> {
            PluginDescription description = pluginContainer.getDescription();
            description.getVersion().ifPresent(version -> description.getName().ifPresent(name -> {
                this.logger.log(Level.INFO, "Checking for updates...");
                int localVersion = Integer.parseInt(version.replace(".", ""));
                int onlineVersion = Integer.parseInt(this.getOnlineVersion(name).replace(".", ""));
                if (onlineVersion > localVersion) {
                    this.logger.log(Level.INFO, "There is a new version available. Current version: " + version + ", newest version: " + onlineVersion);
                }
            }));
        });


    }

    private String getOnlineVersion(String pluginName) {
        Scanner scanner = null;
        try {
            this.logger.log(Level.INFO, "Creating connection to webserver");
            scanner = new Scanner(new URL("https://tentact.de/plugins?" + pluginName.toLowerCase()).openStream());
            this.logger.log(Level.INFO, "Fetched online version");
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (scanner == null) {
            return "0.0";
        }
        if (scanner.hasNextLine()) {
            return scanner.nextLine();
        }
        return "0.0";
    }
}
