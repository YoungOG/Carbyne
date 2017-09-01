package com.medievallords.carbyne.packages.commands;

import com.medievallords.carbyne.utils.MessageManager;
import com.medievallords.carbyne.utils.command.BaseCommand;
import com.medievallords.carbyne.utils.command.Command;
import com.medievallords.carbyne.utils.command.CommandArgs;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.IOException;

/**
 * Created by WE on 2017-08-03.
 */
public class PackageReloadCommand extends BaseCommand {

    @Command(name = "package.reload", aliases = {"p.reload", "pack.reload"}, permission = "carbyne.packages.admin")
    public void execute(CommandArgs commandArgs) {
        String[] args = commandArgs.getArgs();
        Player player = commandArgs.getPlayer();

        reloadConfig();
        getCarbyne().getPackageManager().load();
        MessageManager.sendMessage(player, "&aPackages reloaded");

    }

    public void reloadConfig() {
        try {
            getCarbyne().setPackageFileConfiguration(YamlConfiguration.loadConfiguration(getCarbyne().getPackageFile()));
            getCarbyne().getPackageFileConfiguration().save(getCarbyne().getPackageFile());
        } catch (IOException e) {

        }
    }
}
