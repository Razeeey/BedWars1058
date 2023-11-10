package com.andrei1058.bedwars.arena.tasks;

import com.andrei1058.bedwars.BedWars;
import com.andrei1058.bedwars.api.arena.GameState;
import com.andrei1058.bedwars.api.arena.IArena;
import com.andrei1058.bedwars.arena.Arena;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * @author kayalust
 * 11/7/2023 / 8:36 PM
 * BedWars1058 / com.andrei1058.bedwars.listeners
 */

public class HeightLimitTask implements Runnable {

    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            IArena arena = Arena.getArenaByPlayer(player);

            if (arena == null || !arena.isPlayer(player)) return;
            if (arena.getStatus().equals(GameState.waiting) || arena.getStatus().equals(GameState.starting)) return; // don't send if in waiting game status

            int maxHeight = arena.getConfig().getInt("max-build-y");
            int distance = (int) (maxHeight - player.getLocation().getY());

            if (distance <= 0) {
                sendActionBar(player, ChatColor.translateAlternateColorCodes('&', "&cYou are currently at the height limit! &e(" + (int) player.getLocation().getY() + " blocks)"));
            } else if (distance <= 10) {
                sendActionBar(player, ChatColor.translateAlternateColorCodes('&', "&aYou are &e" + distance + " &ablocks away from the height limit!"));
            }
        }
    }

    private void sendActionBar(Player player, String message) {
        if (!player.isOnline()) return;

        try {
            Class<?> craftPlayerClass = Class.forName("org.bukkit.craftbukkit." + BedWars.getServerVersion() + ".entity.CraftPlayer");
            Object craftPlayer = craftPlayerClass.cast(player);
            Object packet;
            Class<?> packetPlayOutChatClass = Class.forName("net.minecraft.server." + BedWars.getServerVersion() + ".PacketPlayOutChat");
            Class<?> packetClass = Class.forName("net.minecraft.server." + BedWars.getServerVersion() + ".Packet");

            Class<?> iChatBaseComponentClass = Class.forName("net.minecraft.server." + BedWars.getServerVersion() + ".IChatBaseComponent");
            Class<?> chatSerializerClass = Class.forName("net.minecraft.server." + BedWars.getServerVersion() + ".IChatBaseComponent$ChatSerializer");

            Method m3 = chatSerializerClass.getDeclaredMethod("a", String.class);
            Object cbc = iChatBaseComponentClass.cast(m3.invoke(chatSerializerClass, "{\"text\": \"" + message + "\"}"));
            packet = packetPlayOutChatClass.getConstructor(new Class<?>[]{iChatBaseComponentClass, byte.class}).newInstance(cbc, (byte) 2);

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
}
