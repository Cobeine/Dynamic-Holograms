package me.cobeine.holograms.api;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedDataValue;
import me.cobeine.holograms.api.dependency.DependencyManager;
import me.cobeine.holograms.spigot.dependencies.ProtocolLibDependency;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author <a href="https://github.com/Cobeine">Cobeine</a>
 */

public interface ProtocolEntity{
     AtomicInteger entityID = new AtomicInteger(-1);

    void spawn(Player player, Location location);

    void deSpawn(Player player);

    int getId();


    List<WrappedDataValue> getMetadata(Player player);

    default void sendMetaDataPacket(Player player) {
        DependencyManager.ifPresent(ProtocolLibDependency.class, protocolManager -> {
            PacketContainer packet = protocolManager.createPacket(PacketType.Play.Server.ENTITY_METADATA);
            var metadatas = packet.getDataValueCollectionModifier().read(0);
            metadatas.addAll(getMetadata(player));

            packet.getDataValueCollectionModifier().write(0, metadatas);
            packet.getIntegers().write(0, getId());
            protocolManager.sendServerPacket(player, packet,false);
        });


    }

}
