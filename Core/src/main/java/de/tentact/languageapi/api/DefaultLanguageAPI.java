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

package de.tentact.languageapi.api;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.zaxxer.hikari.HikariDataSource;
import de.tentact.languageapi.LanguageAPI;
import de.tentact.languageapi.configuration.LanguageConfig;
import de.tentact.languageapi.configuration.MySQL;
import de.tentact.languageapi.file.FileHandler;
import de.tentact.languageapi.i18n.Translation;
import de.tentact.languageapi.player.ConsoleExecutor;
import de.tentact.languageapi.player.DefaultSpecificPlayerExecutor;
import de.tentact.languageapi.player.PlayerExecutor;
import de.tentact.languageapi.player.SpecificPlayerExecutor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public abstract class DefaultLanguageAPI extends LanguageAPI {

    private final MySQL mySQL;
    private final LanguageConfig languageConfig;

    private final Cache<String, Map<String, String>> translationCache;
    private final Map<String, Translation> translationMap;
    private final PlayerExecutor playerExecutor;
    private final FileHandler fileHandler;
    private final ExecutorService executorService = Executors.newCachedThreadPool(new ThreadFactoryBuilder().setNameFormat("LanguageAPI-Thread-%d").build());

    public DefaultLanguageAPI(LanguageConfig languageConfig) {
        this.languageConfig = languageConfig;
        this.playerExecutor = this.getPlayerExecutor();
        this.mySQL = languageConfig.getMySQL();
        this.translationCache = CacheBuilder.newBuilder().expireAfterWrite(languageConfig.getLanguageSetting().getCachedTime(), TimeUnit.MINUTES).build();
        this.translationMap = new HashMap<>();
        this.fileHandler = new DefaultFileHandler();
    }

    @Override
    public void createLanguage(final String language) {
        if (this.getAvailableLanguages().isEmpty() || !this.isLanguage(language)) {
            this.executorService.execute(() -> {
                this.mySQL.createLanguageTable(language.replace(" ", "").toLowerCase());
                try (Connection connection = this.getDataSource().getConnection();
                     PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO languages(language) VALUES (?)")) {
                    preparedStatement.setString(1, language.toLowerCase());
                    preparedStatement.execute();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
                this.addMessage("languageapi-prefix", "&eLanguageAPI x &7", language);
                this.debug("Creating new language: " + language);
            });
        }
    }

    @Override
    public void deleteLanguage(String language) {
        if (!this.getDefaultLanguage().equalsIgnoreCase(language) && this.isLanguage(language)) {
            this.executorService.execute(() -> {
                try (Connection connection = this.getDataSource().getConnection()) {
                    try (PreparedStatement preparedStatement = connection.prepareStatement("DROP TABLE " + language.toLowerCase() + "; DELETE FROM languages WHERE language=?;")) {
                        preparedStatement.setString(1, language.toLowerCase());
                        preparedStatement.execute();
                    }
                } catch (SQLException throwable) {
                    throwable.printStackTrace();
                }
                this.debug("Deleting language:" + language);
            });
        }
    }


    @Override
    public boolean addMessage(final String translationKey, final String message, final String language, String param) {
        if (!this.isLanguage(language)) {
            throw new IllegalArgumentException("Language " + language + " was not found!");
        }
        this.addParameter(translationKey, param);
        return this.addMessage(translationKey, message, language);
    }

    @Override
    public boolean addMessage(final String translationKey, final String message, final String language) {
        if (!this.isLanguage(language)) {
            return false;
        }
        if (this.isKey(translationKey, language)) {
            return false;
        }
        this.executorService.execute(() -> {
            try (Connection connection = this.getDataSource().getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO " + language.toLowerCase() + " (translationkey, translation) VALUES (?,?);")) {
                preparedStatement.setString(1, translationKey.toLowerCase());
                preparedStatement.setString(2, this.translateColorCode(message));
                preparedStatement.execute();
            } catch (SQLException throwable) {
                throwable.printStackTrace();
            }
        });
        return true;
    }

    @Override
    public void addParameter(final String translationKey, final String param) {
        if (param == null || param.isEmpty()) {
            return;
        }
        if (this.isParameter(translationKey, param)) {
            return;
        }
        String currentParameter = this.getParameter(translationKey);
        if (currentParameter == null) {
            this.setParameter(translationKey, param);
        } else {
            String joinedParameter = currentParameter.replace(" ", "");
            if (joinedParameter.endsWith(",")) {
                joinedParameter += param;
            } else {
                joinedParameter += "," + param;
            }
            this.setParameter(translationKey, joinedParameter);
        }
    }

    @Override
    public void setParameter(String translationKey, String param) {
        if (param == null || param.isEmpty()) {
            return;
        }
        this.executorService.execute(() -> {
            try (Connection connection = this.getDataSource().getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement("REPLACE INTO Parameter(translationkey, param) VALUES (?,?);")) {
                preparedStatement.setString(1, translationKey.toLowerCase());
                preparedStatement.setString(2, param.replace(" ", ""));
                preparedStatement.execute();
            } catch (SQLException throwable) {
                throwable.printStackTrace();
            }
        });
    }

    @Override
    public void deleteParameter(final String translationKey, final String param) {
        if (!this.hasParameter(translationKey)) {
            return;
        }
        String parameter = this.getParameter(translationKey);
        if (parameter == null || !parameter.contains(param)) {
            return;
        }
        this.executorService.execute(() -> {
            try (Connection connection = this.getDataSource().getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement("UPDATE Parameter SET param=? WHERE translationkey=?;")) {
                preparedStatement.setString(1, parameter.replace(param, ""));
                preparedStatement.setString(2, translationKey);
                preparedStatement.execute();
            } catch (SQLException throwable) {
                throwable.printStackTrace();
            }
        });

    }

    @Override
    public void deleteAllParameter(final String translationKey) {
        if (!this.hasParameter(translationKey)) {
            return;
        }
        this.executorService.execute(() -> {
            try (Connection connection = this.getDataSource().getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM Parameter WHERE translationkey=?;")) {
                preparedStatement.setString(1, translationKey);
                preparedStatement.execute();
            } catch (SQLException throwable) {
                throwable.printStackTrace();
            }
        });
    }

    @Override
    public boolean addMessage(final String translationKey, final String language) {
        return this.addMessage(translationKey, translationKey, language);
    }

    @Override
    public boolean addMessage(final String translationKey) {
        return this.addMessage(translationKey, translationKey, this.getDefaultLanguage());
    }

    @Override
    public boolean addMessageToDefault(final String translationKey, final String translation) {
        return this.addMessage(translationKey, translation, this.getDefaultLanguage());
    }

    @Override
    public boolean addMessageToDefault(final String translationKey, final String translation, final String param) {
        this.addParameter(translationKey, param);
        return this.addMessageToDefault(translationKey, translation);
    }

    @Override
    public void addTranslationKeyToMultipleTranslation(final String multipleTranslation, final String translationKey) {
        String[] translationKeys = new String[]{};
        try (Connection connection = this.getDataSource().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT translationkey FROM MultipleTranslation WHERE multipleKey=?;")) {
            preparedStatement.setString(1, multipleTranslation.toLowerCase());
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                translationKeys = resultSet.getString("translationkey").split(",");
            }
            resultSet.close();
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
        List<String> translationKeysAsArrayList = new ArrayList<>(Arrays.asList(translationKeys));
        translationKeysAsArrayList.add(translationKey);
        this.setMultipleTranslation(multipleTranslation, translationKeysAsArrayList, true);
    }

    @Override
    public void copyLanguage(String languageFrom, String languageTo) {
        if (!this.isLanguage(languageFrom.toLowerCase()) || !this.isLanguage(languageTo.toLowerCase())) {
            throw new IllegalArgumentException("Language " + languageFrom + " or " + languageTo + " was not found!");
        }
        this.executorService.execute(() -> {
            try (Connection connection = this.getDataSource().getConnection()) {
                connection.createStatement().execute("INSERT IGNORE " + languageTo + " SELECT * FROM " + languageFrom + ";");
            } catch (SQLException throwable) {
                throwable.printStackTrace();
            }
        });
    }

    @Override
    public boolean hasParameter(String translationKey) {
        if (translationKey == null) {
            return false;
        }
        try (Connection connection = this.getDataSource().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM Parameter WHERE translationkey=?;")) {
            preparedStatement.setString(1, translationKey);
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next();
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
        return false;
    }

    @Override
    public CompletableFuture<Boolean> hasParameterAsync(String translationKey) {
        return CompletableFuture.supplyAsync(() -> this.hasParameter(translationKey));
    }

    @Override
    public @Nullable String getParameter(String translationKey) {
        if (!this.hasParameter(translationKey)) {
            return null;
        }
        try (Connection connection = this.getDataSource().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT param FROM Parameter WHERE translationkey=?;")) {
            preparedStatement.setString(1, translationKey.toLowerCase());
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getString("param");
            }
            resultSet.close();
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
        return null;
    }

    @Override
    public @NotNull CompletableFuture<String> getParameterAsync(String translationKey) {
        return CompletableFuture.supplyAsync(() -> this.getParameter(translationKey));
    }

    @Override
    public boolean isParameter(String translationKey, String parameter) {
        if (!this.hasParameter(translationKey)) {
            return false;
        }
        return this.getParameter(translationKey).contains(parameter);
    }

    @Override
    public CompletableFuture<Boolean> isParameterAsync(String translationKey, String parameter) {
        return CompletableFuture.supplyAsync(() -> this.isParameter(translationKey, parameter));
    }

    @Override
    public void deleteMessageInEveryLang(String translationKey) {
        this.executorService.execute(() -> {
            for (String languages : this.getAvailableLanguages()) {
                if (this.isKey(translationKey, languages)) {
                    this.deleteMessage(translationKey, languages);
                }
            }
        });

    }

    @Override
    public void updateMessage(String translationKey, String message, String language) {
        if (!this.isLanguage(language)) {
            throw new IllegalArgumentException("Language " + language + " was not found!");
        }
        if (!this.isKey(translationKey, language)) {
            throw new IllegalArgumentException("Translationkey " + translationKey + " was not found!");
        }
        this.executorService.execute(() -> {
            try (Connection connection = this.getDataSource().getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement("UPDATE " + language + " SET translation=? WHERE translationkey=?;")) {
                preparedStatement.setString(1, this.translateColorCode(message));
                preparedStatement.setString(2, translationKey.toLowerCase());
                preparedStatement.execute();
            } catch (SQLException throwable) {
                throwable.printStackTrace();
            }
        });
        this.translationCache.invalidate(translationKey.toLowerCase());
    }

    @Override
    public void setMultipleTranslation(String multipleTranslation, List<String> translationKeys, boolean overwrite) {
        if (this.isMultipleTranslation(multipleTranslation)) {
            if (!overwrite) {
                return;
            }
            this.removeMultipleTranslation(multipleTranslation);
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (String translationKey : translationKeys) {
            stringBuilder.append(translationKey.toLowerCase()).append(",");
        }
        this.executorService.execute(() -> {
            try (Connection connection = this.getDataSource().getConnection();
                 PreparedStatement preparedStatement =
                         connection.prepareStatement("INSERT INTO MultipleTranslation(multipleKey, translationkey) VALUES (?,?)")) {
                preparedStatement.setString(1, multipleTranslation);
                preparedStatement.setString(2, stringBuilder.toString());
                preparedStatement.execute();
            } catch (SQLException throwable) {
                throwable.printStackTrace();
            }
        });

    }


    @Override
    public void removeMultipleTranslation(final String multipleTranslation) {
        if (!this.isMultipleTranslation(multipleTranslation)) {
            throw new IllegalArgumentException("Multiple Translation " + multipleTranslation + " was not found");
        }
        this.executorService.execute(() -> {
            try (Connection connection = this.getDataSource().getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM MultipleTranslation WHERE multipleKey=?;")) {
                preparedStatement.setString(1, multipleTranslation);
                preparedStatement.execute();
            } catch (SQLException throwable) {
                throwable.printStackTrace();
            }
        });

    }

    @Override
    public void removeSingleTranslationFromMultipleTranslation(final String multipleTranslation, final String translationKey) {
        if (!this.isMultipleTranslation(multipleTranslation)) {
            throw new IllegalArgumentException(multipleTranslation + " was not found");
        }
        List<String> translationKeysAsArrayList = null;
        try (Connection connection = this.getDataSource().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT translationkey FROM MultipleTranslation WHERE multipleKey=?")) {
            preparedStatement.setString(1, multipleTranslation.toLowerCase());
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                translationKeysAsArrayList = new ArrayList<>(Arrays.asList(resultSet.getString("translationKeys").split(",")));
            }
            resultSet.close();
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
        if (translationKeysAsArrayList == null) {
            return;
        }
        translationKeysAsArrayList.remove(translationKey);
        this.setMultipleTranslation(multipleTranslation, translationKeysAsArrayList, true);
    }

    @Override
    public boolean isMultipleTranslation(final String multipleTranslation) {
        try (Connection connection = this.getDataSource().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM MultipleTranslation WHERE multipleKey=?;")) {
            preparedStatement.setString(1, multipleTranslation.toLowerCase());
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next();
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
        return false;
    }

    @Override
    public void deleteMessage(String translationKey, String language) {
        if (!this.isLanguage(language)) {
            throw new IllegalArgumentException("Language " + language + " was not found!");
        }
        if (!this.isKey(translationKey, language)) {
            throw new IllegalArgumentException("Translationkey " + translationKey + " was not found!");
        }
        try (Connection connection = this.getDataSource().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM ? WHERE translationkey=?;")) {
            preparedStatement.setString(1, language.toLowerCase());
            preparedStatement.setString(2, translationKey.toLowerCase());
            preparedStatement.execute();
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
    }

    @Override
    public boolean isKey(String translationKey, String language) {
        try (Connection connection = this.getDataSource().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM " + language.toLowerCase() + " WHERE translationkey=?;")) {
            preparedStatement.setString(1, translationKey.toLowerCase());
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next();
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
        return false;
    }

    public CompletableFuture<Boolean> isKeyAsync(String translationKey, String language) {
        return CompletableFuture.supplyAsync(() -> this.isKey(translationKey, language));
    }

    @NotNull
    @Override
    public String getMessage(String translationKey, UUID playerUUID) {
        return this.getMessage(translationKey, this.playerExecutor.getPlayerLanguage(playerUUID));
    }

    @Override
    public @NotNull CompletableFuture<String> getMessageAsync(String translationKey, UUID playerUUID) {
        return CompletableFuture.supplyAsync(() -> this.getMessage(translationKey, playerUUID));
    }

    @NotNull
    @Override
    public List<String> getMultipleMessages(String translationKey) {
        return this.getMultipleMessages(translationKey, this.getDefaultLanguage());
    }

    @Override
    public @NotNull CompletableFuture<List<String>> getMultipleMessagesAsync(String translationKey) {
        return CompletableFuture.supplyAsync(() -> this.getMultipleMessages(translationKey));
    }

    @NotNull
    @Override
    public List<String> getMultipleMessages(String translationKey, UUID playerUUID) {
        return this.getMultipleMessages(translationKey, this.playerExecutor.getPlayerLanguage(playerUUID));
    }

    @Override
    public @NotNull CompletableFuture<List<String>> getMultipleMessagesAsync(String translationKey, UUID playerUUID) {
        return CompletableFuture.supplyAsync(() -> this.getMultipleMessages(translationKey, playerUUID));
    }

    @NotNull
    @Override
    public List<String> getMultipleMessages(String translationKey, String language) {
        return this.getMultipleMessages(translationKey, language, "");
    }

    @Override
    public @NotNull CompletableFuture<List<String>> getMultipleMessagesAsync(String translationKey, String language) {
        return CompletableFuture.supplyAsync(() -> this.getMultipleMessages(translationKey, language));
    }

    @Override
    public @NotNull List<String> getMultipleMessages(String multipleKey, String language, String prefixKey) {
        List<String> resolvedMessages = new ArrayList<>();
        String[] translationKeys = new String[0];
        String prefix = "";
        if (prefixKey != null && !prefixKey.isEmpty()) {
            prefix = this.getMessage(prefixKey, language);
        }
        try (Connection connection = this.getDataSource().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT translationkey FROM MultipleTranslation WHERE multipleKey=?;")) {
            preparedStatement.setString(1, multipleKey.toLowerCase());
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                String column = resultSet.getString("translationKey");
                translationKeys = column.split(",");
            }
            resultSet.close();
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
        for (String translationKey : translationKeys) {
            resolvedMessages.add(prefix + this.getMessage(translationKey, language));
        }
        return resolvedMessages;
    }

    @Override
    public @NotNull CompletableFuture<List<String>> getMultipleMessagesAsync(String multipleKey, String language, String prefixKey) {
        return CompletableFuture.supplyAsync(() -> this.getMultipleMessages(multipleKey, language, prefixKey));
    }

    @NotNull
    @Override
    public String getMessage(String translationKey, String language) {
        if (!this.isLanguage(language)) {
            throw new IllegalArgumentException(language + " was not found");
        }
        if (!this.isKey(translationKey, language)) {
            this.languageConfig.debug("Translationkey '" + translationKey + "' not found in language '" + language + "'");
            this.languageConfig.debug("As result you will get the translationKey as translation");
            return translationKey;
        }
        Map<String, String> cacheMap = this.translationCache.getIfPresent(translationKey.toLowerCase());
        if (cacheMap != null && cacheMap.containsKey(language)) {
            return cacheMap.get(language);
        }
        try (Connection connection = this.getDataSource().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT translation FROM " + language.toLowerCase() + " WHERE translationkey=?;")) {
            preparedStatement.setString(1, translationKey);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    String translation = this.translateColorCode(resultSet.getString("translation"));
                    Map<String, String> map = new HashMap<>(1);
                    map.put(language, translation);
                    this.translationCache.put(translationKey, map);
                    return translation;
                }
            }
        } catch (SQLException throwable) {
            throwable.printStackTrace();
            return translationKey;
        }
        return translationKey;
    }

    @Override
    public @NotNull CompletableFuture<String> getMessageAsync(String translationKey, String language) {
        return CompletableFuture.supplyAsync(() -> this.getMessage(translationKey, language));
    }

    @Override
    public boolean isLanguage(String language) {
        if (language == null) {
            return false;
        }
        return this.getAvailableLanguages().contains(language.toLowerCase());
    }

    @Override
    public CompletableFuture<Boolean> isLanguageAsync(@Nullable String language) {
        return CompletableFuture.supplyAsync(() -> this.isLanguage(language));
    }

    @NotNull
    @Override
    public List<String> getAvailableLanguages() {
        List<String> languages = new ArrayList<>();
        try (Connection connection = this.getDataSource().getConnection();
             ResultSet resultSet = connection.createStatement().executeQuery("SELECT language FROM languages")) {
            while (resultSet.next()) {
                languages.add(resultSet.getString("language").toLowerCase());
            }
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
        return languages;
    }

    @Override
    public @NotNull CompletableFuture<List<String>> getAvailableLanguagesAsync() {
        return CompletableFuture.supplyAsync(this::getAvailableLanguages);
    }

    @Override
    public @NotNull List<String> getAllTranslationKeys(String language) {
        List<String> keys = new ArrayList<>();
        if (this.isLanguage(language)) {
            try (Connection connection = this.getDataSource().getConnection();
                 ResultSet resultSet = connection.createStatement().executeQuery("SELECT translationkey FROM " + language)) {
                while (resultSet.next()) {
                    keys.add(resultSet.getString("translationkey"));
                }
            } catch (SQLException throwable) {
                throwable.printStackTrace();
            }
            return keys;
        }
        throw new IllegalArgumentException("Language " + language + " was not found");
    }

    @Override
    public @NotNull CompletableFuture<List<String>> getAllTranslationKeysAsync(String language) {
        return CompletableFuture.supplyAsync(() -> this.getAllTranslationKeys(language));
    }

    @Override
    public @NotNull List<String> getAllTranslations(String language) {
        List<String> messages = new ArrayList<>();
        if (this.isLanguage(language)) {
            try (Connection connection = this.getDataSource().getConnection();
                 ResultSet resultSet = connection.createStatement().executeQuery("SELECT translation FROM " + language)) {
                while (resultSet.next()) {
                    messages.add(resultSet.getString("translation"));
                }
            } catch (SQLException throwable) {
                throwable.printStackTrace();
            }
            return messages;
        }
        throw new IllegalArgumentException("Language " + language + " was not found");
    }

    @Override
    public @NotNull CompletableFuture<List<String>> getAllTranslationsAsync(String language) {
        return CompletableFuture.supplyAsync(() -> this.getAllTranslations(language));
    }

    @Override
    public @NotNull Map<String, String> getKeysAndTranslations(String language) {
        if (!this.isLanguage(language)) {
            throw new IllegalArgumentException("Language " + language + " was not found");
        }
        Map<String, String> keysAndTranslations = new HashMap<>();
        this.getAllTranslationKeys(language).forEach(key -> keysAndTranslations.put(key, this.getMessage(key, language)));
        return keysAndTranslations;
    }

    @Override
    public @NotNull CompletableFuture<Map<String, String>> getKeysAndTranslationsAsync(String language) {
        return CompletableFuture.supplyAsync(() -> this.getKeysAndTranslations(language));
    }

    @Override
    public @NotNull String getDefaultLanguage() {
        return this.languageConfig.getLanguageSetting().getDefaultLanguage().toLowerCase();
    }

    @Override
    public @NotNull String getLanguageAPIPrefix() {
        return this.getLanguageAPIPrefix(this.getDefaultLanguage());
    }

    @Override
    public @NotNull String getLanguageAPIPrefix(String language) {
        return this.getMessage("languageapi-prefix", language);
    }

    @Override
    public @NotNull Translation getTranslation(@NotNull String translationKey) {
        if (this.translationMap.containsKey(translationKey)) {
            return this.translationMap.get(translationKey);
        }
        Translation translation = new DefaultTranslation(translationKey);
        this.updateTranslation(translation);
        return translation;
    }

    @Override
    public @NotNull Translation getTranslationWithPrefix(Translation prefixTranslation, String translationKey) {
        return this.getTranslation(translationKey).setPrefixTranslation(prefixTranslation);
    }

    @Override
    public abstract @NotNull PlayerExecutor getPlayerExecutor();

    @Override
    public abstract @NotNull ConsoleExecutor getConsoleExecutor();

    @Override
    public @NotNull SpecificPlayerExecutor getSpecificPlayerExecutor(@NotNull UUID playerId) {
        return new DefaultSpecificPlayerExecutor(playerId);
    }

    @Override
    public void updateTranslation(Translation translation) {
        this.translationMap.put(translation.getTranslationKey(), translation);
    }

    @Override
    public FileHandler getFileHandler() {
        return this.fileHandler;
    }

    @Override
    public void executeAsync(Runnable command) {
        this.executorService.execute(command);
    }

    private HikariDataSource getDataSource() {
        return this.mySQL.getDataSource();
    }

    private void debug(String message) {
        this.languageConfig.debug(message);
    }

    //Original from bungeecord
    private String translateColorCode(String textToTranslate) {
        char[] b = textToTranslate.toCharArray();
        for (int i = 0; i < b.length - 1; i++) {
            if (b[i] == '&' && "0123456789AaBbCcDdEeFfKkLlMmNnOoRr".indexOf(b[i + 1]) > -1) {
                b[i] = '§';
                b[i + 1] = Character.toLowerCase(b[i + 1]);
            }
        }
        return new String(b);
    }
}
