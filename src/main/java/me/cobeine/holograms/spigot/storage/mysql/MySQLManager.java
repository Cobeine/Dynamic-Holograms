package me.cobeine.holograms.spigot.storage.mysql;

import lombok.Getter;
import me.cobeine.holograms.api.database.DatabaseManager;
import me.cobeine.sqlava.connection.ConnectionResult;
import me.cobeine.sqlava.connection.auth.CredentialsRecord;
import me.cobeine.sqlava.connection.database.MySQLConnection;
import me.cobeine.sqlava.connection.database.query.PreparedQuery;
import me.cobeine.sqlava.connection.database.query.Query;

/**
 * @Author <a href="https://github.com/Cobeine">Cobeine</a>
 */

public class MySQLManager implements DatabaseManager {
    private final MySQLConnection connection;

    public MySQLManager(CredentialsRecord record) {
        this.connection = new MySQLConnection(record);
    }



    @Override
    public ConnectionResult connect() {
        var result = connection.connect();
        if (result.equals(ConnectionResult.SUCCESS)) {
            connection.getTableCommands().createTable(new HologramsTable("holograms"));
        }
        return result;
    }
    @Override
    public void closeConnection() {
        connection.closeConnection();
    }

    public PreparedQuery prepareStatement(Query query) {
        return connection.prepareStatement(query);
    }

    public String getTable() {
        return "holograms";
    }
}
