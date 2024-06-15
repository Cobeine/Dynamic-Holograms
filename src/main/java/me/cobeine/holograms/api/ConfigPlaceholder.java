package me.cobeine.holograms.api;

/**
 * @Author <a href="https://github.com/Cobeine">Cobeine</a>
 */

public interface ConfigPlaceholder<T> {

    default String apply(String text) {
        return text.replace("<" + identifier() + ">", get().toString());
    }

    T get();

    String identifier();
}
