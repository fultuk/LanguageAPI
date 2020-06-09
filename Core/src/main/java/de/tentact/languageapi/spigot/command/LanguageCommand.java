package de.tentact.languageapi.spigot.command;
/*  Created in the IntelliJ IDEA.
    Created by 0utplay | Aldin Sijamhodzic
    Datum: 25.04.2020
    Uhrzeit: 18:58
*/

import de.tentact.languageapi.api.LanguageImpl;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.*;

public class LanguageCommand implements TabExecutor {


    public LanguageImpl languageImpl = LanguageImpl.getInstance();

    private static final List<String> tabComplete = Arrays.asList("add", "remove", "update", "create", "delete", "param", "copy", "translations");

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
                                player.sendMessage(languageImpl.getMessage("languageapi-add-help", player.getUniqueId(), true));
                                return false;
                            }
                            String lang = args[1].toLowerCase();
                            if (!languageImpl.getAvailableLanguages().contains(args[1].toLowerCase())) {
                                player.sendMessage(languageImpl.getMessage("languageapi-lang-not-found", player.getUniqueId(), true)
                                        .replace("%LANG%", lang));
                                return false;
                            }
                            String key = args[2].toLowerCase();
                            if (!languageImpl.isKey(key, lang)) {
                                StringBuilder msg = new StringBuilder();

                                for (int i = 3; i < args.length; i++) {
                                    msg.append(args[i]).append(" ");
                                }
                                languageImpl.addMessage(key, msg.toString(), lang);
                                player.sendMessage(languageImpl.getMessage("languageapi-add-success", player.getUniqueId(), true)
                                        .replace("%KEY%", key)
                                        .replace("%LANG%", lang)
                                        .replace("%MSG%", msg.toString()));
                                return true;

                            } else {
                                player.sendMessage(languageImpl.getMessage("languageapi-key-already-exists", player.getUniqueId(), true)
                                        .replace("%KEY%", key)
                                        .replace("%LANG%", lang));
                                return false;

                            }
                        case "update":
                            if (!(args.length >= 3)) {
                                player.sendMessage(languageImpl.getMessage("languageapi-update-help", player.getUniqueId(), true));
                                return false;
                            }
                            lang = args[1].toLowerCase();
                            if (!languageImpl.getAvailableLanguages().contains(lang)) {
                                player.sendMessage(languageImpl.getMessage("languageapi-lang-not-found", player.getUniqueId(), true)
                                        .replace("%LANG%", lang));
                                return false;
                            }
                            key = args[2].toLowerCase();
                            if (languageImpl.isKey(key, lang)) {

                                editingMessage.add(player);
                                givenParameter.put(player, Arrays.asList(key, lang));
                                player.sendMessage(languageImpl.getMessage("languageapi-update-instructions", player.getUniqueId(), true));
                                return true;
                            } else {
                                player.sendMessage(languageImpl.getMessage("languageapi-key-not-found", player.getUniqueId(), true)
                                        .replace("%KEY%", key)
                                        .replace("%LANG%", lang));
                                return false;
                            }
                        case "create":
                            lang = args[1].toLowerCase();
                            if (!languageImpl.getAvailableLanguages().contains(lang)) {
                                languageImpl.createLanguage(lang);
                                player.sendMessage(languageImpl.getMessage("languageapi-create-success", player.getUniqueId(), true).replace("%LANG%", lang));
                            } else {
                                player.sendMessage(languageImpl.getMessage("languageapi-lang-already-exists", player.getUniqueId(), true).replace("%LANG%", lang));
                            }
                            break;

                        case "delete":
                            lang = args[1].toLowerCase();
                            if (languageImpl.getAvailableLanguages().contains(lang) && !languageImpl.getDefaultLanguage().equalsIgnoreCase(lang)) {
                                languageImpl.deleteLanguage(lang);
                                player.sendMessage(languageImpl.getMessage("languageapi-delete-success", player.getUniqueId(), true).replace("%LANG%", lang));
                            } else if (lang.equalsIgnoreCase("*")) {
                                for (String langs : languageImpl.getAvailableLanguages()) {
                                    languageImpl.deleteLanguage(langs);
                                }
                            } else {
                                player.sendMessage(languageImpl.getMessage("languageapi-lang-not-found", player.getUniqueId(), true)
                                        .replace("%LANG%", lang));
                            }
                            break;
                        case "copy":
                            if (args.length >= 3) {
                                String langfrom = args[1].toLowerCase();
                                String langto = args[2].toLowerCase();
                                if (languageImpl.getAvailableLanguages().contains(langfrom) && languageImpl.getAvailableLanguages().contains(langto)) {
                                    languageImpl.copyLanguage(langfrom, langto);
                                    player.sendMessage(languageImpl.getMessage("languageapi-copy-success", player.getUniqueId(), true)
                                            .replace("%OLDLANG%", langfrom)
                                            .replace("%NEWLANG%", langto));
                                } else {
                                    lang = langfrom;
                                    if (languageImpl.getAvailableLanguages().contains(langfrom)) {
                                        lang = langto;
                                    }
                                    player.sendMessage(languageImpl.getMessage("languageapi-lang-not-found", player.getUniqueId(), true)
                                            .replace("%LANG%", lang));

                                }


                            } else {
                                //HELP
                            }
                            break;
                        case "param": //lang show key
                            key = args[1].toLowerCase();
                            if (!languageImpl.hasParameter(key) || languageImpl.getParameter(key).equalsIgnoreCase("")) {
                                player.sendMessage(languageImpl.getMessage("languageapi-key-has-no-param", player.getUniqueId(), true).replace("%KEY%", key));
                                break;
                            }
                            player.sendMessage(languageImpl.getMessage("languageapi-show-success", player.getUniqueId(), true)
                                    .replace("%PARAM%", languageImpl.getParameter(key)).replace("%KEY%", key));
                        case "translations":
                            lang = args[1].toLowerCase();
                            if (languageImpl.getAvailableLanguages().contains(lang)) {
                                ArrayList<String> allKeys = languageImpl.getAllKeys(lang);
                                for (int i = 0; i < allKeys.size(); i++) {
                                    player.sendMessage(languageImpl.getMessage("languageapi-translation-success", player.getUniqueId(), true)
                                            .replace("%KEY%", allKeys.get(i))
                                            .replace("%MSG%", languageImpl.getAllMessages(lang).get(i)));
                                }
                            }else{
                                player.sendMessage(languageImpl.getMessage("languageapi-lang-not-found", player.getUniqueId(), true)
                                        .replace("%LANG%", lang));
                            }
                            break;
                        case "remove": //lang remove lang key
                            if (args.length >= 3) {
                                lang = args[1].toLowerCase();
                                key = args[2].toLowerCase();
                                if (languageImpl.getDefaultLanguage().contains(lang)) {
                                    if (languageImpl.isKey(key, lang)) {
                                        languageImpl.deleteMessage(key, lang); //EINE SPRACHE EIN KEY
                                        player.sendMessage(languageImpl.getMessage("languageapi-remove-key-in-lang", player.getUniqueId(), true)
                                                .replace("%KEY%", key)
                                                .replace("%LANG%", lang));
                                    } else if (key.endsWith("*")) {
                                        for (String keys : languageImpl.getAllKeys(lang)) { //EINE SPRACHE JEDER KEY
                                            if (keys.startsWith(key.replace("*", ""))) {
                                                languageImpl.deleteMessage(keys, lang);
                                            }
                                        }
                                        player.sendMessage(languageImpl.getMessage("languageapi-remove-every-key-in-lang", player.getUniqueId(), true)
                                                .replace("%LANG%", lang)
                                                .replace("%STARTSWITH%", key.replace("*", "")));
                                    } else {
                                        player.sendMessage(languageImpl.getMessage("languageapi-key-not-found", player.getUniqueId(), true)
                                                .replace("%KEY%", key)
                                                .replace("%LANG%", lang));
                                    }
                                } else if (lang.equalsIgnoreCase("*")) {
                                    if (key.endsWith("*")) { //JEDE SPRACHE JEDER KEY
                                        for (String langs : languageImpl.getAvailableLanguages()) {
                                            for (String keys : languageImpl.getAllKeys(langs)) {
                                                if (keys.startsWith(key.replace("*", ""))) {
                                                    languageImpl.deleteMessage(keys, langs);
                                                }
                                            }
                                        }
                                        player.sendMessage(languageImpl.getMessage("languageapi-remove-every-key-in-every-lang", player.getUniqueId(), true)
                                                .replace("%STARTSWITH%", key.replace("*", "")));
                                    } else { //JEDE SPRACHE EIN KEY
                                        for (String langs : languageImpl.getAvailableLanguages()) {
                                            if (languageImpl.isKey(key, langs)) {
                                                languageImpl.deleteMessage(key, langs);
                                            }
                                        }
                                        player.sendMessage(languageImpl.getMessage("languageapi-remove-key-in-every-lang", player.getUniqueId(), true)
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
                return this.getCompletes(args[1], languageImpl.getAvailableLanguages());
            }
            return this.getCompletes(args[1], languageImpl.getAllKeys(languageImpl.getDefaultLanguage()));
        } else if (args.length == 3) {
            List<String> keyComplete = Arrays.asList("add", "remove", "update");
            if (keyComplete.contains(args[0].toLowerCase())) {
                return this.getCompletes(args[2], languageImpl.getAllKeys(args[1].toLowerCase()));
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
