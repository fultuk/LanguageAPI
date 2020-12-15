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

package de.tentact.languageapi.util;

import org.bukkit.plugin.Plugin;

import java.io.IOException;
import java.net.URL;
import java.util.Scanner;
import java.util.logging.Level;

public class Updater {

    private final int onlineVersion;
    private final int localVersion;
    private final String fullLocalVersion;
    private final String pluginName;
    private final Plugin plugin;

    public Updater(Plugin plugin) {
        this.plugin = plugin;
        plugin.getLogger().log(Level.INFO, "Checking for updates...");
        this.pluginName = plugin.getName();
        this.localVersion = Integer.parseInt(plugin.getDescription().getVersion().replace(".", ""));
        this.fullLocalVersion = plugin.getDescription().getVersion();

        String onlineVersion = this.getOnlineVersion(this.pluginName);

        this.onlineVersion = Integer.parseInt(onlineVersion.replace(".", ""));
        if (this.onlineVersion > this.localVersion) {
            this.plugin.getLogger().log(Level.INFO, "There is a new version available. Current version: " + plugin.getDescription().getVersion() + ", newest version: " + onlineVersion);
        }
        if(this.localVersion > this.onlineVersion) {
            this.plugin.getLogger().log(Level.WARNING, "You are using a snapshot version of the LanguageAPI, this version is not as stable as the release.");
        }
    }


    private String getOnlineVersion(String pluginName) {
        Scanner scanner = null;
        try {
            this.plugin.getLogger().log(Level.INFO, "Creating connection to webserver.");
            scanner = new Scanner(new URL("https://tentact.de/plugins?" + pluginName.toLowerCase()).openStream());
            this.plugin.getLogger().log(Level.INFO, "Fetched online version.");
        } catch (IOException e) {
            this.plugin.getLogger().log(Level.WARNING, "While creating connection to the webserver an error occurred.");
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

    public String getOnlineVersion() {
        return this.getOnlineVersion(pluginName);
    }

    public boolean hasUpdate() {
        return this.onlineVersion > this.localVersion;
    }

    public String getLocalVersion() {
        return this.fullLocalVersion;
    }
}
