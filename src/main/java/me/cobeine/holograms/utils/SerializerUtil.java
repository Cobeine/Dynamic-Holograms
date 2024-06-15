package me.cobeine.holograms.utils;

import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import org.bukkit.Location;

/**
 * @Author <a href="https://github.com/Cobeine">Cobeine</a>
 */

public class SerializerUtil {

    public static JsonObject serializeLocation(Location location) {
        JsonObject object = new JsonObject();
        object.addProperty("world", location.getWorld().getName());
        object.addProperty("x", location.getX());
        object.addProperty("y", location.getY());
        object.addProperty("z", location.getZ());
        return object;
    }

    public static Location deSerializeLocation(JsonObject object) {
        return new Location(
                Bukkit.getWorld(object.get("world").getAsString()),
                object.get("x").getAsDouble(),
                object.get("y").getAsDouble(),
                object.get("z").getAsDouble()
        );
    }



}
