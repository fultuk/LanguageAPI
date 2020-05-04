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

        languageAPI.addMessageExtra("languageapi-add-success", "Der Translationkey %KEY% wurde in %LANG% erfolgreich als '%MSG%' übersetzt.");
        languageAPI.addMessageExtra("languageapi-key-already-exists", "Der Translationkey %KEY% wurde in %LANG% bereits übersetzt." +
                " Verwende /lang change um die Übersetzung zu ändern.");
        languageAPI.addMessageExtra("languageapi-lang-not-found", "Die Sprache %LANG% wurde nicht gefunden.");
        languageAPI.addMessageExtra("languageapi-add-help", "Benutzte /lang add <Sprache> <Translationkey> <Übersetzung> um einen Translationkey zu übersetzten.");
        languageAPI.addMessageExtra("languageapi-remove-success", "Die Übersetzung des Translationkeys %KEY% in %LANG% wurde erfolgreich entfernt.");
        languageAPI.addMessageExtra("languageapi-key-not-found", "Der Translationkey %KEY% wurde in %LANG% nicht gefunden.");
        languageAPI.addMessageExtra("languageapi-change-success", "Die Übersetzung des Translationkeys %KEY% in %LANG% wurde erfolgreich zu '%MSG%' geändert.");
        languageAPI.addMessageExtra("languageapi-create-success", "Die Sprache %LANG% wurde erfolgreich erstellt.");
        languageAPI.addMessageExtra("languageapi-lang-alredy-exists", "Die Sprache %LANG% existiert bereits.");
        languageAPI.addMessageExtra("languageapi-delete-success", "Die Sprache %LANG% wurde erfolgreich gelöscht.");
        languageAPI.addMessageExtra("languageapi-copy-success", "%OLDLANG% wurde erfolgreich in %NEWLANG% kopiert.");
        languageAPI.addMessageExtra("languageapi-key-has-no-param-found", "Der Translationkey %KEY% hat keine Parameter.");
        languageAPI.addMessageExtra("languageapi-show-success", "Es wurden folgende Parameter zu %KEY% gefunden: \n %PARAM%");
    }

}
