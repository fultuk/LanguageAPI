package de.tentact.languageapi.listener;
/*  Created in the IntelliJ IDEA.
    Copyright(c) 2020
    Created by 0utplay | Aldin Sijamhodzic
    Datum: 28.07.2020
    Uhrzeit: 10:37
*/

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import de.tentact.languageapi.LanguageAPI;
import de.tentact.languageapi.LanguageSpigot;

public class ProtocolListener {

    private final LanguageAPI languageAPI = LanguageAPI.getInstance();

    public ProtocolListener(LanguageSpigot languageSpigot) {
        languageSpigot.getProtocolManager().addPacketListener(new PacketAdapter(languageSpigot, ListenerPriority.NORMAL, PacketType.Play.Client.SETTINGS) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                if(event.getPacketType() == PacketType.Play.Client.SETTINGS) {
                    PacketContainer packetContainer = event.getPacket();
                    String language = packetContainer.getStrings().readSafely(0).toLowerCase();
                    if(!languageAPI.isLanguage(language)) {
                        return;
                    }

                    if(languageAPI.getPlayerExecutor().getPlayerLanguage(event.getPlayer().getUniqueId()).equalsIgnoreCase(language)) {
                        return;
                    }
                    languageAPI.getPlayerExecutor().setPlayerLanguage(event.getPlayer().getUniqueId(), language);
                }
            }
        });
    }

}
