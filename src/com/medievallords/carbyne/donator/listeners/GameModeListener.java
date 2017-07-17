package com.medievallords.carbyne.donator.listeners;

import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.donator.GamemodeManager;
import com.medievallords.carbyne.donator.tasks.GamemodeTask;
import com.medievallords.carbyne.utils.MessageManager;
import com.palmergames.bukkit.towny.event.PlayerChangePlotEvent;
import com.palmergames.bukkit.towny.event.TownRemoveResidentEvent;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.*;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Dalton on 6/13/2017.
 */
public class GameModeListener implements Listener {

    private Carbyne main = Carbyne.getInstance();

    private GamemodeManager gamemodeManager;
    @Getter
    private HashMap<Player, Boolean> taskCheck;

    public GameModeListener(GamemodeManager gamemodeManager) {
        this.gamemodeManager = gamemodeManager;
        this.taskCheck = new HashMap<>();
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        Player player = e.getPlayer();
        if (gamemodeManager.getFlyPlayers().contains(player)) {
            player.setAllowFlight(false);
            player.setFlying(false);
            gamemodeManager.getFlyPlayers().remove(player);
        }
        if (gamemodeManager.getGmPlayers().contains(player)) {
            player.setGameMode(GameMode.SURVIVAL);
            gamemodeManager.getGmPlayers().remove(player);
        }
    }

    @EventHandler
    public void onPlotChange(PlayerChangePlotEvent e) {
        Player player = e.getPlayer();

        if (player.hasPermission("carbyne.commands.gamemode.ignore") || player.isOp())
            return;

        if (!gamemodeManager.getFlyPlayers().contains(player) && !gamemodeManager.getGmPlayers().contains(player))
            return;

        TownBlock tb = null;

        try {
            tb = e.getTo().getTownBlock();
        } catch (Exception ignore) {}

        if (tb == null) {
            taskPlayer(player);
            return;
        }

        Resident res = null;
        try {
            res = TownyUniverse.getDataSource().getResident(player.getName());
        } catch (NotRegisteredException ignore) {}

        Town town = null;
        try {
            town = tb.getTown();
        } catch (NotRegisteredException ignore) {}

        if (town == null) {
            taskPlayer(player);
            return;
        }

        try {
            if (town == res.getTown()) {
                taskCheck.put(player, true);
                return;
            }
        } catch (NotRegisteredException ignore) {}

        Nation nation = null;
        try {
            if ((nation = res.getTown().getNation()) == null)
                if (nation.equals(town.getNation())) {
                    taskCheck.put(player, true);
                    return;
                }
        } catch (NotRegisteredException ignore) {}

        taskPlayer(player);
    }

    @EventHandler
    public void onTownLeave(TownRemoveResidentEvent e) {
        Player player = Bukkit.getPlayer(e.getResident().getName());

        if (player == null) {
            OfflinePlayer offline = Bukkit.getPlayer(e.getResident().getName());

            if (offline == null) return;

            if (gamemodeManager.getFlightTowns().containsKey(offline.getUniqueId())) {
                gamemodeManager.getFlightTowns().remove(offline.getUniqueId());

                List<String> temp = main.getGamemodeTownsConfiguration().getStringList("FlightTowns");
                for (String entry : temp) {
                    String[] splitEntry = entry.split(",");
                    if (splitEntry[0].equalsIgnoreCase(offline.getUniqueId().toString())) {
                        temp.remove(entry);
                        break;
                    }
                }

                main.getGamemodeTownsConfiguration().set("FlightTowns", temp);
                try {
                    main.getGamemodeTownsConfiguration().save(main.getGamemodeTownsFile());
                } catch (IOException ignore) {
                }
            }

            if (gamemodeManager.getCreativeTowns().containsKey(offline.getUniqueId())) {
                gamemodeManager.getCreativeTowns().remove(offline.getUniqueId());
                List<String> temp = main.getGateFileConfiguration().getStringList("CreativeTowns");
                for (String entry : temp) {
                    String[] entrySplit = entry.split(",");
                    if (entrySplit[0].equalsIgnoreCase(offline.getUniqueId().toString())) {
                        temp.remove(entry);
                        break;
                    }
                }

                main.getGamemodeTownsConfiguration().set("CreativeTowns", temp);
                try {
                    main.getGamemodeTownsConfiguration().save(main.getGamemodeTownsFile());
                } catch (IOException ignore) {
                }
            }
            return;
        }

        if (gamemodeManager.getFlightTowns().containsKey(player.getUniqueId())) {
            gamemodeManager.getFlightTowns().remove(player.getUniqueId());
            MessageManager.sendMessage(player, "&cTown flight has been disabled!");

            List<String> temp = main.getGamemodeTownsConfiguration().getStringList("FlightTowns");
            for (String entry : temp) {
                String[] splitEntry = entry.split(",");
                if (splitEntry[0].equalsIgnoreCase(player.getUniqueId().toString())) {
                    temp.remove(entry);
                    break;
                }
            }

            main.getGamemodeTownsConfiguration().set("FlightTowns", temp);
            try {
                main.getGamemodeTownsConfiguration().save(main.getGamemodeTownsFile());
            } catch (IOException ignore) {}
        }

        if (gamemodeManager.getCreativeTowns().containsKey(player.getUniqueId())) {
            gamemodeManager.getCreativeTowns().remove(player.getUniqueId());
            MessageManager.sendMessage(player, "&cTown creative is disabled!");
            List<String> temp = main.getGateFileConfiguration().getStringList("CreativeTowns");
            for (String entry : temp) {
                String[] entrySplit = entry.split(",");
                if (entrySplit[0].equalsIgnoreCase(player.getUniqueId().toString())) {
                    temp.remove(entry);
                    break;
                }
            }

            main.getGamemodeTownsConfiguration().set("CreativeTowns", temp);
            try {
                main.getGamemodeTownsConfiguration().save(main.getGamemodeTownsFile());
            } catch (IOException ignore) {}
        }
    }

    private void taskPlayer(Player player) {
        if(GamemodeTask.hasActiveTasks.containsKey(player)) return;
        if (gamemodeManager.getFlyPlayers().contains(player))
            MessageManager.sendMessage(player, "&cYou have left your town, flight will be disabled in 5...");
        else
            MessageManager.sendMessage(player, "&cYou have left your town, Creative will be disabled in 5...");
        taskCheck.put(player, false);
        GamemodeTask gamemodeTask = new GamemodeTask(gamemodeManager, this, player);
        gamemodeTask.runTaskTimerAsynchronously(main, 20L, 20L);
        GamemodeTask.hasActiveTasks.put(player, gamemodeTask);
    }

}
