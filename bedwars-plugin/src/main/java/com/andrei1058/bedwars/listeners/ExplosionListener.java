package com.andrei1058.bedwars.listeners;

import com.andrei1058.bedwars.api.arena.IArena;
import com.andrei1058.bedwars.arena.Arena;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.util.Vector;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import static com.andrei1058.bedwars.BedWars.getAPI;

public class ExplosionListener implements Listener {

    @EventHandler
    public void tntExplode(EntityExplodeEvent e) {
        if (!(e.getEntity() instanceof TNTPrimed)) return;
        Location location = e.getLocation();
        World world = location.getWorld();

        Collection<Entity> nearbyEntities = world.getNearbyEntities(location, 5, 5, 5);
        for (Entity entity : nearbyEntities) {
            if (!(entity instanceof Player)) continue;

            Player source = (Player) entity;
            IArena arena = Arena.getArenaByPlayer(source);
            if (!getAPI().getArenaUtil().isPlaying(source)) continue;

            if (arena.getTeam(source).equals(arena.getTeam(source)) && !source.equals(source))
                continue;

            Vector playerVector = source.getLocation().toVector();
            Vector normalizedVector = playerVector.subtract(location.toVector()).normalize();
            Vector velocity = normalizedVector.multiply(1.5); // Edit 5 to adjust the knockback distance
            source.setVelocity(velocity);
        }

        world.playSound(location, Sound.EXPLODE, 1.0F, 1.0F);
    }

    @EventHandler
    public void tntDirectHit(EntityDamageByEntityEvent e) {
        if (!(e.getDamager() instanceof TNTPrimed)) return;
        if (!(e.getEntity() instanceof Player)) return;

        e.setCancelled(true);
    }

    @EventHandler
    public void onTntExplosion(EntityExplodeEvent event) {

        List<Block> blocksToRemove = new ArrayList<>();

        // For all the blocks that were affected by explosion
        for (Block block : event.blockList()) {
            // Check in a radius for stained glass
            int radius = 1;
            for (int x = -radius; x <= radius; x++) {
                for (int y = -radius; y <= radius; y++) {
                    for (int z = -radius; z <= radius; z++) {
                        Block nearbyBlock = block.getRelative(x, y, z);
                        Material nearbyMaterial = nearbyBlock.getType();
                        String materialName = nearbyMaterial.toString();

                        // If stained glass is found within the radius, add the block to the list of blocks to be removed
                        if(materialName.contains("STAINED_GLASS") || materialName.equalsIgnoreCase("obsidian")) {
                            blocksToRemove.add(block);
                            break; // Breaking out of the inner loop since we already found stained glass
                        }
                    }
                    if(blocksToRemove.contains(block)) break; // Breaking out of the middle loop since we already found stained glass
                }
                if(blocksToRemove.contains(block)) break; // Breaking out of the outer loop since we already found stained glass
            }
        }

        // Remove the blocks from the explosion list
        event.blockList().removeAll(blocksToRemove);
    }




}
