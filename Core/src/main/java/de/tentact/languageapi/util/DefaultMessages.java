package de.tentact.languageapi.util;
/*  Created in the IntelliJ IDEA.
    Created by 0utplay | Aldin Sijamhodzic
    Datum: 03.05.2020
    Uhrzeit: 14:02
*/

import de.tentact.languageapi.ILanguageAPI;
import de.tentact.languageapi.api.LanguageAPI;

public class DefaultMessages {

    private static ILanguageAPI ILanguageAPI = LanguageAPI.getInstance();

    public static void createDefaultPluginMessages() {

        ILanguageAPI.addMessageExtra("languageapi-prefix", "&eLanguageAPI x &7");

        ILanguageAPI.addMessageExtra("languageapi-add-success", "Der Translationkey %KEY% wurde in %LANG% erfolgreich als ''%MSG%'' übersetzt.");
        ILanguageAPI.addParameter("languageapi-add-success", "%MSG%");
        ILanguageAPI.addMessageExtra("languageapi-add-help", "Benutzte /lang add <Sprache> <Translationkey> <Übersetzung> um einen Translationkey zu übersetzten.");

        ILanguageAPI.addMessageExtra("languageapi-key-already-exists", "Der Translationkey %KEY% wurde in %LANG% bereits übersetzt." +
                " Verwende /lang update um die Übersetzung zu ändern.");
        ILanguageAPI.addParameter("languageapi-key-already-exists", "%KEY%, %LANG%");

        ILanguageAPI.addMessageExtra("languageapi-lang-not-found", "Die Sprache %LANG% wurde nicht gefunden.");
        ILanguageAPI.addParameter("languageapi-lang-not-found", "%LANG%");



        ILanguageAPI.addMessageExtra("languageapi-remove-key-in-lang", "Die Übersetzung des Translationkeys %KEY% in %LANG% wurde erfolgreich entfernt.");
        ILanguageAPI.addParameter("languageapi-remove-key-in-lan", "%KEY%, %LANG%");
        ILanguageAPI.addMessageExtra("languageapi-remove-key-in-every-lang", "Der Translationkey %KEY% wurde in jeder Sprache entfernt.");
        ILanguageAPI.addParameter("languageapi-remove-key-in-every-lang", "%KEY%");
        ILanguageAPI.addMessageExtra("languageapi-remove-every-key-in-lang", "Jeder Translationkey beginnend mit %STARTSWITH% wurde in %LANG% entfernt.");
        ILanguageAPI.addParameter("languageapi-remove-every-key-in-lang", "%STARTSWITH%, %LANG%");
        ILanguageAPI.addMessageExtra("languageapi-remove-every-key-in-every-lang", "Jeder Translationkey beginnend mit %STARTSWITH% wurde in jeder Sprache entfernt.");
        ILanguageAPI.addParameter("languageapi-remove-every-key-in-every-lang", "%STARTSWITH%");

        ILanguageAPI.addMessageExtra("languageapi-key-not-found", "Der Translationkey %KEY% wurde in %LANG% nicht gefunden.");
        ILanguageAPI.addParameter("languageapi-key-not-found", "%KEY%, %LANG%");

        ILanguageAPI.addMessageExtra("languageapi-update-success", "&7Die Übersetzung des Translationkeys %KEY% in %LANG% wurde erfolgreich zu ''%MSG%''&7 geändert.");
        ILanguageAPI.addParameter("languageapi-update-success", "%MSG%");
        ILanguageAPI.addMessageExtra("languageapi-update-instructions", "Nun kannst du die Übersetzung in den Chat eingeben, wenn benötigt auch mehrmals.\nMit ''finish'' wird die Übersetzung gespeichert.");
        ILanguageAPI.addMessageExtra("languageapi-update-same", "Es wurde nichts verändert und die Übersetzung wurde abgebrochen.");
        ILanguageAPI.addMessageExtra("languageapi-update-help", "Verwende /lang update <Sprache> <Translationkey> <Übersetzung>");


        ILanguageAPI.addMessageExtra("languageapi-create-success", "Die Sprache %LANG% wurde erfolgreich erstellt.");
        ILanguageAPI.addParameter("languageapi-create-success", "%LANG%");
        ILanguageAPI.addMessageExtra("languageapi-create-help", "Verwende /lang create <Sprache>");

        ILanguageAPI.addMessageExtra("languageapi-lang-already-exists", "Die Sprache %LANG% existiert bereits.");
        ILanguageAPI.addParameter("languageapi-lang-already-exists", "%LANG%");

        ILanguageAPI.addMessageExtra("languageapi-delete-success", "Die Sprache %LANG% wurde erfolgreich gelöscht.");
        ILanguageAPI.addParameter("languageapi-delete-success", "%LANG%");
        ILanguageAPI.addMessageExtra("languageapi-delete-all-langs", "Es wurde jede Sprache gelöscht.");
        ILanguageAPI.addParameter("languageapi-delete-all-langs", "");

        ILanguageAPI.addMessageExtra("languageapi-copy-success", "%OLDLANG% wurde erfolgreich in %NEWLANG% kopiert.");
        ILanguageAPI.addParameter("languageapi-copy-success", "%OLDLANG%, %NEWLANG%");

        ILanguageAPI.addMessageExtra("languageapi-key-has-no-param", "Der Translationkey %KEY% hat keine Parameter.");
        ILanguageAPI.addParameter("languageapi-key-has-no-param", "%LANG%");

        ILanguageAPI.addMessageExtra("languageapi-show-success", "Es wurden folgende Parameter zu %KEY% gefunden: \nParameter: %PARAM%");
        ILanguageAPI.addParameter("languageapi-show-success", "%PARAM%, %KEY%");

        ILanguageAPI.addMessageExtra("languageapi-translation-success", "&6%KEY%&7 - %MSG%");
        ILanguageAPI.addParameter("languageapi-translation-success", "%KEY%, %MSG%");

        ILanguageAPI.addMessageExtra("languageapi-copy-help", "Verwende /lang copy <Sprache> <Sprache2> um eine Sprache zu kopieren.");
    }

}
