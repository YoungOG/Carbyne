package com.medievallords.carbyne.dungeons.dungeons.commands;

import com.destroystokyo.paper.Title;
import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.dungeons.dungeons.instance.DungeonInstance;
import com.medievallords.carbyne.dungeons.player.DPlayer;
import com.medievallords.carbyne.utils.MessageManager;
import com.medievallords.carbyne.utils.StaticClasses;
import com.medievallords.carbyne.utils.command.BaseCommand;
import com.medievallords.carbyne.utils.command.Command;
import com.medievallords.carbyne.utils.command.CommandArgs;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class DungeonLeaveCommand extends BaseCommand {

    @Command(name = "dungeon.leave", inGameOnly = true)
    public void execute(CommandArgs commandArgs) {
        Player player = commandArgs.getPlayer();
        String[] args = commandArgs.getArgs();

        DPlayer dPlayer = StaticClasses.dungeonHandler.getDPlayer(player.getUniqueId());
        if (dPlayer == null) {
            MessageManager.sendMessage(player, "&cYou can only use this command in a dungeon.");
            return;
        }

        if (dPlayer.isInCombat()) {
            MessageManager.sendMessage(dPlayer.getBukkitPlayer(), "&cYou cannot leave while you are in combat.");
            return;
        }

        DungeonInstance instance = dPlayer.getInstance();
        if (player.isOp()) {
            player.sendTitle(new Title.Builder().title("").subtitle(ChatColor.translateAlternateColorCodes('&', "&aYou have left the dungeon")).stay(7).build());
            instance.onLeave(dPlayer);
        } else {
            new BukkitRunnable() {
                private int timer = 5;
                private final Location current = player.getLocation();
                @Override
                public void run() {
                    if (!player.getLocation().getBlock().equals(current.getBlock())) {
                        cancel();
                        player.sendTitle(new Title.Builder().title(ChatColor.translateAlternateColorCodes('&', "&cPreparing to exit the dungeon.")).subtitle(ChatColor.translateAlternateColorCodes('&', "&cCancelled")).stay(7).build());
                        return;
                    }

                    if (timer > 0) {
                        player.sendTitle(new Title.Builder()
                                .title(ChatColor.translateAlternateColorCodes('&', "&cPreparing to exit the dungeon."))
                                .subtitle(ChatColor.translateAlternateColorCodes('&', "&a" + timer
                        )).stay(7).build());
                        timer--;
                    } else {
                        player.sendTitle(new Title.Builder().title("").subtitle(ChatColor.translateAlternateColorCodes('&', "&aYou have left the dungeon")).stay(7).build());
                        instance.onLeave(dPlayer);
                        cancel();
                    }
                }
            }.runTaskTimer(Carbyne.getInstance(), 0, 20);
        }
    }
}
