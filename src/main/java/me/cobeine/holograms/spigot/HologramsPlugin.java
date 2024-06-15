package me.cobeine.holograms.spigot;

import co.aikar.commands.PaperCommandManager;
import lombok.Getter;
import me.cobeine.holograms.api.dependency.DependencyManager;
import me.cobeine.holograms.api.dependency.DependencyRegisterException;
import me.cobeine.holograms.api.holograms.Hologram;
import me.cobeine.holograms.api.loader.PluginLaunchException;
import me.cobeine.holograms.api.loader.SpigotPluginLoader;
import me.cobeine.holograms.spigot.commands.HologramCommand;
import me.cobeine.holograms.spigot.dependencies.PlaceHolderAPIDependency;
import me.cobeine.holograms.spigot.dependencies.ProtocolLibDependency;
import me.cobeine.holograms.spigot.hologram.HologramManager;
import me.cobeine.holograms.spigot.listener.JoinListener;
import me.cobeine.holograms.spigot.storage.StorageHandler;
import me.cobeine.holograms.utils.ExceptionHandler;

import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @Author <a href="https://github.com/Cobeine">Cobeine</a>
 */
@Getter
public class HologramsPlugin extends SpigotPluginLoader {

    private static HologramsPlugin INSTANCE;
    private StorageHandler storageHandler;
    private HologramManager manager;
    private PaperCommandManager commandManager;
    private String uuid;
    @Override
    public void init() throws PluginLaunchException {
        INSTANCE = this;
        this.uuid = UUID.randomUUID().toString();
        registerDependencies();
        this.storageHandler = new StorageHandler(this);
        this.manager = new HologramManager(storageHandler);

        commandManager = new PaperCommandManager(this);
        commandManager.registerDependency(HologramManager.class, manager);
        commandManager.registerCommand(new HologramCommand());
        commandManager.getCommandCompletions().registerCompletion("allholograms", context -> {
            return manager.getAll_holograms().stream().map(Hologram::getId).collect(Collectors.toList());
        });
        commandManager.getCommandCompletions().registerCompletion("holograms", context -> {
            return manager.getPlaced_holograms().stream().map(Hologram::getId).collect(Collectors.toList());
        });

        registerListener(new JoinListener());
    }




    @Override
    public void fini() {
        INSTANCE = null;
        manager.fini(this);
        storageHandler.fini();



    }
    private void registerDependencies() throws PluginLaunchException {
        ExceptionHandler.handle(() -> DependencyManager.registerDependency(PlaceHolderAPIDependency.class));
        try {
            DependencyManager.registerDependency(ProtocolLibDependency.class);
        } catch (DependencyRegisterException exception) {
            throw new PluginLaunchException(exception);
        }
    }

    public static HologramsPlugin getInstance() {
        return INSTANCE;
    }


}
