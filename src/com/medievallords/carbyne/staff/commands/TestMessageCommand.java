package com.medievallords.carbyne.staff.commands;

import com.medievallords.carbyne.utils.command.BaseCommand;
import com.medievallords.carbyne.utils.command.Command;
import com.medievallords.carbyne.utils.command.CommandArgs;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

public class TestMessageCommand extends BaseCommand {

    @Command(name = "testmessage", aliases = {"tm"}, permission = "carbyne.staff.testmessage")
    public void onCommand(CommandArgs commandArgs) {
        String[] args = commandArgs.getArgs();

        if (args.length >= 1) {
            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', StringUtils.join(args, ' ', 0, args.length)));
        }
    }
}
