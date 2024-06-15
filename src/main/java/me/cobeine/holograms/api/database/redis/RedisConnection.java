package me.cobeine.holograms.api.database.redis;

import lombok.Getter;
import me.cobeine.holograms.api.database.redis.pipeline.AbstractRedisPipeline;
import me.cobeine.sqlava.connection.ConnectionResult;
import me.cobeine.sqlava.connection.auth.AuthenticatedConnection;
import me.cobeine.sqlava.connection.auth.CredentialsRecord;
import me.cobeine.sqlava.connection.pool.ConnectionPool;
import me.cobeine.sqlava.connection.pool.PooledConnection;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import redis.clients.jedis.*;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.logging.Logger;

/**
 * The type Redis connection.
 *
 * @Author <a href="https://github.com/Cobeine">Cobeine</a>
 */
@Getter
public final class RedisConnection  implements PooledConnection<JedisPooled, JedisPooled>, AuthenticatedConnection<JedisPooled> {
    private final CredentialsRecord credentialsRecord;
    private ConnectionPool<JedisPooled, JedisPooled> pool;
    private final Logger logger = Logger.getLogger(getClass().getSimpleName());

    /**
     * Instantiates a new Redis connection.
     *
     * @param credentials the credentials
     */
    public RedisConnection(CredentialsRecord credentials) {
        this.credentialsRecord = credentials;
    }

    @Override
    public ConnectionResult connect() {
        GenericObjectPoolConfig<Connection> config = new GenericObjectPoolConfig<>();
        final JedisPooled jedisPool = new JedisPooled(config,
                credentialsRecord.getProperty(BasicRedisCredentials.HOST, String.class),
                credentialsRecord.getProperty(BasicRedisCredentials.PORT, Integer.class),
                credentialsRecord.getProperty(BasicRedisCredentials.TIMEOUT, Integer.class),
                credentialsRecord.getProperty(BasicRedisCredentials.PASSWORD, String.class)
        );

        this.pool = new ConnectionPool<>(jedisPool) {
            @Override
            public JedisPooled resource() {
                return getSource();
            }
        };
        try {
            JedisPooled jedis = getPool().resource();
            //logger.log(Level.INFO,"ping result: " + jedis.ping());
            return ConnectionResult.SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ConnectionResult.FAIL;
    }


    /**
     * Register pipeline.
     *
     * @param pipelines the pipelines
     */
    public void registerPipeline(AbstractRedisPipeline<?>... pipelines) {
        for (AbstractRedisPipeline<?> pipeline : pipelines) {
            CompletableFuture.runAsync(() -> {
                try (JedisPooled jedis = getPool().resource()) {
                    jedis.subscribe(new JedisPubSub() {
                        @Override
                        public void onMessage(String channel, String message) {
                            if (pipeline.getChannelWithDirection().equalsIgnoreCase(channel))
                                try {
                                    pipeline.processMessage(message);
                                } catch (Exception e) {
                                    e.printStackTrace();;
                                }
                        }
                    },pipeline.getChannelWithDirection());
                }
            });
        }
    }

    /**
     * Execute.
     *
     * @param action the action
     */
    public void execute(Consumer<JedisPooled> action) {
        try {
            JedisPooled jedis = getPool().resource();
            action.accept(jedis);
        } catch (Exception e) {
            e.printStackTrace();

        }
    }
    @Override
    public JedisPooled getConnection() {
        return pool.getSource();
    }
}
