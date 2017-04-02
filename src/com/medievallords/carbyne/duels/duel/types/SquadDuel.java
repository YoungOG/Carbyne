package com.medievallords.carbyne.duels.duel.types;

import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.duels.arena.Arena;
import com.medievallords.carbyne.duels.duel.Duel;
import com.medievallords.carbyne.duels.duel.DuelStage;
import com.medievallords.carbyne.squads.Squad;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

/**
 * Created by WE on 2017-04-02.
 *
 */
@Getter
@Setter
public class SquadDuel extends Duel {

    private Squad squadOne, squadTwo;

    public SquadDuel(Arena arena, Squad squadOne, Squad squadTwo) {
        super(arena);
        this.squadOne = squadOne;
        this.squadTwo = squadTwo;
    }

    @Override
    public void start() {
        Arena arena = getArena();
        for (UUID uuid : squadOne.getAllPlayers()) {
            Player player = Bukkit.getPlayer(uuid);

            if (player == null) {
                return;
            }

            player.teleport(arena.getSpawnPointLocations()[0].clone().add(0.0, 0.5, 0.0));

            player.setHealth(player.getMaxHealth());
            player.setFoodLevel(20);

            for (PotionEffectType effectType : PotionEffectType.values()) {
                player.removePotionEffect(effectType);
            }

        }

        for (UUID uuid : squadTwo.getAllPlayers()) {
            Player player = Bukkit.getPlayer(uuid);

            if (player == null) {
                return;
            }

            player.teleport(arena.getSpawnPointLocations()[1].clone().add(0.0, 0.5, 0.0));

            player.setHealth(player.getMaxHealth());
            player.setFoodLevel(20);

            for (PotionEffectType effectType : PotionEffectType.values()) {
                player.removePotionEffect(effectType);
            }
        }
    }

    @Override
    public void countdown() {
        setDuelStage(DuelStage.COUNTING_DOWN);
        new BukkitRunnable() {
            int countdown = 10;

            @Override
            public void run() {
                for (UUID uuid : squadOne.getAllPlayers()) {
                    Player player = Bukkit.getServer().getPlayer(uuid);
                    if (player == null) {
                        return;
                    }
                }

                for (UUID uuid : squadTwo.getAllPlayers()) {
                    Player player = Bukkit.getServer().getPlayer(uuid);
                    if (player == null) {
                        return;
                    }
                }
                countdown--;

                if (countdown <= 0) {
                    this.cancel();
                    start();
                    setDuelStage(DuelStage.FIGHTING);
                }
            }
        }.runTaskTimer(Carbyne.getInstance(), 0, 20);
    }

    @Override
    public void end(UUID winnerId) {
        Carbyne.getInstance().getDuelManager().getDuels().remove(this);
        this.getArena().setDuel(null);
        setDuelStage(DuelStage.ENDED);
    }
}
