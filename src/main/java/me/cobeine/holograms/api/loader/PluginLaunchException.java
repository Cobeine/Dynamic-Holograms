package me.cobeine.holograms.api.loader;

import me.cobeine.holograms.api.dependency.DependencyRegisterException;

/**
 * @Author <a href="https://github.com/Cobeine">Cobeine</a>
 */

public class PluginLaunchException extends Exception{

    public PluginLaunchException(String s) {
        super("Failed to start plugin: " + s);
    }

    public PluginLaunchException(Exception exception) {
        super("Failed to start plugin: " + exception);
    }
}
