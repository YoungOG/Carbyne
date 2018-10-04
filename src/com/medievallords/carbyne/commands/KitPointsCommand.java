package com.medievallords.carbyne.commands;

import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.profiles.Profile;
import com.medievallords.carbyne.profiles.ProfileManager;
import com.medievallords.carbyne.utils.MessageManager;
import com.medievallords.carbyne.utils.StaticClasses;
import com.medievallords.carbyne.utils.command.BaseCommand;
import com.medievallords.carbyne.utils.command.Command;
import com.medievallords.carbyne.utils.command.CommandArgs;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class KitPointsCommand extends BaseCommand {

    private static final ProfileManager profileManager = StaticClasses.profileManager;

    @Command(name = "kitpoints", aliases = {"kitpoint", "kp", "kps"})
    public void onCommand(CommandArgs commandArgs) {
        String[] args = commandArgs.getArgs();
        Player player = commandArgs.getPlayer();

        if (StaticClasses.economyManager.isEconomyHalted()) {
            MessageManager.sendMessage(player, "&cThe economy is temporarily disabled. The administrators will let you know when it is re-enabled.");
            return;
        }

        if (args.length == 0) {
            if (commandArgs.getPlayer() == null) {
                MessageManager.sendMessage(commandArgs.getSender(), "You may not use this command.");
                return;
            }

            Profile profile = profileManager.getProfile(player.getUniqueId());

            MessageManager.sendMessage(player, "&7Kit Points&b: " + profile.getKitPoints());
            return;
        }

        if (args.length >= 1) {
            if (args[0].equalsIgnoreCase("top")) {
                if (!player.hasPermission("carbyne.commands.baltop")) {
                    MessageManager.sendMessage(player, "&cUsage: /kitpoints top");
                    return;
                }

                new BukkitRunnable() {
                    public void run() {
                        HashMap<String, Integer> map = new HashMap<>();
                        for (Profile profiles : profileManager.getLoadedProfiles())
                            map.put(Bukkit.getOfflinePlayer(profiles.getUniqueId()).getName(), profiles.getKitPoints());

                        MessageManager.sendMessage(player, "&7***&cTop 10 Accounts&7***");

                        Object[] a = map.entrySet().toArray();
                        Arrays.sort(a, (o1, o2) -> ((Map.Entry<String, Integer>) o2).getValue().compareTo(((Map.Entry<String, Integer>) o1).getValue()));

                        int topten = 0;
                        for (Object e : a) {
                            if (topten <= 9)
                                MessageManager.sendMessage(player, "&7" + (topten + 1) + ". &c" + ((Map.Entry<String, Double>) e).getKey() + " &7- &b" + ((Map.Entry<String, Integer>) e).getValue());

                            topten++;
                        }
                    }
                }.runTaskAsynchronously(Carbyne.getInstance());
            } else if (args[0].equalsIgnoreCase("give") || args[0].equalsIgnoreCase("add")) {
                if (args.length != 3) {
                    MessageManager.sendMessage(player, "&cUsage: /kitpoints give <player> <amount>");
                    return;
                }

                if (!player.hasPermission("carbyne.commands.kitpoints.admin")) {
                    MessageManager.sendMessage(player, "&cUsage: /kitpoints top");
                    return;
                }

                Profile profile = profileManager.getProfile(args[1]);
                if (profile == null) {
                    MessageManager.sendMessage(player, "&cThe player specified could not be found.");
                    return;
                }

                if (!isInteger(args[2])) {
                    MessageManager.sendMessage(player, "&7Argument must be numerical.\n&cUsage: /kitpoints give <player> <amount>");
                    return;
                }

                profile.setKitPoints(profile.getKitPoints() + Integer.parseInt(args[2]));
                MessageManager.sendMessage(profile.getUniqueId(), "&aYou were given &b" + args[2] + " &akit points!");
                MessageManager.sendMessage(player, "&aYou have given &5" + args[1] + " &b" + args[2] + " &akit points.");
            } else if (args[0].equalsIgnoreCase("set")) {
                if (args.length != 3) {
                    MessageManager.sendMessage(player,"&cUsage: /kitpoints set <player> <amount>");
                    return;
                }

                if (!player.hasPermission("carbyne.commands.kitpoints.admin")) {
                    MessageManager.sendMessage(player, "&cUsage: /kitpoints top");
                    return;
                }

                Profile profile = profileManager.getProfile(args[1]);
                if (profile == null) {
                    MessageManager.sendMessage(player, "&cThe player specified could not be found.");
                    return;
                }

                if (!isInteger(args[2])) {
                    MessageManager.sendMessage(player, "&7Argument must be numerical.\n&cUsage: /kitpoints set <player> <amount>");
                    return;
                }

                profile.setKitPoints(Integer.parseInt(args[2]));
                MessageManager.sendMessage(profile.getUniqueId(), "&aYour kit points have been set to &b" + args[2] + "&a.");
                MessageManager.sendMessage(player, "&aYou have set &5" + args[1] + "'s &akit points to &b" + args[2] + "&a.");
            } else if (args[0].equalsIgnoreCase("take") || args[0].equalsIgnoreCase("remove")) {
                if (args.length != 3) {
                    MessageManager.sendMessage(player,"&cUsage: /kitpoints take <player> <amount>");
                    return;
                }

                if (!player.hasPermission("carbyne.commands.kitpoints.admin")) {
                    MessageManager.sendMessage(player, "&cUsage: /kitpoints top");
                    return;
                }

                Profile profile = profileManager.getProfile(args[1]);
                if (profile == null) {
                    MessageManager.sendMessage(player, "&cThe player specified could not be found.");
                    return;
                }

                if (!isInteger(args[2])) {
                    MessageManager.sendMessage(player, "&7Argument must be numerical.\n&cUsage: /kitpoints take <player> <amount>");
                    return;
                }

                profile.setKitPoints(profile.getKitPoints() - Integer.parseInt(args[2]));
                MessageManager.sendMessage(profile.getUniqueId(), "&aYour kit points have been set to &b" + args[2] + "&a.");
                MessageManager.sendMessage(player, "&aYou have taken &b" + args[2] + " &akit points from &5" + args[1] + "&a.");
            } else if (args[0].equalsIgnoreCase("reset")) {
                if (args.length != 2) {
                    MessageManager.sendMessage(player,"&cUsage: /kitpoints reset <player>>");
                    return;
                }

                if (!player.hasPermission("carbyne.commands.kitpoints.admin")) {
                    MessageManager.sendMessage(player, "&cUsage: /kitpoints top");
                    return;
                }

                Profile profile = profileManager.getProfile(args[1]);
                if (profile == null) {
                    MessageManager.sendMessage(player, "&cThe player specified could not be found.");
                    return;
                }

                profile.setKitPoints(0);
                MessageManager.sendMessage(profile.getUniqueId(), "&aYour kit points have been reset to &b" + args[2] + "&a.");
                MessageManager.sendMessage(player, "&aYou have reset &5" + args[1] + "'s &akit points to &b" + args[2] + "&a.");
            } else
                MessageManager.sendMessage(player, "&cUsage: /kitpoints <top/give/set/take/reset> (player) (amount)");
        }
    }

    private boolean isInteger(String s) {
        try {
            Integer.parseInt(s);
        } catch (NumberFormatException | NullPointerException e) {
            return false;
        }

        return true;
    }
}
