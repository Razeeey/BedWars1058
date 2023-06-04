/*
 * BedWars1058 - A bed wars mini-game.
 * Copyright (C) 2021 Andrei Dascălu
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 * Contact e-mail: andrew.dascalu@gmail.com
 */

package com.andrei1058.bedwars.arena.tasks;

import com.andrei1058.bedwars.api.arena.generator.IGenerator;
import com.andrei1058.bedwars.arena.Arena;
import com.andrei1058.bedwars.arena.OreGenerator;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;

public class OneTick implements Runnable {
    @Override
    public void run() {

        //OneTick generators
        for (IGenerator h : OreGenerator.getRotation()) {
            h.rotate();
        }

        List<Player> onlinePlayers = (List<Player>) Bukkit.getOnlinePlayers();

        Arena.getArenas().forEach(arena -> {
            arena.getPlayers().forEach(p->{onlinePlayers.remove(p);p.setPlayerListName(p.getName() + " " + p.getHealth());});
        });

        onlinePlayers.forEach(p->{p.setPlayerListName(p.getName());});
    }
}
