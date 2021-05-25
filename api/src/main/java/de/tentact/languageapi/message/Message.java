package de.tentact.languageapi.message;

import java.util.Locale;
import java.util.concurrent.CompletableFuture;

public interface Message {

  String build(Locale locale, Object... params);

  CompletableFuture<String> buildAsync(Locale locale, Object... params);

}
