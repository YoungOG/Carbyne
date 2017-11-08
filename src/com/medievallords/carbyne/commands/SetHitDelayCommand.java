package com.medievallords.carbyne.commands;

import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.utils.MessageManager;
import com.medievallords.carbyne.utils.PlayerUtility;
import com.medievallords.carbyne.utils.command.BaseCommand;
import com.medievallords.carbyne.utils.command.Command;
import com.medievallords.carbyne.utils.command.CommandArgs;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class SetHitDelayCommand extends BaseCommand implements Listener {

    private int hitDelayTicks;

    public SetHitDelayCommand() {
        Bukkit.getPluginManager().registerEvents(this, Carbyne.getInstance());

        hitDelayTicks = Carbyne.getInstance().getConfig().getInt("HitDelay");
    }

    @Command(name = "sethitdelay", aliases = {"hitdelay"})
    public void onCommand(CommandArgs commandArgs) {
        String[] args = commandArgs.getArgs();
        CommandSender sender = commandArgs.getSender();

        if (args.length >= 1) {
            MessageManager.sendMessage(sender, "&cUsage: /sethitdelay <delay-in-ticks/reload>");
            return;
        }

        if (args.length == 0) {
            MessageManager.sendMessage(sender, "&aThe current HitDelay is set to &b" + hitDelayTicks + " ticks&a.");
        }

        if (!sender.hasPermission("carbyne.commands.setdelay")) {
            MessageManager.sendMessage(sender, "&cYou do not have permission to use this command.");
            return;
        }

        if (args[0].equalsIgnoreCase("reload")) {
            hitDelayTicks = Carbyne.getInstance().getConfig().getInt("HitDelay");

            for (Player all : PlayerUtility.getOnlinePlayers()) {
                all.setMaximumNoDamageTicks(hitDelayTicks);
            }

            MessageManager.sendMessage(sender, "&aYou have reloaded the config.");

            return;
        }

        try {
            hitDelayTicks = Integer.parseInt(args[0]);
            for (Player all : PlayerUtility.getOnlinePlayers()) {
                all.setMaximumNoDamageTicks(hitDelayTicks);
            }

            MessageManager.sendMessage(sender, "&aYou have set the HitDelay to &b" + hitDelayTicks + " ticks&a.");
        } catch (NumberFormatException ignored) {
            MessageManager.sendMessage(sender, "&cUsage: /sethitdelay <delay-in-ticks>");
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        e.getPlayer().setMaximumNoDamageTicks(hitDelayTicks);
    }
}
