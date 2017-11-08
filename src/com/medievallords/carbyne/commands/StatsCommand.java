package com.medievallords.carbyne.commands;

import com.medievallords.carbyne.economy.account.Account;
import com.medievallords.carbyne.profiles.Profile;
import com.medievallords.carbyne.utils.MessageManager;
import com.medievallords.carbyne.utils.command.BaseCommand;
import com.medievallords.carbyne.utils.command.Command;
import com.medievallords.carbyne.utils.command.CommandArgs;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;

public class StatsCommand extends BaseCommand {

    @Command(name = "stats")
    public void onCommand(CommandArgs commandArgs) {
        String[] args = commandArgs.getArgs();
        Player player = commandArgs.getPlayer();

        if (args.length > 2) {
            MessageManager.sendMessage(player, "&cUsage: /stats <player/reset> [stat]");
            return;
        }

        if (args.length == 0) {
            Profile profile = getProfileManager().getProfile(player.getUniqueId());

            if (profile == null) {
                MessageManager.sendMessage(player, "&cCould not find a profile for " + player.getName() + ".");
                return;
            }

            MessageManager.sendMessage(player, "&7&m============&r&7[ &2" + player.getName() + "'s Stats &7]&m============");
            MessageManager.sendMessage(player, "&aBalance: &b" + Account.getAccount(player.getUniqueId()).getBalance());
            MessageManager.sendMessage(player, "&aKills: &b" + profile.getKills());
            MessageManager.sendMessage(player, "&aCarbyne Kills: &b" + profile.getCarbyneKills());
            MessageManager.sendMessage(player, "&aDeaths: &b" + profile.getDeaths());
            MessageManager.sendMessage(player, "&aCarbyne Deaths: &b" + profile.getCarbyneDeaths());
            MessageManager.sendMessage(player, "&aKD Ratio: &b" + profile.getKDR());
            MessageManager.sendMessage(player, "&aCarbyne KD Ratio: &b" + profile.getCarbyneKDR());
            MessageManager.sendMessage(player, "&aKillstreak: &b" + profile.getKillStreak());
            MessageManager.sendMessage(player, "&bDiamonds &aMined: &b" + player.getStatistic(Statistic.MINE_BLOCK, Material.DIAMOND_ORE));
            MessageManager.sendMessage(player, "&aEmeralds &aMined: &b" + player.getStatistic(Statistic.MINE_BLOCK, Material.EMERALD_ORE));
            MessageManager.sendMessage(player, "&6Gold &aMined: &b" + player.getStatistic(Statistic.MINE_BLOCK, Material.GOLD_ORE));
            MessageManager.sendMessage(player, "&7Iron &aMined: &b" + player.getStatistic(Statistic.MINE_BLOCK, Material.IRON_ORE));
            MessageManager.sendMessage(player, "&8Coal &aMined: &b" + player.getStatistic(Statistic.MINE_BLOCK, Material.COAL_ORE));
        }

        if (args.length == 1) {
            Profile profile = getProfileManager().getProfile(args[0]);

            if (profile == null) {
                MessageManager.sendMessage(player, "&cCould not find a profile for " + args[0] + ".");
                return;
            }

            MessageManager.sendMessage(player, "&7&m============&r&7[ &2" + Bukkit.getOfflinePlayer(profile.getUniqueId()).getName() + "'s Stats &7]&m============");
            MessageManager.sendMessage(player, "&aBalance: &b" + Account.getAccount(profile.getUniqueId()).getBalance());
            MessageManager.sendMessage(player, "&aKills: &b" + profile.getKills());
            MessageManager.sendMessage(player, "&aCarbyne Kills: &b" + profile.getCarbyneKills());
            MessageManager.sendMessage(player, "&aDeaths: &b" + profile.getDeaths());
            MessageManager.sendMessage(player, "&aCarbyne Deaths: &b" + profile.getCarbyneDeaths());
            MessageManager.sendMessage(player, "&aKD Ratio: &b" + profile.getKDR());
            MessageManager.sendMessage(player, "&aCarbyne KD Ratio: &b" + profile.getCarbyneKDR());
            MessageManager.sendMessage(player, "&aKillstreak: &b" + profile.getKillStreak());

            if (Bukkit.getPlayer(profile.getUniqueId()) != null) {
                Player target = Bukkit.getPlayer(profile.getUniqueId());
                MessageManager.sendMessage(player, "&bDiamonds &aMined: &b" + target.getStatistic(Statistic.MINE_BLOCK, Material.DIAMOND_ORE));
                MessageManager.sendMessage(player, "&aEmeralds &aMined: &b" + target.getStatistic(Statistic.MINE_BLOCK, Material.EMERALD_ORE));
                MessageManager.sendMessage(player, "&6Gold &aMined: &b" + target.getStatistic(Statistic.MINE_BLOCK, Material.GOLD_ORE));
                MessageManager.sendMessage(player, "&7Iron &aMined: &b" + target.getStatistic(Statistic.MINE_BLOCK, Material.IRON_ORE));
                MessageManager.sendMessage(player, "&8Coal &aMined: &b" + target.getStatistic(Statistic.MINE_BLOCK, Material.COAL_ORE));
            }
        }

        if (args.length == 2) {

        }
    }
}
