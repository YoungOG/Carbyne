package com.medievallords.carbyne.duels.duel;

import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.duels.arena.Arena;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

/**
 * Created by xwiena22 on 2017-03-something
 * for the Carbyne project.
 */
@Getter
@Setter
public class Duel {

    //ADD A LOOT INVENTORY

    private static final int COUNTDOWN_DURATION = 5;

    private final Arena arena;
    private DuelStage duelStage;
    private final DuelType duelType;
    private List<Item> drops;
    private long startTimeMillis;
    private final HashMap<Player, Integer> playerStates = new HashMap<>();
    private final Set<UUID> teamOneAlive = new HashSet<>();
    private final Set<UUID> teamTwoAlive = new HashSet<>();
    private int money, taskId;

    public Duel(final Arena arena, final DuelType duelType) {
        this.arena = arena;
        this.duelStage = DuelStage.COUNTING_DOWN;
        this.duelType = duelType;
        this.drops = new ArrayList<>();
    }

    public void start() {
        for (final UUID playerId : teamOneAlive) {
            final Player player = Bukkit.getPlayer(playerId);
            if (player != null) {
                for (final Duel duel : arena.getDuels()) {
                    for (final UUID otherId : duel.getTeamOneAlive()) {
                        final Player other = Bukkit.getPlayer(otherId);
                        if (other != null) {
                            other.hidePlayer(player);
                            player.hidePlayer(other);
                        }
                    }

                    for (final UUID otherId : duel.getTeamTwoAlive()) {
                        final Player other = Bukkit.getPlayer(otherId);
                        if (other != null) {
                            other.hidePlayer(player);
                            player.hidePlayer(other);
                        }
                    }
                }
            }
        }

        for (final UUID playerId : teamTwoAlive) {
            final Player player = Bukkit.getPlayer(playerId);
            if (player != null) {
                for (final Duel duel : arena.getDuels()) {
                    for (final UUID otherId : duel.getTeamOneAlive()) {
                        final Player other = Bukkit.getPlayer(otherId);
                        if (other != null) {
                            other.hidePlayer(player);
                            player.hidePlayer(other);
                        }
                    }

                    for (final UUID otherId : duel.getTeamTwoAlive()) {
                        final Player other = Bukkit.getPlayer(otherId);
                        if (other != null) {
                            other.hidePlayer(player);
                            player.hidePlayer(other);
                        }
                    }
                }
            }
        }

        this.duelStage = DuelStage.FIGHTING;
    }

    public void init() {
        this.duelStage = DuelStage.COUNTING_DOWN;
        new BukkitRunnable() {
            private int count = 5;
            @Override
            public void run() {
                if (count-- <= 0) {
                    for (final Player player : playerStates.keySet()) {
                        player.sendActionBar("§d§lGo!");
                    }

                    cancel();
                    start();
                } else {
                    for (final Player player : playerStates.keySet()) {
                        player.sendActionBar("§d§l" + count);
                    }
                }
            }
        }.runTaskTimer(Carbyne.getInstance(), 0, 20);
    }

    public void onDeath(final Player player) {
        if (isTeamOne(player.getUniqueId())) {

        }
    }

    public void onQuit(final Player player) {

    }

    public void end() {
        this.duelStage = DuelStage.ENDED;
    }

    private boolean isTeamOne(final UUID uuid) {
        return teamOneAlive.contains(uuid);
    }
}
