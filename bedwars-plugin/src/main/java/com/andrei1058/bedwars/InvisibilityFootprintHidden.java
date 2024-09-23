package com.andrei1058.bedwars;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.event.PacketListenerPriority;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.particle.type.ParticleTypes;
import com.github.retrooper.packetevents.util.Vector3d;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityMetadata;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerParticle;
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffectType;

public class InvisibilityFootprintHidden implements PacketListener {

    public void setup(JavaPlugin plugin) {
        PacketEvents.setAPI(SpigotPacketEventsBuilder.build(plugin));
        PacketEvents.getAPI().getSettings().reEncodeByDefault(false);
        PacketEvents.getAPI().load();
        PacketEvents.getAPI().getEventManager().registerListener(this, PacketListenerPriority.HIGH);
    }

    @Override
    public void onPacketSend(PacketSendEvent event) {
        try {
            if (event.getPacketType() == PacketType.Play.Server.ENTITY_METADATA) {
                WrapperPlayServerEntityMetadata wrapper = new WrapperPlayServerEntityMetadata(event);
                Entity entity = SpigotConversionUtil.getEntityById(null, wrapper.getEntityId());
                if (!(entity instanceof LivingEntity) || entity == event.getPlayer())
                    return;
                LivingEntity livingEntity = (LivingEntity) entity;
                if (!livingEntity.hasPotionEffect(PotionEffectType.INVISIBILITY))
                    return;
                // Only remove the footprint if the entity is invisible

                for (EntityData entityData : wrapper.getEntityMetadata()) {
                    if (entityData.getIndex() == 0) {
                        byte value = (byte) entityData.getValue();
                        if ((value & 1 << 3) != 0) {
                            entityData.setValue((byte) (value & ~(1 << 3)));
                            event.markForReEncode(true);
                        }
                    }
                }
            } else if (event.getPacketType() == PacketType.Play.Server.PARTICLE) {
                try {
                    WrapperPlayServerParticle wrapper = new WrapperPlayServerParticle(event);
                    if (wrapper.getParticle().getType() == ParticleTypes.BLOCK) {
                        for (Player player : Bukkit.getOnlinePlayers()) {
                            Vector3d wrapperPosition = wrapper.getPosition();
                            Location location = new org.bukkit.Location(player.getWorld(), wrapperPosition.x, wrapperPosition.y, wrapperPosition.z);
                            if (player.hasPotionEffect(PotionEffectType.INVISIBILITY) && player.getLocation().distance(location) < 1.5) {
                                wrapper.setParticleCount((int) Math.floor(wrapper.getParticleCount() * 0.2));
                                event.markForReEncode(true);
                                break;
                            }
                        }
                    }
                } catch (Throwable ex) {
                    ex.printStackTrace();
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
