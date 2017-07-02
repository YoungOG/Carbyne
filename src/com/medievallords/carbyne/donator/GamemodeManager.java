package com.medievallords.carbyne.donator;

import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.utils.MessageManager;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.TownyUniverse;
import lombok.Getter;
import org.bukkit.GameMode;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Dalton on 6/13/2017.
 * From Medieval Core plugin.
 */
@Getter
public class GamemodeManager {

    private Carbyne main = Carbyne.getInstance();

    private List<Player> flyPlayers = new ArrayList<>();
    private List<Player> gmPlayers = new ArrayList<>();
    private HashMap<String, Town> flightTowns = new HashMap<>();
    private HashMap<String, Town> creativeTowns = new HashMap<>();

    public GamemodeManager() {
        load();
    }

    private void load() {
        FileConfiguration gamemodeFileConfiguration = main.getGamemodeTownsConfiguration();

        List<String> temp = gamemodeFileConfiguration.getStringList("FlightTowns");
        if (temp.size() > 0)
            for (String entry : temp) {
                String[] splitEntry = entry.split(",");

                if (splitEntry.length == 2)
                    try {
                        flightTowns.put(splitEntry[0], TownyUniverse.getDataSource().getTown(splitEntry[1]));
                    } catch (NotRegisteredException ex) {}
            }

        temp = gamemodeFileConfiguration.getStringList("CreativeTowns");
        if (temp.size() > 0)
            for (String entry : temp) {
                String[] splitEntry = entry.split(",");

                if (splitEntry.length == 2)
                    try {
                        creativeTowns.put(splitEntry[0], TownyUniverse.getDataSource().getTown(splitEntry[1]));
                    } catch (NotRegisteredException ex) {}
            }
    }

    public void reload() {
        load();
    }

    public void toggleFlight(Player player) {
        if (!main.getGamemodeManager().getFlyPlayers().contains(player)) {
            player.setAllowFlight(true);
            player.setFlying(true);
            main.getGamemodeManager().getFlyPlayers().add(player);
            MessageManager.sendMessage(player, "&cFlight enabled!");
        } else {
            player.setAllowFlight(false);
            player.setFlying(false);
            main.getGamemodeManager().getFlyPlayers().remove(player);
            MessageManager.sendMessage(player, "&cFlight disabled!");
        }
    }

    public void toggleTownCreative(Player player) {
        if (creativeTowns.containsKey(player.getUniqueId().toString())) {
            creativeTowns.remove(player.getUniqueId().toString());
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
            } catch (IOException e) {}
        } else {
            Resident res = null;
            try {
                res = TownyUniverse.getDataSource().getResident(player.getName());
            } catch (NotRegisteredException e) {
                return;
            }

            Town town = null;
            try {
                town = res.getTown();
            } catch (NotRegisteredException noTown) {
                MessageManager.sendMessage(player, "&cYou do not have a town!");
                return;
            }

            creativeTowns.put(player.getUniqueId().toString(), town);
            MessageManager.sendMessage(player, "&cTown creative enabled!");
            List<String> temp = main.getGateFileConfiguration().getStringList("CreativeTowns");
            temp.add(player.getUniqueId().toString() + "," + town.getName().toString());

            main.getGamemodeTownsConfiguration().set("CreativeTowns", temp);
            try {
                main.getGamemodeTownsConfiguration().save(main.getGamemodeTownsFile());
            } catch (IOException e) {
            }
        }
    }

    public void toggleGamemode(Player player) {
        if (!gmPlayers.contains(player)) {
            player.setGameMode(GameMode.CREATIVE);
            gmPlayers.add(player);
            MessageManager.sendMessage(player, "&cCreative enabled!");
        } else {
            player.setGameMode(GameMode.SURVIVAL);
            gmPlayers.remove(player);
            MessageManager.sendMessage(player, "&cCreative disabled!");
        }
    }

    public void toggleTownFlight(Player player) {
        if (flightTowns.containsKey(player.getUniqueId().toString())) {
            flightTowns.remove(player.getUniqueId().toString());
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
            } catch (IOException e) {}
        } else {
            Town town;

            try {
                town = TownyUniverse.getDataSource().getResident(player.getName()).getTown();
            } catch (NotRegisteredException noTown) {
                MessageManager.sendMessage(player, "&cYou need a town to use this command!");
                return;
            }

            flightTowns.put(player.getUniqueId().toString(), town);
            MessageManager.sendMessage(player, "&cTown flight enabled!");

            List<String> temp = main.getGamemodeTownsConfiguration().getStringList("FlightTowns");
            temp.add(player.getUniqueId().toString() + "," + town.getName());

            main.getGamemodeTownsConfiguration().set("FlightTowns", temp);
            try {
                main.getGamemodeTownsConfiguration().save(main.getGamemodeTownsFile());
            } catch (IOException e) {}
        }
    }
}