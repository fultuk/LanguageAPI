package de.tentact.languageapi.message;

import java.util.Map;

public interface Identifier {

    static Identifier of(String translationKey) {
        return new DefaultIdentifier(translationKey);
    }

    static Identifier of(String translationKey, String... parameters) {
        return new DefaultIdentifier(translationKey, parameters);
    }

    Map<Integer, String> getParameters();

    String getTranslationKey();

    Identifier parameters(String... parameters);

    Identifier load();

    Identifier write();
}
