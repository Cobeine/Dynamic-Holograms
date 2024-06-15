package me.cobeine.holograms.spigot.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import me.cobeine.holograms.api.holograms.Hologram;
import me.cobeine.holograms.api.holograms.HologramFactory;
import me.cobeine.holograms.spigot.hologram.HologramManager;
import me.cobeine.holograms.api.holograms.HologramUpdateResult;
import me.cobeine.holograms.spigot.hologram.placeholders.IndexPlaceholder;
import me.cobeine.holograms.utils.TextUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;



/**
 * @Author <a href="https://github.com/Cobeine">Cobeine</a>
 */
@CommandAlias("hologram|dynamichologram|dh")
@CommandPermission("holo.admin")
public class HologramCommand extends BaseCommand {

    @Dependency
    private HologramManager manager;


    @Subcommand("create")
    @Syntax("<id>")
    @Description("creates a new hologram")
    public void create(Player player, String id) {
        if (manager.exists(id)) {
            player.sendMessage(TextUtils.getConfigMessage("exists"));
            return;
        }

        Hologram hologram = HologramFactory.createHologram(id);
        manager.registerNewHologram(hologram,true);
        player.sendMessage(TextUtils.getConfigMessage("created"));
    }

    @Subcommand("delete")
    @Syntax("<id>")
    @CommandCompletion("@allholograms")
    @Description("delete a hologram from the database")
    public void delete(Player player, String id) {
        if (!exists(player, id)) {
            return;
        }
        manager.unregisterHologram(id,true);
        player.sendMessage(TextUtils.getConfigMessage("deleted"));
    }

    @Subcommand("setLine")
    @Syntax("<id> <index> <text>")
    @CommandCompletion("@allholograms")
    public void setLine(Player player, String id, int index, String[] text) {
        if (!exists(player, id)) {
            return;
        }
        StringBuilder line = new StringBuilder();
        for (String s : text) {
            line.append(s).append(" ");
        }
        line.deleteCharAt(line.length() - 1);
        var result = manager.updateHologram(id, hologram -> hologram.setLine(index - 1, line.toString()), true);
        sendResult(player, result, TextUtils.getConfigError("invalid_index", IndexPlaceholder.of(index)));

    }

    @Subcommand("removeLine")
    @Syntax("<id> <index>")
    @CommandCompletion("@allholograms")
    public void removeLine(Player player, String id, int index) {
        if (!exists(player, id)) {
            return;
        }
        var result = manager.updateHologram(id, hologram -> hologram.removeLine(index - 1), true);
        sendResult(player, result, TextUtils.getConfigError("invalid_index", IndexPlaceholder.of(index)));
    }

    @Subcommand("addline")
    @Syntax("<id> <text>")
    @CommandCompletion("@allholograms")
    public void addLine(Player player, String id, String[] text) {
        if (!exists(player, id)) {
            return;
        }
        StringBuilder line = new StringBuilder();
        for (String s : text) {
            line.append(s).append(" ");
        }
        line.deleteCharAt(line.length() - 1);
        var result = manager.updateHologram(id, hologram -> hologram.addLine(line.toString()), true);
        sendResult(player, result, TextUtils.getConfigError("unexpected"));
    }

    @Subcommand("place")
    @Syntax("<id>")
    @CommandCompletion("@allholograms")
    public void place(Player player, String id) {
        if (!exists(player,id)) {
            return;
        }
        if (manager.isPlaced(id)) {
            player.sendMessage(TextUtils.getConfigMessage("is-placed"));
            return;
        }
        manager.placeHologram(id, player.getLocation());
        player.sendMessage(TextUtils.getConfigMessage("placed"));
    }

    @Subcommand("remove")
    @Syntax("<id>")
    @CommandCompletion("@holograms")
    public void remove(Player player, String id) {
        if (!manager.isPlaced(id)) {
            player.sendMessage(TextUtils.getConfigMessage("not-placed"));
            return;
        }
        manager.removeHologram(id);
        player.sendMessage(TextUtils.getConfigMessage("removed"));

    }

    private boolean exists(Player player, String id) {
        if (!manager.exists(id)) {
            player.sendMessage(TextUtils.getConfigMessage("non-exist"));
            return false;
        }
        return true;
    }


    private void sendResult(Player player, HologramUpdateResult result, Component error) {
        switch (result) {
            case SUCCESS -> player.sendMessage(TextUtils.getConfigMessage("update-global"));
            case FAIL -> {
                player.sendMessage(TextUtils.getConfigMessage("update-fail"));
                player.sendMessage(error);
            }
        }
    }

}
