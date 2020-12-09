package de.tentact.languageapi.configuration;
/*  Created in the IntelliJ IDEA.
    Created by 0utplay | Aldin Sijamhodzic
    Datum: 25.04.2020
    Uhrzeit: 16:53
*/

import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
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
        if (isNotConnected()) {
            dataSource = new HikariDataSource();
            dataSource.setJdbcUrl("jdbc:mysql://" + hostname + ":" + port + "/" + database);
            dataSource.setUsername(this.username);
            dataSource.setPassword(this.password);
            this.logger.log(Level.INFO,"Creating connection to database");
        }
    }

    public boolean isNotConnected() {
        return dataSource == null;
    }

    public void closeConnection() {
        if (isNotConnected()) {
            return;
        }
        dataSource.close();
    }

    public void createDefaultTable() {
        if (isNotConnected())
            return;
        try (Connection connection = dataSource.getConnection()) {
            connection.createStatement().execute("CREATE TABLE IF NOT EXISTS playerlanguage(uuid VARCHAR(64) UNIQUE, language VARCHAR(64));");
            connection.createStatement().execute("CREATE TABLE IF NOT EXISTS languages(language VARCHAR(64) UNIQUE);");
            connection.createStatement().execute("CREATE TABLE IF NOT EXISTS Parameter(transkey VARCHAR(64) UNIQUE, param VARCHAR(2000));");
            connection.createStatement().execute("CREATE TABLE IF NOT EXISTS MultipleTranslation(multipleKey VARCHAR(64) UNIQUE , transkeys VARCHAR(2000));");
            this.logger.log(Level.INFO,"Creating default tables");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void createTable(String tableName) {
        if (isNotConnected())
            return;
        try (Connection connection = dataSource.getConnection()) {
            connection.createStatement().execute("CREATE TABLE IF NOT EXISTS " + tableName + "(transkey VARCHAR(64) UNIQUE, translation VARCHAR(2000));");
            this.logger.log(Level.INFO,"Creating table: " + tableName);
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
