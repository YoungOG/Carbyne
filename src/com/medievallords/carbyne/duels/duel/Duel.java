package com.medievallords.carbyne.duels.duel;

import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.duels.arena.Arena;
import com.medievallords.carbyne.utils.MessageManager;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by xwiena22 on 2017-03-14
 * for the Carbyne project.
 */
@Getter
@Setter
public class Duel {

    private static Carbyne main = Carbyne.getInstance();

    private static final int COUNTDOWN_DURATION = 5;

    private Arena arena;
    private UUID[] participants;
    private DuelType duelType;
    private DuelStage duelStage;
    private List<Item> drops;
    private long startTimeMillis;

    public Duel(Arena arena, UUID[] participants, DuelType duelType) {
        this.arena = arena;
        this.participants = participants;
        this.duelType = duelType;
        this.duelStage = DuelStage.COUNTING_DOWN;
        this.drops = new ArrayList<>();

        int locationIndex = 0;

        for (UUID participant : participants) {
            Player player = Bukkit.getPlayer(participant);

            if (player == null) {
                return;
            }

            player.teleport(arena.getSpawnPointLocation()[locationIndex].clone().add(0.0, 0.5, 0.0));

            player.setHealth(player.getMaxHealth());
            player.setFoodLevel(20);

            for (PotionEffectType effectType : PotionEffectType.values()) {
                player.removePotionEffect(effectType);
            }

            locationIndex++;
        }

        for (int i = 0; i <= COUNTDOWN_DURATION; i++) {
            int countdown = i;

            new BukkitRunnable() {
                @Override
                public void run() {
                    for (UUID participant : participants) {
                        if (countdown == COUNTDOWN_DURATION) {
                            MessageManager.sendMessage(participant, "Countdown Ended Seconds: " + (COUNTDOWN_DURATION - countdown));
                        } else {
                            MessageManager.sendMessage(participant, "Seconds: " + (COUNTDOWN_DURATION - countdown));
                        }
                    }

                    if (countdown == COUNTDOWN_DURATION) {
                        duelStage = DuelStage.FIGHTING;
                        startTimeMillis = System.currentTimeMillis();
                    }
                }
            }.runTaskLaterAsynchronously(main, 20 * i);
        }
    }

    public void end(UUID winnerId) {
        duelStage = DuelStage.ENDED;

        new BukkitRunnable() {
            @Override
            public void run() {
                Player winner = Bukkit.getPlayer(winnerId);

                if (winner != null) {
                    winner.teleport(arena.getLobbyLocation());
                }

                for (Item drop : drops) {
                    drop.remove();
                }

                drops.clear();
            }
        }.runTaskLater(main, 30 * 20L);
    }

    public UUID getOpponent(Player player) {
        return getOpponent(player.getUniqueId());
    }

    public UUID getOpponent(UUID uniqueId) {
        if (getFirstParticipant().equals(uniqueId)) {
            return getSecondParticipant();
        } else if (getSecondParticipant().equals(uniqueId)) {
            return getFirstParticipant();
        } else {
            return null;
        }
    }

    public UUID getFirstParticipant() { return participants[0]; }

    public UUID getSecondParticipant() { return participants[1]; }
}
