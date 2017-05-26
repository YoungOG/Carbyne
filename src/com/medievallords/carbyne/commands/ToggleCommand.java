package com.medievallords.carbyne.commands;

import com.medievallords.carbyne.profiles.Profile;
import com.medievallords.carbyne.utils.MessageManager;
import com.medievallords.carbyne.utils.command.BaseCommand;
import com.medievallords.carbyne.utils.command.Command;
import com.medievallords.carbyne.utils.command.CommandArgs;
import org.bukkit.entity.Player;

/**
 * Created by Calvin on 3/24/2017
 * for the Carbyne project.
 */
public class ToggleCommand extends BaseCommand {

    @Command(name = "toggle", inGameOnly = true)
    public void onCommand(CommandArgs commandArgs) {
        Player player = commandArgs.getPlayer();
        String[] args = commandArgs.getArgs();

        Profile profile = getProfileManager().getProfile(player.getUniqueId());

        if (args.length < 1 || args.length > 2) {
            MessageManager.sendMessage(player, "&cUsage: /toggle <effects> [on/off]");
        }

        if (profile == null) {
            MessageManager.sendMessage(player, "&cCould not load your profile. Please contact an administrator.");
            return;
        }

        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("effects")) {
                if (!profile.hasEffectsToggled()) {
                    profile.setShowEffects(true);
                    MessageManager.sendMessage(player, "&aYour particle effects have been enabled.");
                } else {
                    profile.setShowEffects(false);
                    MessageManager.sendMessage(player, "&aYour particle effects have been disabled.");
                }
            } else {
                MessageManager.sendMessage(player, "&cUsage: /toggle <effects> [on/off]");
            }
        }

        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("effects")) {
                if (args[1].equalsIgnoreCase("on")) {
                    profile.setShowEffects(true);

                    MessageManager.sendMessage(player, "&aYour particle effects have been enabled.");
                } else if (args[1].equalsIgnoreCase("off")) {
                    profile.setShowEffects(false);

                    MessageManager.sendMessage(player, "&aYour particle effects have been disabled.");
                } else {
                    MessageManager.sendMessage(player, "&cUsage: /toggle <effects> [on/off]");
                }
            } else {
                MessageManager.sendMessage(player, "&cUsage: /toggle <effects> [on/off]");
            }
        }
    }
}
