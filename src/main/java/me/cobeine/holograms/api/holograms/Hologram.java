package me.cobeine.holograms.api.holograms;

import com.google.gson.JsonArray;
import lombok.Getter;
import me.cobeine.holograms.spigot.HologramsPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author <a href="https://github.com/Cobeine">Cobeine</a>
 */
@Getter
public class Hologram implements Serializable {
    private final String id;
    private List<HologramLine> lines;
    private final Location baseLocation;


    public Hologram(String id, List<String> lines, Location baseLocation) {
        this.id = id;
        this.baseLocation = baseLocation;
        this.lines = lines.stream().map(HologramLine::new).collect(Collectors.toList());
    }
    public Hologram(String id, Location baseLocation,List<HologramLine> lines) {
        this.id = id;
        this.baseLocation = baseLocation;
        this.lines = lines;
    }

    public HologramUpdateResult setLine(int index, String text) {
        if (index < 0) {
            return HologramUpdateResult.FAIL;
        }
        if (index+1 > lines.size()) {
            return HologramUpdateResult.FAIL;
        }
        lines.get(index).setText(text);
        return HologramUpdateResult.SUCCESS;
    }

    public HologramUpdateResult addLine(String text) {
        var line = HologramLine.of(text);
        if (lines.stream().anyMatch(e -> e.getText().equals(text))) {
            return HologramUpdateResult.SUCCESS;
        }
        lines.add(line);
        if (baseLocation != null) {
            spawn();
        }
        return HologramUpdateResult.SUCCESS;
    }

    public HologramUpdateResult removeLine(int index) {
        if (index < 0) {
            return HologramUpdateResult.FAIL;
        }
        if (index+1 > lines.size()) {
            return HologramUpdateResult.FAIL;
        }
        lines.remove(index);
        if (baseLocation != null) {
            deSpawn();
            new BukkitRunnable() {
                public void run() {
                    spawn();
                }
            }.runTaskLater(HologramsPlugin.getInstance(), 1);
        }

        return HologramUpdateResult.SUCCESS;
    }

    public void spawn() {
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            spawn(onlinePlayer);
        }
    }
    public void deSpawn() {
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            deSpawn(onlinePlayer);
        }
    }

    public String serialize() {
        JsonArray array = new JsonArray();
        for (HologramLine line : lines) {
            array.add(line.getText());
        }
        return array.toString();
    }
    public JsonArray serializeAsJsonArray() {
        JsonArray array = new JsonArray();
        for (HologramLine line : lines) {
            array.add(line.getText());
        }
        return array;
    }



    public HologramUpdateResult setLines(List<String> lines) {
        this.lines = lines.stream().map(HologramLine::new).collect(Collectors.toList());
        return HologramUpdateResult.SUCCESS;
    }

    public void spawn(Player player) {
        Location location = baseLocation.clone();
        for (HologramLine line : lines) {
            line.spawn(player, location);
            location = location.subtract(0, 0.26, 0);

        }

    }



    public void deSpawn(Player player) {
        for (HologramLine line : lines) {
            line.deSpawn(player);
        }
    }
}
