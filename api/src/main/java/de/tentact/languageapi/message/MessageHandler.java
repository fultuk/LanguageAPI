package de.tentact.languageapi.message;

import org.jetbrains.annotations.ApiStatus;

import java.util.Locale;
import java.util.concurrent.CompletableFuture;

@ApiStatus.Internal
public interface MessageHandler {

  Identifier loadIdentifier(Identifier identifier);

  CompletableFuture<Identifier> loadIdentifierAsync(Identifier identifier);

  void writeIdentifier(Identifier identifier);

  String getMessage(Identifier identifier, Locale locale);

  CompletableFuture<String> getMessageAsync(Identifier identifier, Locale locale);

  void translateMessage(Identifier identifier, Locale locale, String translation);

}
