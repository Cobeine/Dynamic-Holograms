package me.cobeine.holograms.api.database.redis;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.cobeine.sqlava.connection.auth.CredentialsKey;

/**
 * @Author <a href="https://github.com/Cobeine">Cobeine</a>
 */
@AllArgsConstructor
@Getter
public enum BasicRedisCredentials implements CredentialsKey {
    HOST("host",String.class),
    PORT("port",Integer.class),
    PASSWORD("password",String.class),
    TIMEOUT("timeout",Integer.class);

    private final String key;
    private final Class<?> dataType;

    @Override
    public boolean isProperty() {
         return true;
    }

    @Override
    public CredentialsKey[] array() {
        return values();
    }

}
