package com.medievallords.carbyne.dungeons.dungeons.commands;

import com.destroystokyo.paper.Title;
import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.dungeons.dungeons.instance.DungeonInstance;
import com.medievallords.carbyne.dungeons.dungeons.instance.DungeonStage;
import com.medievallords.carbyne.dungeons.player.DPlayer;
import com.medievallords.carbyne.utils.Cooldowns;
import com.medievallords.carbyne.utils.DateUtil;
import com.medievallords.carbyne.utils.MessageManager;
import com.medievallords.carbyne.utils.StaticClasses;
import com.medievallords.carbyne.utils.command.BaseCommand;
import com.medievallords.carbyne.utils.command.Command;
import com.medievallords.carbyne.utils.command.CommandArgs;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class DungeonJoinCommand extends BaseCommand {

    @Command(name = "dungeon.request.clear", inGameOnly = true)
    public void onComma(CommandArgs commandArgs) {
        Player player = commandArgs.getPlayer();
        String[] args = commandArgs.getArgs();

        if (args.length != 0) {
            MessageManager.sendMessage(player, "&cUsage: &7/dungeon request clear");
            return;
        }

        DPlayer checkSelf = StaticClasses.dungeonHandler.getDPlayer(player.getUniqueId());
        if (checkSelf != null) {
            MessageManager.sendMessage(player, "&cYou are already in a dungeon.");
            return;
        }

        for (DungeonInstance instance : StaticClasses.dungeonHandler.getInstances()) {
            if (instance.getJoiningPlayers().contains(player.getUniqueId())) {
                instance.getJoiningPlayers().remove(player.getUniqueId());
                MessageManager.sendMessage(player, "&aYour requests have been cleared.");
            }
        }

        MessageManager.sendMessage(player, "&cYou do not have any requests.");
    }

    @Command(name = "dungeon.join", inGameOnly = true)
    public void onCommand(CommandArgs commandArgs) {
        Player player = commandArgs.getPlayer();
        String[] args = commandArgs.getArgs();

        if (args.length != 1) {
            MessageManager.sendMessage(player, "&cUsage: &7/dungeon join <player>");
            return;
        }

        DPlayer checkSelf = StaticClasses.dungeonHandler.getDPlayer(player.getUniqueId());
        if (checkSelf != null) {
            MessageManager.sendMessage(player, "&cYou are already in a dungeon.");
            return;
        }

        for (DungeonInstance instance : StaticClasses.dungeonHandler.getInstances()) {
            if (instance.getJoiningPlayers().contains(player.getUniqueId())) {
                MessageManager.sendMessage(player, "&cYou have already asked to join a dungeon. Use &7/dungeon request clear &cto undo.");
                return;
            }
        }

        Player toJoin = Bukkit.getPlayer(args[0]);
        if (toJoin == null) {
            MessageManager.sendMessage(player, "&cCould not find that player.");
            return;
        }

        DPlayer dPlayer = StaticClasses.dungeonHandler.getDPlayer(toJoin.getUniqueId());
        if (dPlayer == null) {
            MessageManager.sendMessage(player, "&cThat player is not in a dungeon.");
            return;
        }

        DungeonInstance instance = dPlayer.getInstance();
        long cooldown = Cooldowns.getCooldown(player.getUniqueId(), "dungeon:name:" + instance.getDungeon().getName());
        if (cooldown > 0) {
            MessageManager.sendMessage(player, "&cYou must wait &7" + DateUtil.readableTime(cooldown, true) + " &cuntil you can enter that dungeon.");
            return;
        }

        if (instance.getStage() != DungeonStage.LOBBY) {
            MessageManager.sendMessage(player, "&cYou cannot join that dungeon at this point.");
            return;
        }

        if (instance.getJoiningPlayers().contains(player.getUniqueId())) {
            MessageManager.sendMessage(player, "&cYou have already asked to join that dungeon.");
            return;
        }

        instance.getJoiningPlayers().add(player.getUniqueId());
        MessageManager.sendMessage(player, "&aYou have requested to join their dungeon.");
        instance.sendAll("&7" + player.getName() + "&a has requested to join the dungeon. Use &7/dungeon accept " + player.getName() + " &ato accept his request.");
    }

    @Command(name = "dungeon.accept", inGameOnly = true)
    public void onComman(CommandArgs commandArgs) {
        Player player = commandArgs.getPlayer();
        String[] args = commandArgs.getArgs();

        DPlayer dPlayer = StaticClasses.dungeonHandler.getDPlayer(player.getUniqueId());
        if (dPlayer == null) {
            MessageManager.sendMessage(player, "&cYou are not in a dungeon.");
            return;
        }

        if (args.length != 1) {
            MessageManager.sendMessage(player, "&cUsage: &7/dungeon accept <player>");
            return;
        }

        Player toJoin = Bukkit.getPlayer(args[0]);
        if (toJoin == null) {
            MessageManager.sendMessage(player, "&cCould not find that player.");
            return;
        }

        DungeonInstance instance = dPlayer.getInstance();
        if (instance.getStage() != DungeonStage.LOBBY) {
            MessageManager.sendMessage(player, "&cYou may not accept players at this moment.");
            return;
        }

        if (!instance.getJoiningPlayers().contains(toJoin.getUniqueId())) {
            MessageManager.sendMessage(player, "&cThat player has not asked to join the dungeon.");
            return;
        }

        MessageManager.sendMessage(player, "&aYou have accepted their request.");

        instance.getPossibleJoins().add(toJoin.getUniqueId());

        new BukkitRunnable() {
            private int timer = 10;
            private final Location current = toJoin.getLocation();
            @Override
            public void run() {
                if (!toJoin.getLocation().getBlock().equals(current.getBlock())) {
                    cancel();
                    instance.getPossibleJoins().remove(toJoin.getUniqueId());
                    instance.getJoiningPlayers().remove(toJoin.getUniqueId());
                    MessageManager.sendMessage(player, "&7" + toJoin.getName() + " &chas failed to join the dungeon.");
                    toJoin.sendTitle(new Title.Builder().title(ChatColor.translateAlternateColorCodes('&', "&cPreparing to exit the dungeon.")).subtitle(ChatColor.translateAlternateColorCodes('&', "&cCancelled")).stay(7).build());
                    return;
                }

                if (timer > 0) {
                    toJoin.sendTitle(new Title.Builder()
                            .title(ChatColor.translateAlternateColorCodes('&', "&cPreparing to exit the dungeon."))
                            .subtitle(ChatColor.translateAlternateColorCodes('&', "&a" + timer
                            )).stay(7).build());
                    timer--;
                } else {
                    toJoin.sendTitle(new Title.Builder().title("").subtitle(ChatColor.translateAlternateColorCodes('&', "&aYou have entered the dungeon")).stay(7).build());
                    instance.onJoinInstance(toJoin);
                    cancel();
                }
            }
        }.runTaskTimer(Carbyne.getInstance(), 0, 20);

    }
}
