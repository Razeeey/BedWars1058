package com.andrei1058.bedwars.listeners;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.util.Vector;

import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.List;

public class ExplosionListener implements Listener {

    /*@EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        Location explosionLocation = event.getLocation();
        List<Block> blockList = event.blockList();
        Iterator<Block> it = blockList.iterator();

        while (it.hasNext()) {
            Block block = it.next();
            if (isProtectedFromExplosion(block, explosionLocation)) {
                it.remove();
            }
        }
    }

    private boolean isProtectedFromExplosion(Block block, Location explosionLocation) {
        Vector direction = block.getLocation().toVector().subtract(explosionLocation.toVector());

        BlockFace face = getBlockFace(direction);

        return block.getRelative(face).getType() == Material.STAINED_GLASS;
    }

    private BlockFace getBlockFace(Vector direction) {
        double x = direction.getX();
        double y = direction.getY();
        double z = direction.getZ();

        if (Math.abs(x) > Math.abs(y) && Math.abs(x) > Math.abs(z)) {
            return x > 0 ? BlockFace.EAST : BlockFace.WEST;
        } else if (Math.abs(y) > Math.abs(z)) {
            return y > 0 ? BlockFace.UP : BlockFace.DOWN;
        } else {
            return z > 0 ? BlockFace.SOUTH : BlockFace.NORTH;
        }
    }*/



}
