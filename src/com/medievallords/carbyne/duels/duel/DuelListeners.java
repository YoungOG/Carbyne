package com.medievallords.carbyne.duels.duel;

import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.duels.duel.request.DuelRequest;
import com.medievallords.carbyne.gear.GearManager;
import com.medievallords.carbyne.squads.Squad;
import com.medievallords.carbyne.utils.MessageManager;
import org.bukkit.Location;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * Created by xwiena22 on 2017-03-14.
 *
 */
public class DuelListeners implements Listener {

    private DuelManager duelManager = Carbyne.getInstance().getDuelManager();
    private GearManager gearManager = Carbyne.getInstance().getGearManager();
    private HashMap<UUID, Location> toSpawn = new HashMap<>();

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();

        Duel duel = duelManager.getDuelFromUUID(player.getUniqueId());
        if (duel == null) {
            return;
        }

        for (ItemStack itemStack : event.getDrops()) {
            Item item = event.getEntity().getWorld().dropItemNaturally(event.getEntity().getLocation(), itemStack);
            duel.getDrops().add(item);
        }

        event.getDrops().clear();

        toSpawn.put(player.getUniqueId(), duel.getArena().getLobbyLocation());
        duel.getPlayersAlive().remove(player.getUniqueId());

        duel.check();
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        if (toSpawn.containsKey(event.getPlayer().getUniqueId())) {
            event.setRespawnLocation(toSpawn.get(event.getPlayer().getUniqueId()));
            toSpawn.remove(event.getPlayer().getUniqueId());
        }
    }

    @EventHandler
    public void onItemDrop(PlayerDropItemEvent event) {
        Player player = event.getPlayer();

        Duel duel = duelManager.getDuelFromUUID(player.getUniqueId());
        if (duel == null) {
            return;
        }

        duel.getDrops().add(event.getItemDrop());

    }

    @EventHandler
    public void onItemPickup(PlayerPickupItemEvent event) {
        Player player = event.getPlayer();

        Duel duel = duelManager.getDuelFromUUID(player.getUniqueId());
        if (duel == null) {
            return;
        }

        duel.getDrops().remove(event.getItem());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        DuelRequest request = DuelRequest.getRequest(player.getUniqueId());
        Duel duel = duelManager.getDuelFromUUID(player.getUniqueId());

        if (request != null) {
            request.cancel();
        } else if (duel != null) {
            duel.getPlayersAlive().remove(player.getUniqueId());
            duel.check();
        }
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        String[] args = event.getMessage().split(" ");
        Player player = event.getPlayer();

        if (event.getMessage().toLowerCase().startsWith("/aac") && !event.getPlayer().hasPermission("carbyne.aac")) {
            event.setCancelled(true);
            return;
        }

        DuelRequest request = DuelRequest.getRequest(player.getUniqueId());
        Duel duel = duelManager.getDuelFromUUID(player.getUniqueId());
        List<String> commands = Carbyne.getInstance().getConfig().getStringList("duel-disabled-commands");

        if (duel != null || request != null) {
            if (commands.contains(args[0])) {
                event.setCancelled(true);
                MessageManager.sendMessage(player, "&cYou can not use this command whilst in the duel");
                return;
            }
        }

        Squad squad = Carbyne.getInstance().getSquadManager().getSquad(player.getUniqueId());

        if (squad == null) {
            return;
        }

        boolean squadInDuel = false;

        for (UUID uuid : squad.getAllPlayers()) {
            DuelRequest requestTo = DuelRequest.getRequest(player.getUniqueId());
            Duel duelTo = duelManager.getDuelFromUUID(player.getUniqueId());

            if (duel != null || request != null) {
                squadInDuel = true;
                break;
            }
        }

        if (squadInDuel) {
            if (commands.contains(args[0])) {
                event.setCancelled(true);
                MessageManager.sendMessage(player, "&cYou can not use this command whilst in the duel");
            }
        }
    }
}