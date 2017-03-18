package com.medievallords.carbyne.spawners.commands;

import com.medievallords.carbyne.spawners.CreateSpawners;
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
public class SpawnerCreateCommand extends BaseCommand {

    @Command(name = "qspawner.create",inGameOnly = true, permission = "carbyne.spawners.admin")
    public void execute(CommandArgs commandArgs) {
        String[] args = commandArgs.getArgs();
        Player player = commandArgs.getPlayer();

        if (args.length != 5) {
            MessageManager.sendMessage(player, "&b/qspawner create <spawnerName> <mobName> <amount> <material> <group>");
            return;
        }

        if (!CreateSpawners.getPos1().containsKey(player) || !CreateSpawners.getPos2().containsKey(player)) {
            MessageManager.sendMessage(player, "You need a selection");
            return;
        }

            if(CreateSpawners.getPos1().get(player) == null || CreateSpawners.getPos2().get(player) == null){
                player.sendMessage("You need to set regions");
                return;
            }
            try {
                int amount = Integer.parseInt(args[2]);
                Material material = Material.getMaterial(args[3].toUpperCase());
                CreateSpawners.createSpawners(player, args[0], CreateSpawners.getPos1().get(player), CreateSpawners.getPos2().get(player), args[1], amount, material, args[4]);
            }catch (Exception exception){
                player.sendMessage("Something went wrong");
            }
    }
}
