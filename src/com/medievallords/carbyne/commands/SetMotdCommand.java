package com.medievallords.carbyne.commands;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerOptions;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.staff.StaffManager;
import com.medievallords.carbyne.utils.MessageManager;
import com.medievallords.carbyne.utils.StaticClasses;
import com.medievallords.carbyne.utils.command.BaseCommand;
import com.medievallords.carbyne.utils.command.Command;
import com.medievallords.carbyne.utils.command.CommandArgs;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SetMotdCommand extends BaseCommand implements Listener {

    private String[] motd;

    public SetMotdCommand() {
        Bukkit.getPluginManager().registerEvents(this, Carbyne.getInstance());

        List<String> initMotd = Carbyne.getInstance().getConfig().getStringList("Motd");
        motd = initMotd.toArray(new String[0]);

        if (motd.length < 1 || motd[0] == null)
            motd = new String[]{"Example", "Motd"};

        for (int i = 0; i < motd.length; i++)
            motd[i] = ChatColor.translateAlternateColorCodes('&', motd[i]);

        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(Carbyne.getInstance(),
                ListenerPriority.NORMAL,
                Collections.singletonList(PacketType.Status.Server.OUT_SERVER_INFO),
                ListenerOptions.ASYNC) {
            public void onPacketSending(final PacketEvent event) {
                List<WrappedGameProfile> profilesToView = new ArrayList<>();
                StaffManager staffManager = StaticClasses.staffManager;
                for (Player player : Bukkit.getOnlinePlayers())
                    if (!staffManager.isVanished(player))
                        profilesToView.add(new WrappedGameProfile("1", ChatColor.translateAlternateColorCodes('&', "&7" + player.getName())));
                event.getPacket().getServerPings().read(0).setPlayersOnline(profilesToView.size());
                event.getPacket().getServerPings().read(0).setMotD(ChatColor.translateAlternateColorCodes('&', motd.length > 1 ? StringEscapeUtils.unescapeJava(motd[0]) + "\n" + StringEscapeUtils.unescapeJava(motd[1]) : StringEscapeUtils.unescapeJava(motd[0])));
                event.getPacket().getServerPings().read(0).setPlayers(profilesToView);
            }
        });
    }

    @Command(name = "reloadmotd", permission = "carbyne.administrator")
    public void execute(CommandArgs commandArgs) {
        Carbyne.getInstance().reloadConfig();
        Carbyne.getInstance().saveConfig();

        List<String> initMotd = Carbyne.getInstance().getConfig().getStringList("Motd");
        motd = initMotd.toArray(new String[0]);

        if (motd.length < 1 || motd[0] == null)
            motd = new String[]{"Example", "Motd"};

        for (int i = 0; i < motd.length; i++)
            motd[i] = ChatColor.translateAlternateColorCodes('&', motd[i]);

        MessageManager.sendMessage(commandArgs.getSender(), "&cThe motd has been changed.");
    }

    @Command(name = "setmotd", aliases = {"motd"}, permission = "carbyne.administrator")
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

                motd[index - 1] = message;
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

    /*@EventHandler
    public void onPing(ServerListPingEvent event) {
        event.setMotd(motd.length > 1 ? StringEscapeUtils.unescapeJava(motd[0]) + "\n" + StringEscapeUtils.unescapeJava(motd[1]) : StringEscapeUtils.unescapeJava(motd[0]));
    }*/
}
