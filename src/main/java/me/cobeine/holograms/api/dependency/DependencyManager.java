package me.cobeine.holograms.api.dependency;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.function.Consumer;

/**
 * @Author <a href="https://github.com/Cobeine">Cobeine</a>
 */
@SuppressWarnings("all")
public final class DependencyManager {
   private static final HashMap<Class<? extends PluginDependency<?>>, PluginDependency<?>> MAP = new HashMap<>();

   public static <T> void registerDependency(@NotNull Class<? extends PluginDependency<T>> clazz) throws DependencyRegisterException {
        PluginDependency<T> dependency;
        try {
            dependency = clazz.newInstance();
        } catch (Exception e) {
            throw new DependencyRegisterException(e, clazz.getSimpleName());
        }
        dependency.registerDependency();
        MAP.put(clazz, dependency);
    }

    public static <T> boolean isRegistered(@NotNull Class<? extends PluginDependency<T>> clazz) {
        return MAP.containsKey(clazz);
    }

    public static <T> void ifPresent(@NotNull Class<? extends PluginDependency<T>> clazz,@NotNull Consumer<T> action) {
        if (!isRegistered(clazz))
            return;
        action.accept((T) MAP.get(clazz).get());
    }
    public static <T> void ifPresentOrElse(@NotNull Class<? extends PluginDependency<T>> clazz,@NotNull Consumer<T> action,Runnable runnable) {
        if (!isRegistered(clazz)){
            runnable.run();
            return;
        }
        action.accept((T) MAP.get(clazz).get());
    }
}
