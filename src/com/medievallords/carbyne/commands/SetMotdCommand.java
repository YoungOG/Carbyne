package com.medievallords.carbyne.commands;

import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.utils.MessageManager;
import com.medievallords.carbyne.utils.command.BaseCommand;
import com.medievallords.carbyne.utils.command.Command;
import com.medievallords.carbyne.utils.command.CommandArgs;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerListPingEvent;

import java.util.List;

public class SetMotdCommand extends BaseCommand implements Listener {

    private String[] motd;

    public SetMotdCommand() {
        Bukkit.getPluginManager().registerEvents(this, Carbyne.getInstance());

        List<String> initMotd = Carbyne.getInstance().getConfig().getStringList("Motd");
        motd = initMotd.toArray(new String[initMotd.size()]);

        if (motd.length < 1 || motd[0] == null)
            motd = new String[]{"Example", "Motd"};

        for (int i = 0; i < motd.length; i++)
            motd[i] = ChatColor.translateAlternateColorCodes('&', motd[i]);
    }

    @Command(name = "setmotd", aliases = {"motd"})
    public void onCommand(CommandArgs commandArgs) {
        String[] args = commandArgs.getArgs();
        CommandSender sender = commandArgs.getSender();

        if (args.length == 0) {
            MessageManager.sendMessage(sender, "&cUsage: /setmotd <1/2> <message>\n&aCurrent MOTD Line 1: " + motd[0] + "&a, Line 2: " + motd[1]);
            return;
        }

        try {
            Integer index = Integer.parseInt(args[0]);
            String message = StringUtils.join(commandArgs.getArgs(), " ", 1, commandArgs.getArgs().length);

            if (index == 1 || index == 2) {
                if (!sender.hasPermission("carbyne.commands.setmotd")) {
                    MessageManager.sendMessage(sender, "&cYou do not have permission to use this command.");
                    return;
                }

                motd[index] = message;
                Carbyne.getInstance().getConfig().set("Motd", motd);
                Carbyne.getInstance().saveConfig();
                MessageManager.sendMessage(sender, "&aYou have set the Motd Index: &b" + index + " &ato: &b" + message + "&a.");
            } else {
                MessageManager.sendMessage(sender, "&cUsage: /setmotd <1/2> <message>");
            }
        } catch (NumberFormatException ignored) {
            MessageManager.sendMessage(sender, "&cUsage: /setmotd <1/2> <message>");
        }
    }

    @EventHandler
    public void onPing(ServerListPingEvent event) {
        event.setMotd(motd.length > 1 ? StringEscapeUtils.unescapeJava(motd[0]) + "\n" + StringEscapeUtils.unescapeJava(motd[1]) : StringEscapeUtils.unescapeJava(motd[0]));
    }
}
