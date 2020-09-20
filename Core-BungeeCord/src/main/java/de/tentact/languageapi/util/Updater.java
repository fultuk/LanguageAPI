package de.tentact.languageapi.util;
/*  Created in the IntelliJ IDEA.
    Created by 0utplay | Aldin Sijamhodzic
    Datum: 09.05.2020
    Uhrzeit: 16:53
*/

import de.tentact.languageapi.LanguageAPI;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
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
        int onlineVersion = Integer.parseInt(this.getOnlineVersion(pluginName).replace(".", ""));
        if(onlineVersion > localVersion) {
            ProxyServer.getInstance().broadcast(TextComponent.fromLegacyText(LanguageAPI.getInstance().getLanguageAPIPrefix()+"Es ist ein neues Update verfügbar. Aktuelle Version: §6"
                    +plugin.getDescription().getVersion()+"§7, neuste Version: §c"+this.getOnlineVersion(pluginName)));
        }
    }

    private String getOnlineVersion(String pluginName){
        Scanner scanner = null;
        try {
            this.plugin.getLogger().log(Level.INFO, "Creating connection to webserver");
            scanner = new Scanner(new URL("https://tentact.de/plugins?"+pluginName.toLowerCase()).openStream());
            this.plugin.getLogger().log(Level.INFO, "Fetched online version");
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(scanner == null) {
            return "0.0";
        }
        if(scanner.hasNextLine()) {
            return scanner.nextLine();
        }
        return "0.0";
    }
}
