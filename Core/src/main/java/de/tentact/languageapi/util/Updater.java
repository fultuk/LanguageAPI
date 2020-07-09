package de.tentact.languageapi.util;
/*  Created in the IntelliJ IDEA.
    Created by 0utplay | Aldin Sijamhodzic
    Datum: 09.05.2020
    Uhrzeit: 16:53
*/

import de.tentact.languageapi.AbstractLanguageAPI;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
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

    public Updater(Plugin plugin) {
        this.pluginName = plugin.getName();
        this.localVersion = Integer.parseInt(plugin.getDescription().getVersion().replace(".", ""));
        this.fullLocalVersion = plugin.getDescription().getVersion();
        String online = this.getOnlineVersion(this.pluginName).replace(".", "");
        Source.log(online, Level.WARNING);
        this.onlineVersion = Integer.parseInt(online);
        if(this.onlineVersion > this.localVersion) {
            Bukkit.broadcastMessage(AbstractLanguageAPI.getInstance().getPrefix()+"Es ist ein neues Update verfügbar. Aktuelle Version: §6"
                    +plugin.getDescription().getVersion()+"§7, neuste Version: §c"+this.getOnlineVersion(this.pluginName));
        }
        this.isEnabled = true;



    }

    public Updater(net.md_5.bungee.api.plugin.Plugin plugin) {
        this.pluginName = plugin.getDescription().getName();
        this.localVersion = Integer.parseInt(plugin.getDescription().getVersion().replace(".", ""));
        this.fullLocalVersion = plugin.getDescription().getVersion();
        this.onlineVersion = Integer.parseInt(this.getOnlineVersion(this.pluginName).replace(".", ""));
        if(this.onlineVersion > this.localVersion) {
            ProxyServer.getInstance().broadcast(TextComponent.fromLegacyText(AbstractLanguageAPI.getInstance().getPrefix()+"Es ist ein neues Update verfügbar. Aktuelle Version: §6"
                    +plugin.getDescription().getVersion()+"§7, neuste Version: §c"+this.getOnlineVersion(this.pluginName)));
        }
        this.isEnabled = true;

    }

    private String getOnlineVersion(String pluginName){
        Scanner scanner = null;
        try {
            scanner = new Scanner(new URL("https://tentact.de/plugins?"+pluginName.toLowerCase()).openStream());
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
