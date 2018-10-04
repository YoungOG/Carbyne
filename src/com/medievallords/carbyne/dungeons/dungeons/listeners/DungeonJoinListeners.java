package com.medievallords.carbyne.dungeons.dungeons.listeners;

import com.medievallords.carbyne.dungeons.dungeons.Dungeon;
import com.medievallords.carbyne.utils.Cooldowns;
import com.medievallords.carbyne.utils.DateUtil;
import com.medievallords.carbyne.utils.MessageManager;
import com.medievallords.carbyne.utils.StaticClasses;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class DungeonJoinListeners implements Listener {

    @EventHandler
    public void a(PlayerInteractEvent event) {
        if (event.getAction() != Action.PHYSICAL) {
            return;
        }

        Player player = event.getPlayer();

        Dungeon dungeon = StaticClasses.dungeonHandler.getDungeon(event.getClickedBlock().getLocation());
        if (dungeon != null) {
            long cooldown = Cooldowns.getCooldown(player.getUniqueId(), "dungeon:name:" + dungeon.getName());
            if (cooldown > 0) {
                MessageManager.sendMessage(player, "&cYou must wait &7" + DateUtil.readableTime(cooldown, true) + " &cuntil you can enter this dungeon.");
                return;
            }
            dungeon.addPlayer(player, event.getClickedBlock().getLocation());
            dungeon.check();
        }
    }

    @EventHandler
    public void a(BlockRedstoneEvent event) {
        if (event.getOldCurrent() <= 0) {
            return;
        }
        Dungeon dungeon = StaticClasses.dungeonHandler.getDungeon(event.getBlock().getLocation());
        if (dungeon != null) {
            dungeon.removePlayer(event.getBlock().getLocation());
            dungeon.check();
        }
    }
}
