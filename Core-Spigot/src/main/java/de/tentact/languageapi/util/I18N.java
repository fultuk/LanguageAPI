package de.tentact.languageapi.util;
/*  Created in the IntelliJ IDEA.
    Created by 0utplay | Aldin Sijamhodzic
    Datum: 03.05.2020
    Uhrzeit: 14:02
*/

import de.tentact.languageapi.LanguageAPI;
import de.tentact.languageapi.api.TranslationImpl;

import java.util.Arrays;

public class I18N {

    private static final LanguageAPI iLanguageAPI = LanguageAPI.getInstance();


    public static void createDefaultPluginMessages() {
        createDefaultPluginMessages(iLanguageAPI.getDefaultLanguage());
    }

    public static void createDefaultPluginMessages(String language) {

        iLanguageAPI.addMessage("languageapi-prefix", "&eLanguageAPI x &7", language);

        iLanguageAPI.addMessage("languageapi-add-success", "Der Translationkey %KEY% wurde in %LANG% erfolgreich als ''%MSG%'' übersetzt.", language);
        iLanguageAPI.addParameter("languageapi-add-success", "%MSG%");
        iLanguageAPI.addMessage("languageapi-add-help", "Verwende /lang add <Sprache> <Translationkey> <Übersetzung> um einen Translationkey zu übersetzten.", language);

        iLanguageAPI.addMessage("languageapi-key-already-exists", "Der Translationkey %KEY% wurde in %LANG% bereits übersetzt." +
                " Verwende /lang update um die Übersetzung zu ändern.", language);
        iLanguageAPI.addParameter("languageapi-key-already-exists", "%KEY%, %LANG%");

        iLanguageAPI.addMessage("languageapi-lang-not-found", "Die Sprache %LANG% wurde nicht gefunden.", language);
        iLanguageAPI.addParameter("languageapi-lang-not-found", "%LANG%");


        iLanguageAPI.addMessage("languageapi-remove-key-in-language", "Die Übersetzung des Translationkeys %KEY% in %LANG% wurde erfolgreich entfernt.", language);
        iLanguageAPI.addParameter("languageapi-remove-key-in-language", "%KEY%, %LANG%");
        iLanguageAPI.addMessage("languageapi-remove-key-in-every-language", "Der Translationkey %KEY% wurde in jeder Sprache entfernt.", language);
        iLanguageAPI.addParameter("languageapi-remove-key-in-every-language", "%KEY%");
        iLanguageAPI.addMessage("languageapi-remove-every-key-in-language", "Jeder Translationkey beginnend mit %STARTSWITH% wurde in %LANG% entfernt.", language);
        iLanguageAPI.addParameter("languageapi-remove-every-key-in-language", "%STARTSWITH%, %LANG%");
        iLanguageAPI.addMessage("languageapi-remove-every-key-in-every-language", "Jeder Translationkey beginnend mit %STARTSWITH% wurde in jeder Sprache entfernt.", language);
        iLanguageAPI.addParameter("languageapi-remove-every-key-in-every-language", "%STARTSWITH%");

        iLanguageAPI.addMessage("languageapi-key-not-found", "Der Translationkey %KEY% wurde in %LANG% nicht gefunden.", language);
        iLanguageAPI.addParameter("languageapi-key-not-found", "%KEY%, %LANG%");

        iLanguageAPI.addMessage("languageapi-update-success", "&7Die Übersetzung des Translationkeys %KEY% in %LANG% wurde erfolgreich zu ''%MSG%''&7 geändert.", language);
        iLanguageAPI.addParameter("languageapi-update-success", "%MSG%");
        iLanguageAPI.addMessage("languageapi-update-instructions", "Nun kannst du die Übersetzung in den Chat eingeben, wenn benötigt auch mehrmals.\nMit ''finish'' wird die Übersetzung gespeichert.", language);
        iLanguageAPI.addMessage("languageapi-update-same", "Es wurde nichts verändert und die Übersetzung wurde abgebrochen.", language);
        iLanguageAPI.addMessage("languageapi-update-help", "Verwende /lang update <Sprache> <Translationkey> <Übersetzung> um eine Übersetzung zu ändern", language);


        iLanguageAPI.addMessage("languageapi-create-success", "Die Sprache %LANG% wurde erfolgreich erstellt.", language);
        iLanguageAPI.addParameter("languageapi-create-success", "%LANG%");
        iLanguageAPI.addMessage("languageapi-create-help", "Verwende /lang create <Sprache> um eine Sprache zu erstellen", language);

        iLanguageAPI.addMessage("languageapi-lang-already-exists", "Die Sprache %LANG% existiert bereits.", language);
        iLanguageAPI.addParameter("languageapi-lang-already-exists", "%LANG%");

        iLanguageAPI.addMessage("languageapi-delete-success", "Die Sprache %LANG% wurde erfolgreich gelöscht.", language);
        iLanguageAPI.addParameter("languageapi-delete-success", "%LANG%");
        iLanguageAPI.addMessage("languageapi-delete-all-langs", "Es wurde jede Sprache gelöscht.");

        iLanguageAPI.addMessage("languageapi-copy-success", "%OLDLANG% wurde erfolgreich in %NEWLANG% kopiert.", language);
        iLanguageAPI.addParameter("languageapi-copy-success", "%OLDLANG%, %NEWLANG%");
        iLanguageAPI.addMessage("languageapi-copy-help", "Verwende /lang copy <Sprache> <Sprache2> um eine Sprache zu kopieren.", language);

        iLanguageAPI.addMessage("languageapi-key-has-no-param", "Der Translationkey %KEY% hat keine Parameter.", language);
        iLanguageAPI.addParameter("languageapi-key-has-no-param", "%LANG%");

        iLanguageAPI.addMessage("languageapi-show-success", "Es wurden folgende Parameter zu %KEY% gefunden: \nParameter: %PARAM%", language);
        iLanguageAPI.addParameter("languageapi-show-success", "%PARAM%, %KEY%");

        iLanguageAPI.addMessage("languageapi-translation-success", "&6%KEY%&7 - %MSG%", language);
        iLanguageAPI.addParameter("languageapi-translation-success", "%KEY%, %MSG%");

        iLanguageAPI.addMessage("languageapi-reload-success", "Die Configuration wurde neugeladen.", language);

        iLanguageAPI.addMessage("languageapi-noperms", "Du hast keine Rechte dazu.", language);

        iLanguageAPI.addMessage("languageapi-player-selected-language", "Du hast die Sprache %LANGUAGE% ausgewählt.", language);
        iLanguageAPI.addParameter("languageapi-player-selected-language","%LANGUAGE%");


        iLanguageAPI.setMultipleTranslation("languageapi-help", Arrays.asList("languageapi-add-help",
                "languageapi-update-help",
                "languageapi-copy-help",
                "languageapi-create-help"), true);
    }

    public static TranslationImpl LANGUAGEAPI_ADD_HELP = new TranslationImpl("languageapi-add-help", true);
    public static TranslationImpl LANGUAGEAPI_ADD_SUCCESS = new TranslationImpl("languageapi-add-success", true);
    public static TranslationImpl LANGUAGEAPI_KEY_ALREADY_EXISTS = new TranslationImpl("languageapi-key-already-exists", true);
    public static TranslationImpl LANGUAGEAPI_LANG_NOT_FOUND = new TranslationImpl("languageapi-lang-not-found");
    public static TranslationImpl LANGUAGEAPI_REMOVE_KEY_IN_LANGUAGE = new TranslationImpl("languageapi-remove-key-in-language",true);
    public static TranslationImpl LANGUAGEAPI_REMOVE_KEY_IN_EVERY_LANGUAGE = new TranslationImpl("languageapi-remove-key-in-every-language",true);
    public static TranslationImpl LANGUAGEAPI_REMOVE_EVERY_KEY_IN_LANGUAGE = new TranslationImpl("languageapi-remove-every-key-in-language",true);
    public static TranslationImpl LANGUAGEAPI_REMOVE_EVERY_KEY_IN_EVERY_LANGUAGE = new TranslationImpl("languageapi-remove-every-key-in-every-language",true);
    public static TranslationImpl LANGUAGEAPI_KEY_NOT_FOUND = new TranslationImpl("languageapi-key-not-found", true);
    public static TranslationImpl LANGUAGEAPI_UPDATE_SUCCESS = new TranslationImpl("languageapi-update-success", true);
    public static TranslationImpl LANGUAGEAPI_UPDATE_INSTRUCTIONS = new TranslationImpl("languageapi-update-instructions", true);
    public static TranslationImpl LANGUAGEAPI_UPDATE_SAME = new TranslationImpl("languageapi-update-same", true);
    public static TranslationImpl LANGUAGEAPI_UPDATE_HELP = new TranslationImpl("languageapi-update-help", true);
    public static TranslationImpl LANGUAGEAPI_CREATE_SUCCESS = new TranslationImpl("languageapi-create-success", true);
    public static TranslationImpl LANGUAGEAPI_CREATE_HELP = new TranslationImpl("languageapi-create-help", true);
    public static TranslationImpl LANGUAGEAPI_LANG_ALREADY_EXISTS = new TranslationImpl("languageapi-lang-already-exists", true);
    public static TranslationImpl LANGUAGEAPI_DELETE_SUCCESS = new TranslationImpl("languageapi-delete-success", true);
    public static TranslationImpl LANGUAGEAPI_DELETE_ALL_LANGS = new TranslationImpl("languageapi-delete-all-langs", true);
    public static TranslationImpl LANGUAGEAPI_COPY_SUCCESS = new TranslationImpl("languageapi-copy-success", true);
    public static TranslationImpl LANGUAGEAPI_COPY_HELP = new TranslationImpl("languageapi-copy-help", true);
    public static TranslationImpl LANGUAGEAPI_KEY_HAS_NO_PARAM = new TranslationImpl("languageapi-key-has-no-param", true);
    public static TranslationImpl LANGUAGEAPI_SHOW_SUCCESS = new TranslationImpl("languageapi-show-success", true);
    public static TranslationImpl LANGUAGEAPI_TRANSLATION_SUCCESS = new TranslationImpl("languageapi-translation-success", true);
    public static TranslationImpl LANGUAGEAPI_RELOAD_SUCCESS = new TranslationImpl("languageapi-reload-success", true);
    public static TranslationImpl LANGUAGEAPI_HELP = new TranslationImpl("languageapi-help", true);
    public static TranslationImpl LANGUAGEAPI_NOPERMS = new TranslationImpl("languageapi-noperms", true);
    public static TranslationImpl LANGUAGEAPI_PLAYER_SELECTED_LANGUAGE = new TranslationImpl("languageapi-player-selected-language", true);

}
