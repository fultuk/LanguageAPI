package de.tentact.languageapi.util;
/*  Created in the IntelliJ IDEA.
    Created by 0utplay | Aldin Sijamhodzic
    Datum: 09.05.2020
    Uhrzeit: 16:53
*/

import com.velocitypowered.api.plugin.PluginContainer;
import com.velocitypowered.api.plugin.PluginDescription;
import com.velocitypowered.api.proxy.ProxyServer;
import de.tentact.languageapi.LanguageAPI;
import net.kyori.text.serializer.legacy.LegacyComponentSerializer;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Updater {

    private final Logger logger;

    public Updater(ProxyServer proxyServer, Logger logger) {
        this.logger = logger;
        Optional<PluginContainer> optionalPluginContainer = proxyServer.getPluginManager().getPlugin("languageapi");
        if (optionalPluginContainer.isPresent()) {
            PluginDescription description = optionalPluginContainer.get().getDescription();
            this.logger.log(Level.INFO, "Checking for updates...");

            String pluginName = description.getName().get();
            int localVersion = Integer.parseInt(description.getVersion().get().replace(".", ""));
            int onlineVersion = Integer.parseInt(this.getOnlineVersion(pluginName).replace(".", ""));
            if (onlineVersion > localVersion) {
                this.logger.log(Level.INFO, "There is a new version available. Current version: " + description.getVersion() + ", newest version: " + onlineVersion);
            }
        }

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
