package com.medievallords.carbyne.commands;

import com.medievallords.carbyne.profiles.Profile;
import com.medievallords.carbyne.utils.DateUtil;
import com.medievallords.carbyne.utils.MessageManager;
import com.medievallords.carbyne.utils.command.BaseCommand;
import com.medievallords.carbyne.utils.command.Command;
import com.medievallords.carbyne.utils.command.CommandArgs;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Created by Calvin on 6/12/2017
 * for the Carbyne project.
 */
public class PvpTimerCommand extends BaseCommand {

    @Command(name = "pvptimer", aliases = {"pvp"})
    public void onCommand(CommandArgs commandArgs) {
        String[] args = commandArgs.getArgs();
        CommandSender sender = commandArgs.getSender();

        if (args.length == 0) {
            showHelp(sender);
            return;
        }

        Player player = (Player) sender;

        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("disable")) {
                if (player == null) {
                    MessageManager.sendMessage(sender, "&cOnly players can use this command.");
                    return;
                }

                Profile profile = getProfileManager().getProfile(player.getUniqueId());

                if (profile.getPvpTime() <= 0 || profile.getRemainingPvPTime() <= 0) {
                    MessageManager.sendMessage(player, "&cYour PvPTimer is not active!");
                    return;
                }

                profile.setPvpTime(0);
                MessageManager.sendMessage(player, "&cYou have disabled your pvp timer");

            } else if (args[0].equalsIgnoreCase("time")) {
                if (player == null) {
                    MessageManager.sendMessage(sender, "&cOnly players can use this command.");
                    return;
                }

                Profile profile = getProfileManager().getProfile(player.getUniqueId());

                if (profile.getPvpTime() <= 0 || profile.getRemainingPvPTime() <= 0) {
                    MessageManager.sendMessage(player, "&cYour PvPTimer is not active!");
                    return;
                }

                MessageManager.sendMessage(player, "&cYour PvPTimer has " + DateUtil.formatDateDiff(profile.getRemainingPvPTime()) + " remaining.");
            }

        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("remove") && player.hasPermission("carbyne.pvptimer.remove")) {
                Player toRemove = Bukkit.getServer().getPlayer(args[1]);

                if (toRemove != null) {
                    Profile rProfile = getProfileManager().getProfile(toRemove.getUniqueId());
                    if (rProfile != null) {
                        if (rProfile.getPvpTime() <= 0) {
                            MessageManager.sendMessage(player, "&cYour PvPTimer is not active!");
                            return;
                        }
                        rProfile.setPvpTime(0);
                        MessageManager.sendMessage(sender, "&aPvP Timer for player has been removed");
                    }
                }
            }

        } else if (args.length == 3) {
            if (args[0].equalsIgnoreCase("set") && player.hasPermission("carbyne.pvptimer.set")) {
                Player toSet = Bukkit.getServer().getPlayer(args[1]);

                if (toSet != null) {
                    Profile rProfile = getProfileManager().getProfile(toSet.getUniqueId());
                    if (rProfile != null) {
                        try {
                            long time = Integer.parseInt(args[2]);
                            rProfile.setPvpTime(System.currentTimeMillis() + (time * 1000));
                            rProfile.setTimeLeft((time * 1000));
                            //rProfile.setTimeLeft(rProfile.getRemainingPvPTime());
                            rProfile.setPvpTimePaused(true);

                            MessageManager.sendMessage(sender, "&aPvP Timer for player has been set to:&b " + time + " seconds");
                        } catch (NumberFormatException e) {
                            MessageManager.sendMessage(sender, "&cYou can only set the duration to a number");
                        }
                    }
                }
            }
        }
    }

    public void showHelp(CommandSender sender) {
        MessageManager.sendMessage(sender, "&6&m»----------------------------«");
        MessageManager.sendMessage(sender, "&a&lPvPTimer Help");
        MessageManager.sendMessage(sender, "&b/pvp disable &6» &7Remove your PvPTimer.");
        MessageManager.sendMessage(sender, "&b/pvp time &6» &7Check your remaining PvPTimer time.");
        MessageManager.sendMessage(sender, "&b/pvp remove <player> &6» &7Remove PvPTimer from a player.");
        MessageManager.sendMessage(sender, "&b/pvp set <player> <duration> &6» &7Set remaining PvPTimer time for a player.");
        MessageManager.sendMessage(sender, "&6&m»----------------------------«");
    }
}
