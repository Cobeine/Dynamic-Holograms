package me.cobeine.holograms.spigot.hologram;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lombok.Getter;
import me.cobeine.holograms.api.database.redis.pipeline.AbstractRedisPipeline;
import me.cobeine.holograms.api.holograms.Hologram;
import me.cobeine.holograms.api.holograms.HologramFactory;
import me.cobeine.holograms.api.holograms.HologramUpdateResult;
import me.cobeine.holograms.spigot.HologramsPlugin;
import me.cobeine.holograms.spigot.storage.StorageHandler;
import me.cobeine.sqlava.connection.database.query.Query;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.logging.Level;

/**
 * @Author <a href="https://github.com/Cobeine">Cobeine</a>
 */

public class HologramManager {
    private final @Getter List<Hologram> all_holograms;
    private final @Getter List<Hologram> placed_holograms;
    private final StorageHandler db;
    public HologramManager(StorageHandler handler) {
        this.db = handler;
        all_holograms = new ArrayList<>();
        placed_holograms = new ArrayList<>();
        loadAllHolograms();
    }

    public void loadAllHolograms() {
        db.getMySQLManager().prepareStatement(Query.select(db.getMySQLManager().getTable())).executeQueryAsync(query -> query.executeIfPresent(resultSet -> {
            while (resultSet.next()) {
                String id = resultSet.getString("id");
                String data = resultSet.getString("lines");
                all_holograms.add(HologramFactory.createHologram(id, data));
            }
            loadPlacedHolograms();
        }).orElse(e -> db.log(Level.WARNING,"Could not load any hologram from the database: " + e)).apply());
    }

    public void loadPlacedHolograms() {
        var config = HologramsPlugin.getInstance().getConfig();
        if (null == config.getConfigurationSection("placed_holograms")) {
            return;
        }
        for (String id : config.getConfigurationSection("placed_holograms").getKeys(false)) {
            var location = config.getLocation("placed_holograms." + id);
            all_holograms.stream().filter(e -> e.getId().equals(id)).findFirst().ifPresentOrElse(e -> {
                HologramsPlugin.getInstance().getLogger().warning("loading hologram: " + id);
                var placedHologram = HologramFactory.createHologram(id, e.getLines(), location);
                placed_holograms.add(placedHologram);

            },() -> HologramsPlugin.getInstance().getLogger().warning("Failed to load hologram: " + id));

        }
    }

    public void fini(JavaPlugin instance) {
        for (Hologram placedHologram : placed_holograms) {
            instance.getConfig().set("placed_holograms." + placedHologram.getId(), placedHologram.getBaseLocation());
        }
        instance.saveConfig();
    }

    public Optional<Hologram> getHologramByID(String id) {
        return all_holograms.stream().filter(e -> e.getId().equals(id)).findFirst();
    }

    public HologramUpdateResult updateHologram(String id, Function<Hologram,HologramUpdateResult> action, Boolean sync) {
        var local = placed_holograms.stream().filter(e -> e.getId().equals(id)).findFirst();
        var global = getHologramByID(id);
        local.ifPresent(action::apply);
        HologramUpdateResult global_result = HologramUpdateResult.FAIL;
        if (global.isEmpty()) {
            return global_result;
        }
        global_result = action.apply(global.get());
        if (sync) {
            db.getMySQLManager().prepareStatement(Query.update(db.getMySQLManager().getTable()).set("`lines`")
                            .where("`id`"))
                    .setParameter(1, global.get().serialize())
                    .setParameter(2, id).executeUpdateAsync();

            sendRedisPub(global.get());
        }
        return global_result;
    }

    private void sendRedisPub(Hologram global) {
        db.getRedisManager().execute(jedis -> {
            JsonObject update = new JsonObject();
            update.addProperty("id", global.getId());
            update.addProperty("server_uuid",HologramsPlugin.getInstance().getUuid());
            update.add("lines", global.serializeAsJsonArray());
            jedis.publish(AbstractRedisPipeline.ChannelDirection.SERVER_TO_SERVER.getDirection() + "holograms:sync", update.toString());
        });
    }

    public boolean exists(String id) {
        return all_holograms.stream().anyMatch(e -> e.getId().equals(id));
    }

    public void registerNewHologram(Hologram hologram, boolean sync) {
        all_holograms.add(hologram);
        if (sync) {
            db.getMySQLManager().prepareStatement(Query.insert(db.getMySQLManager().getTable()).values("`id`","`lines`"))
                    .setParameter(1,hologram.getId())
                    .setParameter(2,hologram.serialize())
                    .executeUpdateAsync();

            db.getRedisManager().execute(jedis -> {
                JsonObject update = new JsonObject();
                update.addProperty("id", hologram.getId());
                update.addProperty("server_uuid",HologramsPlugin.getInstance().getUuid());
                update.add("lines", new JsonArray());
                update.addProperty("new", true);
                jedis.publish(AbstractRedisPipeline.ChannelDirection.SERVER_TO_SERVER.getDirection() + "holograms:sync", update.toString());

            });
        }
    }

    public void unregisterHologram(String id,boolean sync) {
        placed_holograms.stream().filter(e-> e.getId().equals(id)).forEach(e->{
            for (Player player : Bukkit.getOnlinePlayers()) {
                e.deSpawn(player);
            }
        });
        placed_holograms.removeIf(e -> e.getId().equals(id));
        all_holograms.removeIf(e -> e.getId().equals(id));

        db.getMySQLManager().prepareStatement(Query.delete(db.getMySQLManager().getTable()).where("`id`", id))
                .executeUpdateAsync();
        if (sync) {
            db.getRedisManager().execute(jedis -> {
                JsonObject update = new JsonObject();
                update.addProperty("id", id);
                update.addProperty("server_uuid",HologramsPlugin.getInstance().getUuid());
                update.add("lines", new JsonArray());
                jedis.publish(AbstractRedisPipeline.ChannelDirection.SERVER_TO_SERVER.getDirection() + "holograms:sync", update.toString());
            });
        }
    }

    public boolean isPlaced(String id) {
        return placed_holograms.stream().anyMatch(e -> e.getId().equals(id));
    }

    public void removeHologram(String id) {
        placed_holograms.stream().filter(e-> e.getId().equals(id)).findFirst().ifPresent(hologram -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                hologram.deSpawn(player);
            }
            placed_holograms.remove(hologram);
        });
    }

    public void placeHologram(String id,Location location) {
        getHologramByID(id).ifPresent(hologram -> {
            final Hologram placed = HologramFactory.createHologram(hologram.getId(), hologram.getLines(),location);
            placed_holograms.add(placed);
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                placed.spawn(onlinePlayer);
            }
        });

    }

    public void spawnAllPlaced(Player player) {
        for (Hologram placedHologram : placed_holograms) {
            placedHologram.spawn(player);
        }
    }


}
