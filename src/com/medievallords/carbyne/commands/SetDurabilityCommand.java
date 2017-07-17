package com.medievallords.carbyne.commands;

import com.medievallords.carbyne.gear.types.CarbyneGear;
import com.medievallords.carbyne.gear.types.minecraft.MinecraftArmor;
import com.medievallords.carbyne.gear.types.minecraft.MinecraftWeapon;
import com.medievallords.carbyne.utils.MessageManager;
import com.medievallords.carbyne.utils.command.BaseCommand;
import com.medievallords.carbyne.utils.command.Command;
import com.medievallords.carbyne.utils.command.CommandArgs;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Created by WE on 2017-07-06.
 */
public class SetDurabilityCommand extends BaseCommand {

    @Command(name = "setdurability", aliases = {"sd"}, permission = "carbyne.commands.durability", inGameOnly = true)
    public void onCommand(CommandArgs commandArgs) {
        Player player = commandArgs.getPlayer();
        String[] args = commandArgs.getArgs();

        if (args.length != 1) {
            MessageManager.sendMessage(player, "&cSpecify durability");
            return;
        }

        ItemStack itemStack = player.getItemInHand();
        if (itemStack == null) {
            MessageManager.sendMessage(player, "&cYou're not holding an item");
            return;
        }

        int durability = 1;
        try {
            durability = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            MessageManager.sendMessage(player, "&cYou must input a number");
            return;
        }

        CarbyneGear gear = getGearManager().getCarbyneGear(itemStack);
        if (gear != null) {
            gear.setDurability(itemStack, durability);
            MessageManager.sendMessage(player, "&aItems durability set");
            return;
        }


        MinecraftWeapon weapon = getGearManager().getDefaultWeapon(itemStack);
        if (weapon != null) {
            itemStack.setDurability((short) (itemStack.getType().getMaxDurability() - (durability)));
            MessageManager.sendMessage(player, "&aItems durability set");
            return;
        }

        MinecraftArmor armor = getGearManager().getDefaultArmor(itemStack);
        if (armor != null) {
            itemStack.setDurability((short) (itemStack.getType().getMaxDurability() - (durability)));
            MessageManager.sendMessage(player, "&aItems durability set");
        } else {
            MessageManager.sendMessage(player, "&cYou must be holding carbyne or default gear");
        }
    }
}
