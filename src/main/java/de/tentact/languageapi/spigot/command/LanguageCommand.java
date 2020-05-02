package de.tentact.languageapi.spigot.command;
/*  Created in the IntelliJ IDEA.
    Created by 0utplay | Aldin Sijamhodzic
    Datum: 25.04.2020
    Uhrzeit: 18:58
*/

import de.tentact.languageapi.api.LanguageAPI;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LanguageCommand implements CommandExecutor {
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (commandSender instanceof Player) {
            Player player = (Player) commandSender; //                  args0 1   2   3
            if (player.hasPermission("system.languageapi")) { //lang add lang key MSG | lang remove lang key | lang change lang key msg | lang createlang lang | lang deletelang lang
                if (args.length >= 2) {
                    switch (args[0].toLowerCase()) {
                        case "add":
                            if (args.length >= 4) {
                                if (LanguageAPI.getInstance().getAvailableLanguages().contains(args[1].toLowerCase())) {
                                    String key = args[2].toLowerCase();
                                    String lang = args[1].toLowerCase();
                                    if (!LanguageAPI.getInstance().isKey(key, lang)) {
                                        String msg = "";

                                        for (int i = 3; i < args.length; i++) {
                                            msg = msg + args[i] + " ";
                                        }
                                        LanguageAPI.getInstance().addMessage(key, msg, lang);
                                        player.sendMessage("TRYING TO DO");

                                    } else {
                                        player.sendMessage("KEY ALREADY EXISTS");
                                    }
                                } else {
                                    player.sendMessage("lang");
                                }
                            } else {
                                player.sendMessage("args");
                            }
                        case "remove":
                            if (args.length >= 3) {
                                if (LanguageAPI.getInstance().getAvailableLanguages().contains(args[1].toLowerCase())) {
                                    if (LanguageAPI.getInstance().isKey(args[2].toLowerCase(), args[1].toLowerCase())) {
                                        LanguageAPI.getInstance().deleteMessage(args[2].toLowerCase(), args[1].toLowerCase());
                                    }
                                }
                            }
                        case "change":
                            if (args.length >= 4) {
                                if (LanguageAPI.getInstance().getAvailableLanguages().contains(args[1].toLowerCase())) {
                                    String key = args[2].toLowerCase();
                                    String lang = args[1].toLowerCase();
                                    if (LanguageAPI.getInstance().isKey(key, lang)) {
                                        String msg = "";
                                        for (int i = 3; i < args.length; i++) {
                                            msg = msg + args[i] + " ";
                                        }
                                        LanguageAPI.getInstance().updateMessage(key, lang, msg);
                                    }


                                }
                            }
                        case "create":
                            if (!LanguageAPI.getInstance().getAvailableLanguages().contains(args[1].toLowerCase())) {
                                LanguageAPI.getInstance().createLanguage(args[1].toLowerCase());
                            }

                        case "delete":
                            if (LanguageAPI.getInstance().getAvailableLanguages().contains(args[1].toLowerCase()) && !LanguageAPI.getInstance().getDefaultLanguage().equalsIgnoreCase(args[1])) {
                                LanguageAPI.getInstance().deleteLanguage(args[1].toLowerCase());
                            }
                        case "copy":
                            if (args.length >= 3) {
                                String langfrom = args[1].toLowerCase();
                                String langto = args[2].toLowerCase();
                                if (LanguageAPI.getInstance().getAvailableLanguages().contains(langfrom) && LanguageAPI.getInstance().getAvailableLanguages().contains(langto)) {
                                    LanguageAPI.getInstance().copyLanguage(langfrom, langto);
                                    player.sendMessage("TRYING TO DO");
                                }else {
                                    for(String langs : LanguageAPI.getInstance().getAvailableLanguages()) {
                                        player.sendMessage(langs);
                                    }
                                    player.sendMessage("LANG 1" +langfrom);
                                    player.sendMessage("LANG 2" +langto);
                                }
                            }
                    }
                }
            }
        }
        return false;
    }

    void sendHelp(Player player) {
        player.sendMessage("A");
    }
}
