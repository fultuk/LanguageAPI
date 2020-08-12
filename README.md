LanguageAPI - Translate your Plugins
-
What is LanguageAPI?
- 
An API to the LanguageAPI, which aims to make the translation of messages into different languages efficient and easy.
Everything works with a unique key that returns the translation in the correct language. A key can not only lead to a translation,
but can lead also to a collection of keys that lead back to the translations.

Features
-
Powerful API

Ingame commands to do changes

How to use?
-
```java

       @Override
       public void onEnable() {
        //Add your keys in the database
        LanguageAPI languageAPI = LanguageAPI.getInstance();

        languageAPI.addMessage("an-example-translation-key");

        languageAPI.addMessageToDefault("an-example-translation-key", "This is an example translation in the default language");

        //Get a Message
        String translatedMessage = languageAPI.getMessage("an-example-translation-key", "examplelanguage");

        //Get a LanguagePlayer
        UUID playerId = UUID.randomUUID();
        LanguagePlayer languagePlayer = languageAPI.getPlayerManager().getLanguagePlayer(playerId);
        //Get an LanguageOfflinePlayer
        LanguageOfflinePlayer languageOfflinePlayer = languageAPI.getPlayerManager().getLanguageOfflinePlayer(playerId);
        //Use the PlayerExecutor to change properties
        PlayerExecutor playerExecutor = languageAPI.getPlayerExecutor();
        
        //Use the playerexecutor
        playerExecutor.registerPlayer(playerId);
    }
```

Get the API
-

Repository:
```
       <repositories>
		<repository>
		    <id>jitpack.io</id>
		    <url>https://jitpack.io</url>
		</repository>
	</repositories>
```
Dependency:
```
       <dependency>
	    <groupId>com.github.0utplay.LanguageAPI</groupId>
	    <artifactId>API</artifactId>
	    <version>1.7-RELEASE</version>
	</dependency>
```

