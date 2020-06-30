package de.tentact.languageapi.util;
/*  Created in the IntelliJ IDEA.
    Created by 0utplay | Aldin Sijamhodzic
    Datum: 03.05.2020
    Uhrzeit: 14:02
*/

import de.tentact.languageapi.AbstractLanguageAPI;

import java.util.Arrays;

public class I18N {

    private static final AbstractLanguageAPI iLanguageAPI = AbstractLanguageAPI.getInstance();

    public static void createDefaultPluginMessages() {

        iLanguageAPI.addMessageToDefault("languageapi-prefix", "&eLanguageAPI x &7");

        iLanguageAPI.addMessageToDefault("languageapi-add-success", "Der Translationkey %KEY% wurde in %LANG% erfolgreich als ''%MSG%'' übersetzt.");
        iLanguageAPI.addParameter("languageapi-add-success", "%MSG%");
        iLanguageAPI.addMessageToDefault("languageapi-add-help", "Verwende /lang add <Sprache> <Translationkey> <Übersetzung> um einen Translationkey zu übersetzten.");

        iLanguageAPI.addMessageToDefault("languageapi-key-already-exists", "Der Translationkey %KEY% wurde in %LANG% bereits übersetzt." +
                " Verwende /lang update um die Übersetzung zu ändern.");
        iLanguageAPI.addParameter("languageapi-key-already-exists", "%KEY%, %LANG%");

        iLanguageAPI.addMessageToDefault("languageapi-lang-not-found", "Die Sprache %LANG% wurde nicht gefunden.");
        iLanguageAPI.addParameter("languageapi-lang-not-found", "%LANG%");


        iLanguageAPI.addMessageToDefault("languageapi-remove-key-in-lang", "Die Übersetzung des Translationkeys %KEY% in %LANG% wurde erfolgreich entfernt.");
        iLanguageAPI.addParameter("languageapi-remove-key-in-lan", "%KEY%, %LANG%");
        iLanguageAPI.addMessageToDefault("languageapi-remove-key-in-every-lang", "Der Translationkey %KEY% wurde in jeder Sprache entfernt.");
        iLanguageAPI.addParameter("languageapi-remove-key-in-every-lang", "%KEY%");
        iLanguageAPI.addMessageToDefault("languageapi-remove-every-key-in-lang", "Jeder Translationkey beginnend mit %STARTSWITH% wurde in %LANG% entfernt.");
        iLanguageAPI.addParameter("languageapi-remove-every-key-in-lang", "%STARTSWITH%, %LANG%");
        iLanguageAPI.addMessageToDefault("languageapi-remove-every-key-in-every-lang", "Jeder Translationkey beginnend mit %STARTSWITH% wurde in jeder Sprache entfernt.");
        iLanguageAPI.addParameter("languageapi-remove-every-key-in-every-lang", "%STARTSWITH%");

        iLanguageAPI.addMessageToDefault("languageapi-key-not-found", "Der Translationkey %KEY% wurde in %LANG% nicht gefunden.");
        iLanguageAPI.addParameter("languageapi-key-not-found", "%KEY%, %LANG%");

        iLanguageAPI.addMessageToDefault("languageapi-update-success", "&7Die Übersetzung des Translationkeys %KEY% in %LANG% wurde erfolgreich zu ''%MSG%''&7 geändert.");
        iLanguageAPI.addParameter("languageapi-update-success", "%MSG%");
        iLanguageAPI.addMessageToDefault("languageapi-update-instructions", "Nun kannst du die Übersetzung in den Chat eingeben, wenn benötigt auch mehrmals.\nMit ''finish'' wird die Übersetzung gespeichert.");
        iLanguageAPI.addMessageToDefault("languageapi-update-same", "Es wurde nichts verändert und die Übersetzung wurde abgebrochen.");
        iLanguageAPI.addMessageToDefault("languageapi-update-help", "Verwende /lang update <Sprache> <Translationkey> <Übersetzung> um eine Übersetzung zu ändern");


        iLanguageAPI.addMessageToDefault("languageapi-create-success", "Die Sprache %LANG% wurde erfolgreich erstellt.");
        iLanguageAPI.addParameter("languageapi-create-success", "%LANG%");
        iLanguageAPI.addMessageToDefault("languageapi-create-help", "Verwende /lang create <Sprache> um eine Sprache zu erstellen");

        iLanguageAPI.addMessageToDefault("languageapi-lang-already-exists", "Die Sprache %LANG% existiert bereits.");
        iLanguageAPI.addParameter("languageapi-lang-already-exists", "%LANG%");

        iLanguageAPI.addMessageToDefault("languageapi-delete-success", "Die Sprache %LANG% wurde erfolgreich gelöscht.");
        iLanguageAPI.addParameter("languageapi-delete-success", "%LANG%");
        iLanguageAPI.addMessageToDefault("languageapi-delete-all-langs", "Es wurde jede Sprache gelöscht.");
        iLanguageAPI.addParameter("languageapi-delete-all-langs", "");

        iLanguageAPI.addMessageToDefault("languageapi-copy-success", "%OLDLANG% wurde erfolgreich in %NEWLANG% kopiert.");
        iLanguageAPI.addParameter("languageapi-copy-success", "%OLDLANG%, %NEWLANG%");
        iLanguageAPI.addMessageToDefault("languageapi-copy-help", "Verwende /lang copy <Sprache> <Sprache2> um eine Sprache zu kopieren.");

        iLanguageAPI.addMessageToDefault("languageapi-key-has-no-param", "Der Translationkey %KEY% hat keine Parameter.");
        iLanguageAPI.addParameter("languageapi-key-has-no-param", "%LANG%");

        iLanguageAPI.addMessageToDefault("languageapi-show-success", "Es wurden folgende Parameter zu %KEY% gefunden: \nParameter: %PARAM%");
        iLanguageAPI.addParameter("languageapi-show-success", "%PARAM%, %KEY%");

        iLanguageAPI.addMessageToDefault("languageapi-translation-success", "&6%KEY%&7 - %MSG%");
        iLanguageAPI.addParameter("languageapi-translation-success", "%KEY%, %MSG%");


        iLanguageAPI.setMultipleTranslation("languageapi-help", Arrays.asList("languageapi-add-help",
                "languageapi-update-help",
                "languageapi-copy-help",
                "languageapi-create-help"), true);
    }

}
