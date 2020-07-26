package de.tentact.languageapi.mysql;
/*  Created in the IntelliJ IDEA.
    Created by 0utplay | Aldin Sijamhodzic
    Datum: 25.04.2020
    Uhrzeit: 16:53
*/

import com.zaxxer.hikari.HikariDataSource;
import de.tentact.languageapi.util.ConfigUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;

public class MySQL {

    private final String hostname, database, username, password;
    private final int port;
    private HikariDataSource dataSource;

    public MySQL(String hostname, String database, String username, String password, int port) {
        this.hostname = hostname;
        this.database = database;
        this.username = username;
        this.password = password;
        this.port = port;
    }

    public void connect() {
        if (!isConnected()) {
            dataSource = new HikariDataSource();
            dataSource.setJdbcUrl("jdbc:mysql://" + hostname + ":" + port + "/" + database);
            dataSource.setUsername(this.username);
            dataSource.setPassword(this.password);

            ConfigUtil.log("Creating connection to database", Level.INFO);

        }
    }

    public boolean isConnected() {
        return dataSource != null;
    }

    public void closeConnection() {
        if (!isConnected()) {
            return;
        }
        dataSource.close();
    }

    public void createDefaultTable() {
        if (!isConnected())
            return;
        try (Connection connection = dataSource.getConnection()) {
            connection.createStatement().execute("CREATE TABLE IF NOT EXISTS choosenlang(uuid VARCHAR(64), language VARCHAR(64));");
            connection.createStatement().execute("CREATE TABLE IF NOT EXISTS languages(language VARCHAR(64));");
            connection.createStatement().execute("CREATE TABLE IF NOT EXISTS Parameter(transkey VARCHAR(64), param VARCHAR(2000));");
            connection.createStatement().execute("CREATE TABLE IF NOT EXISTS MultipleTranslation(multipleKey VARCHAR(64), transkeys VARCHAR(2000));");
            ConfigUtil.log("Creating default tables", Level.INFO);
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void createTable(String tableName) {
        if (!isConnected())
            return;
        try (Connection connection = dataSource.getConnection()) {
            connection.createStatement().execute("CREATE TABLE IF NOT EXISTS " + tableName + "(transkey VARCHAR(64), translation VARCHAR(2000));");
            ConfigUtil.log("Creating table: " + tableName, Level.INFO);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void update(String sql) {
        if (isConnected()) {
            try (Connection connection = dataSource.getConnection()) {
                connection.createStatement().executeUpdate(sql);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
    }

    public boolean exists(String query) {
        try (Connection connection = this.dataSource.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return true;
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return false;
    }

    public HikariDataSource getDataSource() {
        return this.dataSource;
    }
}