package de.tentact.languageapi.command;
/*  Created in the IntelliJ IDEA.
    Created by 0utplay | Aldin Sijamhodzic
    Datum: 25.04.2020
    Uhrzeit: 18:58
*/

import de.tentact.languageapi.LanguageAPI;
import de.tentact.languageapi.LanguageSpigot;
import de.tentact.languageapi.player.LanguagePlayer;
import de.tentact.languageapi.util.ConfigUtil;
import de.tentact.languageapi.util.I18N;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Node;

import java.util.*;
import java.util.logging.Level;

public class LanguageCommand implements TabExecutor {


    public LanguageAPI languageAPI = LanguageAPI.getInstance();

    private final List<String> tabComplete = Arrays.asList("add", "remove", "update", "create", "delete", "param", "copy", "translations", "reload");

    public static ArrayList<Player> editingMessage = new ArrayList<>();

    public static HashMap<Player, List<String>> givenParameter = new HashMap<>();

    private final LanguageSpigot languageSpigot;

    public LanguageCommand(LanguageSpigot languageSpigot) {
        this.languageSpigot = languageSpigot;
    }

    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, String[] args) {
        if (commandSender instanceof Player) {
            Player player = (Player) commandSender;
            LanguagePlayer languagePlayer = languageAPI.getPlayerManager().getLanguagePlayer(player.getUniqueId());
            assert languagePlayer != null;
            if (player.hasPermission("system.languageapi")) { //lang add lang key MSG | lang remove lang key | lang update lang key msg | lang createlang lang | lang deletelang lang
                if (args.length >= 1) { //lang reload
                    switch (args[0].toLowerCase()) {
                        case "add":
                            if (!(args.length >= 4)) {

                                languagePlayer.sendMessage(I18N.LANGUAGEAPI_ADD_HELP);
                                return false;
                            }
                            String languages = args[1].toLowerCase();
                            if (!languageAPI.getAvailableLanguages().contains(args[1].toLowerCase())) {
                                languagePlayer.sendMessage(I18N.LANGUAGEAPI_LANG_NOT_FOUND.replace("%LANG%", languages));
                                return false;
                            }
                            String key = args[2].toLowerCase();
                            if (!languageAPI.isKey(key, languages)) {
                                StringBuilder msg = new StringBuilder();
                                for (int i = 3; i < args.length; i++) {
                                    msg.append(args[i]).append(" ");
                                }
                                this.languageAPI.addMessage(key, msg.toString(), languages);
                                languagePlayer.sendMessage(I18N.LANGUAGEAPI_ADD_SUCCESS
                                        .replace("%KEY%", key)
                                        .replace("%LANG%", languages)
                                        .replace("%MSG%", msg.toString()));
                                return true;


                            } else {

                                languagePlayer.sendMessage(I18N.LANGUAGEAPI_KEY_ALREADY_EXISTS
                                        .replace("%KEY%", key)
                                        .replace("%LANG%", languages));
                                return false;

                            }
                        case "update":
                            if (!(args.length >= 3)) {
                                languagePlayer.sendMessage(I18N.LANGUAGEAPI_UPDATE_HELP);
                                return false;
                            }
                            languages = args[1].toLowerCase();
                            if (!languageAPI.getAvailableLanguages().contains(languages)) {
                                languagePlayer.sendMessage(I18N.LANGUAGEAPI_LANG_NOT_FOUND
                                        .replace("%LANG%", languages));
                                return false;
                            }
                            key = args[2].toLowerCase();
                            if (languageAPI.isKey(key, languages)) {
                                editingMessage.add(player);
                                givenParameter.put(player, Arrays.asList(key, languages));
                                languagePlayer.sendMessage(I18N.LANGUAGEAPI_UPDATE_INSTRUCTIONS);
                                return true;
                            } else {
                                languagePlayer.sendMessage(I18N.LANGUAGEAPI_KEY_NOT_FOUND.replace("%KEY%", key)
                                        .replace("%LANG%", languages));
                                return false;
                            }
                        case "create":
                            if (!(args.length >= 2)) {
                                languagePlayer.sendMessage(I18N.LANGUAGEAPI_CREATE_HELP);
                                return false;
                            }
                            languages = args[1].toLowerCase();
                            if (!languageAPI.getAvailableLanguages().contains(languages)) {
                                languageAPI.createLanguage(languages);
                                languagePlayer.sendMessage(I18N.LANGUAGEAPI_CREATE_SUCCESS.replace("%LANG%", languages));
                                return true;
                            } else {
                                languagePlayer.sendMessage(I18N.LANGUAGEAPI_LANG_ALREADY_EXISTS.replace("%LANG%", languages));
                                return false;
                            }

                        case "delete":
                            if (!(args.length >= 2)) {
                                return false;
                            }
                            languages = args[1].toLowerCase();
                            if (languageAPI.getAvailableLanguages().contains(languages) && !languageAPI.getDefaultLanguage().equalsIgnoreCase(languages)) {
                                languageAPI.deleteLanguage(languages);
                                languagePlayer.sendMessage(I18N.LANGUAGEAPI_DELETE_SUCCESS.replace("%LANG%", languages));

                                return true;
                            } else if (languages.equalsIgnoreCase("*")) {
                                for (String langs : languageAPI.getAvailableLanguages()) {
                                    languageAPI.deleteLanguage(langs);
                                }
                                languagePlayer.sendMessage(I18N.LANGUAGEAPI_DELETE_SUCCESS.replace("%LANG%", languages));
                                return true;
                            } else {
                                languagePlayer.sendMessage(I18N.LANGUAGEAPI_LANG_NOT_FOUND
                                        .replace("%LANG%", languages));
                                return false;

                            }
                        case "copy":
                            if (args.length >= 3) {
                                String langfrom = args[1].toLowerCase();
                                String langto = args[2].toLowerCase();
                                if (languageAPI.getAvailableLanguages().contains(langfrom) && languageAPI.getAvailableLanguages().contains(langto)) {
                                    languageAPI.copyLanguage(langfrom, langto);
                                    languagePlayer.sendMessage(I18N.LANGUAGEAPI_COPY_SUCCESS.replace("%OLDLANG%", langfrom)
                                            .replace("%NEWLANG%", langto));

                                    return true;
                                } else {
                                    languages = langfrom;
                                    if (languageAPI.getAvailableLanguages().contains(langfrom)) {
                                        languages = langto;
                                    }
                                    languagePlayer.sendMessage(I18N.LANGUAGEAPI_LANG_NOT_FOUND
                                            .replace("%LANG%", languages));
                                    return false;

                                }
                            } else {
                                languagePlayer.sendMessage(I18N.LANGUAGEAPI_COPY_HELP);
                                return false;

                            }
                        case "param": //languages param key
                            if (!(args.length >= 2)) {
                                return false;
                            }
                            key = args[1].toLowerCase();
                            if (!languageAPI.hasParameter(key) || languageAPI.getParameter(key).equalsIgnoreCase("")) {
                                languagePlayer.sendMessage(I18N.LANGUAGEAPI_KEY_HAS_NO_PARAM.replace("%KEY%", key));
                                return false;

                            }
                            languagePlayer.sendMessage(I18N.LANGUAGEAPI_SHOW_SUCCESS.replace("%PARAM%", languageAPI.getParameter(key)).replace("%KEY%", key));
                            return true;
                        case "translations":
                            languages = args[1].toLowerCase();
                            if (languageAPI.getAvailableLanguages().contains(languages)) {
                                ArrayList<String> allKeys = languageAPI.getAllTranslationKeys(languages);
                                for (int i = 0; i < allKeys.size(); i++) {
                                    languagePlayer.sendMessage(I18N.LANGUAGEAPI_TRANSLATION_SUCCESS.replace("%KEY%", allKeys.get(i))
                                            .replace("%MSG%", languageAPI.getAllTranslations(languages).get(i)));

                                }
                                return true;
                            } else {
                                languagePlayer.sendMessage(I18N.LANGUAGEAPI_LANG_NOT_FOUND
                                        .replace("%LANG%", languages));
                                return false;
                            }
                        case "remove": //languages remove languages key
                            if (args.length >= 3) {
                                languages = args[1].toLowerCase();
                                key = args[2].toLowerCase();
                                if (languageAPI.getDefaultLanguage().contains(languages)) {
                                    if (languageAPI.isKey(key, languages)) {
                                        languageAPI.deleteMessage(key, languages); //EINE SPRACHE EIN KEY
                                        languagePlayer.sendMessage(I18N.LANGUAGEAPI_REMOVE_KEY_IN_LANGUAGE.replace("%KEY%", key)
                                                .replace("%LANG%", languages));
                                        return true;
                                    } else if (key.endsWith("*")) {
                                        for (String keys : languageAPI.getAllTranslationKeys(languages)) {
                                            if (keys.startsWith(key.replace("*", ""))) {
                                                languageAPI.deleteMessage(keys, languages);
                                            }
                                        }
                                        languagePlayer.sendMessage(I18N.LANGUAGEAPI_REMOVE_EVERY_KEY_IN_LANGUAGE.replace("%LANG%", languages)
                                                .replace("%STARTSWITH%", key.replace("*", "")));
                                        return true;
                                    } else {
                                        languagePlayer.sendMessage(I18N.LANGUAGEAPI_KEY_NOT_FOUND
                                                .replace("%LANG%", languages).replace("%KEY%", key));
                                        return false;
                                    }
                                } else if (languages.equalsIgnoreCase("*")) {
                                    if (key.endsWith("*")) { //JEDE SPRACHE JEDER KEY
                                        languageAPI.getAvailableLanguages().forEach(langs -> languageAPI.getAllTranslationKeys(langs).forEach(keys -> {
                                            if (keys.startsWith(key.replace("*", ""))) {
                                                if (!keys.startsWith("languageapi-")) {
                                                    languageAPI.deleteMessage(keys, langs);
                                                    Bukkit.getScheduler().runTaskLaterAsynchronously(languageSpigot, () -> languageAPI.deleteAllParameter(key), 45L);
                                                }
                                            }
                                        }));
                                        languagePlayer.sendMessage(I18N.LANGUAGEAPI_REMOVE_EVERY_KEY_IN_EVERY_LANGUAGE
                                                .replace("%STARTSWITH%", key.
                                                        replace("*", "")));
                                    } else { //JEDE SPRACHE EIN KEY
                                        languageAPI.getAvailableLanguages().forEach(langs -> {
                                            if (languageAPI.isKey(key, langs)) {
                                                languageAPI.deleteMessage(key, langs);
                                                languageAPI.deleteAllParameter(key);
                                            }
                                        });
                                        languagePlayer.sendMessage(I18N.LANGUAGEAPI_REMOVE_KEY_IN_EVERY_LANGUAGE.replace("%KEY%", key));
                                    }
                                    return true;
                                }
                            }
                            break;
                        case "reload":
                            ConfigUtil.createSpigotMySQLConfig();
                            I18N.createDefaultPluginMessages();
                            ConfigUtil.init();
                            ConfigUtil.defaultLanguage = null;
                            Bukkit.getScheduler().runTaskLater(languageSpigot, () -> {
                                languagePlayer.sendMessage(I18N.LANGUAGEAPI_RELOAD_SUCCESS);
                                ConfigUtil.log("Reloading config", Level.INFO);
                            }, 50L);
                            break;

                        default:
                            languagePlayer.sendMultipleTranslation(I18N.LANGUAGEAPI_HELP);
                            break;
                    }
                } else {
                    languagePlayer.sendMultipleTranslation(I18N.LANGUAGEAPI_HELP);
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
                return this.getTabCompletes(args[1], languageAPI.getAvailableLanguages());
            }
            return this.getTabCompletes(args[1], languageAPI.getAllTranslationKeys(languageAPI.getDefaultLanguage()));
        } else if (args.length == 3) {
            List<String> keyComplete = Arrays.asList("add", "remove", "update");
            if (keyComplete.contains(args[0].toLowerCase())) {
                return this.getTabCompletes(args[2], languageAPI.getAllTranslationKeys(args[1].toLowerCase()));
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
