package com.medievallords.carbyne.crates.commands;

import com.medievallords.carbyne.crates.Crate;
import com.medievallords.carbyne.utils.Lang;
import com.medievallords.carbyne.utils.MessageManager;
import com.medievallords.carbyne.utils.command.BaseCommand;
import com.medievallords.carbyne.utils.command.Command;
import com.medievallords.carbyne.utils.command.CommandArgs;
import org.bukkit.command.CommandSender;

public class CrateCreateCommand extends BaseCommand {

    @Command(name = "crate.create", permission = "utils.commands.crate.create")
    public void onCommand(CommandArgs command) {
        CommandSender sender = command.getSender();
        String[] args = command.getArgs();

        if (args.length < 1) {
            MessageManager.sendMessage(sender, Lang.TOO_FEW_ARGS_CRATE_CREATE.toString());
            return;
        }

        if (args.length > 1) {
            MessageManager.sendMessage(sender, Lang.TOO_MANY_ARGS_CRATE_CREATE.toString());
            return;
        }

        String name = args[0];

        if (getCrateManager().getCrate(name) != null) {
            MessageManager.sendMessage(sender, Lang.ERROR_CRATE_CREATE.toString());
            return;
        }

        Crate crate = new Crate(name);
        getCrateManager().getCrates().add(crate);

        MessageManager.sendMessage(sender, Lang.SUCCESS_CRATE_CREATE.toString().replace("{CRATE_NAME}", crate.getName()));
    }
}
