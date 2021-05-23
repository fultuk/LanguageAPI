package de.tentact.languageapi.translation;

import java.util.List;
import java.util.Locale;

public interface Message {

    Builder builder();

    List<String> getParameters();

    String getTranslation();

    String getTranslationKey();

    Locale getLocale();

    boolean isDefaultLocale();

    interface Builder {

        Builder parameters(List<String> parameters);

        Builder parameters(String... parameters);

        Builder translation(String translation);

        Builder translationKey(String translationKey);

        Builder locale(Locale locale);

        Builder defaultLocale(boolean defaultLocale);

        Message build();

    }

}
