package de.tentact.languageapi.util;
/*  Created in the IntelliJ IDEA.
    Created by 0utplay | Aldin Sijamhodzic
    Datum: 03.05.2020
    Uhrzeit: 14:02
*/

import de.tentact.languageapi.api.LanguageAPI;

public class DefaultMessages {

    private static LanguageAPI languageAPI = LanguageAPI.getInstance();

    public static void createDefaultPluginMessages() {

        languageAPI.addMessageExtra("languageapi-add-success", "Der Translationkey %KEY% wurde in %LANG% erfolgreich als ''%MSG%'' übersetzt.");
        languageAPI.addParameter("languageapi-add-success", "%MSG%");

        languageAPI.addMessageExtra("languageapi-key-already-exists", "Der Translationkey %KEY% wurde in %LANG% bereits übersetzt." +
                " Verwende /lang change um die Übersetzung zu ändern.");
        languageAPI.addParameter("languageapi-key-already-exists", "%KEY%, %LANG%");

        languageAPI.addMessageExtra("languageapi-lang-not-found", "Die Sprache %LANG% wurde nicht gefunden.");
        languageAPI.addParameter("languageapi-lang-not-found", "%LANG%");

        languageAPI.addMessageExtra("languageapi-add-help", "Benutzte /lang add <Sprache> <Translationkey> <Übersetzung> um einen Translationkey zu übersetzten.");

        languageAPI.addMessageExtra("languageapi-remove-success", "Die Übersetzung des Translationkeys %KEY% in %LANG% wurde erfolgreich entfernt.");
        languageAPI.addParameter("languageapi-remove-success", "%KEY%, %LANG%");

        languageAPI.addMessageExtra("languageapi-key-not-found", "Der Translationkey %KEY% wurde in %LANG% nicht gefunden.");
        languageAPI.addParameter("languageapi-key-not-found", "%KEY%, %LANG%");

        languageAPI.addMessageExtra("languageapi-change-success", "Die Übersetzung des Translationkeys %KEY% in %LANG% wurde erfolgreich zu ''%MSG%'' geändert.");
        languageAPI.addParameter("languageapi-change-success", "%MSG%");

        languageAPI.addMessageExtra("languageapi-create-success", "Die Sprache %LANG% wurde erfolgreich erstellt.");
        languageAPI.addParameter("languageapi-create-success", "%LANG%");

        languageAPI.addMessageExtra("languageapi-lang-already-exists", "Die Sprache %LANG% existiert bereits.");
        languageAPI.addParameter("languageapi-lang-already-exists", "%LANG%");

        languageAPI.addMessageExtra("languageapi-delete-success", "Die Sprache %LANG% wurde erfolgreich gelöscht.");
        languageAPI.addParameter("languageapi-delete-success", "%LANG%");

        languageAPI.addMessageExtra("languageapi-copy-success", "%OLDLANG% wurde erfolgreich in %NEWLANG% kopiert.");
        languageAPI.addParameter("languageapi-copy-success", "%OLDLANG%, %NEWLANG%");

        languageAPI.addMessageExtra("languageapi-key-has-no-param-found", "Der Translationkey %KEY% hat keine Parameter.");
        languageAPI.addParameter("languageapi-key-has-no-param-foun", "%LANG%");

        languageAPI.addMessageExtra("languageapi-show-success", "Es wurden folgende Parameter zu %KEY% gefunden: \nParameter: %PARAM%");
        languageAPI.addParameter("languageapi-show-success", "%PARAM%, %KEY%");
    }

}
