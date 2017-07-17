package com.medievallords.carbyne.duels.duel.types;

import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.duels.arena.Arena;
import com.medievallords.carbyne.duels.duel.Duel;
import com.medievallords.carbyne.duels.duel.DuelStage;
import com.medievallords.carbyne.economy.account.Account;
import com.medievallords.carbyne.squads.Squad;
import com.medievallords.carbyne.utils.MessageManager;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.github.paperspigot.Title;

import java.util.Arrays;
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

        getPlayersAlive().addAll(Arrays.asList(participants));
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

            player.getActivePotionEffects().clear();

            locationIndex++;
        }

        Player one = Bukkit.getPlayer(getFirstParticipant());
        Player two = Bukkit.getPlayer(getSecondParticipant());

        Squad squadOne = Carbyne.getInstance().getSquadManager().getSquad(getFirstParticipant());
        Squad squadTwo = Carbyne.getInstance().getSquadManager().getSquad(getSecondParticipant());

        if (squadOne != null && squadTwo != null) {
            if (squadOne.equals(squadTwo)) {

                if (squadOne.getLeader().equals(one.getUniqueId())) {
                    if (squadOne.getMembers().size() > 0) {
                        squadOne.setLeader(squadOne.getMembers().get(0));
                        squadOne.getMembers().remove(squadOne.getMembers().get(0));

                        MessageManager.sendMessage(one, "&cYou have left the squad.");
                        squadOne.sendAllMembersMessage("&b" + one.getName() + " &chas left the squad.");
                        MessageManager.sendMessage(squadOne.getLeader(), "&aThe previous squad leader has left. You are the now the new squad leader.");
                    } else {
                        squadOne.disbandParty(one.getUniqueId());
                    }

                }
            }

            if (squadOne != null && squadOne.getLeader().equals(two.getUniqueId())) {
                if (squadOne.getMembers().size() > 0) {
                    squadOne.setLeader(squadOne.getMembers().get(0));
                    squadOne.getMembers().remove(squadOne.getMembers().get(0));

                    MessageManager.sendMessage(two, "&cYou have left the squad.");
                    squadOne.sendAllMembersMessage("&b" + two.getName() + " &chas left the squad.");
                    MessageManager.sendMessage(squadOne.getLeader(), "&aThe previous squad leader has left. You are the now the new squad leader.");
                } else {
                    squadOne.disbandParty(two.getUniqueId());
                }

            }
        }

        MessageManager.broadcastMessage("&6A duel has started between &b" + one.getName() + "&6 and&b " + two.getName() + "&6.");
        task();

    }

    @Override
    public void end(UUID winnerId) {
        stopTask();

        if (winnerId != null) {
            Player player = Bukkit.getServer().getPlayer(winnerId);

            if (player == null) {
                return;
            }

            player.teleport(getArena().getLobbyLocation().clone().add(0,0.2,0));
            player.setHealth(player.getMaxHealth());
            player.setFireTicks(0);
            player.getActivePotionEffects().clear();

            Account.getAccount(player.getUniqueId()).setBalance(Account.getAccount(player.getUniqueId()).getBalance() + getBets());

            if (participants[0].equals(winnerId)) {
                MessageManager.broadcastMessage("&b" + player.getName() + " &6has won a duel against &b" + Bukkit.getServer().getOfflinePlayer(participants[1]).getName());
            } else {
                MessageManager.broadcastMessage("&b" + player.getName() + " &6has won a duel against &b" + Bukkit.getServer().getOfflinePlayer(participants[0]).getName());
            }
        } else {
            for (UUID uuid : getPlayersAlive()) {
                Player player = Bukkit.getServer().getPlayer(uuid);

                if (player == null) {
                    return;
                }

                player.teleport(getArena().getLobbyLocation().clone().add(0,0.2,0));
                player.setHealth(player.getMaxHealth());
                player.setFireTicks(0);
                player.getActivePotionEffects().clear();
            }

            for (UUID uuid : participants) {
                if (getPlayerBets().containsKey(uuid)) {
                    Account.getAccount(uuid).setBalance(Account.getAccount(uuid).getBalance() + getBets());
                }
            }
        }

        for (Item item : getDrops()) {
            if (item == null)
                continue;

            item.remove();
        }

        getDrops().clear();

        Carbyne.getInstance().getDuelManager().getDuels().remove(this);
        getArena().setDuel(null);
        setDuelStage(DuelStage.ENDED);
    }

    @Override
    public void countdown() {
        setDuelStage(DuelStage.COUNTING_DOWN);
        new BukkitRunnable() {
            int countdown = 5;

            @Override
            public void run() {
                for (UUID uuid : participants) {
                    Player player = Bukkit.getServer().getPlayer(uuid);

                    if (player != null) {
                        player.sendTitle(new Title.Builder().title(ChatColor.translateAlternateColorCodes('&', "&c" + countdown)).stay(20).build());
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
    public void check() {
        if (getPlayersAlive().size() == 1) {
            setEnded(true);
            Player p = Bukkit.getPlayer(getPlayersAlive().get(0));
            p.setFireTicks(0);
            p.setHealth(p.getMaxHealth());
            new BukkitRunnable() {
                @Override
                public void run() {
                    end(getPlayersAlive().get(0));
                }
            }.runTaskLater(Carbyne.getInstance(), 300);
        } else if (getPlayersAlive().isEmpty()) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    end(null);
                }
            }.runTaskLater(Carbyne.getInstance(), 300);
        }
    }

    public UUID getFirstParticipant() {
        return participants[0];
    }

    public UUID getSecondParticipant() {
        return participants[1];
    }

    @Override
    public void task() {
        this.taskId = Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Carbyne.getInstance(), new Runnable() {
            @Override
            public void run() {
                end(null);
            }
        }, 800 * 20);
    }

    @Override
    public void stopTask() {
        Bukkit.getServer().getScheduler().cancelTask(this.taskId);
    }
}
