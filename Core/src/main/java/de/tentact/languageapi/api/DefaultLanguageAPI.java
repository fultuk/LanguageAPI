/*
 * MIT License
 *
 * Copyright (c) 2020 0utplay
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
import de.tentact.languageapi.player.PlayerExecutor;
import de.tentact.languageapi.player.PlayerManager;
import de.tentact.languageapi.player.SpecificPlayerExecutor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

public abstract class DefaultLanguageAPI extends LanguageAPI {

    private final MySQL mySQL;
    private final LanguageConfig languageConfig;

    private final Cache<String, HashMap<String, String>> translationCache;
    private final Map<String, Translation> translationMap;
    private final PlayerExecutor playerExecutor;
    private final ExecutorService executorService = Executors.newCachedThreadPool(new ThreadFactoryBuilder().setNameFormat("LanguageAPI-Thread-%d").build());

    public DefaultLanguageAPI(LanguageConfig languageConfig) {
        this.languageConfig = languageConfig;
        this.playerExecutor = this.getPlayerExecutor();
        this.mySQL = languageConfig.getMySQL();
        this.translationCache = CacheBuilder.newBuilder().expireAfterWrite(languageConfig.getLanguageSetting().getCachedTime(), TimeUnit.MINUTES).build();
        this.translationMap = new HashMap<>();
    }

    @Override
    public void createLanguage(final String language) {
        if (this.getAvailableLanguages().isEmpty() || !this.isLanguage(language)) {
            this.executorService.execute(() -> {
                this.mySQL.createTable(language.replace(" ", "").toLowerCase());
                try (Connection connection = this.getDataSource().getConnection();
                     PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO languages(language) VALUES (?)")) {
                    preparedStatement.setString(1, language.toLowerCase());
                    preparedStatement.execute();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
                this.addMessage("languageapi-prefix", "&eLanguageAPI x &7", language);
                this.logInfo("Creating new language:" + language);
            });

        }
    }

    @Override
    public void deleteLanguage(String language) {
        if (!this.getDefaultLanguage().equalsIgnoreCase(language) && this.isLanguage(language)) {
            this.executorService.execute(() -> {
                try (Connection connection = this.getDataSource().getConnection()) {
                    try (PreparedStatement preparedStatement = connection.prepareStatement("DROP TABLE ?;")) {
                        preparedStatement.setString(1, language.toLowerCase());
                        preparedStatement.execute();
                    }
                    try (PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM languages WHERE language=?;")) {
                        preparedStatement.setString(1, language.toLowerCase());
                        preparedStatement.execute();
                    }
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
                this.logInfo("Deleting language:" + language);
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
                 PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO " + language.toLowerCase() + " (transkey, translation) VALUES (?,?);")) {
                preparedStatement.setString(1, translationKey.toLowerCase());
                preparedStatement.setString(2, this.replaceColor(message));
                preparedStatement.execute();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        });
        return true;
    }

    @Override
    public void addParameter(final String translationKey, final String param) {
        if (param == null || param.isEmpty()) {
            return;
        }
        this.executorService.execute(() -> {
            try (Connection connection = this.getDataSource().getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement("REPLACE INTO Parameter(transkey, param) VALUES (?,?);")) {
                preparedStatement.setString(1, translationKey.toLowerCase());
                preparedStatement.setString(2, param.replace(" ", ""));
                preparedStatement.execute();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        });

    }

    @Override
    public void deleteParameter(final String translationKey, final String param) {
        if (!this.hasParameter(translationKey)) {
            return;
        }
        if (!this.getParameter(translationKey).contains(param)) {
            return;
        }
        this.executorService.execute(() -> {
            try (Connection connection = this.getDataSource().getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement("UPDATE Parameter SET param=? WHERE transkey=?;")) {
                preparedStatement.setString(1, this.getParameter(translationKey).replace(param, ""));
                preparedStatement.setString(2, translationKey);
                preparedStatement.execute();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
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
                 PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM Parameter WHERE transkey=?;")) {
                preparedStatement.setString(1, translationKey);
                preparedStatement.execute();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
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
    public void addTranslationKeyToMultipleTranslation(final String multipleTranslation, final String transkey) {
        String[] translationKeys = new String[]{};
        try (Connection connection = this.getDataSource().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT transkeys FROM MultipleTranslation WHERE multipleKey=?;")) {
            preparedStatement.setString(1, multipleTranslation.toLowerCase());
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                translationKeys = resultSet.getString("transkeys").split(",");
            }
            resultSet.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        ArrayList<String> translationKeysAsArrayList = new ArrayList<>(Arrays.asList(translationKeys));
        translationKeysAsArrayList.add(transkey);
        this.setMultipleTranslation(multipleTranslation, translationKeysAsArrayList, true);
    }

    @Override
    public void copyLanguage(String langfrom, String langto) {
        if (!this.isLanguage(langfrom.toLowerCase()) || !this.isLanguage(langto.toLowerCase())) {
            throw new IllegalArgumentException("Language " + langfrom + " or " + langto + " was not found!");
        }
        try (Connection connection = this.getDataSource().getConnection()) {
            connection.createStatement().execute("INSERT IGNORE " + langto + " SELECT * FROM " + langfrom + ";");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    @Override
    public boolean hasParameter(String translationKey) {
        try (Connection connection = this.getDataSource().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM Parameter WHERE transkey=?;")) {
            preparedStatement.setString(1, translationKey);
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return false;
    }

    @Override
    public @Nullable String getParameter(String translationKey) {
        if (!this.hasParameter(translationKey)) {
            return null;
        }
        try (Connection connection = this.getDataSource().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT param FROM Parameter WHERE transkey=?;")) {
            preparedStatement.setString(1, translationKey.toLowerCase());
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getString("param");
            }
            resultSet.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        throw new IllegalArgumentException(translationKey + " has no parameter");
    }

    @Override
    public boolean isParameter(String translationKey, String param) {
        if (!this.hasParameter(translationKey)) {
            return false;
        }
        return this.getParameter(translationKey).contains(param);
    }

    @Override
    public void deleteMessageInEveryLang(String transkey) {
        this.executorService.execute(() -> {
            for (String langs : this.getAvailableLanguages()) {
                if (this.isKey(transkey, langs)) {
                    this.deleteMessage(transkey, langs);
                }
            }
        });

    }

    @Override
    public void updateMessage(String transkey, String message, String language) {
        if (!this.isLanguage(language)) {
            throw new IllegalArgumentException("Language " + language + " was not found!");
        }
        if (!this.isKey(transkey, language)) {
            throw new IllegalArgumentException("Translationkey " + transkey + " was not found!");
        }
        this.executorService.execute(() -> {
            try (Connection connection = this.getDataSource().getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement("UPDATE " + language + " SET translation=? WHERE transkey=?;")) {
                preparedStatement.setString(1, this.replaceColor(message));
                preparedStatement.setString(2, transkey.toLowerCase());
                preparedStatement.execute();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        });
        translationCache.invalidate(transkey.toLowerCase());
    }

    @Override
    public void setMultipleTranslation(String multipleTranslation, List<String> translationKeys, boolean overwrite) {
        if (isMultipleTranslation(multipleTranslation)) {
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
                         connection.prepareStatement("INSERT INTO MultipleTranslation(multipleKey, transkeys) VALUES (?,?)")) {
                preparedStatement.setString(1, multipleTranslation);
                preparedStatement.setString(2, stringBuilder.toString());
                preparedStatement.execute();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        });

    }


    @Override
    public void removeMultipleTranslation(final String multipleTranslation) {
        if (!isMultipleTranslation(multipleTranslation)) {
            throw new IllegalArgumentException(multipleTranslation + " was not found");
        }
        this.executorService.execute(() -> {
            try (Connection connection = this.getDataSource().getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM MultipleTranslation WHERE multipleKey=?;")) {
                preparedStatement.setString(1, multipleTranslation);
                preparedStatement.execute();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        });

    }

    @Override
    public void removeSingleTranslationFromMultipleTranslation(final String multipleTranslation, final String transkey) {
        if (!isMultipleTranslation(multipleTranslation)) {
            throw new IllegalArgumentException(multipleTranslation + " was not found");
        }
        List<String> translationKeysAsArrayList = null;
        try (Connection connection = this.getDataSource().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT transkeys FROM MultipleTranslation WHERE multipleKey=?")) {
            preparedStatement.setString(1, multipleTranslation.toLowerCase());
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                translationKeysAsArrayList = new ArrayList<>(Arrays.asList(resultSet.getString("transkeys").split(",")));
            }
            resultSet.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        if (translationKeysAsArrayList == null) {
            return;
        }
        translationKeysAsArrayList.remove(transkey);
        this.setMultipleTranslation(multipleTranslation, translationKeysAsArrayList, true);
    }

    @Override
    public boolean isMultipleTranslation(final String multipleTranslation) {
        try (Connection connection = this.getDataSource().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM MultipleTranslation WHERE multipleKey=?;")) {
            preparedStatement.setString(1, multipleTranslation.toLowerCase());
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return false;
    }

    @Override
    public void deleteMessage(String transkey, String language) {
        if (!this.isLanguage(language)) {
            throw new IllegalArgumentException("Language " + language + " was not found!");
        }
        if (!this.isKey(transkey, language)) {
            throw new IllegalArgumentException("Translationkey " + transkey + " was not found!");
        }
        try (Connection connection = this.getDataSource().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM ? WHERE transkey=?;")) {
            preparedStatement.setString(1, language.toLowerCase());
            preparedStatement.setString(2, transkey.toLowerCase());
            preparedStatement.execute();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    @Override
    public boolean isKey(String transkey, String lang) {
        try (Connection connection = this.getDataSource().getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM " + lang.toLowerCase() + " WHERE transkey=?;")) {
            preparedStatement.setString(1, transkey.toLowerCase());
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return false;
    }

    @NotNull
    @Override
    public String getMessage(String transkey, UUID playerUUID) {
        return this.getMessage(transkey, this.playerExecutor.getPlayerLanguage(playerUUID));
    }


    @NotNull
    @Override
    public List<String> getMultipleMessages(String transkey) {
        return this.getMultipleMessages(transkey, this.getDefaultLanguage());
    }

    @NotNull
    @Override
    public List<String> getMultipleMessages(String transkey, UUID playerUUID) {
        return this.getMultipleMessages(transkey, this.playerExecutor.getPlayerLanguage(playerUUID));
    }

    @NotNull
    @Override
    public List<String> getMultipleMessages(String transkey, String language) {
        return this.getMultipleMessages(transkey, language, "");
    }

    @Override
    public @NotNull List<String> getMultipleMessages(String transkey, String language, String prefixKey) {
        List<String> resolvedMessages = new ArrayList<>();
        String[] translationKeys = new String[]{};
        String prefix = "";
        if (prefixKey != null && !prefixKey.isEmpty()) {
            prefix = this.getMessage(prefixKey, language);
        }
        try (Connection connection = this.getDataSource().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT transkeys FROM MultipleTranslation WHERE multipleKey=?;")) {
            preparedStatement.setString(1, transkey.toLowerCase());
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                String mysqlString = resultSet.getString("transkeys");
                translationKeys = mysqlString.split(",");
            }
            resultSet.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        for (String translationKey : translationKeys) {
            resolvedMessages.add(prefix + this.getMessage(translationKey, language));
        }
        return resolvedMessages;
    }

    @NotNull
    @Override
    public String getMessage(String transkey, String lang) {
        if (!this.isLanguage(lang)) {
            throw new IllegalArgumentException(lang + " was not found");
        }
        if (!this.isKey(transkey, lang)) {
            this.languageConfig.getLogger().log(Level.WARNING, "Translationkey '" + transkey + "' not found in language '" + lang + "'");
            this.languageConfig.getLogger().log(Level.WARNING, "As result you will get the translationkey as translation");
            return transkey;
        }
        if (this.translationCache.getIfPresent(transkey) != null && Objects.requireNonNull(this.translationCache.getIfPresent(transkey)).containsKey(lang)) {
            return Objects.requireNonNull(this.translationCache.getIfPresent(transkey)).get(lang);
        }
        try (Connection connection = this.getDataSource().getConnection();
             ResultSet resultSet = connection.createStatement().executeQuery("SELECT translation FROM " + lang.toLowerCase() + " WHERE transkey='" + transkey.toLowerCase() + "';")) {
            if (resultSet.next()) {
                String translation = this.replaceColor(resultSet.getString("translation"));

                HashMap<String, String> cacheMap = new HashMap<>();
                cacheMap.put(lang, translation);
                translationCache.put(transkey, cacheMap);
                return translation;
            }
        } catch (SQLException throwables) {
            return transkey;
        }
        return transkey;
    }

    @Override
    public boolean isLanguage(String language) {
        if (this.getAvailableLanguages().isEmpty()) {
            throw new UnsupportedOperationException("There are no languages available");
        }
        return this.getAvailableLanguages().contains(language.toLowerCase());
    }

    @NotNull
    @Override
    public List<String> getAvailableLanguages() {
        List<String> languages = new ArrayList<>();
        try (Connection connection = this.getDataSource().getConnection();
             ResultSet rs = connection.createStatement().executeQuery("SELECT language FROM languages")) {
            while (rs.next()) {
                languages.add(rs.getString("language").toLowerCase());
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return languages;
    }

    @Override
    public @NotNull List<String> getAllTranslationKeys(String language) {
        List<String> keys = new ArrayList<>();
        if (this.isLanguage(language)) {
            try (Connection connection = this.getDataSource().getConnection();
                 ResultSet resultSet = connection.createStatement().executeQuery("SELECT transkey FROM " + language)) {
                while (resultSet.next()) {
                    keys.add(resultSet.getString("transkey"));
                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            return keys;
        }
        throw new IllegalArgumentException(language + " was not found");
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
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            return messages;
        }
        throw new IllegalArgumentException(language + " was not found");
    }

    @Override
    public @NotNull Map<String, String> getKeysAndTranslations(String language) {
        if (!this.isLanguage(language)) {
            throw new IllegalArgumentException(language + " was not found");
        }
        Map<String, String> keysAndTranslations = new HashMap<>();
        this.getAllTranslationKeys(language).forEach(key -> keysAndTranslations.put(key, this.getMessage(key, language)));
        return keysAndTranslations;
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
    public abstract @NotNull PlayerManager getPlayerManager();

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
    public abstract @NotNull SpecificPlayerExecutor getSpecificPlayerExecutor(@NotNull UUID playerId);

    @Override
    public void updateTranslation(Translation translation) {
        this.translationMap.put(translation.getTranslationKey(), translation);
    }


    @Override
    public abstract FileHandler getFileHandler();

    @Override
    public void executeAsync(Runnable command) {
        this.executorService.execute(command);
    }

    private HikariDataSource getDataSource() {
        return this.mySQL.getDataSource();
    }

    private void logInfo(String message) {
        this.languageConfig.getLogger().log(Level.INFO, message);
    }

    private String replaceColor(String value) {
        return value.replace('&', '§');
    }

}
