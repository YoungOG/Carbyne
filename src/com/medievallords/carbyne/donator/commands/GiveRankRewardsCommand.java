package com.medievallords.carbyne.donator.commands;

import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.profiles.Profile;
import com.medievallords.carbyne.utils.MessageManager;
import com.medievallords.carbyne.utils.StaticClasses;
import com.medievallords.carbyne.utils.command.BaseCommand;
import com.medievallords.carbyne.utils.command.Command;
import com.medievallords.carbyne.utils.command.CommandArgs;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GiveRankRewardsCommand extends BaseCommand implements Listener {

    public Set<RankObject> rankObjects = new HashSet<>();

    public GiveRankRewardsCommand() {
        load();
    }

    @Command(name = "giverankrewards", aliases = {"rankrewards"}, permission = "carbyne.command.giverankrewards", inGameOnly = false)
    public void onCommand(CommandArgs commandArgs) {
        CommandSender sender = commandArgs.getSender();
        String[] args = commandArgs.getArgs();

        if (args.length < 3) {
            MessageManager.sendMessage(sender, "&cUsage: /rankrewards <set/check> <user> (true/false)");
            return;
        }

        Profile profile = StaticClasses.profileManager.getProfile(args[2]);

        if (profile == null) {
            MessageManager.sendMessage(sender, "&cThat user could not be found.");
            return;
        }

        if (args[0].equalsIgnoreCase("check"))
            MessageManager.sendMessage(sender, "&5" + args[2] + " &a" + (profile.isHasClaimedRankRewards() ? "does have" : "does not have") + ".");
        else if (args[0].equalsIgnoreCase("set")) {
            if (args.length < 4) {
                MessageManager.sendMessage(sender, "&cUsage: /rankrewards <set> <user> <true/false>");
                return;
            }

            Boolean set = Boolean.parseBoolean(args[3]);

            profile.setHasClaimedRankRewards(set);
            Player player = Bukkit.getPlayer(profile.getUniqueId());
            if (player != null) {
                for (RankObject rankObject : rankObjects)
                    if (rankObject.getRankName().equalsIgnoreCase(Carbyne.getInstance().getPermissions().getPrimaryGroup(player)))
                        rankObject.run(player);
            }
            MessageManager.sendMessage(sender, "&aYou have set &5" + args[2] + "&a's rank rewards to &b" + set + "&a.");
        }
    }

    public void load() {
        if (Carbyne.getInstance().getConfig().getStringList("RankRewardCommands").size() > 0) {
            for (String arg : Carbyne.getInstance().getConfig().getStringList("RankRewardCommands")) {
                String[] args = arg.split(",");

                if (args.length != 2)
                    continue;

                RankObject rankObject = getRankObject(args[0]);

                if (rankObject == null) {
                    rankObject = new RankObject(args[0]);
                    rankObjects.add(rankObject);
                }

                if (!rankObject.getCommands().contains(args[1]))
                    rankObject.getCommands().add(args[1]);
            }
        }
    }

    public RankObject getRankObject(String rankName) {
        for (RankObject rankObject : rankObjects)
            if (rankObject.getRankName().equalsIgnoreCase(rankName))
                return rankObject;

        return null;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        Profile profile = StaticClasses.profileManager.getProfile(player.getUniqueId());

        if (profile != null && !profile.isHasClaimedRankRewards())
            for (RankObject rankObject : rankObjects)
                if (rankObject.getRankName().equalsIgnoreCase(Carbyne.getInstance().getPermissions().getPrimaryGroup(player))) {
                    rankObject.run(player);
                    profile.setHasClaimedRankRewards(true);
                }
    }

    @Getter
    @Setter
    public class RankObject {

        private String rankName;
        private List<String> commands = new ArrayList<>();

        public RankObject(String rankName) {
            this.rankName = rankName;
        }

        public void run(Player player) {
            for (String command : commands)
                Carbyne.getInstance().getServer().dispatchCommand(Carbyne.getInstance().getServer().getConsoleSender(), command.replace("/", "").replace("%player%", player.getName()).replace("%uniqueId%", player.getUniqueId().toString()));
        }
    }
}
