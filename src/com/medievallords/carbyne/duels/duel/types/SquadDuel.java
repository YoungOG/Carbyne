package com.medievallords.carbyne.duels.duel.types;

import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.duels.arena.Arena;
import com.medievallords.carbyne.duels.duel.Duel;
import com.medievallords.carbyne.duels.duel.DuelStage;
import com.medievallords.carbyne.economy.objects.Account;
import com.medievallords.carbyne.squads.Squad;
import com.medievallords.carbyne.utils.MessageManager;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.github.paperspigot.Title;

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

        getPlayersAlive().addAll(squadOne.getAllPlayers());
        getPlayersAlive().addAll(squadTwo.getAllPlayers());
    }

    @Override
    public void start() {
        Arena arena = getArena();

        for (UUID uuid : squadOne.getAllPlayers()) {
            Player player = Bukkit.getPlayer(uuid);

            if (player == null) {
                continue;
            }

            player.playSound(player.getLocation(), Sound.NOTE_PLING, 1, 1);
            player.sendTitle(new Title.Builder().title(ChatColor.translateAlternateColorCodes('&', "&cGO!")).stay(7).build());
            player.teleport(arena.getSpawnPointLocations()[0].clone().add(0.0, 0.5, 0.0));

            player.setHealth(player.getMaxHealth());
            player.setFoodLevel(20);

            player.getActivePotionEffects().clear();
        }

        for (UUID uuid : squadTwo.getAllPlayers()) {
            Player player = Bukkit.getPlayer(uuid);

            if (player == null) {
                continue;
            }

            player.playSound(player.getLocation(), Sound.NOTE_PLING, 1, 1);
            player.sendTitle(new Title.Builder().title(ChatColor.translateAlternateColorCodes('&', "&cGO!")).stay(7).build());
            player.teleport(arena.getSpawnPointLocations()[1].clone().add(0.0, 0.5, 0.0));

            player.setHealth(player.getMaxHealth());
            player.setFoodLevel(20);

            player.getActivePotionEffects().clear();
        }

        task();
        MessageManager.broadcastMessage("&6A squad fight duel has started between &b" + Bukkit.getServer().getPlayer(squadOne.getLeader()).getName() + "'s squad &6and &b" + Bukkit.getServer().getPlayer(squadTwo.getLeader()).getName() + "'s squad");
    }

    @Override
    public void countdown() {
        setDuelStage(DuelStage.COUNTING_DOWN);
        new BukkitRunnable() {
            int countdown = 5;

            @Override
            public void run() {
                if (countdown <= 0) {
                    this.cancel();
                    start();
                    setDuelStage(DuelStage.FIGHTING);
                } else {
                    for (UUID uuid : squadOne.getAllPlayers()) {
                        Player player = Bukkit.getServer().getPlayer(uuid);

                        if (player != null) {
                            player.playSound(player.getLocation(), Sound.NOTE_PLING, 1, 1);
                            player.sendTitle(new Title.Builder().title(ChatColor.translateAlternateColorCodes('&', "&c" + countdown)).stay(20).build());
                        }
                    }

                    for (UUID uuid : squadTwo.getAllPlayers()) {
                        Player player = Bukkit.getServer().getPlayer(uuid);

                        if (player != null) {
                            player.playSound(player.getLocation(), Sound.NOTE_PLING, 1, 1);
                            player.sendTitle(new Title.Builder().title(ChatColor.translateAlternateColorCodes('&', "&c" + countdown)).stay(20).build());
                        }
                    }
                    countdown--;
                }
            }
        }.runTaskTimer(Carbyne.getInstance(), 0, 20);
    }

    @Override
    public void end(UUID winnerId) {
        stopTask();

        if (winnerId != null) {
            Player player = Bukkit.getServer().getPlayer(winnerId);

            if (player != null) {
                player.teleport(getArena().getLobbyLocation().clone().add(0,0.2,0));
                player.setHealth(player.getMaxHealth());
                player.setFireTicks(0);

                Account.getAccount(winnerId).setBalance(Account.getAccount(winnerId).getBalance() + getBets());

                if (squadTwo.getAllPlayers().contains(winnerId)) {
                    MessageManager.broadcastMessage("&b" + player.getName() + "'s squad &6has won a duel against &b" + Bukkit.getServer().getOfflinePlayer(squadOne.getLeader()).getName() + "'s squad");
                } else {
                    MessageManager.broadcastMessage("&b" + player.getName() + "'s squad &6has won a duel against &b" + Bukkit.getServer().getOfflinePlayer(squadTwo.getLeader()).getName() + "'s squad");
                }
            }
        }

        if (winnerId == null){
            for (UUID uuid : getSquadOne().getAllPlayers()) {
                if (getPlayerBets().containsKey(uuid)) {
                    Account.getAccount(uuid).setBalance(Account.getAccount(uuid).getBalance() + getPlayerBets().get(uuid));
                }
            }

            for (UUID uuid : getSquadTwo().getAllPlayers()) {
                if (getPlayerBets().containsKey(uuid)) {
                    Account.getAccount(uuid).setBalance(Account.getAccount(uuid).getBalance() + getPlayerBets().get(uuid));
                }
            }
        }

        for (UUID uuid : getPlayersAlive()) {
            if (winnerId != null && uuid.equals(winnerId)) {
                continue;
            }

            Player player = Bukkit.getServer().getPlayer(uuid);

            if (player == null) {
                continue;
            }

            player.teleport(getArena().getLobbyLocation().clone().add(0,0.2,0));
            player.setHealth(player.getMaxHealth());
            player.setFireTicks(0);
        }

        for (Item item : getDrops()) {
            if (item == null)
                continue;

            item.remove();
        }
        getDrops().clear();

        Carbyne.getInstance().getDuelManager().getDuels().remove(this);
        this.getArena().setDuel(null);
        setDuelStage(DuelStage.ENDED);
    }

    @Override
    public void check() {
        int o = squadOne.getAllPlayers().size();

        if (getPlayersAlive().isEmpty()) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    end(null);
                }
            }.runTaskLater(Carbyne.getInstance(), 300);
        }

        for (UUID uuid : squadOne.getAllPlayers()) {
            if (!getPlayersAlive().contains(uuid)) {
                o--;
            }
        }

        int t = squadTwo.getAllPlayers().size();

        for (UUID uuid : squadTwo.getAllPlayers()) {
            if (!getPlayersAlive().contains(uuid)) {
                t--;
            }
        }

        if (t <= 0) {
            for (UUID uuid : squadOne.getAllPlayers()) {
                if (getPlayersAlive().contains(uuid)) {
                    setEnded(true);
                    for (UUID u : getPlayersAlive()) {
                        Player p = Bukkit.getPlayer(u);
                        p.setFireTicks(0);
                        p.setHealth(p.getMaxHealth());
                    }

                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            end(uuid);
                        }
                    }.runTaskLater(Carbyne.getInstance(), 300);
                    break;
                }
            }

            return;
        }

        if (o <= 0) {
            for (UUID uuid : squadTwo.getAllPlayers()) {
                if (getPlayersAlive().contains(uuid)) {
                    setEnded(true);
                    for (UUID u : getPlayersAlive()) {
                        Player p = Bukkit.getPlayer(u);
                        p.setFireTicks(0);
                        p.setHealth(p.getMaxHealth());
                    }

                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            end(uuid);
                        }
                    }.runTaskLater(Carbyne.getInstance(), 300);
                    break;
                }
            }
        }
    }

    @Override
    public void task() {
        this.taskId = Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Carbyne.getInstance(), () -> end(null), 1450 * 20);
    }

    @Override
    public void stopTask() {
        Bukkit.getServer().getScheduler().cancelTask(this.taskId);
    }
}
