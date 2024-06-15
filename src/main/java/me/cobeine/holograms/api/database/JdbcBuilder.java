package me.cobeine.holograms.api.database;

import java.util.HashMap;

/**
 * @Author <a href="https://github.com/Cobeine">Cobeine</a>
 */

public final class JdbcBuilder {

    public static final String BASE_URL = "jdbc:mysql://%s:%s/%s";
    private boolean auto_reconnect;
    private final HashMap<String, Object> map;

    JdbcBuilder() {
        this.map = new HashMap<>();
    }

    public JdbcBuilder host(String host) {
        map.put("host", host);
        return this;
    }

    public JdbcBuilder port(int port) {
        map.put("port", port);
        return this;
    }

    public JdbcBuilder database(String database) {
        map.put("database", database);
        return this;
    }

    public JdbcBuilder setAuto_reconnect(boolean auto_reconnect) {
        this.auto_reconnect = auto_reconnect;
        return this;
    }

    public String build() {
        return String.format(BASE_URL, map.get("host"), map.get("port"), map.get("database")) + (auto_reconnect ? "?autoReconnect=true" : "");
    }

    public static JdbcBuilder newBuilder() {
        return new JdbcBuilder();
    }
}
