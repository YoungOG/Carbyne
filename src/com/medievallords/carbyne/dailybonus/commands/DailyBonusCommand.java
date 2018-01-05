package com.medievallords.carbyne.dailybonus.commands;

import com.medievallords.carbyne.profiles.Profile;
import com.medievallords.carbyne.utils.DateUtil;
import com.medievallords.carbyne.utils.MessageManager;
import com.medievallords.carbyne.utils.command.BaseCommand;
import com.medievallords.carbyne.utils.command.Command;
import com.medievallords.carbyne.utils.command.CommandArgs;
import org.bukkit.entity.Player;

/**
 * Created by Calvin on 11/18/2017
 * for the Carbyne project.
 */
public class DailyBonusCommand extends BaseCommand {

    @Command(name = "DailyBonus", inGameOnly = true)
    public void onCommand(CommandArgs commandArgs) {
        Player player = commandArgs.getPlayer();
        String[] args = commandArgs.getArgs();

        if (!player.hasPermission("carbyne.command.dailybonus")) {
            MessageManager.sendMessage(player, "&cYou do not have permission to use this.");
            return;
        }

        if (args.length == 0) {
            MessageManager.sendMessage(player, "&cDaily Bonus\n&c/dailybonus &7- Shows this page\n&c/dailybonus &7- Shows this page\n&c/dailybonus reset <day/week> &7- Reset the day/week\n&c/dailybonus set day <day> &7- Set the days");
            return;
        }

        if (args[0].equalsIgnoreCase("reset")) {
            if (args.length > 1) {
                Profile profile = getProfileManager().getProfile(player.getUniqueId());

                if (args[1].equalsIgnoreCase("day")) {
                    profile.setHasClaimedDailyReward(false);
                    profile.setHasCompletedDailyChallenge(false);
                    profile.getDailyRewards().put(profile.getDailyRewardDay(), false);

                    try {
                        profile.setDailyRewardDayTime(DateUtil.parseDateDiff("1day", true));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    MessageManager.sendMessage(player, "&aDailyBonus day has been reset.");
                } else if (args[1].equalsIgnoreCase("week")) {
                    profile.assignNewWeeklyRewards();
                    MessageManager.sendMessage(player, "&aDailyBonus weekly rewards has been reset.");
                } else
                    MessageManager.sendMessage(player, "&cUsage: /dailybonus reset <day/week>");
            } else
                MessageManager.sendMessage(player, "&cUsage: /dailybonus reset <day/week>");
        } else if (args[0].equalsIgnoreCase("set")) {
            if (args.length > 2) {
                if (args[1].equalsIgnoreCase("day")) {
                    try {
                        int number = (Integer.parseInt(args[2]) - 1);

                        if (number < 0) {
                            MessageManager.sendMessage(player, "&cYou cannot enter a day lower than 1.");
                            return;
                        }

                        if (number > 6) {
                            MessageManager.sendMessage(player, "You cannot enter a day higher than 7.");
                            return;
                        }

                        Profile profile = getProfileManager().getProfile(player.getUniqueId());
                        profile.setDailyRewardDay(number);

                        MessageManager.sendMessage(player, "&aYour DailyBonus day has been set to &b" + (number + 1) + "&a.");
                    } catch (NumberFormatException e) {
                        MessageManager.sendMessage(player, "&cPlease enter a proper integer.");
                    }
                } else
                    MessageManager.sendMessage(player, "&cUsage: /dailybonus set day <day>");
            } else
                MessageManager.sendMessage(player, "&cUsage: /dailybonus set day <day>");
        } else
            MessageManager.sendMessage(player, "&cUsage: /dailybonus");
    }
}
