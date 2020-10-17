package de.tentact.languageapi.command;

/*  Created in the IntelliJ IDEA.
    Created by 0utplay | Aldin Sijamhodzic
    Datum: 25.04.2020
    Uhrzeit: 18:58
*/

import de.tentact.languageapi.LanguageAPI;
import de.tentact.languageapi.LanguageSpigot;
import de.tentact.languageapi.configuration.LanguageInventory;
import de.tentact.languageapi.configuration.SpigotConfiguration;
import de.tentact.languageapi.i18n.I18N;
import de.tentact.languageapi.player.LanguagePlayer;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.*;
import java.util.logging.Level;

public class LanguageCommand implements TabExecutor {

    public final LanguageAPI languageAPI = LanguageAPI.getInstance();
    private final List<String> tabComplete = Arrays.asList("add", "remove", "update", "create", "delete",
            "param", "copy", "translations", "reload", "import", "export", "help", "info");
    public final ArrayList<Player> editingMessage = new ArrayList<>();
    public final HashMap<Player, List<String>> givenParameter = new HashMap<>();
    private final LanguageSpigot languageSpigot;
    private final LanguageInventory languageInventory;
    private final String version;

    public LanguageCommand(LanguageSpigot languageSpigot) {
        this.languageSpigot = languageSpigot;
        this.languageInventory = languageSpigot.spigotConfiguration.getLanguageInventory();
        this.version = languageSpigot.getVersion();
    }

    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, String[] args) {
        if (commandSender instanceof Player) {
            Player player = (Player) commandSender;
            LanguagePlayer languagePlayer = this.languageAPI.getPlayerManager().getLanguagePlayer(player.getUniqueId());
            if (languagePlayer == null) {
                return false;
            }
            if (player.hasPermission("system.languageapi")) { //lang add lang key MSG | lang remove lang key | lang update lang key msg | lang createlang lang | lang deletelang lang
                if (args.length >= 1) { //lang reload
                    switch (args[0].toLowerCase()) {
                        case "add":
                            if (this.checkDoesNotHavePermission(player, args)) {
                                return false;
                            }
                            if (!(args.length >= 4)) {
                                languagePlayer.sendMessage(I18N.LANGUAGEAPI_ADD_HELP.get());
                                return false;
                            }
                            String languages = args[1].toLowerCase();
                            if (!this.languageAPI.getAvailableLanguages().contains(args[1].toLowerCase())) {
                                languagePlayer.sendMessage(I18N.LANGUAGEAPI_LANG_NOT_FOUND.get().replace("%LANG%", languages));
                                return false;
                            }
                            String key = args[2].toLowerCase();
                            if (!this.languageAPI.isKey(key, languages)) {
                                StringBuilder msg = new StringBuilder();
                                for (int i = 3; i < args.length; i++) {
                                    msg.append(args[i]).append(" ");
                                }
                                this.languageAPI.addMessage(key, msg.toString(), languages);
                                languagePlayer.sendMessage(I18N.LANGUAGEAPI_ADD_SUCCESS.get()
                                        .replace("%KEY%", key)
                                        .replace("%LANG%", languages)
                                        .replace("%MSG%", msg.toString()));
                                return true;
                            } else {
                                languagePlayer.sendMessage(I18N.LANGUAGEAPI_KEY_ALREADY_EXISTS.get()
                                        .replace("%KEY%", key)
                                        .replace("%LANG%", languages));
                                return false;
                            }
                        case "update":
                            if (this.checkDoesNotHavePermission(player, args)) {
                                return false;
                            }
                            if (!(args.length >= 3)) {
                                languagePlayer.sendMessage(I18N.LANGUAGEAPI_UPDATE_HELP.get());
                                return false;
                            }
                            languages = args[1].toLowerCase();
                            if (!this.languageAPI.getAvailableLanguages().contains(languages)) {
                                languagePlayer.sendMessage(I18N.LANGUAGEAPI_LANG_NOT_FOUND.get()
                                        .replace("%LANG%", languages));
                                return false;
                            }
                            key = args[2].toLowerCase();
                            if (this.languageAPI.isKey(key, languages)) {
                                editingMessage.add(player);
                                givenParameter.put(player, Arrays.asList(key, languages));
                                languagePlayer.sendMessage(I18N.LANGUAGEAPI_UPDATE_INSTRUCTIONS.get());
                                return true;
                            } else {
                                languagePlayer.sendMessage(I18N.LANGUAGEAPI_KEY_NOT_FOUND.get().replace("%KEY%", key)
                                        .replace("%LANG%", languages));
                                return false;
                            }
                        case "create":
                            if (this.checkDoesNotHavePermission(player, args)) {
                                return false;
                            }
                            if (!(args.length >= 2)) {
                                languagePlayer.sendMessage(I18N.LANGUAGEAPI_CREATE_HELP.get());
                                return false;
                            }
                            languages = args[1].toLowerCase();
                            if (!this.languageAPI.getAvailableLanguages().contains(languages)) {
                                this.languageAPI.createLanguage(languages);
                                languagePlayer.sendMessage(I18N.LANGUAGEAPI_CREATE_SUCCESS.get().replace("%LANG%", languages));
                                return true;
                            } else {
                                languagePlayer.sendMessage(I18N.LANGUAGEAPI_LANG_ALREADY_EXISTS.get().replace("%LANG%", languages));
                                return false;
                            }
                        case "delete":
                            if (this.checkDoesNotHavePermission(player, args)) {
                                return false;
                            }
                            if (!(args.length >= 2)) {
                                return false;
                            }
                            languages = args[1].toLowerCase();
                            if (this.languageAPI.getAvailableLanguages().contains(languages) && !this.languageAPI.getDefaultLanguage().equalsIgnoreCase(languages)) {
                                this.languageAPI.deleteLanguage(languages);
                                languagePlayer.sendMessage(I18N.LANGUAGEAPI_DELETE_SUCCESS.get().replace("%LANG%", languages));

                                return true;
                            } else if (languages.equalsIgnoreCase("*")) {
                                this.languageAPI.getAvailableLanguages().forEach(language -> this.languageAPI.deleteLanguage(language));

                                languagePlayer.sendMessage(I18N.LANGUAGEAPI_DELETE_ALL_LANGS.get());
                                return true;
                            } else {
                                languagePlayer.sendMessage(I18N.LANGUAGEAPI_LANG_NOT_FOUND.get()
                                        .replace("%LANG%", languages));
                                return false;
                            }
                        case "copy":
                            if (this.checkDoesNotHavePermission(player, args)) {
                                return false;
                            }
                            if (args.length >= 3) {
                                String langfrom = args[1].toLowerCase();
                                String langto = args[2].toLowerCase();
                                if (this.languageAPI.getAvailableLanguages().contains(langfrom) && this.languageAPI.getAvailableLanguages().contains(langto)) {
                                    this.languageAPI.copyLanguage(langfrom, langto);
                                    languagePlayer.sendMessage(I18N.LANGUAGEAPI_COPY_SUCCESS.get().replace("%OLDLANG%", langfrom)
                                            .replace("%NEWLANG%", langto));
                                    return true;
                                } else {
                                    languages = langfrom;
                                    if (this.languageAPI.getAvailableLanguages().contains(langfrom)) {
                                        languages = langto;
                                    }
                                    languagePlayer.sendMessage(I18N.LANGUAGEAPI_LANG_NOT_FOUND.get()
                                            .replace("%LANG%", languages));
                                    return false;
                                }
                            } else {
                                languagePlayer.sendMessage(I18N.LANGUAGEAPI_COPY_HELP.get());
                                return false;
                            }
                        case "param": //languages param key
                            if (this.checkDoesNotHavePermission(player, args)) {
                                return false;
                            }
                            if (!(args.length >= 2)) {
                                return false;
                            }
                            key = args[1].toLowerCase();
                            if (!this.languageAPI.hasParameter(key) || this.languageAPI.getParameter(key).equalsIgnoreCase("")) {
                                languagePlayer.sendMessage(I18N.LANGUAGEAPI_KEY_HAS_NO_PARAM.get().replace("%KEY%", key));
                                return false;
                            }
                            languagePlayer.sendMessage(I18N.LANGUAGEAPI_SHOW_SUCCESS.get().replace("%PARAM%", this.languageAPI.getParameter(key)).replace("%KEY%", key));
                            return true;
                        case "translations":
                            if (this.checkDoesNotHavePermission(player, args)) {
                                return false;
                            }
                            languages = args[1].toLowerCase();
                            if (this.languageAPI.getAvailableLanguages().contains(languages)) {
                                List<String> allKeys = this.languageAPI.getAllTranslationKeys(languages);
                                for (int i = 0; i < allKeys.size(); i++) {
                                    languagePlayer.sendMessage(I18N.LANGUAGEAPI_TRANSLATION_SUCCESS.get().replace("%KEY%", allKeys.get(i))
                                            .replace("%MSG%", this.languageAPI.getAllTranslations(languages).get(i)));
                                }
                                return true;
                            } else {
                                languagePlayer.sendMessage(I18N.LANGUAGEAPI_LANG_NOT_FOUND.get()
                                        .replace("%LANG%", languages));
                                return false;
                            }
                        case "remove": //languages remove languages key
                            if (this.checkDoesNotHavePermission(player, args)) {
                                return false;
                            }
                            if (args.length >= 3) {
                                languages = args[1].toLowerCase();
                                key = args[2].toLowerCase();
                                if (this.languageAPI.getDefaultLanguage().contains(languages)) {
                                    if (this.languageAPI.isKey(key, languages)) {
                                        this.languageAPI.deleteMessage(key, languages); //EINE SPRACHE EIN KEY
                                        languagePlayer.sendMessage(I18N.LANGUAGEAPI_REMOVE_KEY_IN_LANGUAGE.get().replace("%KEY%", key)
                                                .replace("%LANG%", languages));
                                        return true;
                                    } else if (key.endsWith("*")) {
                                        for (String keys : this.languageAPI.getAllTranslationKeys(languages)) {
                                            if (keys.startsWith(key.replace("*", ""))) {
                                                this.languageAPI.deleteMessage(keys, languages);
                                            }
                                        }
                                        languagePlayer.sendMessage(I18N.LANGUAGEAPI_REMOVE_EVERY_KEY_IN_LANGUAGE.get().replace("%LANG%", languages)
                                                .replace("%STARTSWITH%", key.replace("*", "")));
                                        return true;
                                    } else {
                                        languagePlayer.sendMessage(I18N.LANGUAGEAPI_KEY_NOT_FOUND.get()
                                                .replace("%LANG%", languages).replace("%KEY%", key));
                                        return false;
                                    }
                                } else if (languages.equalsIgnoreCase("*")) {
                                    if (key.endsWith("*")) { //JEDE SPRACHE JEDER KEY
                                        this.languageAPI.getAvailableLanguages().forEach(langs -> this.languageAPI.getAllTranslationKeys(langs).forEach(keys -> {
                                            if (keys.startsWith(key.replace("*", ""))) {
                                                if (!keys.startsWith("languageapi-")) {
                                                    this.languageAPI.deleteMessage(keys, langs);
                                                    Bukkit.getScheduler().runTaskLaterAsynchronously(languageSpigot, () -> this.languageAPI.deleteAllParameter(key), 45L);
                                                }
                                            }
                                        }));
                                        languagePlayer.sendMessage(I18N.LANGUAGEAPI_REMOVE_EVERY_KEY_IN_EVERY_LANGUAGE.get()
                                                .replace("%STARTSWITH%", key.
                                                        replace("*", "")));
                                    } else { //JEDE SPRACHE EIN KEY
                                        this.languageAPI.getAvailableLanguages().forEach(langs -> {
                                            if (this.languageAPI.isKey(key, langs)) {
                                                this.languageAPI.deleteMessage(key, langs);
                                                this.languageAPI.deleteAllParameter(key);
                                            }
                                        });
                                        languagePlayer.sendMessage(I18N.LANGUAGEAPI_REMOVE_KEY_IN_EVERY_LANGUAGE.get().replace("%KEY%", key));
                                    }
                                    return true;
                                }
                            }
                            break;
                        case "import": //lang import FILE BOOL
                            if (this.checkDoesNotHavePermission(player, args)) {
                                return false;
                            }
                            if (args.length < 3) {
                                languagePlayer.sendMessage(I18N.LANGUAGEAPI_IMPORT_HELP.get());
                                return false;
                            }
                            if (!this.isBoolean(args[2])) {
                                languagePlayer.sendMessage(I18N.LANGUAGEAPI_IMPORT_HELP.get());
                                return false;
                            }
                            File file = new File("plugins/LanguageAPI/import", args[1]);
                            if (!file.exists()) {
                                languagePlayer.sendMessage(I18N.LANGUAGEAPI_IMPORT_FILE_NOT_FOUND.get().replace("%FILE%", args[1]));
                                return false;
                            }
                            boolean passed = this.languageAPI.getFileHandler().loadFile(file, Boolean.parseBoolean(args[2]));
                            if (!passed) {
                                languagePlayer.sendMessage(I18N.LANGUAGEAPI_IMPORT_ERROR.get());
                                return false;
                            }
                            languagePlayer.sendMessage(I18N.LANGUAGEAPI_IMPORT_SUCCESS.get().replace("%FILE%", args[1]));
                            break;
                        case "export":
                            if (this.checkDoesNotHavePermission(player, args)) {
                                return false;
                            }
                            if (args.length < 2) {
                                languagePlayer.sendMessage(I18N.LANGUAGEAPI_EXPORT_HELP.get());
                                return false;
                            }
                            if (!this.languageAPI.isLanguage(args[1]) && !args[1].equalsIgnoreCase("@a")) {
                                languagePlayer.sendMessage(I18N.LANGUAGEAPI_LANG_NOT_FOUND.get());
                                return false;
                            }
                            if (args[1].equalsIgnoreCase("@a")) {
                                passed = this.languageAPI.getFileHandler().exportAll();
                                if(!passed) {
                                    languagePlayer.sendMessage(I18N.LANGUAGEAPI_EXPORT_ERROR.get().replace("%LANGUAGE%", "@a"));
                                    return false;
                                }
                                languagePlayer.sendMessage(I18N.LANGUAGEAPI_EXPORT_ALL_SUCCESS.get());
                            } else {
                                passed = this.languageAPI.getFileHandler().exportLanguageToFile(args[1]);
                                if (!passed) {
                                    languagePlayer.sendMessage(I18N.LANGUAGEAPI_EXPORT_ERROR.get().replace("%LANGUAGE%", args[1]));
                                    return false;
                                }
                                languagePlayer.sendMessage(I18N.LANGUAGEAPI_EXPORT_SUCCESS.get().replace("%FILE%", args[1].toLowerCase() + ".yml"));
                            }
                            break;

                        case "reload":
                            if (this.checkDoesNotHavePermission(player, args)) {
                                return false;
                            }
                            this.languageSpigot.getLogger().log(Level.INFO, "Reloading config.json...");
                            this.languageSpigot.spigotConfiguration = new SpigotConfiguration(this.languageSpigot.getLogger());
                            languagePlayer.sendMessage(I18N.LANGUAGEAPI_RELOAD_SUCCESS.get());
                            break;
                        case "help":
                            if (this.checkDoesNotHavePermission(player, args)) {
                                return false;
                            }
                            languagePlayer.sendMultipleTranslation(I18N.LANGUAGEAPI_HELP.get());
                            break;
                        case "info":
                            languagePlayer.sendMessage(I18N.LANGUAGEAPI_INFO.get().replace("%VERSION%", version));
                            break;
                        default:
                            languagePlayer.sendMultipleTranslation(I18N.LANGUAGEAPI_HELP.get());
                            break;
                    }
                } else {
                    player.openInventory(this.languageInventory.getLanguageInventory());
                }
            } else {
                languagePlayer.sendMessage(I18N.LANGUAGEAPI_NOPERMS.get());
            }
        }
        return false;

    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, String[] args) {
        if (args.length == 1) {
            return this.getTabCompletes(args[0], tabComplete);
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("create")) {
                return Collections.emptyList();
            }
            if (!args[0].equalsIgnoreCase("param")) {
                return this.getTabCompletes(args[1], this.languageAPI.getAvailableLanguages());
            }
            return this.getTabCompletes(args[1], this.languageAPI.getAllTranslationKeys(this.languageAPI.getDefaultLanguage()));
        } else if (args.length == 3) {
            List<String> keyComplete = Arrays.asList("add", "remove", "update");
            if (keyComplete.contains(args[0].toLowerCase())) {
                return this.getTabCompletes(args[2], this.languageAPI.getAllTranslationKeys(args[1].toLowerCase()));
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

    private boolean isBoolean(String input) {
        return input.equalsIgnoreCase("true") || input.equalsIgnoreCase("false");
    }

    private boolean checkDoesNotHavePermission(Player player, String[] args) {
        LanguagePlayer languagePlayer = this.languageAPI.getPlayerExecutor().getLanguagePlayer(player.getUniqueId());
        if (player.hasPermission("system.languageapi." + args[0])) {
            return false;
        }
        if (languagePlayer != null) {
            languagePlayer.sendMessage(I18N.LANGUAGEAPI_NOPERMS.get());
        }
        return true;
    }
}
