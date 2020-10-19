package de.tentact.languageapi.util;
/*  Created in the IntelliJ IDEA.
    Created by 0utplay | Aldin Sijamhodzic
    Datum: 09.05.2020
    Uhrzeit: 16:53
*/

import net.md_5.bungee.api.plugin.Plugin;

import java.io.IOException;
import java.net.URL;
import java.util.Scanner;
import java.util.logging.Level;

public class Updater {

    private final Plugin plugin;

    public Updater(Plugin plugin) {
        this.plugin = plugin;
        plugin.getLogger().log(Level.INFO, "Checking for updates...");
        String pluginName = plugin.getDescription().getName();
        int localVersion = Integer.parseInt(plugin.getDescription().getVersion().replace(".", ""));
        String fetchedVersion = this.getOnlineVersion(pluginName);

        int onlineVersion = Integer.parseInt(fetchedVersion.replace(".", ""));
        if (onlineVersion > localVersion) {
            this.plugin.getLogger().log(Level.INFO, "There is a new version available. Current version: " + plugin.getDescription().getVersion() + ", newest version: " + fetchedVersion);
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
}
