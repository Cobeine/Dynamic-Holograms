package me.cobeine.holograms.spigot.storage;

import lombok.Getter;
import me.cobeine.holograms.api.database.JdbcBuilder;
import me.cobeine.holograms.api.database.redis.BasicRedisCredentials;
import me.cobeine.holograms.api.loader.PluginLaunchException;
import me.cobeine.holograms.spigot.HologramsPlugin;
import me.cobeine.holograms.spigot.storage.mysql.MySQLManager;
import me.cobeine.holograms.spigot.storage.redis.RedisManager;
import me.cobeine.sqlava.connection.ConnectionResult;
import me.cobeine.sqlava.connection.auth.CredentialsRecord;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.logging.Level;

import static me.cobeine.sqlava.connection.auth.BasicMySQLCredentials.*;

/**
 * @Author <a href="https://github.com/Cobeine">Cobeine</a>
 */

public class StorageHandler {
    private@Getter MySQLManager mySQLManager;
    private@Getter RedisManager redisManager;

    public StorageHandler(HologramsPlugin plugin) throws PluginLaunchException {
        loadConfig(plugin);


        setupMysql(plugin.getConfig());
        setupRedis(plugin.getConfig());

        if (mySQLManager.connect().equals(ConnectionResult.FAIL)) {
            throw new PluginLaunchException("FAILED TO CONNECT TO MYSQL DATABASE");
        }
        if (redisManager.connect().equals(ConnectionResult.FAIL)) {
            throw new PluginLaunchException("FAILED TO CONNECT TO REDIS DATABASE");
        }
    }
    private void loadConfig(HologramsPlugin pl) {
        pl.getConfig().options().copyDefaults(true);
        pl.saveDefaultConfig();
    }

    private void setupMysql(FileConfiguration config) {
        final String MYSQL_PATH = "database.mysql.";
        var url = JdbcBuilder.newBuilder()
                .host(config.getString(MYSQL_PATH + "host"))
                .port(config.getInt(MYSQL_PATH + "port"))
                .setAuto_reconnect(true)
                .database(config.getString(MYSQL_PATH + "database"))
                .build();
        var mysql_creds = CredentialsRecord.builder()
                .add(USERNAME,config.getString(MYSQL_PATH + "username"))
                .add(PASSWORD,config.getString(MYSQL_PATH + "password"))
                .add(DATABASE,config.getString(MYSQL_PATH + "database"))
                .add(PORT,config.getInt(MYSQL_PATH + "port"))
                .add(POOL_SIZE,config.getInt(MYSQL_PATH + "pool_size"))
                .add(JDBC_URL, url)
                .build();

        mySQLManager = new MySQLManager(mysql_creds);
    }

    private void setupRedis(FileConfiguration config) {
        var redis_creds =  CredentialsRecord.builder();
        final String REDIS_PATH = "database.redis.";
        for (BasicRedisCredentials value : BasicRedisCredentials.values()) {
            redis_creds = redis_creds.add(value, config.get(REDIS_PATH + value.name().toLowerCase()));
        }
         redisManager = new RedisManager(redis_creds.build());
    }

    public void log(Level level, String s) {
        HologramsPlugin.getInstance().getLogger().log(level,s);
    }

    public void fini() {
        mySQLManager.closeConnection();
        redisManager.closeConnection();
    }
}
