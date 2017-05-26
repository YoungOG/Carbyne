package com.medievallords.carbyne.duels.duel.request;

import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.duels.arena.Arena;
import com.medievallords.carbyne.economy.account.Account;
import com.medievallords.carbyne.squads.Squad;
import com.medievallords.carbyne.utils.JSONMessage;
import com.medievallords.carbyne.utils.MessageManager;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * Created by Calvin on 3/17/2017
 * for the Carbyne project.
 */
@Getter
@Setter
public class DuelRequest {

    public static List<DuelRequest> requests = new ArrayList<>();

    private HashMap<UUID, Boolean> players = new HashMap<>();
    private HashMap<UUID, Boolean> playersSquadFight = new HashMap<>();
    private boolean squadFight = false;
    private Arena arena;
    private HashMap<UUID, Integer> bets = new HashMap<>();
    private int taskId;

    public DuelRequest(HashMap<UUID, Boolean> players, boolean squadFight, Arena arena) {
        this.players = players;
        this.squadFight = squadFight;
        this.arena = arena;
        requests.add(this);

        for (UUID uuid : players.keySet()) {
            Player player = Bukkit.getServer().getPlayer(uuid);

            if (player != null) {
                JSONMessage.create("Place a bet using: ").color(ChatColor.GREEN).suggestCommand("/duel bet <a>").tooltip("Click to use command")
                        .then("/duel bet <amount>").color(ChatColor.RED)
                        .suggestCommand("/duel bet <a>").tooltip("Click to use command").send(player);
            }
        }
        runTask();
    }

    public void request() {
        Squad one = Carbyne.getInstance().getSquadManager().getSquad(getFirstPlayer());
        Squad two = Carbyne.getInstance().getSquadManager().getSquad(getSecondPlayer());
        UUID[] uuids = new UUID[2];
        uuids[0] = getFirstPlayer();
        uuids[1] = getSecondPlayer();
        int bet = -500;

        for (UUID uuid : bets.keySet()) {
            bet += bets.get(uuid);
        }

        arena.requestDuel(squadFight, uuids, one, two, bet, bets);
        arena.getDuelists().clear();
        requests.remove(this);
    }

    public void cancel() {
        for (UUID uuid : players.keySet()) {
            Player player = Bukkit.getServer().getPlayer(uuid);

            if (player == null) {
                return;
            }

            if (Bukkit.getOfflinePlayer(uuid) != null && bets.containsKey(uuid)) {
                if (Account.getAccount(player.getUniqueId()) != null)
                    //noinspection ConstantConditions
                    Account.getAccount(player.getUniqueId()).setBalance(Account.getAccount(player.getUniqueId()).getBalance() + bets.get(player.getUniqueId()));
            }

            player.teleport(arena.getLobbyLocation());
        }

        players = null;
        arena.getDuelists().clear();
        arena = null;
        requests.remove(this);
    }

    public static DuelRequest getRequest(UUID uuid) {
        for (DuelRequest request : requests) {
            if (request.getPlayers().containsKey(uuid)) {
                return request;
            }
        }

        return null;
    }

    public void requestSquadFight() {
        Squad one = Carbyne.getInstance().getSquadManager().getSquad(getFirstPlayer());
        Squad two = Carbyne.getInstance().getSquadManager().getSquad(getSecondPlayer());

        if (one == null || two == null) {
            sendMessageToAll("&cYou can not start a squad fight since you need to be in a squad");
            return;
        }

        if (one.equals(two)) {
            sendMessageToAll("&cYou can not start a squad fight with your own squad");
        }

        if (one.getAllPlayers().size() != two.getAllPlayers().size()) {
            sendMessageToAll("&cYou can not start an unfair squad fight");
            return;
        }

        int i = 0;

        for (UUID uuid : playersSquadFight.keySet()) {
            if (playersSquadFight.get(uuid)) {
                i++;
            }
        }

        if (i >= 2) {
            sendMessageToAll("&6The duel has been set to a squad fight");
            squadFight = true;
        }
    }

    public void sendMessageToAll(String message) {
        for (UUID uuid : players.keySet()) {
            Player player = Bukkit.getServer().getPlayer(uuid);

            if (player != null) {
                MessageManager.sendMessage(player, message);
            }
        }
    }

    public UUID getFirstPlayer() {
        return (UUID) players.keySet().toArray()[0];
    }

    public UUID getSecondPlayer() {
        return (UUID) players.keySet().toArray()[1];
    }

    public void runTask() {
        taskId = Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Carbyne.getInstance(), new Runnable() {
            @Override
            public void run() {
                cancel();
            }
        }, 220);
    }

    public void cancelTask() {
        Bukkit.getServer().getScheduler().cancelTask(taskId);
    }

}
