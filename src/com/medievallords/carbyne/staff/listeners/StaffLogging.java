package com.medievallords.carbyne.staff.listeners;

import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.utils.webhook.DiscordMessage;
import com.medievallords.carbyne.utils.webhook.StaffHook;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by Dalton on 9/7/2017
 * for the Carbyne project.
 */
public class StaffLogging implements Listener {

    private final StaffHook staffHook;
    private final String username = "Staff Logger";
    private final String image = "https://i.stack.imgur.com/oZidd.jpg";

    private final List<String> blackList = Arrays.asList("t", "n", "town", "nation");

    public StaffLogging() {
        staffHook = new StaffHook("https://discordapp.com/api/webhooks/367488913650089985/mOd3IIQIQ4lAxiC6N-qOSDc7WHhlswTeAEYzIjTXNcj0QTv8VLX42oAlXDAwBtRU6PDP");
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        final Player player = event.getPlayer();

        if (!player.hasPermission("carbyne.staff.logging"))
            return;

        final String message = event.getMessage();
        new BukkitRunnable() {
            public void run() {
                for (String black : blackList)
                    if (message.startsWith(black))
                        return;

                staffHook.sendMessage(new DiscordMessage(username, new SimpleDateFormat("hh:mm a").format(new Date()) + " - " + player.getName() + ": " + message, image));
            }

        }.runTaskAsynchronously(Carbyne.getInstance());
    }
}
