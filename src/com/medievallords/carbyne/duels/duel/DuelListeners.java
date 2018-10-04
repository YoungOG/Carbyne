package com.medievallords.carbyne.duels.duel;

import com.boydti.fawe.util.TaskManager;
import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.duels.arena.Arena;
import com.medievallords.carbyne.duels.duel.request.DuelRequest;
import com.medievallords.carbyne.squads.Squad;
import com.medievallords.carbyne.utils.MessageManager;
import com.medievallords.carbyne.utils.PlayerHealth;
import com.medievallords.carbyne.utils.StaticClasses;
import com.nisovin.magicspells.BuffManager;
import com.nisovin.magicspells.events.SpellTargetEvent;
import org.bukkit.Location;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by xwiena22 on 2017-03-14.
 */
public class DuelListeners implements Listener {

    private HashMap<UUID, Location> toSpawn = new HashMap<>();

    /*
    HIDE:
    - Potions
    - Any projectile (Arrows, Snowballs, Eggs etc)
    - Players
    - Spells
    - Messages (from spells)
    - Custom Effects (Carbyne)

     */

//
//    @EventHandler
//    public void onDeath(PlayerDeathEvent event) {
//        Player player = event.getEntity();
//
//        Duel duel = StaticClasses.duelManager.getDuelFromUUID(player.getUniqueId());
//        if (duel == null)
//            return;
//
//        for (ItemStack itemStack : event.getDrops()) {
//            Item item = event.getEntity().getWorld().dropItemNaturally(event.getEntity().getLocation(), itemStack);
//            duel.getDrops().add(item);
//        }
//
//        event.getDrops().clear();
//
//        toSpawn.put(player.getUniqueId(), duel.getArena().getLobbyLocation());
//        duel.getPlayersAlive().remove(player.getUniqueId());
//
//        duel.check();
//    }
//
//    @EventHandler
//    public void onRespawn(PlayerRespawnEvent event) {
//        if (toSpawn.containsKey(event.getPlayer().getUniqueId())) {
//            Location spawn = toSpawn.get(event.getPlayer().getUniqueId());
//            toSpawn.remove(event.getPlayer().getUniqueId());
//            TaskManager.IMP.later(new Runnable() {
//                @Override
//                public void run() {
//                    event.getPlayer().teleport(spawn);
//                }
//            }, 30);
//        }
//    }
//
//    @EventHandler
//    public void onItemDrop(PlayerDropItemEvent event) {
//        Player player = event.getPlayer();
//
//        Duel duel = StaticClasses.duelManager.getDuelFromUUID(player.getUniqueId());
//        if (duel == null)
//            return;
//
//        duel.getDrops().add(event.getItemDrop());
//
//    }
//
//    @EventHandler(priority = EventPriority.HIGH)
//    public void onMagic(SpellTargetEvent event) {
//        if (StaticClasses.duelManager.getDuelFromUUID(event.getCaster().getUniqueId()) != null && event.getTarget() instanceof Player) {
//            Player target = (Player) event.getTarget();
//
//            Squad casterS = StaticClasses.squadManager.getSquad(event.getCaster().getUniqueId());
//            Squad targetS = StaticClasses.squadManager.getSquad(target.getUniqueId());
//
//            if (casterS != null && casterS.equals(targetS)) {
//                event.setCancelled(true);
//                return;
//            }
//
//            if (StaticClasses.duelManager.getDuelFromUUID(target.getUniqueId()) != null)
//                event.setCancelled(false);
//        }
//    }
//
//    @EventHandler
//    public void onItemPickup(PlayerPickupItemEvent event) {
//        Player player = event.getPlayer();
//
//        Duel duel = StaticClasses.duelManager.getDuelFromUUID(player.getUniqueId());
//        if (duel == null)
//            return;
//
//        duel.getDrops().remove(event.getItem());
//    }
//
//    @EventHandler
//    public void onPlayerQuit(PlayerQuitEvent event) {
//        Player player = event.getPlayer();
//
////        DuelRequest request = DuelRequest.getRequest(player.getUniqueId());
////        Duel duel = StaticClasses.duelManager.getDuelFromUUID(player.getUniqueId());
////
////        if (request != null)
////            request.cancel();
////        else if (duel != null)
////            player.setHealth(0);
//    }
//
//    @EventHandler
//    public void onCommand(PlayerCommandPreprocessEvent event) {
//        String[] args = event.getMessage().replace("/", "").split(" ");
//        Player player = event.getPlayer();
//        List<String> commands = Carbyne.getInstance().getConfig().getStringList("duel-disabled-commands");
//
//        for (Arena arena : StaticClasses.duelManager.getArenas())
//            if (arena.getDuelists().contains(event.getPlayer().getUniqueId()))
//                if (commands.contains(args[0].toLowerCase())) {
//                    event.setCancelled(true);
//                    MessageManager.sendMessage(player, "&cYou can not use this command whilst in the duel");
//                    return;
//                }
//
//        if (event.getMessage().toLowerCase().startsWith("/aac") && !event.getPlayer().hasPermission("carbyne.aac")) {
//            event.setCancelled(true);
//            return;
//        }
//
////        DuelRequest request = DuelRequest.getRequest(player.getUniqueId());
////        Duel duel = StaticClasses.duelManager.getDuelFromUUID(player.getUniqueId());
////
////        if (duel != null || request != null)
////            if (commands.contains(args[0].toLowerCase())) {
////                event.setCancelled(true);
////                MessageManager.sendMessage(player, "&cYou can not use this command whilst in the duel");
////            }
//    }
}