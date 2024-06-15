package me.cobeine.holograms.spigot.dependencies;

import com.comphenix.protocol.ProtocolLib;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import me.cobeine.holograms.api.dependency.DependencyRegisterException;
import me.cobeine.holograms.api.dependency.PluginDependency;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @Author <a href="https://github.com/Cobeine">Cobeine</a>
 */

public class ProtocolLibDependency implements PluginDependency<ProtocolManager> {

    @Override
    public @Nullable ProtocolManager get() {
        return ProtocolLibrary.getProtocolManager();
    }

    @Override
    public void registerDependency() throws DependencyRegisterException {
        if (Bukkit.getPluginManager().getPlugin(name()) == null) {
            throw new DependencyRegisterException(this);
        }
    }

    @Override
    public @NotNull String name() {
        return "ProtocolLib";
    }
}
