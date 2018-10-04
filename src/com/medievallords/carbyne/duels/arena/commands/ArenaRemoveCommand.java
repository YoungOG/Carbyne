package com.medievallords.carbyne.duels.arena.commands;

import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.duels.arena.Arena;
import com.medievallords.carbyne.utils.MessageManager;
import com.medievallords.carbyne.utils.StaticClasses;
import com.medievallords.carbyne.utils.command.BaseCommand;
import com.medievallords.carbyne.utils.command.Command;
import com.medievallords.carbyne.utils.command.CommandArgs;
import org.bukkit.entity.Player;

import java.io.IOException;

/**
 * Created by Calvin on 3/17/2017
 * for the Carbyne project.
 */
public class ArenaRemoveCommand extends BaseCommand {

//    @Command(name = "arena.remove", aliases = {"arena.delete", "arena.del"}, permission = "carbyne.commands.arena")
//    public void onCommand(CommandArgs commandArgs) {
//        String[] args = commandArgs.getArgs();
//        Player player = commandArgs.getPlayer();
//
//        if (args.length != 1) {
//            MessageManager.sendMessage(player, "&cUsage: /arena");
//            return;
//        }
//
//        Arena arena = StaticClasses.duelManager.getArena(args[0]);
//
//        if (arena == null) {
//            MessageManager.sendMessage(player, "&cCould not find an arena with the ID \'" + args[0] + "\'.");
//            return;
//        }
//
//        StaticClasses.duelManager.getArenas().remove(arena);
//
//        if (Carbyne.getInstance().getArenasFileConfiguration().getConfigurationSection("Arenas").contains(arena.getArenaId())) {
//            Carbyne.getInstance().getArenasFileConfiguration().getConfigurationSection("Arenas").set(arena.getArenaId(), null);
//            
//            try {
//                Carbyne.getInstance().getArenasFileConfiguration().save(Carbyne.getInstance().getArenasFile());
//            } catch (IOException e) {
//                MessageManager.sendMessage(player, "&cFailed to save the arenas.yml");
//                e.printStackTrace();
//            }
//        }
//
//        MessageManager.sendMessage(player, "&aYou have removed an arena with the ID \'&b" + arena.getArenaId() + "&a\'.");
//    }
}
