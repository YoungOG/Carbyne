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
 *
 */
public class GamemodeCommand extends BaseCommand {

    @Command(name = "tgm", inGameOnly = true)
    public void onCommand(CommandArgs commandArgs) {
        Player player = commandArgs.getPlayer();
        String[] args = commandArgs.getArgs();

        if (player.hasPermission("carbyne.commands.gamemode.ignore") && args.length == 0) {
            getGamemodeManager().toggleGamemode(player);
            return;
        }

        WorldCoord wc = WorldCoord.parseWorldCoord(player);


        Resident res;
        try {
            res = TownyUniverse.getDataSource().getResident(player.getName());
        } catch (NotRegisteredException ignore) {
            return;
        }

        Town residentsTown;
        try {
            residentsTown = res.getTown();
        } catch (NotRegisteredException residentHasNoTown) {
            MessageManager.sendMessage(player, "&cYou must have a town do to this!");
            return;
        }

        if (args.length == 1 && args[0].equalsIgnoreCase("toggle") && player.hasPermission("carbyne.commands.tgm.toggle")) {
            getGamemodeManager().toggleTownCreative(commandArgs.getPlayer());
            return;
        }

        Town check;
        try {
            check = wc.getTownBlock().getTown();
        } catch (NotRegisteredException townNotRegistered) {
            MessageManager.sendMessage(player, "&cYou are not in your town!");
            return;
        }

        if (check.equals(residentsTown)) {
            if (getGamemodeManager().getCreativeTowns().containsKey(residentsTown)) {
                getGamemodeManager().toggleGamemode(player);
                return;
            } else if (player.hasPermission("carbyne.commands.tgm")) {
                getGamemodeManager().toggleGamemode(player);
                return;
            } else {
                MessageManager.sendMessage(player, "&cYou cannot do this!");
            }
        } else {

            Nation nation = null;
            try {
                nation = check.getNation();
            } catch (NotRegisteredException ignore) {
            }

            try {
                if (nation.equals(residentsTown.getNation())) {
                    if (getGamemodeManager().getCreativeTowns().containsKey(residentsTown)) {
                        getGamemodeManager().toggleGamemode(player);
                        return;
                    } else if (player.hasPermission("carbyne.commands.tgm")) {
                        getGamemodeManager().toggleGamemode(player);
                        return;
                    } else {
                        MessageManager.sendMessage(player, "&cYou cannot do this!");
                        return;
                    }
                }
            } catch (NotRegisteredException | NullPointerException notInNationOrTown) {
                MessageManager.sendMessage(player, "&cYou are not in your town!");
                return;
            }
        }

        MessageManager.sendMessage(player, "&cYou cannot do this!");
    }
}
