package com.medievallords.carbyne.dungeons.dungeons.commands;


import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.dungeons.dungeons.Dungeon;
import com.medievallords.carbyne.utils.LocationSerialization;
import com.medievallords.carbyne.utils.MessageManager;
import com.medievallords.carbyne.utils.StaticClasses;
import com.medievallords.carbyne.utils.command.BaseCommand;
import com.medievallords.carbyne.utils.command.Command;
import com.medievallords.carbyne.utils.command.CommandArgs;
import io.lumine.xikage.mythicmobs.MythicMobs;
import io.lumine.xikage.mythicmobs.mobs.MythicMob;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by WE on 2017-09-28.
 */

public class DungeonSetCommand extends BaseCommand {

    @Command(name = "dungeon.set", permission = "dungeon.command.admin", inGameOnly = true)
    public void execute(CommandArgs commandArgs) {
        Player player = commandArgs.getPlayer();
        String[] args = commandArgs.getArgs();

        if (args.length == 3) {
            Dungeon dungeon = StaticClasses.dungeonHandler.getDungeon(args[0]);
            if (dungeon == null) {
                MessageManager.sendMessage(player, "&cCould not find a dungeon with that name");
                return;
            }

            if (args[1].equalsIgnoreCase("location")) {
                Location target = player.getTargetBlock((Set<Material>) null, 10).getLocation();
                ConfigurationSection section = Carbyne.getInstance().getDungeonsFileConfiguration().getConfigurationSection("Dungeons." + dungeon.getName());
                switch (args[2].toLowerCase()) {
                    case "spawn":
                        section.set("SpawnLocation", LocationSerialization.serializeLocation(target));
                        dungeon.setSpawnLocation(target);
                        MessageManager.sendMessage(player, "&6The &cspawn &6location has  has been set to where you're looking");
                        break;
                    case "lobby":
                        section.set("LobbyLocation", LocationSerialization.serializeLocation(target));
                        dungeon.setLobbyLocation(target);
                        MessageManager.sendMessage(player, "&6The &clobby &6location has has been set to where you're looking");
                        break;
                    case "complete":
                        section.set("CompleteLocation", LocationSerialization.serializeLocation(target));
                        dungeon.setCompleteLocation(target);
                        MessageManager.sendMessage(player, "&6The &ccomplete &6location has has been set to where you're looking");
                        break;
                    case "ready":
                        section.set("ReadyLocation", LocationSerialization.serializeLocation(target));
                        dungeon.setReadyLocation(target);
                        MessageManager.sendMessage(player, "&6The &cready &6location has been set to where you're looking");
                        break;
                }

                try {
                    Carbyne.getInstance().getDungeonsFileConfiguration().save(Carbyne.getInstance().getDungeonsFile());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (args[1].equalsIgnoreCase("world")) {
                World world = Bukkit.getWorld(args[2]);
                if (world == null) {
                    MessageManager.sendMessage(player, "&cCould not find a world with that name");
                    return;
                }

                dungeon.setWorld(world);
                MessageManager.sendMessage(player, "&6World &b" + args[2] + "&6 has been set");
                ConfigurationSection section = Carbyne.getInstance().getDungeonsFileConfiguration().getConfigurationSection("Dungeons." + dungeon.getName());
                section.set("CGWorld", world.getName());

                try {
                    Carbyne.getInstance().getDungeonsFileConfiguration().save(Carbyne.getInstance().getDungeonsFile());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else {
            MessageManager.sendMessage(player, "&cUsage: &7/dungeon set <dungeon> location|world <locationName|worldName>");
        }
    }

    @Command(name = "dungeon.addspawner", inGameOnly = true, permission = "dungeon.command.admin")
    public void onA(CommandArgs commandArgs) {
        Player player = commandArgs.getPlayer();
        String[] args = commandArgs.getArgs();

        if (args.length != 2) {
            MessageManager.sendMessage(player, "&cUsage: &7/dungeon addspawner <dungeon> <mob>");
            return;
        }

        Dungeon dungeon = StaticClasses.dungeonHandler.getDungeon(args[0]);
        if (dungeon == null) {
            MessageManager.sendMessage(player, "&cCould not find a dungeon with that name");
            return;
        }

        MythicMob mob = MythicMobs.inst().getMobManager().getMythicMob(args[1]);
        if (mob == null) {
            MessageManager.sendMessage(player, "&cCould not find a mob with that name");
            return;
        }

        Location target = player.getTargetBlock((Set<Material>) null, 10).getLocation();
        dungeon.getSpawners().put(target, mob);

        List<String> ser = new ArrayList<>();
        for (Location location : dungeon.getSpawners().keySet()) {
            ser.add(LocationSerialization.serializeLocation(location) + "," + dungeon.getSpawners().get(location).getInternalName());
        }

        ConfigurationSection section = Carbyne.getInstance().getDungeonsFileConfiguration().getConfigurationSection("Dungeons." + dungeon.getName());
        section.set("Spawners", ser);

        try {
            Carbyne.getInstance().getDungeonsFileConfiguration().save(Carbyne.getInstance().getDungeonsFile());
        } catch (IOException e) {
            e.printStackTrace();
        }

        MessageManager.sendMessage(player, "&cMob has been added to target location.");
    }

    @Command(name = "dungeon.removespawner", inGameOnly = true, permission = "dungeon.command.admin")
    public void onB(CommandArgs commandArgs) {
        Player player = commandArgs.getPlayer();
        String[] args = commandArgs.getArgs();

        if (args.length != 1) {
            MessageManager.sendMessage(player, "&cUsage: &7/dungeon removespawner <dungeon>");
            return;
        }

        Dungeon dungeon = StaticClasses.dungeonHandler.getDungeon(args[0]);
        if (dungeon == null) {
            MessageManager.sendMessage(player, "&cCould not find a dungeon with that name");
            return;
        }

        Location target = player.getTargetBlock((Set<Material>) null, 10).getLocation();
        if (!dungeon.getSpawners().containsKey(target)) {
            MessageManager.sendMessage(player, "&cThat dungeon does not have that spawner.");
            return;
        }

        dungeon.getSpawners().remove(target);

        List<String> ser = new ArrayList<>();
        for (Location location : dungeon.getSpawners().keySet()) {
            ser.add(LocationSerialization.serializeLocation(location) + "," + dungeon.getSpawners().get(location).getInternalName());
        }

        ConfigurationSection section = Carbyne.getInstance().getDungeonsFileConfiguration().getConfigurationSection("Dungeons." + dungeon.getName());
        section.set("Spawners", ser);

        try {
            Carbyne.getInstance().getDungeonsFileConfiguration().save(Carbyne.getInstance().getDungeonsFile());
        } catch (IOException e) {
            e.printStackTrace();
        }

        MessageManager.sendMessage(player, "&cSpawner has been removed from dungeon.");
    }

    @Command(name = "dungeon.setjoin", inGameOnly = true, permission = "dungeon.command.admin")
    public void onC(CommandArgs commandArgs) {
        Player player = commandArgs.getPlayer();
        String[] args = commandArgs.getArgs();

        if (args.length != 2) {
            MessageManager.sendMessage(player, "&cUsage: &7/dungeon setjoin <dungeon> <amount>");
            return;
        }

        Dungeon dungeon = StaticClasses.dungeonHandler.getDungeon(args[0]);
        if (dungeon == null) {
            MessageManager.sendMessage(player, "&cCould not find a dungeon with that name");
            return;
        }

        int amount = 10;
        try {
            amount = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            MessageManager.sendMessage(player, "&cAmount must be a number.");
            return;
        }

        dungeon.setMinJoin(amount);
        ConfigurationSection section = Carbyne.getInstance().getDungeonsFileConfiguration().getConfigurationSection("Dungeons." + dungeon.getName());
        section.set("MinJoin", amount);

        try {
            Carbyne.getInstance().getDungeonsFileConfiguration().save(Carbyne.getInstance().getDungeonsFile());
        } catch (IOException e) {
            e.printStackTrace();
        }

        MessageManager.sendMessage(player, "&cMinimum allowed players has been set to &c" + amount + "&c.");
    }

    @Command(name = "dungeon.addjoin", inGameOnly = true, permission = "dungeon.command.admin")
    public void a(CommandArgs commandArgs) {
        Player player = commandArgs.getPlayer();
        String[] args = commandArgs.getArgs();

        if (args.length != 1) {
            MessageManager.sendMessage(player, "&cUsage: &7/dungeon addjoin <dungeon>");
            return;
        }

        Dungeon dungeon = StaticClasses.dungeonHandler.getDungeon(args[0]);
        if (dungeon == null) {
            MessageManager.sendMessage(player, "&cCould not find a dungeon with that name");
            return;
        }

        Block block = player.getTargetBlock((Set<Material>) null, 10);
        if (!block.getType().toString().contains("PLATE")) {
            MessageManager.sendMessage(player, "&cYou must be looking at a pressure-plate.");
            return;
        }

        dungeon.getJoinLocations().put(block.getLocation(), null);
        ConfigurationSection section = Carbyne.getInstance().getDungeonsFileConfiguration().getConfigurationSection("Dungeons." + dungeon.getName());
        List<String> ser2 = new ArrayList<>();
        for (Location location : dungeon.getJoinLocations().keySet()) {
            ser2.add(LocationSerialization.serializeLocation(location));
        }

        section.set("JoiningLocations", ser2);

        try {
            Carbyne.getInstance().getDungeonsFileConfiguration().save(Carbyne.getInstance().getDungeonsFile());
        } catch (IOException e) {
            e.printStackTrace();
        }

        MessageManager.sendMessage(player, "&aPressure plate location has been added.");
    }

    @Command(name = "dungeon.removejoin", inGameOnly = true, permission = "dungeon.command.admin")
    public void b(CommandArgs commandArgs) {
        Player player = commandArgs.getPlayer();
        String[] args = commandArgs.getArgs();

        if (args.length != 1) {
            MessageManager.sendMessage(player, "&cUsage: &7/dungeon removejoin <dungeon>");
            return;
        }

        Dungeon dungeon = StaticClasses.dungeonHandler.getDungeon(args[0]);
        if (dungeon == null) {
            MessageManager.sendMessage(player, "&cCould not find a dungeon with that name");
            return;
        }

        Block block = player.getTargetBlock((Set<Material>) null, 10);
        if (!block.getType().toString().contains("PLATE")) {
            MessageManager.sendMessage(player, "&cYou must be looking at a pressure-plate.");
            return;
        }

        if (!dungeon.getJoinLocations().containsKey(block.getLocation())) {
            MessageManager.sendMessage(player, "&cThat dungeon does not have that location added.");
            return;
        }

        dungeon.getJoinLocations().remove(block.getLocation());
        ConfigurationSection section = Carbyne.getInstance().getDungeonsFileConfiguration().getConfigurationSection("Dungeons." + dungeon.getName());
        List<String> ser2 = new ArrayList<>();
        for (Location location : dungeon.getJoinLocations().keySet()) {
            ser2.add(LocationSerialization.serializeLocation(location));
        }

        section.set("JoiningLocations", ser2);

        try {
            Carbyne.getInstance().getDungeonsFileConfiguration().save(Carbyne.getInstance().getDungeonsFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
        MessageManager.sendMessage(player, "&aPressure plate location has been remove.");
    }

    @Command(name = "dungeon.setitem", inGameOnly = true, permission = "dungeon.command.admin")
    public void d(CommandArgs commandArgs) {
        Player player = commandArgs.getPlayer();
        String[] args = commandArgs.getArgs();

        if (args.length != 2) {
            MessageManager.sendMessage(player, "&cUsage: &7/dungeon setitem <dungeon> <(mythicItem|material):(amount)>");
            return;
        }

        Dungeon dungeon = StaticClasses.dungeonHandler.getDungeon(args[0]);
        if (dungeon == null) {
            MessageManager.sendMessage(player, "&cCould not find a dungeon with that name");
            return;
        }

        Block block = player.getTargetBlock((Set<Material>) null, 10);
        if (!block.getType().toString().contains("CHEST")) {
            MessageManager.sendMessage(player, "&cYou must be looking at a chest.");
            return;
        }

        if (args[1].equalsIgnoreCase("null")) {
            dungeon.getLootChests().remove(block.getLocation());
            ConfigurationSection section = Carbyne.getInstance().getDungeonsFileConfiguration().getConfigurationSection("Dungeons." + dungeon.getName());
            List<String> ser2 = new ArrayList<>();
            for (Location location : dungeon.getLootChests().keySet()) {
                ser2.add(LocationSerialization.serializeLocation(location) + "," + dungeon.getLootChests().get(location));
            }

            section.set("Items", ser2);

            try {
                Carbyne.getInstance().getDungeonsFileConfiguration().save(Carbyne.getInstance().getDungeonsFile());
            } catch (IOException e) {
                e.printStackTrace();
            }
            MessageManager.sendMessage(player, "&cChests item has been cleared.");
            return;
        }

        dungeon.getLootChests().put(block.getLocation(), args[1]);
        ConfigurationSection section = Carbyne.getInstance().getDungeonsFileConfiguration().getConfigurationSection("Dungeons." + dungeon.getName());
        List<String> ser2 = new ArrayList<>();
        for (Location location : dungeon.getLootChests().keySet()) {
            ser2.add(LocationSerialization.serializeLocation(location) + "," + dungeon.getLootChests().get(location));
        }

        section.set("Items", ser2);

        try {
            Carbyne.getInstance().getDungeonsFileConfiguration().save(Carbyne.getInstance().getDungeonsFile());
        } catch (IOException e) {
            e.printStackTrace();
        }

        MessageManager.sendMessage(player, "&cChests item has been added.");
    }
}
