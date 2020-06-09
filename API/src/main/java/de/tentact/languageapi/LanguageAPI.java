package de.tentact.languageapi;

import java.util.ArrayList;
import java.util.UUID;

/*  Created in the IntelliJ IDEA.
    Copyright(c) 2020
    Created by 0utplay | Aldin Sijamhodzic
    Datum: 09.06.2020
    Uhrzeit: 23:29
*/
public interface LanguageAPI {

    void createLanguage(final String langName);

    void deleteLanguage(String langName);

    void changePlayerLanguage(UUID playerUUID, String newLang);

    void createPlayer(UUID playerUUID);

    boolean playerExists(UUID playerUUID);

    void addMessage(final String transkey, final String message, final String lang, String param);

    void addMessage(final String transkey, final String message, final String lang);

    void addParameter(final String transkey, final String param);

    void deleteParameter(final String transkey, final String param);

    void deleteAllParameter(final String transkey);

    void addMessage(final String transkey, final String lang);

    void addMessage(final String transkey);

    void addMessageExtra(final String transkey, final String translation);

    void copyLanguage(String langfrom, String langto);

    boolean hasParameter(String translationKey);

    String getParameter(String translationKey);

    void deleteMessageInEveryLang(String transkey);

    void updateMessage(String transkey, String lang, String message);

    void deleteMessage(String transkey, String lang);

    String getPlayerLanguage(UUID playerUUID);

    boolean isKey(String transkey, String lang);

    String getMessage(String transkey, UUID playerUUID, boolean usePrefix);

    String getMessage(String transkey, UUID playerUUID);

    String getMessage(String transkey, String lang);

    boolean isLanguage(String lang);

    ArrayList<String> getAvailableLanguages();

    ArrayList<String> getLangUpdate();

    ArrayList<String> getAllKeys(String lang);

    ArrayList<String> getAllMessages(String lang);

    String getDefaultLanguage();

    String getPrefix();
}
