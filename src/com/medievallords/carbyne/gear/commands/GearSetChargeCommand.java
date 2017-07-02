package com.medievallords.carbyne.gear.commands;

import com.medievallords.carbyne.gear.types.carbyne.CarbyneWeapon;
import com.medievallords.carbyne.utils.MessageManager;
import com.medievallords.carbyne.utils.command.BaseCommand;
import com.medievallords.carbyne.utils.command.Command;
import com.medievallords.carbyne.utils.command.CommandArgs;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Created by WE on 2017-03-29.
 */
public class GearSetChargeCommand extends BaseCommand {

    @Command(name = "charge", aliases = {"cgc"}, permission = "carbyne.gear.administrator")
    public void execute(CommandArgs commandArgs) {
        String[] args = commandArgs.getArgs();
        CommandSender sender = commandArgs.getSender();

        if (args.length != 2) {
            MessageManager.sendMessage(sender, "&cUsage: /charge <player> <amount>");
            return;
        }

        Player player = Bukkit.getServer().getPlayer(args[0]);
        if (player == null) {
            MessageManager.sendMessage(sender, "&cCould not find that player");
            return;
        }

        if (player.getItemInHand() == null) {
            MessageManager.sendMessage(sender, "&cPlayer does not have an item in hand");
            return;
        }

        CarbyneWeapon carbyneWeapon = getGearManager().getCarbyneWeapon(player.getItemInHand());
        if (carbyneWeapon == null) {
            MessageManager.sendMessage(sender, "&cPlayer is not holding a Carbyne-Weapon");
            return;
        }

        int charge = carbyneWeapon.getSpecialCharge(player.getItemInHand());
        try {
            charge = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            MessageManager.sendMessage(sender, "&cCharge amount must be a number");
        }

        if (charge > carbyneWeapon.getSpecial().getRequiredCharge()) {
            carbyneWeapon.setSpecialCharge(player.getItemInHand(), carbyneWeapon.getSpecial().getRequiredCharge());
        } else {
            carbyneWeapon.setSpecialCharge(player.getItemInHand(), charge);
        }

        MessageManager.sendMessage(sender, "&aCharge has been set to &5" + charge + " for &7" + player.getName());
        MessageManager.sendMessage(player, "&aYour weapon charge has been set to: &b" + charge);
    }
}
