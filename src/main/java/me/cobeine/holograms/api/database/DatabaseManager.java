package me.cobeine.holograms.api.database;

import me.cobeine.sqlava.connection.ConnectionResult;
import me.cobeine.sqlava.connection.auth.CredentialsRecord;

/**
 * @Author <a href="https://github.com/Cobeine">Cobeine</a>
 */

public interface DatabaseManager {


    ConnectionResult connect();

    void closeConnection();


}
