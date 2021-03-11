/*
 * MIT License
 *
 * Copyright (c) 2020 0utplay (Aldin Sijamhodzic)
 * Copyright (c) 2020 contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package de.tentact.languageapi.i18n;

import de.tentact.languageapi.LanguageAPI;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public enum I18N {

    LANGUAGEAPI_ADD_HELP("languageapi-add-help", "Verwende /lang add <Sprache> <Translationkey> <Übersetzung> um einen Translationkey zu übersetzten."),
    LANGUAGEAPI_PREFIX("languageapi-prefix", "&eLanguageAPI x &7"),
    LANGUAGEAPI_ADD_SUCCESS("languageapi-add-success", "Der Translationkey %KEY% wurde in %LANG% erfolgreich als ''%MSG%'' übersetzt.", "%MSG%,%KEY%"),
    LANGUAGEAPI_KEY_ALREADY_EXISTS("languageapi-key-already-exists", "Der Translationkey %KEY% wurde in %LANG% bereits übersetzt." +
            " Verwende /lang update um die Übersetzung zu ändern.", "%KEY%, %LANG%"),
    LANGUAGEAPI_LANG_NOT_FOUND("languageapi-lang-not-found", "Die Sprache %LANG% wurde nicht gefunden.", "%LANG%"),
    LANGUAGEAPI_REMOVE_KEY_IN_LANGUAGE("languageapi-remove-key-in-language", "Die Übersetzung des Translationkeys %KEY% in %LANG% wurde erfolgreich entfernt.",
            "%KEY%, %LANG%"),
    LANGUAGEAPI_REMOVE_KEY_IN_EVERY_LANGUAGE("languageapi-remove-key-in-every-language", "Der Translationkey %KEY% wurde in jeder Sprache entfernt.",
            "%KEY%"),
    LANGUAGEAPI_REMOVE_EVERY_KEY_IN_LANGUAGE("languageapi-remove-every-key-in-language", "Jeder Translationkey beginnend mit %STARTSWITH% wurde in %LANG% entfernt.",
            "%STARTSWITH%, %LANG%"),
    LANGUAGEAPI_REMOVE_EVERY_KEY_IN_EVERY_LANGUAGE("languageapi-remove-every-key-in-every-language", "Jeder Translationkey beginnend mit %STARTSWITH% wurde in jeder Sprache entfernt.",
            "%STARTSWITH%"),
    LANGUAGEAPI_KEY_NOT_FOUND("languageapi-key-not-found", "Der Translationkey %KEY% wurde in %LANG% nicht gefunden.", "%KEY%, %LANG%"),
    LANGUAGEAPI_UPDATE_SUCCESS("languageapi-update-success", "&7Die Übersetzung des Translationkeys %KEY% in %LANG% wurde erfolgreich zu ''%MSG%''&7 geändert.",
            "%MSG%"),
    LANGUAGEAPI_LANGUAGES_LIST("languageapi-languages-list", "Aktuell sind folgende Sprachen verfügbar: %LANGUAGES%", "%LANGUAGES%"),
    LANGUAGE_LIST_HELP("languageapi-list-help", "Verwende /lang list um die alle verfügbaren Sprachen anzuzeigen"),
    LANGUAGEAPI_UPDATE_INSTRUCTIONS("languageapi-update-instructions", "Nun kannst du die Übersetzung in den Chat eingeben, wenn benötigt auch mehrmals.\nMit ''finish'' wird die Übersetzung gespeichert."),
    LANGUAGEAPI_UPDATE_SAME("languageapi-update-same", "Es wurde nichts verändert und die Übersetzung wurde abgebrochen."),
    LANGUAGEAPI_UPDATE_HELP("languageapi-update-help", "Verwende /lang update <Sprache> <Translationkey> <Übersetzung> um eine Übersetzung zu ändern"),
    LANGUAGEAPI_CREATE_SUCCESS("languageapi-create-success", "Die Sprache %LANG% wurde erfolgreich erstellt.", "%LANG%"),
    LANGUAGEAPI_CREATE_HELP("languageapi-create-help", "Verwende /lang create <Sprache> um eine Sprache zu erstellen"),
    LANGUAGEAPI_LANG_ALREADY_EXISTS("languageapi-lang-already-exists", "Die Sprache %LANG% existiert bereits.", "%LANG%"),
    LANGUAGEAPI_DELETE_SUCCESS("languageapi-delete-success", "Die Sprache %LANG% wurde erfolgreich gelöscht.", "%LANG%"),
    LANGUAGEAPI_DELETE_ALL_LANGS("languageapi-delete-all-langs", "Jede Sprache wurde gelöscht."),
    LANGUAGEAPI_COPY_SUCCESS("languageapi-copy-success", "%OLDLANG% wurde erfolgreich in %NEWLANG% kopiert.", "%OLDLANG%, %NEWLANG%"),
    LANGUAGEAPI_COPY_HELP("languageapi-copy-help", "Verwende /lang copy <Sprache> <Sprache2> um eine Sprache zu kopieren."),
    LANGUAGEAPI_KEY_HAS_NO_PARAM("languageapi-key-has-no-param", "Der Translationkey %KEY% hat keine Parameter.", "%LANG%"),
    LANGUAGEAPI_SHOW_SUCCESS("languageapi-show-success", "Es wurden folgende Parameter zu %KEY% gefunden: \nParameter: %PARAM%", "%PARAM%, %KEY%"),
    LANGUAGEAPI_TRANSLATION_SUCCESS("languageapi-translation-success", "&6%KEY%&7 - %MSG%", "%KEY%, %MSG%"),
    LANGUAGEAPI_RELOAD_SUCCESS("languageapi-reload-success", "Die Configuration wurde neugeladen."),
    LANGUAGEAPI_IMPORT_SUCCESS("languageapi-import-success", "Die Datei %FILE% wurde erfolgreich importiert.", "%FILE%"),
    LANGUAGEAPI_IMPORT_HELP("languageapi-import-help", "Verwende /lang import <File> <doOverwrite> um eine Datei zu importieren. \n" +
            "Diese muss in /plugins/LanguageAPI/import/ liegen."),
    LANGUAGEAPI_IMPORT_ERROR("languageapi-import-error", "Beim Importieren der Datei ist ein Fehler aufgetreten. \n" +
            "Die Datei muss 'language: LANGUAGEANAME' enthalten."),
    LANGUAGEAPI_IMPORT_FILE_NOT_FOUND("languageapi-import-file-not-found", "Die Datei %FILE% wurde in /plugins/LanguageAPI/import nicht gefunden.", "%FILE%"),
    LANGUAGEAPI_EXPORT_SUCCESS("languageapi-export-succes", "Die Datei %FILE% wurde erfolgreich nach /plugins/LanguageAPI/export/%FILE% exportiert", "%FILE%"),
    LANGUAGEAPI_EXPORT_ALL_SUCCESS("languageapi-export-all-success", "Es wurden alle Sprachen erfolgreich exportiert."),
    LANGUAGEAPI_EXPORT_HELP("languageapi-export-help", "Verwende /lang export <Sprache>"),
    LANGUAGEAPI_EXPORT_ERROR("languageapi-export-error", "Beim Exportieren der Sprache %LANGUAGE% ist ein Fehler aufgetreten.", "%LANGUAGE%"),
    LANGUAGEAPI_NOPERMS("languageapi-noperms", "Du hast keine Rechte dazu."),
    LANGUAGEAPI_PLAYER_SELECTED_LANGUAGE("languageapi-player-selected-language", "Du hast die Sprache %LANGUAGE% ausgewählt.", "%LANGUAGE%"),
    LANGUAGEAPI_INFO("languageapi-info", "Du benutzt folgende Version der LanguageAPI: %VERSION%", "%VERSION%"),
    LANGUAGEAPI_HELP("languageapi-help", Arrays.asList(
            LANGUAGEAPI_ADD_HELP.key,
            LANGUAGEAPI_UPDATE_HELP.key,
            LANGUAGEAPI_COPY_HELP.key,
            LANGUAGEAPI_CREATE_HELP.key,
            LANGUAGEAPI_IMPORT_HELP.key,
            LANGUAGEAPI_EXPORT_HELP.key,
            LANGUAGE_LIST_HELP.key
    ));

    private final String key;

    I18N(String key, String defaultTranslation) {
        this(key, defaultTranslation, Collections.emptyList());
    }

    I18N(String key, String defaultTranslation, String parameter) {
        this.key = key;
        LanguageAPI.getInstance().addMessageToDefault(key, defaultTranslation, Arrays.asList(parameter.split(",")));
    }

    I18N(String key, String defaultTranslation, List<String> parameter) {
        this.key = key;
        LanguageAPI.getInstance().addMessageToDefault(key, defaultTranslation, parameter);
    }

    I18N(String key, List<String> keys) {
        this.key = key;
        LanguageAPI.getInstance().addMultipleTranslations(key, new ArrayList<>(keys));
    }

    public Translation get() {
        return this.get(true);
    }

    public Translation get(boolean withPrefix) {
        if (withPrefix) {
            return LanguageAPI.getInstance().getTranslationWithPrefix(LANGUAGEAPI_PREFIX.get(false), this.key);
        }
        return LanguageAPI.getInstance().getTranslation(this.key);
    }
}
