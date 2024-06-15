package me.cobeine.holograms.spigot.dependencies;

import me.clip.placeholderapi.PlaceholderAPI;
import me.cobeine.holograms.api.dependency.DependencyRegisterException;
import me.cobeine.holograms.api.dependency.PluginDependency;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

/**
 * @Author <a href="https://github.com/Cobeine">Cobeine</a>
 */

public class PlaceHolderAPIDependency implements PluginDependency<PlaceholderAPI> {

    @Override
    public PlaceholderAPI get() {
        return null;
    }//no need since the method we need is already static

    @Override
    public void registerDependency() throws DependencyRegisterException {
        if (Bukkit.getPluginManager().getPlugin(name()) == null) {
            throw new DependencyRegisterException(this,"placeholders will not work");
        }
    }



    @Override
    public @NotNull String name() {
        return "PlaceHolderAPI";
    }
}
