package me.cobeine.holograms.spigot.storage.redis;

import lombok.Getter;
import me.cobeine.holograms.api.database.DatabaseManager;
import me.cobeine.holograms.api.database.redis.RedisConnection;
import me.cobeine.holograms.spigot.HologramsPlugin;
import me.cobeine.holograms.spigot.storage.redis.pipelines.HologramSyncPipeline;
import me.cobeine.sqlava.connection.ConnectionResult;
import me.cobeine.sqlava.connection.auth.CredentialsRecord;
import redis.clients.jedis.JedisPooled;

import java.util.function.Consumer;

/**
 * @Author <a href="https://github.com/Cobeine">Cobeine</a>
 */

public class RedisManager  implements DatabaseManager {
    private final RedisConnection connection;
    private @Getter boolean connected = false;
    public RedisManager(CredentialsRecord record) {
        connection = new RedisConnection(record);
    }
    @Override
    public ConnectionResult connect() {
        var result = connection.connect();
        if (result.equals(ConnectionResult.SUCCESS)) {
            connection.registerPipeline(new HologramSyncPipeline());
            connection.execute(jedisPooled -> {
                var ping = jedisPooled.ping();
                if (ping!= null) {
                    connected =true;
                }
            });
        }
        return result;

    }
    @Override
    public void closeConnection() {
        connection.closeConnection();
    }

    public void execute(Consumer<JedisPooled> jedisConsumer) {
        if (!connected) {
            HologramsPlugin.getInstance().getLogger().severe("Failed to get redis resource pool, aborting execution");
            return;
        }

        connection.execute(jedisConsumer);
    }
}
