package com.medievallords.carbyne.duels.duel;

import com.medievallords.carbyne.duels.arena.Arena;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;

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

    private Arena arena;
    private DuelStage duelStage;
    private List<Item> drops;
    private long startTimeMillis;
    private Set<UUID> teamOneAlive = new HashSet<>();
    private Set<UUID> teamTwoAlive = new HashSet<>();
    private int money, taskId;

    public Duel(Arena arena) {
        this.arena = arena;
        this.duelStage = DuelStage.COUNTING_DOWN;
        this.drops = new ArrayList<>();
    }

    public void start() {
        for (UUID playerId : teamOneAlive) {
            Player player = Bukkit.getPlayer(playerId);
            if (player != null) {
                for (Duel duel : arena.getDuels()) {
                    for (UUID otherId : duel.getTeamOneAlive()) {
                        Player other = Bukkit.getPlayer(otherId);
                        if (other != null) {
                            other.hidePlayer(player);
                            player.hidePlayer(other);
                        }
                    }

                    for (UUID otherId : duel.getTeamTwoAlive()) {
                        Player other = Bukkit.getPlayer(otherId);
                        if (other != null) {
                            other.hidePlayer(player);
                            player.hidePlayer(other);
                        }
                    }
                }
            }
        }

        for (UUID playerId : teamTwoAlive) {
            Player player = Bukkit.getPlayer(playerId);
            if (player != null) {
                for (Duel duel : arena.getDuels()) {
                    for (UUID otherId : duel.getTeamOneAlive()) {
                        Player other = Bukkit.getPlayer(otherId);
                        if (other != null) {
                            other.hidePlayer(player);
                            player.hidePlayer(other);
                        }
                    }

                    for (UUID otherId : duel.getTeamTwoAlive()) {
                        Player other = Bukkit.getPlayer(otherId);
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

    public void countdown() {
        this.duelStage = DuelStage.COUNTING_DOWN;
    }

    public void end(UUID winnerId) {

        this.duelStage = DuelStage.ENDED;
    }

    public void check() {

    }

    public void task() {

    }

    public void stopTask() {

    }

}
