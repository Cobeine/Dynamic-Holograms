package me.cobeine.holograms.api.holograms;

import com.google.gson.*;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author <a href="https://github.com/Cobeine">Cobeine</a>
 */

public interface HologramFactory {
    static Hologram createHologram(String id, Location location, List<String> lines) {
        return new Hologram(id, lines, location);
    }
    static Hologram createHologram(String id, List<HologramLine> lines,Location location) {
        return new Hologram(id,location,lines);
    }

    static Hologram createHologram(String id, String data) {
        JsonArray array = (JsonArray) JsonParser.parseString(data);
        List<String> lines = new ArrayList<>();
        for (JsonElement element : array) {
            lines.add(element.getAsString());
        }
        return new Hologram(id, lines, null);
    }

    static Hologram createHologram(String id) {
        return new Hologram(id, new ArrayList<>(), null);
    }
}
