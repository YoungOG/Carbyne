package com.medievallords.carbyne.duels.duel.types;

import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.duels.arena.Arena;
import com.medievallords.carbyne.duels.duel.Duel;
import com.medievallords.carbyne.duels.duel.DuelStage;
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
public class RegularDuel extends Duel {

    private UUID[] participants;

    public RegularDuel(Arena arena, UUID[] participants) {
        super(arena);
        this.participants = participants;
    }

    @Override
    public void start() {
        Arena arena = getArena();

        int locationIndex = 0;
        for (UUID participant : participants) {
            Player player = Bukkit.getPlayer(participant);

            if (player == null) {
                return;
            }

            player.teleport(arena.getSpawnPointLocations()[locationIndex].clone().add(0.0, 0.5, 0.0));

            player.setHealth(player.getMaxHealth());
            player.setFoodLevel(20);

            for (PotionEffectType effectType : PotionEffectType.values()) {
                player.removePotionEffect(effectType);
            }

            locationIndex++;
        }

    }

    @Override
    public void end(UUID winnerId) {
        Carbyne.getInstance().getDuelManager().getDuels().remove(this);
        getArena().setDuel(null);
        setDuelStage(DuelStage.ENDED);
    }

    @Override
    public void countdown() {
        setDuelStage(DuelStage.COUNTING_DOWN);
        new BukkitRunnable() {
            int countdown = 10;

            @Override
            public void run() {
                for (UUID uuid : participants) {
                    Player player = Bukkit.getServer().getPlayer(uuid);
                    if (player != null) {

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

    public UUID getFirstParticipant() {
        return participants[0];
    }

    public UUID getSecondParticipant() {
        return participants[1];
    }
}
