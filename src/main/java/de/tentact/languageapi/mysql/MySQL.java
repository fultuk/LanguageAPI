package de.tentact.languageapi.mysql;
/*  Created in the IntelliJ IDEA.
    Created by 0utplay | Aldin Sijamhodzic
    Datum: 25.04.2020
    Uhrzeit: 16:53
*/

import de.tentact.languageapi.util.Source;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;

public class MySQL {

    private final String hostname, database, username, password;

    private int port;
    private Connection con;

    public MySQL(String hostname, String database, String username, String password, int port) {
        this.hostname = hostname;
        this.database = database;
        this.username = username;
        this.password = password;
        this.port = port;
    }

    public void connect() {
        if (!isConnected()) {
            try {
                con = DriverManager.getConnection("jdbc:mysql://" + hostname + ":" + port + "/" + database, username, password);
                Source.defaultLog("§aMySQL Connected", Level.INFO);
            } catch (SQLException ex) {
                Source.defaultLog("§cDie MySQL konnte nicht verbunden werden. Prüfe, ob deine Angaben stimmen und der Server online ist.", Level.WARNING);
                ex.printStackTrace();
                Source.defaultLog("§cDie MySQL konnte nicht verbunden werden. Prüfe, ob deine Angaben stimmen und der Server online ist.", Level.WARNING);
            }
        }

    }

    public boolean isConnected() {
        return con != null;
    }

    public void close() {
        if (isConnected()) {
            try {
                con.close();
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    public void createDefaultTable() {
        new Thread(() -> {
            if(!isConnected())
                return;
            try {
                con.createStatement().executeUpdate("CREATE TABLE IF NOT EXISTS choosenlang(uuid VARCHAR(64), language VARCHAR(64));");
                con.createStatement().executeUpdate("CREATE TABLE IF NOT EXISTS languages(language VARCHAR(64));");
                con.createStatement().execute("CREATE TABLE IF NOT EXISTS Parameter(transkey VARCHAR(64), param VARCHAR(2000));");
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }).start();

    }

    public void createTable(String tableName) {
        new Thread(() -> {
            if(!isConnected())
                return;
            try {
                con.createStatement().executeUpdate("CREATE TABLE IF NOT EXISTS " + tableName + "(transkey VARCHAR(64), translation VARCHAR(2000));");
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }).start();
    }

    public void update(String sql) {
        if (isConnected()) {
            try {
                con.createStatement().executeUpdate(sql);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public ResultSet getResult(String sql) {
        if (isConnected()) {
            try {
                return con.createStatement().executeQuery(sql);
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return null;
    }

}
