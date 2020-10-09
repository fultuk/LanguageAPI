package de.tentact.languageapi.util;
/*  Created in the IntelliJ IDEA.
    Created by 0utplay | Aldin Sijamhodzic
    Datum: 09.05.2020
    Uhrzeit: 16:53
*/

import de.tentact.languageapi.LanguageAPI;
import org.bukkit.Bukkit;
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
    private final boolean isEnabled;
    private final Plugin plugin;

    public Updater(Plugin plugin) {
        this.plugin = plugin;
        plugin.getLogger().log(Level.INFO, "Checking for updates...");
        this.pluginName = plugin.getName();
        this.localVersion = Integer.parseInt(plugin.getDescription().getVersion().replace(".", ""));
        this.fullLocalVersion = plugin.getDescription().getVersion();

        String online = this.getOnlineVersion(this.pluginName).replace(".", "");

        this.onlineVersion = Integer.parseInt(online);
        if (this.onlineVersion > this.localVersion) {
            Bukkit.broadcastMessage(LanguageAPI.getInstance().getLanguageAPIPrefix() + "Es ist ein neues Update verfügbar. Aktuelle Version: §6"
                    + plugin.getDescription().getVersion() + "§7, neuste Version: §c" + this.getOnlineVersion(this.pluginName));
        }
        this.isEnabled = true;
    }


    private String getOnlineVersion(String pluginName) {
        Scanner scanner = null;
        try {
            this.plugin.getLogger().log(Level.INFO, "Creating connection to webserver");
            scanner = new Scanner(new URL("https://tentact.de/plugins?" + pluginName.toLowerCase()).openStream());
            this.plugin.getLogger().log(Level.INFO, "Creating connection to webserver");
        } catch (IOException e) {
            System.err.print("While creating connection to the webserver an error occurred");
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

    public boolean isEnabled() {
        return this.isEnabled;
    }
}
