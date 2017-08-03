package com.medievallords.carbyne.donator.commands;

import com.medievallords.carbyne.utils.MessageManager;
import com.medievallords.carbyne.utils.command.BaseCommand;
import com.medievallords.carbyne.utils.command.Command;
import com.medievallords.carbyne.utils.command.CommandArgs;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.*;
import org.bukkit.entity.Player;

/**
 * Created by Dalton on 6/13/2017.
 */
public class GamemodeCommand extends BaseCommand {

    @Command(name = "tgm", inGameOnly = true)
    public void onCommand(CommandArgs commandArgs) {
        Player player = commandArgs.getPlayer();
        String[] args = commandArgs.getArgs();

        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("toggle") && player.hasPermission("carbyne.commands.tgm.toggle"))
                getGamemodeManager().toggleTownCreative(player);
            else
                MessageManager.sendMessage(player, "&cYou do not have permission to use this command!");
            return;
        }

        if (player.hasPermission("carbyne.commands.gamemode.ignore")) {
            getGamemodeManager().toggleGamemode(player);
            return;
        }

        if (player.hasPermission("carbyne.commands.tgm")) {
            WorldCoord wc = null;
            wc = WorldCoord.parseWorldCoord(player.getLocation());

            Town check;
            try {
                check = wc.getTownBlock().getTown();
            } catch (NotRegisteredException noTown) {
                MessageManager.sendMessage(player, "&cYou are not in your town!");
                return;
            }

            Resident res = null;
            try {
                res = TownyUniverse.getDataSource().getResident(player.getName());
            } catch (NotRegisteredException e) {
            }

            Town residentTown;
            try {
                residentTown = res.getTown();
            } catch (NotRegisteredException noTown) {
                MessageManager.sendMessage(player, "&cYou are not in your town!");
                return;
            }

            Nation nation = null;
            try {
                nation = check.getNation();
            } catch (NotRegisteredException e) {
            }

            if (residentTown.equals(check)) {
                getGamemodeManager().toggleGamemode(player);
                return;
            } else {
                try {
                    if (nation.equals(residentTown.getNation())) {
                        getGamemodeManager().toggleGamemode(player);
                        return;
                    }
                } catch (NotRegisteredException noTown) {
                    MessageManager.sendMessage(player, "&cYou are not in your town!");
                    return;
                }
            }
            MessageManager.sendMessage(player, "&cYou are not in your town!");
            return;
        } else {
            Town town;
            try {
                town = TownyUniverse.getDataSource().getResident(player.getName()).getTown();
            } catch (NotRegisteredException noTown) {
                MessageManager.sendMessage(player, "&cYou are not in your town!");
                return;
            }

            if (getGamemodeManager().getCreativeTowns().containsKey(town.getName())) {
                getGamemodeManager().toggleGamemode(player);
                return;
            }

            MessageManager.sendMessage(player, "&cYou do not have permission to use this command!");
        }
    }
}
