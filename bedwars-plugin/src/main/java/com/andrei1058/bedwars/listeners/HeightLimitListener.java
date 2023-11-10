package com.andrei1058.bedwars.listeners;

import com.andrei1058.bedwars.BedWars;
import com.andrei1058.bedwars.api.arena.IArena;
import com.andrei1058.bedwars.arena.Arena;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * @author kayalust
 * 11/7/2023 / 8:36 PM
 * BedWars1058 / com.andrei1058.bedwars.listeners
 */

public class HeightLimitListener implements Listener {

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        IArena arena = Arena.getArenaByPlayer(player);

        if (!arena.isPlayer(player)) return;

        double maxHeight = arena.getConfig().getInt("arena.max-build-y"); // this isn't included in the arena class for some reason
        double distance = player.getLocation().getY() - maxHeight;

        if (distance <= 0) {
            sendActionBar(player, ChatColor.translateAlternateColorCodes('&', "&cYou are currently at the height limit! &e(" + maxHeight + " blocks)"));
        } else if (distance <= 10) {
            sendActionBar(player, ChatColor.translateAlternateColorCodes('&', "&aYou are &e" + distance + " &ablocks away from the height limit!"));
        }
    }

    // From ConnorLinfoot
    private void sendActionBar(Player player, String message) {
        if (!player.isOnline()) return;

        try {
            // i think this only works on 1.8 lol but im using the ServerVersion var anyways
            Class<?> craftPlayerClass = Class.forName("org.bukkit.craftbukkit." + BedWars.getServerVersion() + ".entity.CraftPlayer");
            Object craftPlayer = craftPlayerClass.cast(player);

            Object packet;
            Class<?> packetPlayOutChatClass = Class.forName("net.minecraft.server." + BedWars.getServerVersion() + ".PacketPlayOutChat");
            Class<?> packetClass = Class.forName("net.minecraft.server." + BedWars.getServerVersion() + ".Packet");

            Class<?> chatComponentTextClass = Class.forName("net.minecraft.server." + BedWars.getServerVersion() + ".ChatComponentText");
            Class<?> iChatBaseComponentClass = Class.forName("net.minecraft.server." + BedWars.getServerVersion() + ".IChatBaseComponent");

            try {
                Class<?> chatMessageTypeClass = Class.forName("net.minecraft.server." + BedWars.getServerVersion() + ".ChatMessageType");
                Object[] chatMessageTypes = chatMessageTypeClass.getEnumConstants();
                Object chatMessageType = null;

                for (Object obj : chatMessageTypes) {
                    if (obj.toString().equals("GAME_INFO")) {
                        chatMessageType = obj;
                    }
                }

                Object chatCompontentText = chatComponentTextClass.getConstructor(new Class<?>[]{String.class}).newInstance(message);
                packet = packetPlayOutChatClass.getConstructor(new Class<?>[]{iChatBaseComponentClass, chatMessageTypeClass}).newInstance(chatCompontentText, chatMessageType);
            } catch (ClassNotFoundException cnfe) {
                Object chatCompontentText = chatComponentTextClass.getConstructor(new Class<?>[]{String.class}).newInstance(message);
                packet = packetPlayOutChatClass.getConstructor(new Class<?>[]{iChatBaseComponentClass, byte.class}).newInstance(chatCompontentText, (byte) 2);
            }

            Method craftPlayerHandleMethod = craftPlayerClass.getDeclaredMethod("getHandle");
            Object craftPlayerHandle = craftPlayerHandleMethod.invoke(craftPlayer);
            Field playerConnectionField = craftPlayerHandle.getClass().getDeclaredField("playerConnection");
            Object playerConnection = playerConnectionField.get(craftPlayerHandle);
            Method sendPacketMethod = playerConnection.getClass().getDeclaredMethod("sendPacket", packetClass);

            sendPacketMethod.invoke(playerConnection, packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendActionBar(final Player player, final String message, int duration) {
        sendActionBar(player, message);

        if (duration >= 0) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    sendActionBar(player, "");
                }
            }.runTaskLater(BedWars.plugin, duration + 1);
        }
        while (duration > 40) {
            duration -= 40;
            new BukkitRunnable() {
                @Override
                public void run() {
                    sendActionBar(player, message);
                }
            }.runTaskLater(BedWars.plugin, duration);
        }
    }
}
