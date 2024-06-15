package me.cobeine.holograms.utils;

import me.clip.placeholderapi.PlaceholderAPI;
import me.cobeine.holograms.api.ConfigPlaceholder;
import me.cobeine.holograms.api.dependency.DependencyManager;
import me.cobeine.holograms.spigot.HologramsPlugin;
import me.cobeine.holograms.spigot.dependencies.PlaceHolderAPIDependency;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * @Author <a href="https://github.com/Cobeine">Cobeine</a>
 */
public class TextUtils {


    public static Component colorize(String text, Player player) {
        if (DependencyManager.isRegistered(PlaceHolderAPIDependency.class)) {
            return colorize(PlaceholderAPI.setPlaceholders(player, text));
        }
        return colorize(text);
    }
        public static Component colorize(String text) {
            return LegacyComponentSerializer.legacyAmpersand().deserialize(text);
    }

    public static Component getConfigMessage(String path) {
        return colorize(HologramsPlugin.getInstance().getConfig().getString("messages." + path));
    }

    public static Component getConfigError(String path, @Nullable ConfigPlaceholder<?> placeholder) {
        if (placeholder != null) {
            return colorize(placeholder.apply(Objects.requireNonNull(HologramsPlugin.getInstance().getConfig().getString("errors." + path))));
        }
        return colorize(HologramsPlugin.getInstance().getConfig().getString("errors." + path));
    }

    public static Component getConfigError(String path) {
        return getConfigError(path, null);
    }

    public static String colorizeLegacy(String s, Player player) {
        if (DependencyManager.isRegistered(PlaceHolderAPIDependency.class)) {
            return PlaceholderAPI.setPlaceholders(player, s.replace("&", "§"));
        }
        return s.replace("&", "§");
    }

}

