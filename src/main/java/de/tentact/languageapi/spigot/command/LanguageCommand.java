package de.tentact.languageapi.spigot.command;
/*  Created in the IntelliJ IDEA.
    Created by 0utplay | Aldin Sijamhodzic
    Datum: 25.04.2020
    Uhrzeit: 18:58
*/

import de.tentact.languageapi.api.LanguageAPI;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.*;

public class LanguageCommand implements TabExecutor {


    public LanguageAPI languageAPI = LanguageAPI.getInstance();

    private static List<String> tabComplete = Arrays.asList("add", "remove", "update", "create", "delete", "param", "copy", "translations");

    public static ArrayList<Player> editingMessage = new ArrayList<>();

    public static HashMap<Player, List<String>> givenParameter = new HashMap<>();


    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (commandSender instanceof Player) {
            Player player = (Player) commandSender; //
            if (player.hasPermission("system.languageapi")) { //lang add lang key MSG | lang remove lang key | lang update lang key msg | lang createlang lang | lang deletelang lang
                if (args.length >= 2) {
                    switch (args[0].toLowerCase()) {
                        case "add":
                            if (!(args.length >= 4)) {
                                player.sendMessage(languageAPI.getPrefix() + languageAPI.getMessage("languageapi-add-help", player.getUniqueId()));
                                return false;
                            }
                            String lang = args[1].toLowerCase();
                            if (!languageAPI.getAvailableLanguages().contains(args[1].toLowerCase())) {
                                player.sendMessage(languageAPI.getPrefix() + languageAPI.getMessage("languageapi-lang-not-found", player.getUniqueId())
                                        .replace("%LANG%", lang));
                                return false;
                            }
                            String key = args[2].toLowerCase();
                            if (!languageAPI.isKey(key, lang)) {
                                StringBuilder msg = new StringBuilder();

                                for (int i = 3; i < args.length; i++) {
                                    msg.append(args[i]).append(" ");
                                }
                                languageAPI.addMessage(key, msg.toString(), lang);
                                player.sendMessage(languageAPI.getPrefix() + languageAPI.getMessage("languageapi-add-success", player.getUniqueId())
                                        .replace("%KEY%", key)
                                        .replace("%LANG%", lang)
                                        .replace("%MSG%", msg.toString()));

                            } else {
                                player.sendMessage(languageAPI.getPrefix() + languageAPI.getMessage("languageapi-key-already-exists", player.getUniqueId())
                                        .replace("%KEY%", key)
                                        .replace("%LANG%", lang));

                            }

                            break;
                        case "update":
                            if (!(args.length >= 3)) {
                                //HELP
                                return false;
                            }
                            lang = args[1].toLowerCase();
                            if (!languageAPI.getAvailableLanguages().contains(lang)) {
                                player.sendMessage(languageAPI.getPrefix() + languageAPI.getMessage("languageapi-lang-not-found", player.getUniqueId())
                                        .replace("%LANG%", lang));
                                return false;
                            }
                            key = args[2].toLowerCase();
                            if (languageAPI.isKey(key, lang)) {

                                editingMessage.add(player);
                                givenParameter.put(player, Arrays.asList(key, lang));
                                player.sendMessage(languageAPI.getPrefix() + languageAPI.getMessage("languageapi-update-instructions", player.getUniqueId()));
                            } else {
                                player.sendMessage(languageAPI.getPrefix() + languageAPI.getMessage("languageapi-key-not-found", player.getUniqueId())
                                        .replace("%KEY%", key)
                                        .replace("%LANG%", lang));
                            }
                            break;
                        case "create":
                            lang = args[1].toLowerCase();
                            if (!languageAPI.getAvailableLanguages().contains(lang)) {
                                languageAPI.createLanguage(lang);
                                player.sendMessage(languageAPI.getPrefix() + languageAPI.getMessage("languageapi-create-success", player.getUniqueId()).replace("%LANG%", lang));
                            } else {
                                player.sendMessage(languageAPI.getPrefix() + languageAPI.getMessage("languageapi-lang-already-exists", player.getUniqueId()).replace("%LANG%", lang));
                            }
                            break;

                        case "delete":
                            lang = args[1].toLowerCase();
                            if (languageAPI.getAvailableLanguages().contains(lang) && !languageAPI.getDefaultLanguage().equalsIgnoreCase(lang)) {
                                languageAPI.deleteLanguage(lang);
                                player.sendMessage(languageAPI.getPrefix() + languageAPI.getMessage("languageapi-delete-success", player.getUniqueId()).replace("%LANG%", lang));
                            } else if (lang.equalsIgnoreCase("*")) {
                                for (String langs : languageAPI.getAvailableLanguages()) {
                                    languageAPI.deleteLanguage(langs);
                                }
                            } else {
                                player.sendMessage(languageAPI.getPrefix() + languageAPI.getMessage("languageapi-lang-not-found", player.getUniqueId())
                                        .replace("%LANG%", lang));
                            }
                            break;
                        case "copy":
                            if (args.length >= 3) {
                                String langfrom = args[1].toLowerCase();
                                String langto = args[2].toLowerCase();
                                if (languageAPI.getAvailableLanguages().contains(langfrom) && languageAPI.getAvailableLanguages().contains(langto)) {
                                    languageAPI.copyLanguage(langfrom, langto);
                                    player.sendMessage(languageAPI.getPrefix() + languageAPI.getMessage("languageapi-copy-success", player.getUniqueId())
                                            .replace("%OLDLANG%", langfrom)
                                            .replace("%NEWLANG%", langto));
                                } else {
                                    lang = langfrom;
                                    if (languageAPI.getAvailableLanguages().contains(langfrom)) {
                                        lang = langto;
                                    }
                                    player.sendMessage(languageAPI.getPrefix() + languageAPI.getMessage("languageapi-lang-not-found", player.getUniqueId())
                                            .replace("%LANG%", lang));

                                }


                            } else {
                                //HELP
                            }
                            break;
                        case "param": //lang show key
                            key = args[1].toLowerCase();
                            if (!languageAPI.hasParameter(key) || languageAPI.getParameter(key).equalsIgnoreCase("")) {
                                player.sendMessage(languageAPI.getPrefix() + languageAPI.getMessage("languageapi-key-has-no-param", player.getUniqueId()).replace("%KEY%", key));
                                break;
                            }
                            player.sendMessage(languageAPI.getPrefix() + languageAPI.getMessage("languageapi-show-success", player.getUniqueId())
                                    .replace("%PARAM%", languageAPI.getParameter(key)).replace("%KEY%", key));
                        case "translations":
                            lang = args[1].toLowerCase();
                            if (languageAPI.getAvailableLanguages().contains(lang)) {
                                ArrayList<String> allKeys = languageAPI.getAllKeys(lang);
                                for (int i = 0; i < allKeys.size(); i++) {
                                    player.sendMessage(languageAPI.getMessage("languageapi-translation-success", player.getUniqueId())
                                            .replace("%KEY%", allKeys.get(i))
                                            .replace("%MSG%", languageAPI.getAllMessages(lang).get(i)));
                                }
                            }
                            break;
                        case "remove": //lang drop lang key
                            if (args.length >= 3) {
                                lang = args[1].toLowerCase();
                                key = args[2].toLowerCase();
                                if (languageAPI.getDefaultLanguage().contains(lang)) {
                                    if (languageAPI.isKey(key, lang)) {
                                        languageAPI.deleteMessage(key, lang); //EINE SPRACHE EIN KEY
                                        player.sendMessage(languageAPI.getMessage("languageapi-remove-key-in-lang", player.getUniqueId())
                                                .replace("%KEY%", key)
                                                .replace("%LANG%", lang));
                                    } else if (key.endsWith("*")) {
                                        for (String keys : languageAPI.getAllKeys(lang)) { //EINE SPRACJE JEDER KEY
                                            if (keys.startsWith(key.replace("*", ""))) {
                                                languageAPI.deleteMessage(keys, lang);
                                            }
                                        }
                                        player.sendMessage(languageAPI.getMessage("languageapi-remove-every-key-in-lang", player.getUniqueId())
                                                .replace("%LANG%", lang)
                                                .replace("%STARTSWITH%", key.replace("*", "")));
                                    } else {
                                        player.sendMessage(languageAPI.getMessage("languageapi-key-not-found", player.getUniqueId())
                                                .replace("%KEY%", key)
                                                .replace("%LANG%", lang));
                                    }
                                } else if (lang.equalsIgnoreCase("*")) {
                                    if (key.endsWith("*")) { //JEDE SPRACHE JEDER KEY
                                        for (String langs : languageAPI.getAvailableLanguages()) {
                                            for (String keys : languageAPI.getAllKeys(langs)) {
                                                if (keys.startsWith(key.replace("*", ""))) {
                                                    languageAPI.deleteMessage(keys, langs);
                                                }
                                            }
                                        }
                                        player.sendMessage(languageAPI.getMessage("languageapi-remove-every-key-in-every-lang", player.getUniqueId())
                                                .replace("%STARTSWITH%", key.replace("*", "")));
                                    } else { //JEDE SPRACHE EIN KEY
                                        for (String langs : languageAPI.getAvailableLanguages()) {
                                            if (languageAPI.isKey(key, langs)) {
                                                languageAPI.deleteMessage(key, langs);
                                            }
                                        }
                                        player.sendMessage(languageAPI.getMessage("languageapi-remove-key-in-every-lang", player.getUniqueId())
                                                .replace("%KEY%", key));
                                    }
                                }
                            }
                            break;
                    }
                }
            }
        }
        return false;

    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] args) {
        if (args.length == 1) {
            return getCompletes(args[0], tabComplete);
        } else if (args.length == 2) {
            if (!args[0].equalsIgnoreCase("param")) {
                return getCompletes(args[1], languageAPI.getAvailableLanguages());
            }
            return getCompletes(args[1], languageAPI.getAllKeys(languageAPI.getDefaultLanguage()));
        } else if (args.length == 3) {
            List<String> keyComplete = Arrays.asList("add", "remove", "update");
            if (keyComplete.contains(args[0].toLowerCase())) {
                return getCompletes(args[2], languageAPI.getAllKeys(args[1].toLowerCase()));
            }
        }
        return Collections.emptyList();

    }

    public List<String> getCompletes(String playerInput, List<String> tabComplete) {
        List<String> possibleCompletes = new ArrayList<>();
        StringUtil.copyPartialMatches(playerInput, tabComplete, possibleCompletes);
        Collections.sort(possibleCompletes);
        return possibleCompletes;
    }
}
