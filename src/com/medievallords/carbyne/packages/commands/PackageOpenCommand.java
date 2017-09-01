package com.medievallords.carbyne.packages.commands;

import com.medievallords.carbyne.packages.Package;
import com.medievallords.carbyne.utils.MessageManager;
import com.medievallords.carbyne.utils.command.BaseCommand;
import com.medievallords.carbyne.utils.command.Command;
import com.medievallords.carbyne.utils.command.CommandArgs;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Created by WE on 2017-08-03.
 */
public class PackageOpenCommand extends BaseCommand {

    @Command(name = "package.open", aliases = {"p.open", "pack.open"}, inGameOnly = true)
    public void execute(CommandArgs commandArgs) {
        String[] args = commandArgs.getArgs();
        Player player = commandArgs.getPlayer();

        if (player.getItemInHand() == null || player.getItemInHand().getType() == Material.AIR) {
            MessageManager.sendMessage(player, "&cYou must be holding a package");
            return;
        }

        ItemStack hand = player.getItemInHand();

        if (hand.getItemMeta() == null || !hand.getItemMeta().hasLore()) {
            MessageManager.sendMessage(player, "&cYou must be holding a package");
            return;
        }

        Package p = Package.getPackage(hand);

        if (p == null) {
            MessageManager.sendMessage(player, "&cYou must be holding a package");
            return;
        }

        p.openPackage(player);
        if (player.getItemInHand().getAmount() < 2) {
            player.setItemInHand(null);
        } else {
            player.getItemInHand().setAmount(player.getItemInHand().getAmount() - 1);
        }
    }
}
