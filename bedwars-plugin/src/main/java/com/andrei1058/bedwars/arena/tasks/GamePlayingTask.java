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

import com.andrei1058.bedwars.BedWars;
import com.andrei1058.bedwars.api.arena.GameState;
import com.andrei1058.bedwars.api.arena.IArena;
import com.andrei1058.bedwars.api.arena.generator.IGenerator;
import com.andrei1058.bedwars.api.arena.team.ITeam;
import com.andrei1058.bedwars.api.configuration.ConfigPath;
import com.andrei1058.bedwars.api.events.player.PlayerInvisibilityPotionEvent;
import com.andrei1058.bedwars.api.language.Language;
import com.andrei1058.bedwars.api.language.Messages;
import com.andrei1058.bedwars.api.tasks.PlayingTask;
import com.andrei1058.bedwars.arena.Arena;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;

import java.util.Iterator;
import java.util.Map;

import static com.andrei1058.bedwars.BedWars.nms;
import static com.andrei1058.bedwars.api.language.Language.getMsg;

public class GamePlayingTask implements Runnable, PlayingTask {

    private Arena arena;
    private BukkitTask task;
    private BukkitTask task2;
    private int beds_destroy_countdown, dragon_spawn_countdown, game_end_countdown;

    public GamePlayingTask(Arena arena) {
        this.arena = arena;
        this.beds_destroy_countdown = BedWars.config.getInt(ConfigPath.GENERAL_CONFIGURATION_BEDS_DESTROY_COUNTDOWN);
        this.dragon_spawn_countdown = BedWars.config.getInt(ConfigPath.GENERAL_CONFIGURATION_DRAGON_SPAWN_COUNTDOWN);
        this.game_end_countdown = BedWars.config.getInt(ConfigPath.GENERAL_CONFIGURATION_GAME_END_COUNTDOWN);
        this.task = Bukkit.getScheduler().runTaskTimer(BedWars.plugin, this::run, 0, 20L);
        this.task2 = Bukkit.getScheduler().runTaskTimer(BedWars.plugin, this::run2, 0, 1L);
    }

    public Arena getArena() {
        return arena;
    }

    @Override
    public BukkitTask getBukkitTask() {
        return task;
    }
    public BukkitTask getBukkitTask2() {
        return task2;
    }

    /**
     * Get task ID
     */
    public int getTask() {
        return task.getTaskId();
    }

    public int getBedsDestroyCountdown() {
        return beds_destroy_countdown;
    }

    public int getDragonSpawnCountdown() {
        return dragon_spawn_countdown;
    }

    public int getGameEndCountdown() {
        return game_end_countdown;
    }

    @Override
    public void run() {
        switch (getArena().getNextEvent()) {
            case EMERALD_GENERATOR_TIER_II:
            case EMERALD_GENERATOR_TIER_III:
            case DIAMOND_GENERATOR_TIER_II:
            case DIAMOND_GENERATOR_TIER_III:
                if (getArena().upgradeDiamondsCount > 0) {
                    getArena().upgradeDiamondsCount--;
                    if (getArena().upgradeDiamondsCount == 0) {
                        getArena().updateNextEvent();
                    }
                }
                if (getArena().upgradeEmeraldsCount > 0) {
                    getArena().upgradeEmeraldsCount--;
                    if (getArena().upgradeEmeraldsCount == 0) {
                        getArena().updateNextEvent();
                    }
                }
                break;
            case BEDS_DESTROY:
                beds_destroy_countdown--;
                if (getBedsDestroyCountdown() == 0) {
                    for (Player p : getArena().getPlayers()) {
                        nms.sendTitle(p, getMsg(p, Messages.NEXT_EVENT_TITLE_ANNOUNCE_BEDS_DESTROYED), getMsg(p, Messages.NEXT_EVENT_SUBTITLE_ANNOUNCE_BEDS_DESTROYED), 0, 40, 10);
                        p.sendMessage(getMsg(p, Messages.NEXT_EVENT_CHAT_ANNOUNCE_BEDS_DESTROYED));
                    }
                    for (Player p : getArena().getSpectators()) {
                        nms.sendTitle(p, getMsg(p, Messages.NEXT_EVENT_TITLE_ANNOUNCE_BEDS_DESTROYED), getMsg(p, Messages.NEXT_EVENT_SUBTITLE_ANNOUNCE_BEDS_DESTROYED), 0, 40, 10);
                        p.sendMessage(getMsg(p, Messages.NEXT_EVENT_CHAT_ANNOUNCE_BEDS_DESTROYED));
                    }
                    for (ITeam t : getArena().getTeams()) {
                        t.setBedDestroyed(true);
                    }
                    getArena().updateNextEvent();
                }
                break;
            case ENDER_DRAGON:
                dragon_spawn_countdown--;
                if (getDragonSpawnCountdown() == 0) {
                    for (Player p : getArena().getPlayers()) {
                        nms.sendTitle(p, getMsg(p, Messages.NEXT_EVENT_TITLE_ANNOUNCE_SUDDEN_DEATH), getMsg(p, Messages.NEXT_EVENT_SUBTITLE_ANNOUNCE_SUDDEN_DEATH), 0, 40, 10);
                        for (ITeam t : getArena().getTeams()) {
                            if (t.getMembers().isEmpty()) continue;
                            p.sendMessage(getMsg(p, Messages.NEXT_EVENT_CHAT_ANNOUNCE_SUDDEN_DEATH).replace("{TeamDragons}", String.valueOf(t.getDragons()))
                                    .replace("{TeamColor}", t.getColor().chat().toString()).replace("{TeamName}", t.getDisplayName(Language.getPlayerLanguage(p))));
                        }
                    }
                    for (Player p : getArena().getSpectators()) {
                        nms.sendTitle(p, getMsg(p, Messages.NEXT_EVENT_TITLE_ANNOUNCE_SUDDEN_DEATH), getMsg(p, Messages.NEXT_EVENT_SUBTITLE_ANNOUNCE_SUDDEN_DEATH), 0, 40, 10);
                        for (ITeam t : getArena().getTeams()) {
                            if (t.getMembers().isEmpty()) continue;
                            p.sendMessage(getMsg(p, Messages.NEXT_EVENT_CHAT_ANNOUNCE_SUDDEN_DEATH).replace("{TeamDragons}", String.valueOf(t.getDragons()))
                                    .replace("{TeamColor}", t.getColor().chat().toString()).replace("{TeamName}", t.getDisplayName(Language.getPlayerLanguage(p))));
                        }
                    }
                    getArena().updateNextEvent();
                    for (ITeam team : arena.getTeams()){
                        for (IGenerator o : team.getGenerators()) {
                            Location l = o.getLocation();
                            for (int y = 0; y < 20; y++) {
                                l.clone().subtract(0, y, 0).getBlock().setType(Material.AIR);
                            }
                        }
                    }
                    for (ITeam t : getArena().getTeams()) {
                        if (t.getMembers().isEmpty()) continue;
                        for (int x = 0; x < t.getDragons(); x++) {
                            nms.spawnDragon(getArena().getConfig().getArenaLoc("waiting.Loc").add(0, 10, 0), t);
                        }
                    }
                }
                break;
            case GAME_END:
                game_end_countdown--;
                if (getGameEndCountdown() == 0) {
                    getArena().checkWinner();
                    getArena().changeStatus(GameState.restarting);
                }
                break;
        }


        /* AFK SYSTEM FOR PLAYERS */
        int current = 0;
        for (Player p : getArena().getPlayers()) {
            if (Arena.afkCheck.get(p.getUniqueId()) == null) {
                Arena.afkCheck.put(p.getUniqueId(), current);
            } else {
                current = Arena.afkCheck.get(p.getUniqueId());
                current++;
                Arena.afkCheck.replace(p.getUniqueId(), current);
                if (current == 45) {
                    BedWars.getAPI().getAFKUtil().setPlayerAFK(p, true);
                }
            }
        }

        /* INVISIBILITY FOR ARMOR */
        if (!getArena().getShowTime().isEmpty()) {
            for (Iterator<Map.Entry<Player, Integer>> it = getArena().getShowTime().entrySet().iterator(); it.hasNext();) {
                Map.Entry<Player, Integer> e = it.next();
                Player hidden = e.getKey();

                if (e.getValue() <= 0) {
                    // Respawn invis time ended → show player again
                    for (Player viewer : Bukkit.getOnlinePlayers()) {
                        if (viewer.equals(hidden)) continue;

                        viewer.showPlayer(hidden);           // Bukkit API: respawn entity
                        BedWars.nms.showArmor(hidden, viewer); // re-show armor packets
                    }

                    it.remove();
                    hidden.removePotionEffect(PotionEffectType.INVISIBILITY); // optional safeguard

                    Bukkit.getPluginManager().callEvent(new PlayerInvisibilityPotionEvent(
                            PlayerInvisibilityPotionEvent.Type.REMOVED,
                            getArena().getTeam(hidden),
                            hidden,
                            getArena()
                    ));
                } else {
                    BedWars.plugin.getLogger().info("ShowTime tick for " + hidden.getName() + ": " + e.getValue());
                    e.setValue(e.getValue() - 1);
                }
            }
        }


        for (IGenerator o : getArena().getOreGenerators()) {
            o.spawn();
        }
    }

    public void run2() {
        int distance = 0;
        for (ITeam t : getArena().getTeams()) {
            if (t.getSize() > 1) {
                for (Player p : t.getMembers()) {
                    for (Player p2 : t.getMembers()) {
                        if (p2 == p) continue;
                        if (distance == 0) {
                            distance = (int) p.getLocation().distance(p2.getLocation());
                        } else if ((int) p.getLocation().distance(p2.getLocation()) < distance) {
                            distance = (int) p.getLocation().distance(p2.getLocation());
                        }
                    }
                    nms.playAction(p, getMsg(p, Messages.FORMATTING_ACTION_BAR_TRACKING).replace("{team}", t.getColor().chat() + t.getDisplayName(Language.getPlayerLanguage(p)))
                            .replace("{distance}", t.getColor().chat().toString() + distance).replace("&", "§"));
                }
            }

            // spawn items
            for (IGenerator o : t.getGenerators()) {
                o.spawn();
            }
        }

        /* RESPAWN SESSION */
        if (!getArena().getRespawnSessions().isEmpty()) {
            for (Iterator<Map.Entry<Player, Long>> it = getArena().getRespawnSessions().entrySet().iterator(); it.hasNext(); ) {
                Map.Entry<Player, Long> e = it.next();
                Player p = e.getKey();

                int secondsTimeUntilRespawn = (int) ((e.getValue() - System.currentTimeMillis()) / 1000);

                if (secondsTimeUntilRespawn <= 0) {
                    IArena a = Arena.getArenaByPlayer(p);
                    if (a == null) {
                        it.remove();
                        continue;
                    }
                    ITeam t = a.getTeam(p);
                    if (t == null) {
                        a.addSpectator(p, true, null);
                    } else {
                        t.respawnMember(p);
                        p.setAllowFlight(false);
                        p.setFlying(false);
                    }
                    it.remove();
                } else {
                    // Always update the title (it overwrites itself smoothly)
                    nms.sendTitle(
                            p,
                            getMsg(p, Messages.PLAYER_DIE_RESPAWN_TITLE).replace("{time}", String.valueOf(secondsTimeUntilRespawn)),
                            getMsg(p, Messages.PLAYER_DIE_RESPAWN_SUBTITLE).replace("{time}", String.valueOf(secondsTimeUntilRespawn)),
                            0, 30, 10
                    );

                    // Only send chat once per second
                    Map<Player, Integer> notified = getArena().getRespawnNotifiedSeconds();
                    Integer lastSecond = notified.get(p);
                    if (lastSecond == null || lastSecond != secondsTimeUntilRespawn) {
                        p.sendMessage(
                                getMsg(p, Messages.PLAYER_DIE_RESPAWN_CHAT).replace("{time}", String.valueOf(secondsTimeUntilRespawn))
                        );
                        notified.put(p, secondsTimeUntilRespawn);
                    }
                }
            }
        }
    }
            public void cancel () {
                task.cancel();
                task2.cancel();
            }

        }

