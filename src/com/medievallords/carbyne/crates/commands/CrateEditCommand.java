package com.medievallords.carbyne.crates.commands;

import com.medievallords.carbyne.crates.Crate;
import com.medievallords.carbyne.utils.Lang;
import com.medievallords.carbyne.utils.MessageManager;
import com.medievallords.carbyne.utils.command.BaseCommand;
import com.medievallords.carbyne.utils.command.Command;
import com.medievallords.carbyne.utils.command.CommandArgs;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CrateEditCommand extends BaseCommand {

    @Command(name = "crate.edit", permission = "utils.commands.crate.edit", inGameOnly = true)
    public void onCommand(CommandArgs command) {
        CommandSender sender = command.getSender();
        Player player = (Player) sender;
        String[] args = command.getArgs();

        if (args.length < 1) {
            MessageManager.sendMessage(sender, Lang.TOO_FEW_ARGS_CRATE_EDIT.toString());
            return;
        }

        if (args.length > 1) {
            MessageManager.sendMessage(sender, Lang.TOO_MANY_ARGS_CRATE_EDIT.toString());
            return;
        }

        String name = args[0];

        if (getCrateManager().getCrate(name) == null) {
            MessageManager.sendMessage(sender, Lang.CRATE_NOT_FOUND.toString().replace("{NAME}", name));
            return;
        }

        Crate crate = getCrateManager().getCrate(name);
        crate.editRewards(player);
        MessageManager.sendMessage(sender, Lang.SUCCESS_CRATE_EDIT.toString().replace("{CRATE_NAME}", crate.getName()));
    }
}
