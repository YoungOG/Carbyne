package com.medievallords.carbyne.crates.commands;

import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.utils.Lang;
import com.medievallords.carbyne.utils.MessageManager;
import com.medievallords.carbyne.utils.StaticClasses;
import com.medievallords.carbyne.utils.command.BaseCommand;
import com.medievallords.carbyne.utils.command.Command;
import com.medievallords.carbyne.utils.command.CommandArgs;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class CrateReloadCommand extends BaseCommand {

    @Command(name = "crate.reload", permission = "utils.reload")
    public void onCommand(CommandArgs command) {
        CommandSender sender = command.getSender();
        String[] args = command.getArgs();

        if (args.length < 0) {
            MessageManager.sendMessage(sender, Lang.TOO_FEW_ARGS_CRATE_RELOAD.toString());
            return;
        }

        if (args.length > 0) {
            MessageManager.sendMessage(sender, Lang.TOO_MANY_ARGS_CRATE_RELOAD.toString());
            return;
        }

        Carbyne.getInstance().reloadConfig();
        Lang.setFile(YamlConfiguration.loadConfiguration(new File(Carbyne.getInstance().getDataFolder(), "lang.yml")));

        if (Carbyne.getInstance().getCratesFile() == null) {
            Carbyne.getInstance().setCratesFile(new File(Carbyne.getInstance().getDataFolder(), "crates.yml"));
        }

        Carbyne.getInstance().setCratesFileConfiguration(YamlConfiguration.loadConfiguration(Carbyne.getInstance().getCratesFile()));

        StaticClasses.crateManager.load(YamlConfiguration.loadConfiguration(Carbyne.getInstance().getCratesFile()));

        MessageManager.sendMessage(sender, Lang.SUCCESS_CRATE_RELOAD.toString());
    }
}
