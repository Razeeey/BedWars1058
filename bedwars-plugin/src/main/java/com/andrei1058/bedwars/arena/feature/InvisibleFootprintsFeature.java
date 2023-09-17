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
        Bukkit.getScheduler().runTaskTimer(BedWars.plugin, new FootprintTask(), 5, 20L);
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
                location.setY(Math.floor(location.getY())+.1);

                if (!location.clone().subtract(0, 1, 0).getBlock().isEmpty()) {
                    // 90 degrees to left or right to set footprint to the side
                    float angle = player.getLocation().getYaw() + (leftRight ? -90 : 90);
                    double x = Math.cos(Math.toRadians(angle)) * 0.25d;
                    double z = Math.sin(Math.toRadians(angle)) * 0.25d;

                    location.add(x, 0.025D, z);

                    BedWars.nms.playFootprint(player, location);
                }
            }
            leftRight = !leftRight;
        }

    }
}