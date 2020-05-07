package de.tentact.languageapi.spigot.command;
/*  Created in the IntelliJ IDEA.
    Created by 0utplay | Aldin Sijamhodzic
    Datum: 25.04.2020
    Uhrzeit: 18:58
*/

import de.tentact.languageapi.api.LanguageAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class LanguageCommand implements CommandExecutor, TabCompleter {


    public LanguageAPI languageAPI = LanguageAPI.getInstance();

    private static List<String> tabComplete = Arrays.asList("add","remove", "change", "create", "delete", "param", "copy");

    public static ArrayList<Player> editingMessage = new ArrayList<>();

    public static HashMap<Player, List<String>> givenParameter = new HashMap<>();


    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (commandSender instanceof Player) {
            Player player = (Player) commandSender; //               args[] 0    1    2   3  length    1     2   3
            if (player.hasPermission("system.languageapi")) { //lang add lang key MSG | lang remove lang key | lang change lang key msg | lang createlang lang | lang deletelang lang
                if (args.length >= 2) {
                    switch (args[0].toLowerCase()) {
                        case "add":
                            if (args.length >= 4) {
                                String lang = args[1].toLowerCase();
                                if (languageAPI.getAvailableLanguages().contains(args[1].toLowerCase())) {
                                    String key = args[2].toLowerCase();
                                    if (!languageAPI.isKey(key, lang)) {
                                        StringBuilder msg = new StringBuilder();

                                        for (int i = 3; i < args.length; i++) {
                                            msg.append(args[i]).append(" ");
                                        }
                                        languageAPI.addMessage(key, msg.toString(), lang);
                                        player.sendMessage(languageAPI.getPrefix()+languageAPI.getMessage("languageapi-add-success", player.getUniqueId())
                                                .replace("%KEY%", key)
                                                .replace("%LANG%", lang)
                                                .replace("%MSG%", msg.toString()));

                                    } else {
                                        player.sendMessage(languageAPI.getPrefix()+languageAPI.getMessage("languageapi-key-already-exists", player.getUniqueId())
                                                .replace("%KEY%", key)
                                                .replace("%LANG%", lang));

                                    }
                                } else {
                                    player.sendMessage(languageAPI.getPrefix()+languageAPI.getMessage("languageapi-lang-not-found", player.getUniqueId())
                                            .replace("%LANG%", lang));
                                }
                            } else {
                                player.sendMessage(languageAPI.getPrefix()+languageAPI.getMessage("languageapi-add-help", player.getUniqueId()));
                            }
                            break;

                        case "remove":
                            if (args.length >= 3) {
                                String lang = args[1].toLowerCase();
                                String key = args[2].toLowerCase();
                                if (languageAPI.getAvailableLanguages().contains(lang)) {
                                    if (languageAPI.isKey(key, lang)) {
                                        languageAPI.deleteMessage(key, lang);
                                        player.sendMessage(languageAPI.getPrefix()+languageAPI.getMessage("languageapi-remove-success", player.getUniqueId())
                                                .replace("%KEY%", key).replace("%LANG", lang));
                                        break;
                                    } else {
                                        player.sendMessage(languageAPI.getPrefix()+languageAPI.getMessage("languageapi-key-not-found", player.getUniqueId())
                                                .replace("%KEY%", key)
                                                .replace("%LANG%", lang));
                                        break;
                                    }
                                } else {
                                    player.sendMessage(languageAPI.getPrefix()+languageAPI.getMessage("languageapi-lang-not-found", player.getUniqueId())
                                            .replace("%LANG%", lang));
                                    break;
                                }
                            } else {
                                //HELP
                                break;
                            }

                        case "update":
                            if (args.length >= 3) {
                                String lang = args[1].toLowerCase();
                                if (languageAPI.getAvailableLanguages().contains(lang)) {
                                    String key = args[2].toLowerCase();
                                    if (languageAPI.isKey(key, lang)) {

                                        editingMessage.add(player);
                                        givenParameter.put(player, Arrays.asList(key, lang));
                                        player.sendMessage(languageAPI.getPrefix()+languageAPI.getMessage("languageapi-update-instructions", player.getUniqueId()));
                                    } else {
                                        player.sendMessage(languageAPI.getPrefix()+languageAPI.getMessage("languageapi-key-not-found", player.getUniqueId())
                                                .replace("%KEY%", key)
                                                .replace("%LANG%", lang));
                                    }
                                } else {
                                    player.sendMessage(languageAPI.getPrefix()+languageAPI.getMessage("languageapi-lang-not-found", player.getUniqueId())
                                            .replace("%LANG%", lang));
                                }
                            } else {
                                //HELP
                            }
                            break;
                        case "create":
                            String lang = args[1].toLowerCase();
                            if (!languageAPI.getAvailableLanguages().contains(lang)) {
                                languageAPI.createLanguage(lang);
                                player.sendMessage(languageAPI.getPrefix()+languageAPI.getMessage("languageapi-create-success", player.getUniqueId()).replace("%LANG%", lang));
                            } else {
                                player.sendMessage(languageAPI.getPrefix()+languageAPI.getMessage("languageapi-lang-already-exists", player.getUniqueId()).replace("%LANG%", lang));
                            }
                            break;

                        case "delete":
                            lang = args[1].toLowerCase();
                            if (languageAPI.getAvailableLanguages().contains(lang) && !languageAPI.getDefaultLanguage().equalsIgnoreCase(lang)) {
                                languageAPI.deleteLanguage(lang);
                                player.sendMessage(languageAPI.getPrefix()+languageAPI.getMessage("languageapi-delete-success", player.getUniqueId()).replace("%LANG%", lang));
                            } else {
                                player.sendMessage(languageAPI.getPrefix()+languageAPI.getMessage("languageapi-lang-not-found", player.getUniqueId())
                                        .replace("%LANG%", lang));
                            }
                            break;
                        case "copy":
                            if (args.length >= 3) {
                                String langfrom = args[1].toLowerCase();
                                String langto = args[2].toLowerCase();
                                if (languageAPI.getAvailableLanguages().contains(langfrom) && languageAPI.getAvailableLanguages().contains(langto)) {
                                    languageAPI.copyLanguage(langfrom, langto);
                                    player.sendMessage(languageAPI.getPrefix()+languageAPI.getMessage("languageapi-copy-success", player.getUniqueId())
                                            .replace("%OLDLANG%", langfrom)
                                            .replace("%NEWLANG%", langto));
                                } else {
                                    player.sendMessage(languageAPI.getPrefix()+languageAPI.getMessage("languageapi-lang-not-found", player.getUniqueId())
                                            .replace("%OLDLANG%", langfrom)
                                            .replace("%NEWLANG%", langto));
                                }
                            } else {
                                //HELP
                            }
                            break;
                        case "param": //lang show key
                            String key = args[1].toLowerCase();
                            if (!languageAPI.hasParameter(key)) {
                                player.sendMessage(languageAPI.getPrefix()+languageAPI.getMessage("languageapi-key-has-no-param-found", player.getUniqueId()).replace("%KEY%", key));
                                break;
                            }
                            player.sendMessage(languageAPI.getPrefix()+languageAPI.getMessage("languageapi-show-success", player.getUniqueId())
                                    .replace("%PARAM%", languageAPI.getParameter(key)).replace("%KEY%", key));
                        case "":
                    }
                }
            }
        }
        return false;

    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] args) {
        if(commandSender instanceof Player) {
            Player player = (Player) commandSender;
            if(player.hasPermission("system.languageapi")) {
                return tabComplete;
            }
        }

        return null;
    }
}
