package com.andrei1058.bedwars.listeners;

import com.andrei1058.bedwars.api.arena.IArena;
import com.andrei1058.bedwars.arena.Arena;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

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

        // TODO: stuff
    }
}
