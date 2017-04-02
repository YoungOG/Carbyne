package com.medievallords.carbyne.crates.commands;

import com.medievallords.carbyne.crates.Crate;
import com.medievallords.carbyne.utils.Lang;
import com.medievallords.carbyne.utils.MessageManager;
import com.medievallords.carbyne.utils.command.BaseCommand;
import com.medievallords.carbyne.utils.command.Command;
import com.medievallords.carbyne.utils.command.CommandArgs;
import org.bukkit.command.CommandSender;

public class CrateRenameCommand extends BaseCommand {

    @Command(name = "crate.rename", permission = "utils.commands.crate.rename")
    public void onCommand(CommandArgs command) {
        CommandSender sender = command.getSender();
        String[] args = command.getArgs();

        if (args.length < 2) {
            MessageManager.sendMessage(sender, Lang.TOO_FEW_ARGS_CRATE_RENAME.toString());
            return;
        }

        if (args.length > 2) {
            MessageManager.sendMessage(sender, Lang.TOO_MANY_ARGS_CRATE_RENAME.toString());
            return;
        }

        String currentName = args[0];
        String newName = args[1];

        if (getCrateManager().getCrate(currentName) == null) {
            MessageManager.sendMessage(sender, Lang.CRATE_NOT_FOUND.toString().replace("{CURRENT_NAME}", currentName));
            return;
        }

        Crate crate = getCrateManager().getCrate(currentName);

        if (getCrateManager().getCrate(newName) != null) {
            MessageManager.sendMessage(sender, Lang.CRATE_RENAME_NAME_EXISTS.toString().replace("{CURRENT_NAME}", currentName).replace("{NEW_NAME}", newName));
            return;
        }

        currentName = crate.getName();
        crate.setName(newName);

        getCarbyne().getCrateFileConfiguration().getConfigurationSection("Crates").set(currentName, newName);

        crate.save(getCarbyne().getCrateFileConfiguration());

        MessageManager.sendMessage(sender, Lang.SUCCESS_CRATE_RENAME.toString().replace("{CURRENT_NAME}", currentName).replace("{NEW_NAME}", newName));
    }
}
