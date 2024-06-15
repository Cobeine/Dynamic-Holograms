package me.cobeine.holograms.spigot.hologram.placeholders;

import lombok.AllArgsConstructor;
import me.cobeine.holograms.api.ConfigPlaceholder;

/**
 * @Author <a href="https://github.com/Cobeine">Cobeine</a>
 */
@AllArgsConstructor(staticName = "of")

public class IndexPlaceholder implements ConfigPlaceholder<Integer> {
    private int index;
    @Override
    public Integer get() {
        return index;
    }

    @Override
    public String identifier() {
        return "index";
    }
}
