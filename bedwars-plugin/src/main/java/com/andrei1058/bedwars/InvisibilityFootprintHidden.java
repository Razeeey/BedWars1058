package com.andrei1058.bedwars;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.event.PacketListenerPriority;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityMetadata;
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder;
import org.bukkit.plugin.java.JavaPlugin;

public class InvisibilityFootprintHidden implements PacketListener {

    public void setup(JavaPlugin plugin) {
        PacketEvents.setAPI(SpigotPacketEventsBuilder.build(plugin));
        PacketEvents.getAPI().load();
        PacketEvents.getAPI().getEventManager().registerListener(this, PacketListenerPriority.HIGH);
    }

    @Override
    public void onPacketSend(PacketSendEvent event) {
        System.out.println(event.getPacketType());

        if (event.getPacketType() == PacketType.Play.Server.ENTITY_METADATA) {
            WrapperPlayServerEntityMetadata wrapper = new WrapperPlayServerEntityMetadata(event);
            for (EntityData entityData : wrapper.getEntityMetadata()) {
                if (entityData.getIndex() == 0) {
                    byte value = (byte) entityData.getValue();
                    if ((value & 1 << 3) != 0) {
                        entityData.setValue((byte) (value & ~(1 << 3)));
                        event.markForReEncode(true);
                    }
                }
            }
        }
    }

}
