package com.medievallords.carbyne.regeneration.commands;

import com.medievallords.carbyne.utils.MessageManager;
import com.medievallords.carbyne.utils.command.BaseCommand;
import com.medievallords.carbyne.utils.command.Command;
import com.medievallords.carbyne.utils.command.CommandArgs;
import org.bukkit.entity.Player;

/**
 * Created by Calvin on 3/23/2017
 * for the Carbyne project.
 */
public class RegenerationBypassCommand extends BaseCommand {

    @Command(name = "bypass", aliases = {"build"}, permission = "carbyne.commands.bypass", inGameOnly = true)
    public void onCommand(CommandArgs cmdArgs) {
        Player player = cmdArgs.getPlayer();
        String[] args = cmdArgs.getArgs();

        if (args.length != 0) {
            MessageManager.sendMessage(player, "&cUsage: /bypass");
            return;
        }

        if (getRegenerationHandler().getBypassers().contains(player.getUniqueId())) {
            getRegenerationHandler().getBypassers().remove(player.getUniqueId());
            MessageManager.sendMessage(player, "&cYou are no longer bypassing regeneration.");

        } else {
            getRegenerationHandler().getBypassers().add(player.getUniqueId());
            MessageManager.sendMessage(player, "&cYou are now bypassing regeneration.");
        }
    }
}
