package de.tentact.languageapi.util;
/*  Created in the IntelliJ IDEA.
    Created by 0utplay | Aldin Sijamhodzic
    Datum: 03.05.2020
    Uhrzeit: 14:02
*/

import de.tentact.languageapi.api.LanguageImpl;

public class DefaultMessages {

    private static LanguageImpl languageImpl = LanguageImpl.getInstance();

    public static void createDefaultPluginMessages() {

        languageImpl.addMessageExtra("languageapi-prefix", "&eLanguageAPI x &7");

        languageImpl.addMessageExtra("languageapi-add-success", "Der Translationkey %KEY% wurde in %LANG% erfolgreich als ''%MSG%'' übersetzt.");
        languageImpl.addParameter("languageapi-add-success", "%MSG%");
        languageImpl.addMessageExtra("languageapi-add-help", "Benutzte /lang add <Sprache> <Translationkey> <Übersetzung> um einen Translationkey zu übersetzten.");

        languageImpl.addMessageExtra("languageapi-key-already-exists", "Der Translationkey %KEY% wurde in %LANG% bereits übersetzt." +
                " Verwende /lang update um die Übersetzung zu ändern.");
        languageImpl.addParameter("languageapi-key-already-exists", "%KEY%, %LANG%");

        languageImpl.addMessageExtra("languageapi-lang-not-found", "Die Sprache %LANG% wurde nicht gefunden.");
        languageImpl.addParameter("languageapi-lang-not-found", "%LANG%");



        languageImpl.addMessageExtra("languageapi-remove-key-in-lang", "Die Übersetzung des Translationkeys %KEY% in %LANG% wurde erfolgreich entfernt.");
        languageImpl.addParameter("languageapi-remove-key-in-lan", "%KEY%, %LANG%");
        languageImpl.addMessageExtra("languageapi-remove-key-in-every-lang", "Der Translationkey %KEY% wurde in jeder Sprache entfernt.");
        languageImpl.addParameter("languageapi-remove-key-in-every-lang", "%KEY%");
        languageImpl.addMessageExtra("languageapi-remove-every-key-in-lang", "Jeder Translationkey beginnend mit %STARTSWITH% wurde in %LANG% entfernt.");
        languageImpl.addParameter("languageapi-remove-every-key-in-lang", "%STARTSWITH%, %LANG%");
        languageImpl.addMessageExtra("languageapi-remove-every-key-in-every-lang", "Jeder Translationkey beginnend mit %STARTSWITH% wurde in jeder Sprache entfernt.");
        languageImpl.addParameter("languageapi-remove-every-key-in-every-lang", "%STARTSWITH%");

        languageImpl.addMessageExtra("languageapi-key-not-found", "Der Translationkey %KEY% wurde in %LANG% nicht gefunden.");
        languageImpl.addParameter("languageapi-key-not-found", "%KEY%, %LANG%");

        languageImpl.addMessageExtra("languageapi-update-success", "&7Die Übersetzung des Translationkeys %KEY% in %LANG% wurde erfolgreich zu ''%MSG%''&7 geändert.");
        languageImpl.addParameter("languageapi-update-success", "%MSG%");
        languageImpl.addMessageExtra("languageapi-update-instructions", "Nun kannst du die Übersetzung in den Chat eingeben, wenn benötigt auch mehrmals.\nMit ''finish'' wird die Übersetzung gespeichert.");
        languageImpl.addMessageExtra("languageapi-update-same", "Es wurde nichts verändert und die Übersetzung wurde abgebrochen.");
        languageImpl.addMessageExtra("languageapi-update-help", "Verwende /lang update <Sprache> <Translationkey> <Übersetzung>");


        languageImpl.addMessageExtra("languageapi-create-success", "Die Sprache %LANG% wurde erfolgreich erstellt.");
        languageImpl.addParameter("languageapi-create-success", "%LANG%");
        languageImpl.addMessageExtra("languageapi-create-help", "Verwende /lang create <Sprache>");

        languageImpl.addMessageExtra("languageapi-lang-already-exists", "Die Sprache %LANG% existiert bereits.");
        languageImpl.addParameter("languageapi-lang-already-exists", "%LANG%");

        languageImpl.addMessageExtra("languageapi-delete-success", "Die Sprache %LANG% wurde erfolgreich gelöscht.");
        languageImpl.addParameter("languageapi-delete-success", "%LANG%");
        languageImpl.addMessageExtra("languageapi-delete-all-langs", "Es wurde jede Sprache gelöscht.");
        languageImpl.addParameter("languageapi-delete-all-langs", "");

        languageImpl.addMessageExtra("languageapi-copy-success", "%OLDLANG% wurde erfolgreich in %NEWLANG% kopiert.");
        languageImpl.addParameter("languageapi-copy-success", "%OLDLANG%, %NEWLANG%");

        languageImpl.addMessageExtra("languageapi-key-has-no-param", "Der Translationkey %KEY% hat keine Parameter.");
        languageImpl.addParameter("languageapi-key-has-no-param", "%LANG%");

        languageImpl.addMessageExtra("languageapi-show-success", "Es wurden folgende Parameter zu %KEY% gefunden: \nParameter: %PARAM%");
        languageImpl.addParameter("languageapi-show-success", "%PARAM%, %KEY%");

        languageImpl.addMessageExtra("languageapi-translation-success", "&6%KEY%&7 - %MSG%");
        languageImpl.addParameter("languageapi-translation-success", "%KEY%, %MSG%");

        languageImpl.addMessageExtra("languageapi-copy-help", "Verwende /lang copy <Sprache> <Sprache2> um eine Sprache zu kopieren.");
    }

}
