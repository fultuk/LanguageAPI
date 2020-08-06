package de.tentact.languageapi;
/*  Created in the IntelliJ IDEA.
    Created by 0utplay | Aldin Sijamhodzic
    Datum: 25.04.2020
    Uhrzeit: 17:01
*/

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import de.tentact.languageapi.command.LanguageCommand;
import de.tentact.languageapi.listener.ChatListener;
import de.tentact.languageapi.listener.JoinListener;
import de.tentact.languageapi.listener.ProtocolListener;
import de.tentact.languageapi.mysql.MySQL;
import de.tentact.languageapi.util.ConfigUtil;
import de.tentact.languageapi.util.I18N;
import de.tentact.languageapi.util.Updater;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.logging.Level;

public class LanguageSpigot extends JavaPlugin {

    private Updater updater;
    private MySQL mySQL;
    private int protocolRestart = 6;
    private ProtocolManager protocolManager;

    @Override
    public void onEnable() {
        getLogger().log(Level.INFO, "Starting LanguageAPI");

        getLogger().log(Level.INFO, "Checking for ProtocolLib...");

        if (!this.hasProtocolLib()) {
            getLogger().log(Level.INFO, "ProtocolLib was not found.\nDownloading ProtocolLib...");
            this.installProtocolLib();
        } else {
            getLogger().log(Level.INFO, "Found ProtocolLib...");
            //protocolManager = ProtocolLibrary.getProtocolManager();
            //new ProtocolListener(this);
        }

        ConfigUtil.createSpigotMySQLConfig();
        ConfigUtil.init();
        ConfigUtil.initLogger(this.getLogger());

        this.mySQL = ConfigUtil.getMySQL();
        this.mySQL.connect();
        this.mySQL.createDefaultTable();

        LanguageAPI.getInstance().createLanguage(ConfigUtil.getDefaultLanguage());
        I18N.createDefaultPluginMessages();
        this.updater = new Updater(this);

        Objects.requireNonNull(this.getCommand("languageapi")).setExecutor(new LanguageCommand(this));
        Objects.requireNonNull(this.getCommand("languageapi")).setTabCompleter(new LanguageCommand(this));

        PluginManager pm = Bukkit.getPluginManager();
        pm.registerEvents(new JoinListener(), this);
        pm.registerEvents(new ChatListener(), this);

    }

    @Override
    public void onDisable() {
        mySQL.closeConnection();
    }

    public Updater getUpdater() {
        return this.updater;
    }

    public boolean hasProtocolLib() {
        PluginManager pluginManager = Bukkit.getPluginManager();

        return pluginManager.getPlugin("ProtocolLib") != null;
    }

    public void installProtocolLib() {
        try (BufferedInputStream inputStream = new BufferedInputStream(new URL("https://ci.dmulloy2.net/job/ProtocolLib/lastSuccessfulBuild/artifact/target/ProtocolLib.jar").openStream());
             FileOutputStream fileOutputStream = new FileOutputStream("/plugins/ProtocolLib.jar")) {
            byte[] data = new byte[1024];
            int byteContent;
            while ((byteContent = inputStream.read(data, 0, 1024)) != -1) {
                fileOutputStream.write(data, 0, byteContent);
            }
            getLogger().log(Level.INFO, "Finished download of ProtocolLib");

            Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> {
                if(protocolRestart != 0) {
                    protocolRestart--;
                }
                getLogger().log(Level.WARNING, "Stopping Server in "+protocolRestart+"s to inject ProtocolLib...");
                if(protocolRestart == 0) {
                    getLogger().log(Level.WARNING, "Stopping Server...");
                    Bukkit.shutdown();
                }
            }, 0L, 20L);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public ProtocolManager getProtocolManager() {
        return this.protocolManager;
    }
}
