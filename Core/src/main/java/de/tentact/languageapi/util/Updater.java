package de.tentact.languageapi.util;
/*  Created in the IntelliJ IDEA.
    Created by 0utplay | Aldin Sijamhodzic
    Datum: 09.05.2020
    Uhrzeit: 16:53
*/

import de.tentact.languageapi.api.LanguageAPI;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.io.IOException;
import java.net.URL;
import java.util.Scanner;

public class Updater {

    private final int onlineVersion;
    private final int localVersion;
    private final String fullLocalVersion;
    private final String pluginName;
    private final boolean enabled;

    public Updater(Plugin plugin) {
        pluginName = plugin.getName();
        localVersion = Integer.parseInt(plugin.getDescription().getVersion().replace(".", ""));
        fullLocalVersion = plugin.getDescription().getVersion();
        onlineVersion = Integer.parseInt(getOnlineVersion(pluginName).replace(".", ""));
        if(onlineVersion > localVersion) {
            Bukkit.broadcastMessage(LanguageAPI.getInstance().getPrefix()+"Es ist ein neues Update verfügbar. Aktuelle Version: §6"
                    +plugin.getDescription().getVersion()+"§7, neuste Version: §c"+getOnlineVersion(pluginName));
        }
        enabled = true;



    }

    public Updater(net.md_5.bungee.api.plugin.Plugin plugin) {
        pluginName = plugin.getDescription().getName();
        localVersion = Integer.parseInt(plugin.getDescription().getVersion().replace(".", ""));
        fullLocalVersion = plugin.getDescription().getVersion();
        onlineVersion = Integer.parseInt(getOnlineVersion(pluginName).replace(".", ""));
        if(onlineVersion > localVersion) {
            ProxyServer.getInstance().broadcast(new TextComponent(LanguageAPI.getInstance().getPrefix()+"Es ist ein neues Update verfügbar. Aktuelle Version: §6"
                    +plugin.getDescription().getVersion()+"§7, neuste Version: §c"+getOnlineVersion(pluginName)));
        }
        enabled = true;

    }

    private String getOnlineVersion(String pluginName){
        Scanner scanner = null;
        try {
            scanner = new Scanner(new URL("http://tentact.de/plugins?"+pluginName.toLowerCase()).openStream());
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
    public String getOnlineVersion() {
        return getOnlineVersion(pluginName);
    }
    public boolean hasUpdate() {
        return onlineVersion > localVersion;
    }
    public String getLocalVersion() {
        return fullLocalVersion;
    }

    public boolean isEnabled() {
        return enabled;
    }
}
