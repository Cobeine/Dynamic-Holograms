package me.cobeine.holograms.api.dependency;


/**
 * @Author <a href="https://github.com/Cobeine">Cobeine</a>
 */

public class DependencyRegisterException extends Exception{

    public DependencyRegisterException(PluginDependency<?> pluginDependency) {
        this(pluginDependency, "Plugin is not loaded!");
    }

    public DependencyRegisterException(PluginDependency<?> pluginDependency,String text) {
        super(String.format("Failed to register dependency %s: %s", pluginDependency.name(),text));
    }

    public DependencyRegisterException(Exception e,String className) {
        super(String.format("Failed to register dependency class %s: %s", className,e.getCause()));

    }
}
