package com.medievallords.carbyne.spawners.commands;

import com.medievallords.carbyne.utils.ItemBuilder;
import com.medievallords.carbyne.utils.MessageManager;
import com.medievallords.carbyne.utils.command.BaseCommand;
import com.medievallords.carbyne.utils.command.Command;
import com.medievallords.carbyne.utils.command.CommandArgs;
import org.bukkit.Material;
import org.bukkit.entity.Player;

/**
 * Created by Williams on 2017-03-17.
 * for the Carbyne project.
 */
public class SpawnerCommand extends BaseCommand {

    @Command(name = "qspawner", aliases = "qs", inGameOnly = true, permission = "carbyne.spawners.admin")
    public void execute(CommandArgs commandArgs) {
        String[] args = commandArgs.getArgs();
        Player player = commandArgs.getPlayer();

        if (args.length == 0) {
            MessageManager.sendMessage(player, "&b/qspawner create <spawnerName> <mobName> <amount> <material> <group>");
        }
        if (args[0].equalsIgnoreCase("wand")) {
            player.getInventory().addItem(new ItemBuilder(Material.GOLD_AXE).name("&5&l&nWand").build());
        } else {
            MessageManager.sendMessage(player, "&b/qspawner create <spawnerName> <mobName> <amount> <material> <group>");
        }
    }
}
