package de.tentact.languageapi.spigot.command;
/*  Created in the IntelliJ IDEA.
    Created by 0utplay | Aldin Sijamhodzic
    Datum: 25.04.2020
    Uhrzeit: 18:58
*/

import de.tentact.languageapi.AbstractLanguageAPI;
import de.tentact.languageapi.LanguageSpigot;
import de.tentact.languageapi.mysql.MySQL;
import de.tentact.languageapi.util.I18N;
import de.tentact.languageapi.util.Source;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.logging.Level;

public class LanguageCommand implements TabExecutor {


    public AbstractLanguageAPI abstractLanguageAPI = AbstractLanguageAPI.getInstance();

    private final List<String> tabComplete = Arrays.asList("add", "remove", "update", "create", "delete", "param", "copy", "translations");

    public static ArrayList<Player> editingMessage = new ArrayList<>();

    public static HashMap<Player, List<String>> givenParameter = new HashMap<>();


    private LanguageSpigot languageSpigot;

    public LanguageCommand(LanguageSpigot languageSpigot) {
        this.languageSpigot = languageSpigot;
    }


    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, String[] args) {
        if (commandSender instanceof Player) {
            Player player = (Player) commandSender;
            if (player.hasPermission("system.languageapi")) { //lang add lang key MSG | lang remove lang key | lang update lang key msg | lang createlang lang | lang deletelang lang
                if (args.length >= 1) { //lang reload
                    switch (args[0].toLowerCase()) {
                        case "add":
                            if (!(args.length >= 4)) {
                                player.sendMessage(abstractLanguageAPI.getMessage("languageapi-add-help", player.getUniqueId(), true));
                                return false;
                            }
                            String languages = args[1].toLowerCase();
                            if (!abstractLanguageAPI.getAvailableLanguages().contains(args[1].toLowerCase())) {
                                player.sendMessage(abstractLanguageAPI.getMessage("languageapi-languages-not-found", player.getUniqueId(), true)
                                        .replace("%LANG%", languages));
                                return false;
                            }
                            String key = args[2].toLowerCase();
                            if (!abstractLanguageAPI.isKey(key, languages)) {
                                StringBuilder msg = new StringBuilder();

                                for (int i = 3; i < args.length; i++) {
                                    msg.append(args[i]).append(" ");
                                }
                                abstractLanguageAPI.addMessage(key, msg.toString(), languages);
                                player.sendMessage(abstractLanguageAPI.getMessage("languageapi-add-success", player.getUniqueId(), true)
                                        .replace("%KEY%", key)
                                        .replace("%LANG%", languages)
                                        .replace("%MSG%", msg.toString()));
                                return true;

                            } else {
                                player.sendMessage(abstractLanguageAPI.getMessage("languageapi-key-already-exists", player.getUniqueId(), true)
                                        .replace("%KEY%", key)
                                        .replace("%LANG%", languages));
                                return false;

                            }
                        case "update":
                            if (!(args.length >= 3)) {
                                player.sendMessage(abstractLanguageAPI.getMessage("languageapi-update-help", player.getUniqueId(), true));
                                return false;
                            }
                            languages = args[1].toLowerCase();
                            if (!abstractLanguageAPI.getAvailableLanguages().contains(languages)) {
                                player.sendMessage(abstractLanguageAPI.getMessage("languageapi-languages-not-found", player.getUniqueId(), true)
                                        .replace("%LANG%", languages));
                                return false;
                            }
                            key = args[2].toLowerCase();
                            if (abstractLanguageAPI.isKey(key, languages)) {
                                editingMessage.add(player);
                                givenParameter.put(player, Arrays.asList(key, languages));
                                player.sendMessage(abstractLanguageAPI.getMessage("languageapi-update-instructions", player.getUniqueId(), true));
                                return true;
                            } else {
                                player.sendMessage(abstractLanguageAPI.getMessage("languageapi-key-not-found", player.getUniqueId(), true)
                                        .replace("%KEY%", key)
                                        .replace("%LANG%", languages));
                                return false;
                            }
                        case "create":
                            if (!(args.length >= 2)) {
                                //create help
                                return false;
                            }
                            languages = args[1].toLowerCase();
                            if (!abstractLanguageAPI.getAvailableLanguages().contains(languages)) {
                                abstractLanguageAPI.createLanguage(languages);
                                player.sendMessage(abstractLanguageAPI.getMessage("languageapi-create-success", player.getUniqueId(), true).replace("%LANG%", languages));
                                return true;
                            } else {
                                player.sendMessage(abstractLanguageAPI.getMessage("languageapi-languages-already-exists", player.getUniqueId(), true)
                                        .replace("%LANG%", languages));
                                return false;
                            }

                        case "delete":
                            if (!(args.length >= 2)) {
                                return false;
                            }
                            languages = args[1].toLowerCase();
                            if (abstractLanguageAPI.getAvailableLanguages().contains(languages) && !abstractLanguageAPI.getDefaultLanguage().equalsIgnoreCase(languages)) {
                                abstractLanguageAPI.deleteLanguage(languages);
                                player.sendMessage(abstractLanguageAPI.getMessage("languageapi-delete-success",
                                        player.getUniqueId(),
                                        true)
                                        .replace("%LANG%", languages));
                                return true;
                            } else if (languages.equalsIgnoreCase("*")) {
                                for (String langs : abstractLanguageAPI.getAvailableLanguages()) {
                                    abstractLanguageAPI.deleteLanguage(langs);
                                }
                                player.sendMessage(abstractLanguageAPI.getMessage("languageapi-delete-success",
                                        player.getUniqueId(),
                                        true)
                                        .replace("%LANG%", languages));
                                return true;
                            } else {
                                player.sendMessage(abstractLanguageAPI.getMessage("languageapi-languages-not-found", player.getUniqueId(), true)
                                        .replace("%LANG%", languages));
                                return false;

                            }
                        case "copy":
                            if (args.length >= 3) {
                                String langfrom = args[1].toLowerCase();
                                String langto = args[2].toLowerCase();
                                if (abstractLanguageAPI.getAvailableLanguages().contains(langfrom) && abstractLanguageAPI.getAvailableLanguages().contains(langto)) {
                                    abstractLanguageAPI.copyLanguage(langfrom, langto);
                                    player.sendMessage(abstractLanguageAPI.getMessage("languageapi-copy-success", player.getUniqueId(), true)
                                            .replace("%OLDLANG%", langfrom)
                                            .replace("%NEWLANG%", langto));
                                    return true;
                                } else {
                                    languages = langfrom;
                                    if (abstractLanguageAPI.getAvailableLanguages().contains(langfrom)) {
                                        languages = langto;
                                    }
                                    player.sendMessage(abstractLanguageAPI.getMessage("languageapi-languages-not-found", player.getUniqueId(), true)
                                            .replace("%LANG%", languages));
                                    return false;

                                }
                            } else {
                                player.sendMessage(abstractLanguageAPI.getMessage("languageapi-copy-help", player.getUniqueId(), true));
                                return false;

                            }
                        case "param": //languages param key
                            if (!(args.length >= 2)) {
                                return false;
                            }
                            key = args[1].toLowerCase();
                            if (!abstractLanguageAPI.hasParameter(key) || abstractLanguageAPI.getParameter(key).equalsIgnoreCase("")) {
                                player.sendMessage(abstractLanguageAPI.getMessage("languageapi-key-has-no-param", player.getUniqueId(), true).replace("%KEY%", key));
                                return false;

                            }
                            player.sendMessage(abstractLanguageAPI.getMessage("languageapi-show-success", player.getUniqueId(), true)
                                    .replace("%PARAM%", abstractLanguageAPI.getParameter(key)).replace("%KEY%", key));
                            return true;
                        case "translations":
                            languages = args[1].toLowerCase();
                            if (abstractLanguageAPI.getAvailableLanguages().contains(languages)) {
                                ArrayList<String> allKeys = abstractLanguageAPI.getAllTranslationKeys(languages);
                                for (int i = 0; i < allKeys.size(); i++) {
                                    player.sendMessage(abstractLanguageAPI.getMessage("languageapi-translation-success", player.getUniqueId(), true)
                                            .replace("%KEY%", allKeys.get(i))
                                            .replace("%MSG%", abstractLanguageAPI.getAllTranslations(languages).get(i)));

                                }
                                return true;
                            } else {
                                player.sendMessage(abstractLanguageAPI.getMessage("languageapi-lang-not-found", player.getUniqueId(), true)
                                        .replace("%LANG%", languages));
                                return false;
                            }
                        case "remove": //languages remove languages key
                            if (args.length >= 3) {
                                languages = args[1].toLowerCase();
                                key = args[2].toLowerCase();
                                if (abstractLanguageAPI.getDefaultLanguage().contains(languages)) {
                                    if (abstractLanguageAPI.isKey(key, languages)) {
                                        abstractLanguageAPI.deleteMessage(key, languages); //EINE SPRACHE EIN KEY
                                        player.sendMessage(abstractLanguageAPI.getMessage("languageapi-remove-key-in-language", player.getUniqueId(), true)
                                                .replace("%KEY%", key)
                                                .replace("%LANG%", languages));
                                        return true;
                                    } else if (key.endsWith("*")) {
                                        for (String keys : abstractLanguageAPI.getAllTranslationKeys(languages)) {
                                            if (keys.startsWith(key.replace("*", ""))) {
                                                abstractLanguageAPI.deleteMessage(keys, languages);
                                            }
                                        }
                                        player.sendMessage(abstractLanguageAPI.getMessage("languageapi-remove-every-key-in-language", player.getUniqueId(), true)
                                                .replace("%LANG%", languages)
                                                .replace("%STARTSWITH%", key.replace("*", "")));
                                        return true;
                                    } else {
                                        player.sendMessage(abstractLanguageAPI.getMessage("languageapi-key-not-found", player.getUniqueId(), true)
                                                .replace("%KEY%", key)
                                                .replace("%LANG%", languages));
                                        return false;
                                    }
                                } else if (languages.equalsIgnoreCase("*")) {
                                    if (key.endsWith("*")) { //JEDE SPRACHE JEDER KEY
                                        abstractLanguageAPI.getAvailableLanguages().forEach(langs -> abstractLanguageAPI.getAllTranslationKeys(langs).forEach(keys -> {
                                            if (keys.startsWith(key.replace("*", ""))) {
                                                if(!keys.startsWith("languageapi-")) {
                                                    abstractLanguageAPI.deleteMessage(keys, langs);
                                                    Bukkit.getScheduler().runTaskLater(languageSpigot, () -> {
                                                        abstractLanguageAPI.deleteAllParameter(key);
                                                    }, 45L);
                                                }
                                            }
                                        }));

                                        player.sendMessage(abstractLanguageAPI.getMessage("languageapi-remove-every-key-in-every-language", player.getUniqueId(), true)
                                                .replace("%STARTSWITH%", key.replace("*", "")));
                                    } else { //JEDE SPRACHE EIN KEY
                                        abstractLanguageAPI.getAvailableLanguages().forEach(langs -> {
                                            if (abstractLanguageAPI.isKey(key, langs)) {
                                                abstractLanguageAPI.deleteMessage(key, langs);
                                                abstractLanguageAPI.deleteAllParameter(key);
                                            }
                                        });

                                        player.sendMessage(abstractLanguageAPI.getMessage("languageapi-remove-key-in-every-language", player.getUniqueId(), true)
                                                .replace("%KEY%", key));
                                    }
                                    return true;
                                }
                            }
                            break;
                        case "reload":
                            Source.createSpigotMySQLConfig();
                            I18N.createDefaultPluginMessages();
                            Source.initSpigot();
                            Source.defaultLanguage = null;
                            Bukkit.getScheduler().runTaskLater(languageSpigot, () ->  {
                                player.sendMessage(abstractLanguageAPI.getMessage("languageapi-reload-success", player.getUniqueId(), true));
                                Source.log("Reloading config", Level.INFO);
                            }, 50L);
                            break;

                        default:
                            abstractLanguageAPI.getMultipleMessages("languageapi-help", player.getUniqueId(), true)
                                    .forEach(player::sendMessage);

                            break;
                    }
                } else {
                    abstractLanguageAPI.getMultipleMessages("languageapi-help", player.getUniqueId(), true)
                            .forEach(player::sendMessage);
                    return false;
                }
            }
        }
        return false;

    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, String[] args) {
        if (args.length == 1) {
            return this.getTabCompletes(args[0], tabComplete);
        } else if (args.length == 2) {
            if (!args[0].equalsIgnoreCase("param")) {
                return this.getTabCompletes(args[1], abstractLanguageAPI.getAvailableLanguages());
            }
            return this.getTabCompletes(args[1], abstractLanguageAPI.getAllTranslationKeys(abstractLanguageAPI.getDefaultLanguage()));
        } else if (args.length == 3) {
            List<String> keyComplete = Arrays.asList("add", "remove", "update");
            if (keyComplete.contains(args[0].toLowerCase())) {
                return this.getTabCompletes(args[2], abstractLanguageAPI.getAllTranslationKeys(args[1].toLowerCase()));
            }
        }
        return Collections.emptyList();

    }

    private List<String> getTabCompletes(String playerInput, List<String> tabComplete) {
        List<String> possibleCompletes = new ArrayList<>();
        StringUtil.copyPartialMatches(playerInput, tabComplete, possibleCompletes);
        Collections.sort(possibleCompletes);
        return possibleCompletes;
    }
}
