package me.cobeine.holograms.api.holograms;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedDataValue;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import lombok.Getter;
import me.cobeine.holograms.api.ProtocolEntity;
import me.cobeine.holograms.api.dependency.DependencyManager;
import me.cobeine.holograms.spigot.dependencies.ProtocolLibDependency;
import me.cobeine.holograms.utils.TextUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * @Author <a href="https://github.com/Cobeine">Cobeine</a>
 */
@Getter
public class HologramLine implements ProtocolEntity {
    private final int id;
    private String text;
    private final UUID uuid;

     HologramLine(String text) {
        this.text = text;
        this.uuid = UUID.randomUUID();
        this.id = entityID.decrementAndGet();
    }

    public static HologramLine of(String text) {
        return new HologramLine(text);
    }

    @Override
    public void spawn(Player player,Location location) {
        DependencyManager.ifPresent(ProtocolLibDependency.class, protocolManager-> {
            PacketContainer packet = protocolManager.createPacket(PacketType.Play.Server.SPAWN_ENTITY);
            packet.getIntegers().write(0, id);
            packet.getUUIDs().write(0, uuid);
            packet.getEntityTypeModifier().write(0, EntityType.ARMOR_STAND);
            packet.getDoubles().write(0, location.getX());
            packet.getDoubles().write(1, location.getY());
            packet.getDoubles().write(2, location.getZ());
            protocolManager.sendServerPacket(player,packet,false);
            sendMetaDataPacket(player);
        });
    }

    @Override
    public void deSpawn(Player player) {
        DependencyManager.ifPresent(ProtocolLibDependency.class, protocolManager-> {
            PacketContainer packet = protocolManager.createPacket(PacketType.Play.Server.ENTITY_DESTROY);
            packet.getModifier().write(0, new IntArrayList(new int[]{id}));
            protocolManager.sendServerPacket(player,packet,false);
        });
    }

    @Override
    public List<WrappedDataValue> getMetadata(Player player) {
        List<WrappedDataValue> metadatas = new ArrayList<>();
        var name = new WrappedDataValue(2,
                WrappedDataWatcher.Registry.getChatComponentSerializer(true),
                Optional.of(WrappedChatComponent.fromText(TextUtils.colorizeLegacy(text,player)).getHandle())
        );
        metadatas.add(name);
        metadatas.add(new WrappedDataValue(0,WrappedDataWatcher.Registry.get(Byte.class),(byte) 0x20 ));
        metadatas.add(new WrappedDataValue(3,WrappedDataWatcher.Registry.get(Boolean.class),true));
        metadatas.add(new WrappedDataValue(15,WrappedDataWatcher.Registry.get(Byte.class),(byte) 0x01 ));
        return metadatas;
    }

    public void setText(String text) {
        this.text = text;
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            sendMetaDataPacket(onlinePlayer);
        }
    }
}
