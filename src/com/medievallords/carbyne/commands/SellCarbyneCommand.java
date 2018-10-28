package com.medievallords.carbyne.commands;

import com.medievallords.carbyne.utils.command.BaseCommand;

public class SellCarbyneCommand extends BaseCommand {

    /*@Command(name = "cg.sell", inGameOnly = true, permission = "carbyne.item.sell")
    public void onCommand(CommandArgs commandArgs) {
        Player player = commandArgs.getPlayer();

        //cg sell - Sells the item in hand

        ItemStack itemStack = player.getInventory().getItemInMainHand();
        if (itemStack == null) {
            MessageManager.sendMessage(player, "&cYou must be holding an item.");return;
        }

        CarbyneGear carbyneGear = StaticClasses.gearManager.getCarbyneGear(itemStack);
        if (carbyneGear == null) {
            MessageManager.sendMessage(player, "&cYou must be holding a carbyne gear item.");
            return;
        }

        if (carbyneGear.getState() == GearState.VISIBLE) {
            MessageManager.sendMessage(player, "&cYou may only sell non-purchasable items.");
            return;
        }

        int amount = 0;

        switch (carbyneGear.getGearCode().toLowerCase()) {
            case "ah":
            case "ac":
            case "al":
            case "ab":
            case "ph":
            case "pc":
            case "pl":
            case "pb":
                amount = 3000;
                break;
            case "decapitator":
            case "bewitchedblade":
            case "frostbite":
            case "warhammer":
            case "roar":
            case "prizedsword":
            case "scorch":
            case "odospickaxe":
            case "rapierofunfairness":
            case "aetherexcalibur":
            case "odosembrace":
            case "vanguardblade":
            case "soulsucker":
            case "blackiceblade":
                amount = 1500;
                break;
            case "gravedigger":
            case "deathspite":
            case "repulser":
                amount = 250;
                break;
        }

        if (amount == 0) {
            MessageManager.sendMessage(player, "&cYou may not sell that item.");
            return;
        }

        player.setItemInHand(null);
        StaticClasses.economyManager.deposit(player.getUniqueId(), amount);
        MessageManager.sendMessage(player, "&aYou have sold an item for&7: " + amount + "&a.");
    }*/
}
