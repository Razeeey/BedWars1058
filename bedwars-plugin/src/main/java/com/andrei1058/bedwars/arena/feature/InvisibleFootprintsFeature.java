package com.andrei1058.bedwars.arena.feature;

import com.andrei1058.bedwars.BedWars;
import com.andrei1058.bedwars.api.arena.GameState;
import com.andrei1058.bedwars.api.arena.IArena;
import com.andrei1058.bedwars.arena.Arena;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

public class InvisibleFootprintsFeature {

    private static InvisibleFootprintsFeature instance;
    private static boolean leftRight;

    private InvisibleFootprintsFeature() {
        Bukkit.getScheduler().runTaskTimer(BedWars.plugin, new FootprintTask(), 5, 40L);
    }

    public static void init() {
        if(instance == null) {
            instance = new InvisibleFootprintsFeature();
        }
        leftRight = true;
    }

    private static class FootprintTask implements Runnable {
        @Override
        public void run() {
            for (Player player : Bukkit.getOnlinePlayers()) {
                IArena arena = Arena.getArenaByPlayer(player);
                if(arena == null) return;
                if (arena.getStatus() != GameState.playing) return;
                if (!arena.isPlayer(player)) return;
                if (!player.hasPotionEffect(PotionEffectType.INVISIBILITY)) return;
                Location location = player.getLocation();
                location.setY(Math.floor(location.getY()));
                if (!location.clone().subtract(0, 1, 0).getBlock().isEmpty()) {
                    double x = Math.cos(Math.toRadians(player.getLocation().getYaw())) * 0.25d;
                    double y = Math.sin(Math.toRadians(player.getLocation().getYaw())) * 0.25d;

                    if (leftRight)
                        location.add(x, 0.025D, y);
                    else
                        location.subtract(x, -0.025D, y);

                    BedWars.nms.playFootprint(player, location);
                }
            }
            leftRight = !leftRight;
        }
    }
}