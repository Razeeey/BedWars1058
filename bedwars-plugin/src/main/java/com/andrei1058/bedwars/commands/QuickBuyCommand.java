package com.andrei1058.bedwars.commands;

import com.andrei1058.bedwars.arena.Arena;
import com.andrei1058.bedwars.shop.ShopManager;
import com.andrei1058.bedwars.shop.quickbuy.PlayerQuickBuyCache;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;

/**
 * @author kayalust
 * 11/18/2023 / 9:13 AM
 * BedWars1058 / com.andrei1058.bedwars.commands
 */

public class QuickBuyCommand extends BukkitCommand {
    public QuickBuyCommand(String name) {
        super(name);
    }

    @Override
    public boolean execute(CommandSender s, String st, String[] args) {
        if (s instanceof ConsoleCommandSender) {
            s.sendMessage("This command is for players!");
            return true;
        }

        Player p = (Player) s;

        if (Arena.isInArena(p)) {
            p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cYou can't do this here!"));
            return true;
        }

        ShopManager.shop.open(p, PlayerQuickBuyCache.getQuickBuyCache(p.getUniqueId()),true);
        return false;
    }
}
