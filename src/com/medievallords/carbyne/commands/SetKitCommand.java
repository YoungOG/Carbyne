package com.medievallords.carbyne.commands;

import com.medievallords.carbyne.kits.Kit;
import com.medievallords.carbyne.kits.KitManager;
import com.medievallords.carbyne.profiles.Profile;
import com.medievallords.carbyne.profiles.ProfileManager;
import com.medievallords.carbyne.utils.MessageManager;
import com.medievallords.carbyne.utils.StaticClasses;
import com.medievallords.carbyne.utils.command.BaseCommand;
import com.medievallords.carbyne.utils.command.Command;
import com.medievallords.carbyne.utils.command.CommandArgs;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class SetKitCommand extends BaseCommand {

    @Command(name = "setkit", aliases = {"skit", "createkit", "addkit"}, permission = "carbyne.administrator")
    public void onCommand(CommandArgs commandArgs) {
        String[] args = commandArgs.getArgs();
        Player player = commandArgs.getPlayer();

        if (args.length != 5) {
            MessageManager.sendMessage(player, "&cUsage:&7 /setKit <name> <material;data> <cost> <delay> <hidden>");
            return;
        }

        if (!isInteger(args[2], args[3])) {
            MessageManager.sendMessage(player, "&7Argument must be numerical.\n&cUsage: /setKit <name> <material;data> <cost> <delay> <hidden>");
            return;
        }

        String kitName = args[0];
        String materialData = args[1].toUpperCase();
        if (!materialData.contains(";")) {
            MessageManager.sendMessage(player, "&cThe material data needs to be in this format&7: <material;data>");
            return;
        }
        
        if (materialData.split(";").length != 2) {
            MessageManager.sendMessage(player, "&cThe material data needs to be in this format&7: <material;data>");
            return;
        }

        try {
            Material.valueOf(materialData.split(";")[0]);
        } catch (IllegalArgumentException e) {
            MessageManager.sendMessage(player, "&cThe material data needs to be in this format&7: <material;data>");
            return;
        }

        if (!isInteger(materialData.split(";")[1])) {
            MessageManager.sendMessage(player, "&cThe material data needs to be in this format&7: <material;data>");
            return;
        }

        int cost = Integer.parseInt(args[2]);
        int delay = Integer.parseInt(args[3]);
        boolean hidden = Boolean.parseBoolean(args[4]);

        Kit kit = StaticClasses.kitManager.getKit(kitName);

        if (kit == null) {
            kit = new Kit(kitName);
            StaticClasses.kitManager.getKits().add(kit);
        } else {
            MessageManager.sendMessage(player, "&cThere is already a kit with that name.");
            return;
        }

        List<ItemStack> contents = new ArrayList<>();
        for (ItemStack is : player.getInventory()) {
            if (is == null)
                continue;

            contents.add(is.clone());
        }

        kit.setMaterialData(materialData);
        kit.setContents(contents);
        kit.setCost(cost);
        kit.setDelay(delay);
        kit.setHidden(hidden);

        MessageManager.sendMessage(player, "&aYou have successfully created a new kit called &b" + kit.getName() + "&a, with the materialData &b" + materialData + "&a, and a cost of &b" + cost + "&a, a delay of &b" + delay + "&a, and is hidden &b" + hidden + "&a.");
    }

    public static boolean isInteger(String... list) {
        try {
            for (String s : list)
                Integer.parseInt(s);
        } catch(NumberFormatException | NullPointerException e) {
            return false;
        }

        return true;
    }
}
