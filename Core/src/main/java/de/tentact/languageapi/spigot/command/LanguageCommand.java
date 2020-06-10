package de.tentact.languageapi.spigot.command;
/*  Created in the IntelliJ IDEA.
    Created by 0utplay | Aldin Sijamhodzic
    Datum: 25.04.2020
    Uhrzeit: 18:58
*/

import de.tentact.languageapi.ILanguageAPI;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class LanguageCommand implements TabExecutor {


    public ILanguageAPI iLanguageAPI = ILanguageAPI.getInstance();

    private static final List<String> tabComplete = Arrays.asList("add", "remove", "update", "create", "delete", "param", "copy", "translations");

    public static ArrayList<Player> editingMessage = new ArrayList<>();

    public static HashMap<Player, List<String>> givenParameter = new HashMap<>();


    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, String[] args) {
        if (commandSender instanceof Player) {
            Player player = (Player) commandSender; //
            if (player.hasPermission("system.languageapi")) { //lang add lang key MSG | lang remove lang key | lang update lang key msg | lang createlang lang | lang deletelang lang
                if (args.length >= 2) {
                    switch (args[0].toLowerCase()) {
                        case "add":
                            if (!(args.length >= 4)) {
                                player.sendMessage(iLanguageAPI.getMessage("languageapi-add-help", player.getUniqueId(), true));
                                return false;
                            }
                            String lang = args[1].toLowerCase();
                            if (!iLanguageAPI.getAvailableLanguages().contains(args[1].toLowerCase())) {
                                player.sendMessage(iLanguageAPI.getMessage("languageapi-lang-not-found", player.getUniqueId(), true)
                                        .replace("%LANG%", lang));
                                return false;
                            }
                            String key = args[2].toLowerCase();
                            if (!iLanguageAPI.isKey(key, lang)) {
                                StringBuilder msg = new StringBuilder();

                                for (int i = 3; i < args.length; i++) {
                                    msg.append(args[i]).append(" ");
                                }
                                iLanguageAPI.addMessage(key, msg.toString(), lang);
                                player.sendMessage(iLanguageAPI.getMessage("languageapi-add-success", player.getUniqueId(), true)
                                        .replace("%KEY%", key)
                                        .replace("%LANG%", lang)
                                        .replace("%MSG%", msg.toString()));
                                return true;

                            } else {
                                player.sendMessage(iLanguageAPI.getMessage("languageapi-key-already-exists", player.getUniqueId(), true)
                                        .replace("%KEY%", key)
                                        .replace("%LANG%", lang));
                                return false;

                            }
                        case "update":
                            if (!(args.length >= 3)) {
                                player.sendMessage(iLanguageAPI.getMessage("languageapi-update-help", player.getUniqueId(), true));
                                return false;
                            }
                            lang = args[1].toLowerCase();
                            if (!iLanguageAPI.getAvailableLanguages().contains(lang)) {
                                player.sendMessage(iLanguageAPI.getMessage("languageapi-lang-not-found", player.getUniqueId(), true)
                                        .replace("%LANG%", lang));
                                return false;
                            }
                            key = args[2].toLowerCase();
                            if (iLanguageAPI.isKey(key, lang)) {

                                editingMessage.add(player);
                                givenParameter.put(player, Arrays.asList(key, lang));
                                player.sendMessage(iLanguageAPI.getMessage("languageapi-update-instructions", player.getUniqueId(), true));
                                return true;
                            } else {
                                player.sendMessage(iLanguageAPI.getMessage("languageapi-key-not-found", player.getUniqueId(), true)
                                        .replace("%KEY%", key)
                                        .replace("%LANG%", lang));
                                return false;
                            }
                        case "create":
                            lang = args[1].toLowerCase();
                            if (!iLanguageAPI.getAvailableLanguages().contains(lang)) {
                                iLanguageAPI.createLanguage(lang);
                                player.sendMessage(iLanguageAPI.getMessage("languageapi-create-success", player.getUniqueId(), true).replace("%LANG%", lang));
                            } else {
                                player.sendMessage(iLanguageAPI.getMessage("languageapi-lang-already-exists", player.getUniqueId(), true).replace("%LANG%", lang));
                            }
                            break;

                        case "delete":
                            lang = args[1].toLowerCase();
                            if (iLanguageAPI.getAvailableLanguages().contains(lang) && !iLanguageAPI.getDefaultLanguage().equalsIgnoreCase(lang)) {
                                iLanguageAPI.deleteLanguage(lang);
                                player.sendMessage(iLanguageAPI.getMessage("languageapi-delete-success", player.getUniqueId(), true).replace("%LANG%", lang));
                            } else if (lang.equalsIgnoreCase("*")) {
                                for (String langs : iLanguageAPI.getAvailableLanguages()) {
                                    iLanguageAPI.deleteLanguage(langs);
                                }
                            } else {
                                player.sendMessage(iLanguageAPI.getMessage("languageapi-lang-not-found", player.getUniqueId(), true)
                                        .replace("%LANG%", lang));
                            }
                            break;
                        case "copy":
                            if (args.length >= 3) {
                                String langfrom = args[1].toLowerCase();
                                String langto = args[2].toLowerCase();
                                if (iLanguageAPI.getAvailableLanguages().contains(langfrom) && iLanguageAPI.getAvailableLanguages().contains(langto)) {
                                    iLanguageAPI.copyLanguage(langfrom, langto);
                                    player.sendMessage(iLanguageAPI.getMessage("languageapi-copy-success", player.getUniqueId(), true)
                                            .replace("%OLDLANG%", langfrom)
                                            .replace("%NEWLANG%", langto));
                                } else {
                                    lang = langfrom;
                                    if (iLanguageAPI.getAvailableLanguages().contains(langfrom)) {
                                        lang = langto;
                                    }
                                    player.sendMessage(iLanguageAPI.getMessage("languageapi-lang-not-found", player.getUniqueId(), true)
                                            .replace("%LANG%", lang));

                                }


                            } else {
                                //HELP
                            }
                            break;
                        case "param": //lang show key
                            key = args[1].toLowerCase();
                            if (!iLanguageAPI.hasParameter(key) || iLanguageAPI.getParameter(key).equalsIgnoreCase("")) {
                                player.sendMessage(iLanguageAPI.getMessage("languageapi-key-has-no-param", player.getUniqueId(), true).replace("%KEY%", key));
                                break;
                            }
                            player.sendMessage(iLanguageAPI.getMessage("languageapi-show-success", player.getUniqueId(), true)
                                    .replace("%PARAM%", iLanguageAPI.getParameter(key)).replace("%KEY%", key));
                        case "translations":
                            lang = args[1].toLowerCase();
                            if (iLanguageAPI.getAvailableLanguages().contains(lang)) {
                                ArrayList<String> allKeys = iLanguageAPI.getAllKeys(lang);
                                for (int i = 0; i < allKeys.size(); i++) {
                                    player.sendMessage(iLanguageAPI.getMessage("languageapi-translation-success", player.getUniqueId(), true)
                                            .replace("%KEY%", allKeys.get(i))
                                            .replace("%MSG%", iLanguageAPI.getAllMessages(lang).get(i)));
                                }
                            }else{
                                player.sendMessage(iLanguageAPI.getMessage("languageapi-lang-not-found", player.getUniqueId(), true)
                                        .replace("%LANG%", lang));
                            }
                            break;
                        case "remove": //lang remove lang key
                            if (args.length >= 3) {
                                lang = args[1].toLowerCase();
                                key = args[2].toLowerCase();
                                if (iLanguageAPI.getDefaultLanguage().contains(lang)) {
                                    if (iLanguageAPI.isKey(key, lang)) {
                                        iLanguageAPI.deleteMessage(key, lang); //EINE SPRACHE EIN KEY
                                        player.sendMessage(iLanguageAPI.getMessage("languageapi-remove-key-in-lang", player.getUniqueId(), true)
                                                .replace("%KEY%", key)
                                                .replace("%LANG%", lang));
                                    } else if (key.endsWith("*")) {
                                        for (String keys : iLanguageAPI.getAllKeys(lang)) { //EINE SPRACHE JEDER KEY
                                            if (keys.startsWith(key.replace("*", ""))) {
                                                iLanguageAPI.deleteMessage(keys, lang);
                                            }
                                        }
                                        player.sendMessage(iLanguageAPI.getMessage("languageapi-remove-every-key-in-lang", player.getUniqueId(), true)
                                                .replace("%LANG%", lang)
                                                .replace("%STARTSWITH%", key.replace("*", "")));
                                    } else {
                                        player.sendMessage(iLanguageAPI.getMessage("languageapi-key-not-found", player.getUniqueId(), true)
                                                .replace("%KEY%", key)
                                                .replace("%LANG%", lang));
                                    }
                                } else if (lang.equalsIgnoreCase("*")) {
                                    if (key.endsWith("*")) { //JEDE SPRACHE JEDER KEY
                                        for (String langs : iLanguageAPI.getAvailableLanguages()) {
                                            for (String keys : iLanguageAPI.getAllKeys(langs)) {
                                                if (keys.startsWith(key.replace("*", ""))) {
                                                    iLanguageAPI.deleteMessage(keys, langs);
                                                }
                                            }
                                        }
                                        player.sendMessage(iLanguageAPI.getMessage("languageapi-remove-every-key-in-every-lang", player.getUniqueId(), true)
                                                .replace("%STARTSWITH%", key.replace("*", "")));
                                    } else { //JEDE SPRACHE EIN KEY
                                        for (String langs : iLanguageAPI.getAvailableLanguages()) {
                                            if (iLanguageAPI.isKey(key, langs)) {
                                                iLanguageAPI.deleteMessage(key, langs);
                                            }
                                        }
                                        player.sendMessage(iLanguageAPI.getMessage("languageapi-remove-key-in-every-lang", player.getUniqueId(), true)
                                                .replace("%KEY%", key));
                                    }
                                }
                            }
                            break;
                    }
                }else{
                    //SEND ALL COMMANDS
                }
            }
        }
        return false;

    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] args) {
        if (args.length == 1) {
            return this.getCompletes(args[0], tabComplete);
        } else if (args.length == 2) {
            if (!args[0].equalsIgnoreCase("param")) {
                return this.getCompletes(args[1], iLanguageAPI.getAvailableLanguages());
            }
            return this.getCompletes(args[1], iLanguageAPI.getAllKeys(iLanguageAPI.getDefaultLanguage()));
        } else if (args.length == 3) {
            List<String> keyComplete = Arrays.asList("add", "remove", "update");
            if (keyComplete.contains(args[0].toLowerCase())) {
                return this.getCompletes(args[2], iLanguageAPI.getAllKeys(args[1].toLowerCase()));
            }
        }
        return Collections.emptyList();

    }

    private List<String> getCompletes(String playerInput, List<String> tabComplete) {
        List<String> possibleCompletes = new ArrayList<>();
        StringUtil.copyPartialMatches(playerInput, tabComplete, possibleCompletes);
        Collections.sort(possibleCompletes);
        return possibleCompletes;
    }
}
