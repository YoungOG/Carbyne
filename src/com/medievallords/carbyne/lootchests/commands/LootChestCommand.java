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

/**
 * Created by Dalton on 6/8/2017.
 */
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

                        if(getLootChestManager().getLootChests().containsKey(location)) {
                            LootChest lootChest = getLootChestManager().getLootChests().get(location);
                            lootChest.setFace(face);
                         main.getLootChestFileConfiguration().set("LootChests." + "DOWN" + ".Face", face.name());
                            try { main.getLootChestFileConfiguration().save(main.getLootChestFile()); } catch(IOException ex) {}
                        }
                        else MessageManager.sendMessage(sender, "&cLocation does not contain a valid loot chest!");
                    } catch (Exception ex) {
                        MessageManager.sendMessage(sender,"&cThe BlockFace specified is invalid!");
                    }
                }
            }
        }
    }
}
