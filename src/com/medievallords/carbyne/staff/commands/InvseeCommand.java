package com.medievallords.carbyne.staff.commands;

import com.medievallords.carbyne.utils.MessageManager;
import com.medievallords.carbyne.utils.SpecialPlayerInventory;
import com.medievallords.carbyne.utils.command.BaseCommand;
import com.medievallords.carbyne.utils.command.Command;
import com.medievallords.carbyne.utils.command.CommandArgs;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * Created by Calvin on 6/11/2017
 * for the Carbyne project.
 */
public class InvseeCommand extends BaseCommand {

    @Command(name = "invsee", permission = "carbyne.commands.invsee", inGameOnly = true)
    public void onCommand(CommandArgs commandArgs) {
        String[] args = commandArgs.getArgs();
        Player player = commandArgs.getPlayer();

        if (args.length == 1) {
            if (Bukkit.getPlayer(args[0]) == null) {
                MessageManager.sendMessage(player, "&cPlayer \"" + args[0] + "\" could not be found.");
                return;
            }

            Player target = Bukkit.getPlayer(args[0]);

            openInventory(player, target);
        }
    }

    public void openInventory(Player p, Player t) {
        if (t == null) {
            MessageManager.sendMessage(p, "&cThat player could not be found.");
            return;
        }

        SpecialPlayerInventory inv = getStaffManager().getInventories().get(t.getUniqueId());

        if (inv == null) {
            inv = new SpecialPlayerInventory(t, t.isOnline());
        }

        p.openInventory(inv.getBukkitInventory());
    }
}
