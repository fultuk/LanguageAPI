package de.tentact.languageapi;

import java.util.ArrayList;
import java.util.UUID;

/*  Created in the IntelliJ IDEA.
    Copyright(c) 2020
    Created by 0utplay | Aldin Sijamhodzic
    Datum: 09.06.2020
    Uhrzeit: 23:29
*/
public abstract class ILanguageAPI {

    private static ILanguageAPI iLanguageAPI;

    public static ILanguageAPI getInstance() {
        return iLanguageAPI;
    }

    public static void setInstance(ILanguageAPI iLanguageAPI) {
        ILanguageAPI.iLanguageAPI = iLanguageAPI;
    }

    public abstract void createLanguage(final String langName);

    public abstract void deleteLanguage(String langName);

    public abstract void changePlayerLanguage(UUID playerUUID, String newLang);

    public abstract void createPlayer(UUID playerUUID);

    public abstract boolean playerExists(UUID playerUUID);

    public abstract void addMessage(final String transkey, final String message, final String lang, String param);

    public abstract void addMessage(final String transkey, final String message, final String lang);

    public abstract void addParameter(final String transkey, final String param);

    public abstract void deleteParameter(final String transkey, final String param);

    public abstract void deleteAllParameter(final String transkey);

    public abstract void addMessage(final String transkey, final String lang);

    public abstract void addMessage(final String transkey);

    public abstract void addMessageExtra(final String transkey, final String translation);

    public abstract void copyLanguage(String langfrom, String langto);

    public abstract boolean hasParameter(String translationKey);

    public abstract String getParameter(String translationKey);

    public abstract void deleteMessageInEveryLang(String transkey);

    public abstract void updateMessage(String transkey, String lang, String message);

    public abstract void deleteMessage(String transkey, String lang);

    public abstract String getPlayerLanguage(UUID playerUUID);

    public abstract boolean isKey(String transkey, String lang);

    public abstract String getMessage(String transkey, UUID playerUUID, boolean usePrefix);

    public abstract String getMessage(String transkey, UUID playerUUID);

    public abstract String getMessage(String transkey, String lang);

    public abstract boolean isLanguage(String lang);

    public abstract ArrayList<String> getAvailableLanguages();

    public abstract ArrayList<String> getLangUpdate();

    public abstract ArrayList<String> getAllKeys(String lang);

    public abstract ArrayList<String> getAllMessages(String lang);

    public abstract String getDefaultLanguage();

    public abstract String getPrefix();
}
