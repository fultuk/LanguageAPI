package de.tentact.languageapi.translation;
/*  Created in the IntelliJ IDEA.
    Copyright(c) 2020
    Created by 0utplay | Aldin Sijamhodzic
    Datum: 07.07.2020
    Uhrzeit: 15:47
*/

import de.tentact.languageapi.AbstractLanguageAPI;

import java.util.UUID;

/**
 *
 */

public class Translation {

    private final String translationkey;
    private final AbstractLanguageAPI abstractLanguageAPI = AbstractLanguageAPI.getInstance();
    private boolean usePrefix = false;

    /**
     *
     * @param translationkey
     */
    public Translation(String translationkey) {
        this.translationkey = translationkey;
    }

    /**
     *
     * @param playerUUID
     * @return
     */
    public String getMessage(UUID playerUUID) {
        return this.abstractLanguageAPI.getMessage(this.translationkey, playerUUID, usePrefix);
    }

    /**
     *
     * @param language
     * @return
     */
    public String getMessage(String language) {
        return this.abstractLanguageAPI.getMessage(this.translationkey, language, usePrefix);
    }

    /**
     *
     * @return
     */
    public String getParameter() {
       return this.abstractLanguageAPI.getParameter(this.translationkey);
    }
    public Translation setPrefix(boolean usePrefix) {
        this.usePrefix = usePrefix;
        return this;
    }
}
