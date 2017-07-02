package com.medievallords.carbyne.commands;

import com.medievallords.carbyne.utils.MessageManager;
import com.medievallords.carbyne.utils.command.BaseCommand;
import com.medievallords.carbyne.utils.command.Command;
import com.medievallords.carbyne.utils.command.CommandArgs;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Created by Williams on 2017-04-02.
 * for the Carbyne project.
 */
public class ChatCommand extends BaseCommand {

    @Command(name = "chat", permission = "carbyne.administrator")
    public void execute(CommandArgs commandArgs) {
        String[] args = commandArgs.getArgs();
        CommandSender sender = commandArgs.getSender();

        if (args.length <= 1) {
            MessageManager.sendMessage(sender, "&cUsage: /chat <player> <msg>");
        }

        StringBuilder msg = new StringBuilder();
        for (int i = 1; i < args.length; i++) {
            msg.append(args[i]).append(" ");
        }

        Player player = Bukkit.getServer().getPlayer(args[0]);
        if (player == null) {
            MessageManager.sendMessage(sender,"&cCould not find that player");
            return;
        }

        player.chat(ChatColor.translateAlternateColorCodes('&', msg.toString()));
    }
}
