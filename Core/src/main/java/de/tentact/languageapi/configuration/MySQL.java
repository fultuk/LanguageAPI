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

package de.tentact.languageapi.configuration;

import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Logger;

public class MySQL {

    private final String hostname, database, username, password;
    private final int port;
    private transient HikariDataSource dataSource;
    private transient Logger logger;

    public MySQL(String hostname, String database, String username, String password, int port) {
        this.hostname = hostname;
        this.database = database;
        this.username = username;
        this.password = password;
        this.port = port;
    }

    public void connect() {
        if (this.isNotConnected()) {
            this.dataSource = new HikariDataSource();
            this.dataSource.setJdbcUrl("jdbc:mysql://" + hostname + ":" + port + "/" + database);
            this.dataSource.setUsername(this.username);
            this.dataSource.setPassword(this.password);
            this.logger.info("Creating connection to database");
        }
    }

    public boolean isNotConnected() {
        if (this.dataSource == null) {
            return true;
        }
        return this.dataSource.isClosed();
    }

    public void closeConnection() {
        if (this.isNotConnected()) {
            return;
        }
        this.dataSource.close();
    }

    public void createDefaultTable() {
        if (this.isNotConnected()) {
            return;
        }
        try (Connection connection = this.dataSource.getConnection()) {
            connection.createStatement().execute("CREATE TABLE IF NOT EXISTS playerlanguage(uuid VARCHAR(36) PRIMARY KEY, language VARCHAR(8));");
            connection.createStatement().execute("CREATE TABLE IF NOT EXISTS languages(language VARCHAR(32) PRIMARY KEY);");
            connection.createStatement().execute("CREATE TABLE IF NOT EXISTS Parameter(translationkey VARCHAR(128), param VARCHAR(2000), PRIMARY KEY (translationkey));");
            connection.createStatement().execute("CREATE TABLE IF NOT EXISTS MultipleTranslation(multipleKey VARCHAR(128), translationkey VARCHAR(2000), PRIMARY KEY (multipleKey));");
            this.logger.info("Creating default tables");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void createLanguageTable(String tableName) {
        if (this.isNotConnected()) {
            return;
        }
        try (Connection connection = this.dataSource.getConnection()) {
            connection.createStatement().execute("CREATE TABLE IF NOT EXISTS " + tableName + "(translationkey VARCHAR(128) UNIQUE, translation VARCHAR(2000));");
            this.logger.info("Creating table: " + tableName);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public HikariDataSource getDataSource() {
        return this.dataSource;
    }

    public void setLogger(Logger logger) {
        this.logger = logger;
    }
}
