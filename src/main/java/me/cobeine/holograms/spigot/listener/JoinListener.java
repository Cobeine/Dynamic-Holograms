package me.cobeine.holograms.spigot.listener;

import me.cobeine.holograms.spigot.HologramsPlugin;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 * @Author <a href="https://github.com/Cobeine">Cobeine</a>
 */

public class JoinListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Bukkit.getScheduler().runTaskLater(HologramsPlugin.getInstance(), () -> {
            HologramsPlugin.getInstance().getManager().spawnAllPlaced(event.getPlayer());
        }, 10);
    }

}
