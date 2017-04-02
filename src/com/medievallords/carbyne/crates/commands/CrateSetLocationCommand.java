package com.medievallords.carbyne.crates.commands;

import com.medievallords.carbyne.crates.Crate;
import com.medievallords.carbyne.utils.Lang;
import com.medievallords.carbyne.utils.MessageManager;
import com.medievallords.carbyne.utils.command.BaseCommand;
import com.medievallords.carbyne.utils.command.Command;
import com.medievallords.carbyne.utils.command.CommandArgs;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.util.HashSet;

public class CrateSetLocationCommand extends BaseCommand {

    @Command(name = "crate.setlocation", permission = "utils.commands.crate.location", aliases = {"crate.setblock", "crate.location", "crate.block"}, inGameOnly = true)
    public void onCommand(CommandArgs command) {
        CommandSender sender = command.getSender();
        String[] args = command.getArgs();

        if (args.length < 1) {
            MessageManager.sendMessage(sender, Lang.TOO_FEW_ARGS_CRATE_SET_LOCATION.toString());
            return;
        }

        if (args.length > 1) {
            MessageManager.sendMessage(sender, Lang.TOO_MANY_ARGS_CRATE_SET_LOCATION.toString());
            return;
        }

        String name = args[0];

        if (getCrateManager().getCrate(name) == null) {
            MessageManager.sendMessage(sender, Lang.CRATE_NOT_FOUND.toString().replace("{NAME}", name));
            return;
        }

        Crate crate = getCrateManager().getCrate(name);
        crate.setLocation(Bukkit.getPlayer(sender.getName()).getTargetBlock((HashSet<Byte>) null, 50).getLocation());
        crate.save(getCarbyne().getCrateFileConfiguration());

        MessageManager.sendMessage(sender, Lang.SUCCESS_CRATE_SET_LOCATION.toString().replace("{CRATE_NAME}", crate.getName()).replace("{WORLD}", crate.getLocation().getWorld().getName()).replace("{X}", "" + crate.getLocation().getBlockX()).replace("{Y}", "" + crate.getLocation().getBlockY()).replace("{Z}", "" + crate.getLocation().getBlockZ()));
    }
}
