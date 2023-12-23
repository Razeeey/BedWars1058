package com.andrei1058.bedwars.arena.tasks;

import com.andrei1058.bedwars.BedWars;
import com.andrei1058.bedwars.api.arena.GameState;
import com.andrei1058.bedwars.api.arena.IArena;
import com.andrei1058.bedwars.api.configuration.ConfigPath;
import com.andrei1058.bedwars.api.language.Language;
import com.andrei1058.bedwars.api.language.Messages;
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
            if (arena.isReSpawning(player)) return;

            int maxHeight = arena.getConfig().getInt(ConfigPath.ARENA_CONFIGURATION_MAX_BUILD_Y);
            int distance = (int) (maxHeight - player.getLocation().getY());

            if (distance <= 0) {
                sendActionBar(player, ChatColor.translateAlternateColorCodes('&', Language.getMsg(player, Messages.ARENA_HEIGHT_LIMIT_REACHED).replace("{height}", String.valueOf((int) player.getLocation().getY()))));
            } else if (distance <= arena.getConfig().getInt(ConfigPath.ARENA_CONFIGURATION_MAX_BUILD_DISTANCE)) {
                sendActionBar(player, ChatColor.translateAlternateColorCodes('&', Language.getMsg(player, Messages.ARENA_HEIGHT_LIMIT).replace("{distance}", String.valueOf(distance))));
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
        } catch (Exception ignored) {

        }
    }
}
