package com.medievallords.carbyne.lootchests.commands;

import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.lootchests.LootChest;
import com.medievallords.carbyne.utils.LocationSerialization;
import com.medievallords.carbyne.utils.MessageManager;
import com.medievallords.carbyne.utils.command.BaseCommand;
import com.medievallords.carbyne.utils.command.Command;
import com.medievallords.carbyne.utils.command.CommandArgs;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.HashSet;

public class LootChestCommand extends BaseCommand {

    private Carbyne main = Carbyne.getInstance();

    @Command(name = "lootchest", permission = "carbyne.command.lootchest", inGameOnly = true)
    public void onCommand(CommandArgs cmdArgs) {
        String[] args = cmdArgs.getArgs();
        Player sender = cmdArgs.getPlayer();

        if (args.length == 0) {
            MessageManager.sendMessage(sender, new String[]
                    {
                            "&7============[ &bLootchest &7]============",
                            "&a/lootchest create [name] [lootTableName] [timeString]",
                            "&a/lootchest remove [name]",
                            "&a/lootchest list",
                            "&a/lootchest reload"
                    });
            return;
        }

        if (!sender.getWorld().getName().equalsIgnoreCase("world")) {
            MessageManager.sendMessage(sender, "&cThis command can only be used in the spawn world!");
            return;
        }

        if (args.length >= 1) {
            if (args[0].equalsIgnoreCase("create")) {
                if (!(args.length == 4)) {
                    MessageManager.sendMessage(sender, "&cUsage: /lootchest create uniqueName lootTableName respawnString");
                    return;
                }

                try {
                    String uniqueName = args[1];

                    if (getLootChestManager().findLootChestWithName(uniqueName) != null) {
                        MessageManager.sendMessage(sender, "&cThe name " + uniqueName + " is already in use!");
                        return;
                    }

                    if (!getLootChestManager().getLootTables().containsKey(args[2])) {
                        MessageManager.sendMessage(sender, "&cThere is no loot table called " + args[2] + "!");
                        return;
                    }

                    Block block = sender.getTargetBlock((HashSet<Byte>) null, 10);

                    if (block != null && (block.getType() != Material.CHEST && block.getType() != Material.TRAPPED_CHEST)) {
                        MessageManager.sendMessage(sender, "&cYou must be looking at a Chest within a ten block range.");
                        return;
                    }

                    BlockFace blockFace = block.getFace(block);

                    Location location = block.getLocation();

                    String lootTableName = args[2];
                    String respawnString = args[3];
                    int maxItems = 0;

                    getLootChestManager().getLootChests().put(location, new LootChest(getLootChestManager(), uniqueName, lootTableName, location, respawnString, maxItems, blockFace));

                    String cp = "LootChests." + uniqueName;
                    main.getLootChestFileConfiguration().set(cp + ".LootTable", lootTableName);
                    main.getLootChestFileConfiguration().set(cp + ".Location", LocationSerialization.serializeLocation(location));
                    main.getLootChestFileConfiguration().set(cp + ".RespawnTime", respawnString);
                    main.getLootChestFileConfiguration().set(cp + ".MaxItems", maxItems);
                    main.getLootChestFileConfiguration().set(cp + ".Face", blockFace.name());
                    main.getLootChestFileConfiguration().save(main.getLootChestFile());
                    MessageManager.sendMessage(sender, "&2Created Loot Chest!");
                } catch (Exception e) {
                    MessageManager.sendMessage(sender, "&cFailed to create loot chest!");
                }
            } else if (args[0].equalsIgnoreCase("remove")) {
                if (!(args.length == 2)) {
                    MessageManager.sendMessage(sender, "&cUsage: /lootchest remove uniqueName");
                    return;
                }

                LootChest chest;

                if ((chest = getLootChestManager().findLootChestWithName(args[1])) == null) {
                    MessageManager.sendMessage(sender, "&cA loot chest with this name does not exist!");
                    return;
                }

                getLootChestManager().getLootChests().remove(chest.getLocation());

                try {
                    main.getLootChestFileConfiguration().set("LootChests." + args[1] + ".LootTable", null);
                    main.getLootChestFileConfiguration().set("LootChests." + args[1] + ".RespawnTime", null);
                    main.getLootChestFileConfiguration().set("LootChests." + args[1] + ".Location", null);
                    main.getLootChestFileConfiguration().set("LootChests." + args[1] + ".Face", null);
                    main.getLootChestFileConfiguration().set("LootChests." + args[1], null);
                    main.getLootChestFileConfiguration().save(main.getLootChestFile());
                    MessageManager.sendMessage(sender, "&2Successfully removed loot chest!");
                } catch (IOException ex) {
                    MessageManager.sendMessage(sender, "&cFailed to remove loot chest from file!");
                }
            } else if (args[0].equalsIgnoreCase("reload")) {
                getLootChestManager().reload();
                MessageManager.sendMessage(sender, "&cLoots reloaded");
            } else if (args[0].equalsIgnoreCase("face")) {
                if (args.length == 2) {
                    try {
                        BlockFace face = BlockFace.valueOf(args[1]);

                        Block block = sender.getTargetBlock((HashSet<Byte>) null, 10);
                        Location location = block.getLocation();

                        if (getLootChestManager().getLootChests().containsKey(location)) {
                            LootChest lootChest = getLootChestManager().getLootChests().get(location);
                            lootChest.setFace(face);
                            main.getLootChestFileConfiguration().set("LootChests." + "DOWN" + ".Face", face.name());
                            try {
                                main.getLootChestFileConfiguration().save(main.getLootChestFile());
                            } catch (IOException ex) {
                            }
                        } else MessageManager.sendMessage(sender, "&cLocation does not contain a valid loot chest!");
                    } catch (Exception ex) {
                        MessageManager.sendMessage(sender, "&cThe BlockFace specified is invalid!");
                    }
                }
            } else if (args[0].equalsIgnoreCase("list")) {
//                if (getLootChestManager().getLootChests().size() <= 0) {
//                    MessageManager.sendMessage(sender, "&cThere are no available lootchest to display.");
//                    return;
//                }
//
//                MessageManager.sendMessage(sender, "&aAvailable LootChests:");
//
//                JSONMessage message = JSONMessage.create("");
//
//                for (int i = 0; i < getLootChestManager().getLootChests().values().size(); i++) {
//                    if (i < getLootChestManager().getLootChests().values().size() - 1) {
//                        Location loc = getLootChestManager().getLootChests().keySet().
////                        LootChest chest = getLootChestManager().getLootChests().keySet().;
//
//
//                        message.then(gate.getGateId()).color(ChatColor.AQUA)
//                                .tooltip(getMessageForLootchest(gate))
//                                .then(", ").color(ChatColor.GRAY);
//                    } else {
//                        Gate gate = getGateManager().getGates().get(i);
//
//                        message.then(gate.getGateId()).color(ChatColor.AQUA)
//                                .tooltip(getMessageForLootchest(gate));
//                        }
//                    }
//
//                    message.send(sender);
//                } else {
//                    MessageManager.sendMessage(sender, "&aAvailable Gates:");
//
//                    List<String> gateIds = new ArrayList<>();
//                    for (Gate gate : getGateManager().getGates()) {
//                        gateIds.add("&a" + gate.getGateId());
//                    }
//
//                    MessageManager.sendMessage(sender, gateIds.toString().replace("[", "").replace("]", "").replace(",", ChatColor.GRAY + ","));
//                }
            }
        }
    }

//    public JSONMessage getMessageForLootchest(LootChest chest) {
//        JSONMessage message2 = JSONMessage.create("");
//
//        message2.then(ChatColor.translateAlternateColorCodes('&', "&aGate Id: &b" + gate.getGateId()) + "\n");
//        message2.then(ChatColor.translateAlternateColorCodes('&', " &aHeartbeat Alive: &b" + (gate.getHeartbeat() != null ? gate.getHeartbeat().isAlive() : "False")) + "\n");
//        message2.then(ChatColor.translateAlternateColorCodes('&', " &aActive Length: &b" + gate.getActiveLength()) + "\n");
//        message2.then(ChatColor.translateAlternateColorCodes('&', " &aCurrent Length: &b" + gate.getCurrentLength()) + "\n");
//        message2.then(ChatColor.translateAlternateColorCodes('&', " &aIs Open: &b" + gate.isOpen()) + "\n");
//        message2.then(ChatColor.translateAlternateColorCodes('&', " &aKeeping Open: &b" + gate.isKeepOpen()) + "\n");
//        message2.then(ChatColor.translateAlternateColorCodes('&', " &aKeeping Closed: &b" + gate.isKeepClosed()) + "\n");
//        message2.then("\n");
//        message2.then(ChatColor.translateAlternateColorCodes('&', " &aPressure Plates(&b" + gate.getPressurePlateMap().keySet().size() + "&a):") + "\n");
//
//        int id = 0;
//
//        for (Location location : gate.getPressurePlateMap().keySet()) {
//            id++;
//            message2.then(ChatColor.translateAlternateColorCodes('&', "   &b" + id + "&7. &aActive: &b" + gate.getPressurePlateMap().get(location) + "&a, World: &b" + location.getWorld().getName() + "&a, X: &b" + location.getBlockX() + "&a, Y: &b" + location.getBlockY() + "&a, Z: &b" + location.getBlockZ()) + "\n");
//        }
//
//        message2.then("\n");
//        message2.then(ChatColor.translateAlternateColorCodes('&', " &aRedstone Blocks(&b" + gate.getRedstoneBlockLocations().size() + "&a):") + "\n");
//
//        id = 0;
//        for (Location location : gate.getRedstoneBlockLocations()) {
//            id++;
//            message2.then(ChatColor.translateAlternateColorCodes('&', "   &b" + id + "&7. &aType: &b" + (location.getBlock() != null ? location.getBlock().getType() : "Null" ) + "&a, World: &b" + location.getWorld().getName() + "&a, X: &b" + location.getBlockX() + "&a, Y: &b" + location.getBlockY() + "&a, Z: &b" + location.getBlockZ()) + "\n");
//        }
//
//        message2.then("\n");
//        message2.then(ChatColor.translateAlternateColorCodes('&', " &aButton(&b" + gate.getButtonLocations().size() + "&a):") + "\n");
//
//        id = 0;
//        for (Location location : gate.getButtonLocations()) {
//            id++;
//            message2.then(ChatColor.translateAlternateColorCodes('&', "   &b" + id + "&7. &aType: &b" + (location.getBlock() != null ? location.getBlock().getType() : "Null" ) + "&a, World: &b" + location.getWorld().getName() + "&a, X: &b" + location.getBlockX() + "&a, Y: &b" + location.getBlockY() + "&a, Z: &b" + location.getBlockZ()) + "\n");
//        }
//
//        if (getCarbyne().isMythicMobsEnabled()) {
//            message2.then("\n");
//
//            int spawnerCount = 0;
//
//            for (MythicSpawner spawner : gate.getMythicSpawners().values()) {
//                if (spawner != null) {
//                    spawnerCount++;
//                }
//            }
//
//            message2.then(ChatColor.translateAlternateColorCodes('&', " &aMythic Spawners(&b" + gate.getMythicSpawners().keySet().size() + "&a:&b" + spawnerCount + "&a):") + "\n");
//
//            id = 0;
//            for (String spawnerName : gate.getMythicSpawners().keySet()) {
//                id++;
//                message2.then(ChatColor.translateAlternateColorCodes('&', "   &b" + id + "&7. &aName: &b" + spawnerName + "&a, Null: &b" + (gate.getMythicSpawners().get(spawnerName) == null) + (gate.getMythicSpawners().get(spawnerName) != null ? "&a, Mob Count: &b" + gate.getMythicSpawners().get(spawnerName).getNumberOfMobs() : "")) + "\n");
//            }
//        }
//
//        return message2;
//    }
}