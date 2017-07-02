package com.medievallords.carbyne.commands;

import com.bizarrealex.aether.scoreboard.Board;
import com.bizarrealex.aether.scoreboard.cooldown.BoardCooldown;
import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.listeners.CombatTagListeners;
import com.medievallords.carbyne.profiles.Profile;
import com.medievallords.carbyne.utils.MessageManager;
import com.medievallords.carbyne.utils.command.BaseCommand;
import com.medievallords.carbyne.utils.command.Command;
import com.medievallords.carbyne.utils.command.CommandArgs;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class LogoutCommand extends BaseCommand {

    @Command(name = "logout", aliases = {"disconnect", "log", "dc"})
    public void onCommand(CommandArgs commandArgs) {
        Player player = commandArgs.getPlayer();

        Profile profile = getProfileManager().getProfile(player.getUniqueId());

        if (profile == null) {
            player.kickPlayer(ChatColor.YELLOW + "You have safely logged out.");
            return;
        }

        if (CombatTagListeners.getDisabled().containsKey(player.getUniqueId())) {
            CombatTagListeners.getDisabled().get(player.getUniqueId()).cancel();
        }

        CombatTagListeners.getDisabled().put(player.getUniqueId(), new BukkitRunnable() {
            public void run() {
                profile.setSafelyLogged(true);
                player.kickPlayer(ChatColor.YELLOW + "You have safely logged out.");
            }
        });

        CombatTagListeners.getDisabled().get(player.getUniqueId()).runTaskLater(Carbyne.getInstance(), 15 * 20L);
        MessageManager.sendMessage(player, "&cLogging out of the server! Do not move!");

        CombatTagListeners.getCount().put(player.getUniqueId(), 16);

        CombatTagListeners.getCounters().put(player.getUniqueId(), new BukkitRunnable() {
            public void run() {
                if (CombatTagListeners.getCount().get(player.getUniqueId()) <= 16 && CombatTagListeners.getCount().get(player.getUniqueId()) >= 1) {
                    CombatTagListeners.getCount().put(player.getUniqueId(), CombatTagListeners.getCount().get(player.getUniqueId()) - 1);
                    MessageManager.sendMessage(player, "&cLogging out in &4" + CombatTagListeners.getCount().get(player.getUniqueId()) + "&c..");
                } else {
                    MessageManager.sendMessage(player, "&cLogging out..");
                }
            }
        });

        CombatTagListeners.getCounters().get(player.getUniqueId()).runTaskTimer(getCarbyne(), 0L, 20);

        Board board = Board.getByPlayer(player);

        new BoardCooldown(board, "logout", 15.0D);
    }
}
