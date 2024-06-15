package me.cobeine.holograms.spigot.storage.redis.pipelines;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.cobeine.holograms.api.database.redis.pipeline.BasicRedisPipeline;
import me.cobeine.holograms.api.holograms.HologramFactory;
import me.cobeine.holograms.spigot.HologramsPlugin;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author <a href="https://github.com/Cobeine">Cobeine</a>
 */

public class HologramSyncPipeline extends BasicRedisPipeline {


    public HologramSyncPipeline() {
        super(ChannelDirection.SERVER_TO_SERVER, "holograms:sync");
    }

    @Override
    public void handle(JsonObject message) {
        String uuid = message.get("server_uuid").getAsString();
        if (HologramsPlugin.getInstance().getUuid().equals(uuid)) {
            return;
        }
        String id = message.get("id").getAsString();
        JsonArray array_of_lines = message.get("lines").getAsJsonArray();
        if (array_of_lines.isJsonNull() || array_of_lines.isEmpty()) {
            if (message.has("new")) {
                HologramsPlugin.getInstance().getManager().registerNewHologram(HologramFactory.createHologram(id), false);
                return;
            }
            HologramsPlugin.getInstance().getManager().unregisterHologram(id, false);
            return;
        }
        List<String> lines = new ArrayList<>();
        for (JsonElement line : array_of_lines) {
            lines.add(line.getAsString());
        }
        HologramsPlugin.getInstance().getManager().updateHologram(id,hologram -> hologram.setLines(lines),false);
    }
}
