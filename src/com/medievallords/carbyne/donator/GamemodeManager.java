package com.medievallords.carbyne.donator;

import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.utils.MessageManager;
import com.medievallords.carbyne.utils.StaticClasses;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.TownyUniverse;
import lombok.Getter;
import org.bukkit.GameMode;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
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

    private List<Player> flyPlayers = new ArrayList<>();
    private List<Player> gmPlayers = new ArrayList<>();
    private HashMap<Town, String> flightTowns = new HashMap<>();
    private HashMap<Town, String> creativeTowns = new HashMap<>();

    public GamemodeManager() {
        load();
    }

    private void load() {
        FileConfiguration gamemodeFileConfiguration = Carbyne.getInstance().getDonatorTownsFileConfiguration();

        List<String> temp = gamemodeFileConfiguration.getStringList("FlightTowns");
        if (temp.size() > 0)
            for (String entry : temp) {
                String[] splitEntry = entry.split(",");

                if (splitEntry.length == 2)
                    try {
                        flightTowns.put(TownyUniverse.getDataSource().getTown(splitEntry[1]), splitEntry[0]);
                    } catch (NotRegisteredException ex) {}
            }

        temp = gamemodeFileConfiguration.getStringList("CreativeTowns");
        if (temp.size() > 0)
            for (String entry : temp) {
                String[] splitEntry = entry.split(",");

                if (splitEntry.length == 2)
                    try {
                        creativeTowns.put(TownyUniverse.getDataSource().getTown(splitEntry[1]), splitEntry[0]);
                    } catch (NotRegisteredException ex) {}
            }
    }

    public void reload() {
        load();
    }

    public void toggleFlight(Player player) {
        if (!StaticClasses.gamemodeManager.getFlyPlayers().contains(player)) {
            player.setAllowFlight(true);
            player.setFlying(true);
            StaticClasses.gamemodeManager.getFlyPlayers().add(player);
            MessageManager.sendMessage(player, "&cFlight enabled!");
        } else {
            player.setAllowFlight(false);
            player.setFlying(false);
            StaticClasses.gamemodeManager.getFlyPlayers().remove(player);
            MessageManager.sendMessage(player, "&cFlight disabled!");
        }
    }

    public void toggleTownCreative(Player player) {

        Town town;
        Resident resident;
        try {
            resident = TownyUniverse.getDataSource().getResident(player.getName());
        } catch (NotRegisteredException e) {
            MessageManager.sendMessage(player, "&c An error has occurred");
            return;
        }

        try {
            town = resident.getTown();
        } catch (NotRegisteredException e) {
            MessageManager.sendMessage(player, "&c You must have a town to do this");
            return;
        }

        if (creativeTowns.containsKey(town)) {
            creativeTowns.remove(town);
            MessageManager.sendMessage(player, "&cTown creative is disabled!");
            List<String> temp = Carbyne.getInstance().getDonatorTownsFileConfiguration().getStringList("CreativeTowns");
            for (String entry : temp) {
                String[] entrySplit = entry.split(",");
                if (entrySplit[0].equalsIgnoreCase(town.getName())) {
                    temp.remove(entry);
                    break;
                }
            }

            Carbyne.getInstance().getDonatorTownsFileConfiguration().set("CreativeTowns", temp);

            try {
                Carbyne.getInstance().setDonatorTownsFileConfiguration(YamlConfiguration.loadConfiguration(Carbyne.getInstance().getDonatorTownsFile()));
                Carbyne.getInstance().getDonatorTownsFileConfiguration().save(Carbyne.getInstance().getDonatorTownsFile());
            } catch (IOException e) {}
        } else {
            Resident res = null;
            try {
                res = TownyUniverse.getDataSource().getResident(player.getName());
            } catch (NotRegisteredException e) {
                return;
            }

            Town town2 = null;
            try {
                town2 = res.getTown();
            } catch (NotRegisteredException noTown) {
                MessageManager.sendMessage(player, "&cYou do not have a town!");
                return;
            }

            creativeTowns.put(town2, player.getUniqueId().toString());
            MessageManager.sendMessage(player, "&cTown creative enabled!");
            List<String> temp = Carbyne.getInstance().getDonatorTownsFileConfiguration().getStringList("CreativeTowns");
            temp.add(town.getName() + "," + player.getUniqueId().toString());

            Carbyne.getInstance().getDonatorTownsFileConfiguration().set("CreativeTowns", temp);
            try {
                Carbyne.getInstance().setDonatorTownsFileConfiguration(YamlConfiguration.loadConfiguration(Carbyne.getInstance().getDonatorTownsFile()));
                Carbyne.getInstance().getDonatorTownsFileConfiguration().save(Carbyne.getInstance().getDonatorTownsFile());
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
        Town town;
        Resident resident;
        try {
            resident = TownyUniverse.getDataSource().getResident(player.getName());
        } catch (NotRegisteredException e) {
            MessageManager.sendMessage(player, "&c An error has occurred");
            return;
        }

        try {
            town = resident.getTown();
        } catch (NotRegisteredException e) {
            MessageManager.sendMessage(player, "&c You must have a town to do this");
            return;
        }

        if (flightTowns.containsKey(town)) {
            flightTowns.remove(town);
            MessageManager.sendMessage(player, "&cTown flight has been disabled!");

            List<String> temp = Carbyne.getInstance().getDonatorTownsFileConfiguration().getStringList("FlightTowns");
            for (String entry : temp) {
                String[] splitEntry = entry.split(",");
                if (splitEntry[0].equalsIgnoreCase(town.getName())) {
                    temp.remove(entry);
                    break;
                }
            }

            Carbyne.getInstance().getDonatorTownsFileConfiguration().set("FlightTowns", temp);
            try {
                Carbyne.getInstance().setDonatorTownsFileConfiguration(YamlConfiguration.loadConfiguration(Carbyne.getInstance().getDonatorTownsFile()));
                Carbyne.getInstance().getDonatorTownsFileConfiguration().save(Carbyne.getInstance().getDonatorTownsFile());
            } catch (IOException e) {}
        } else {

            flightTowns.put(town, player.getUniqueId().toString());
            MessageManager.sendMessage(player, "&cTown flight enabled!");

            List<String> temp = Carbyne.getInstance().getDonatorTownsFileConfiguration().getStringList("FlightTowns");
            temp.add(town.getName() + "," + player.getUniqueId().toString());

            Carbyne.getInstance().getDonatorTownsFileConfiguration().set("FlightTowns", temp);
            try {
                Carbyne.getInstance().setDonatorTownsFileConfiguration(YamlConfiguration.loadConfiguration(Carbyne.getInstance().getDonatorTownsFile()));
                Carbyne.getInstance().getDonatorTownsFileConfiguration().save(Carbyne.getInstance().getDonatorTownsFile());
            } catch (IOException e) {}
        }
    }
}