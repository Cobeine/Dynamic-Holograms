package me.cobeine.holograms.utils;

import me.cobeine.holograms.spigot.HologramsPlugin;

import java.util.function.Consumer;
import java.util.logging.Level;

/**
 * @Author <a href="https://github.com/Cobeine">Cobeine</a>
 */

public class ExceptionHandler {

    public static void handle(ThrowableRunnable runnable, Consumer<Exception> exceptionConsumer) {
        try {
            runnable.run();
        } catch (Exception e) {
            exceptionConsumer.accept(e);
        }
    }
    public static void handle(ThrowableRunnable runnable) {
        try {
            runnable.run();
        } catch (Exception e) {
            HologramsPlugin.getInstance().getLogger().log(Level.SEVERE, String.valueOf(e));
        }
    }


}
