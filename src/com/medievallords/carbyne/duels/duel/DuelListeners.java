package com.medievallords.carbyne.duels.duel;

import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.duels.arena.Arena;
import com.medievallords.carbyne.duels.duel.request.DuelRequest;
import com.medievallords.carbyne.gear.GearManager;
import com.medievallords.carbyne.gear.types.carbyne.CarbyneArmor;
import com.medievallords.carbyne.gear.types.minecraft.MinecraftArmor;
import com.medievallords.carbyne.squads.Squad;
import com.medievallords.carbyne.utils.MessageManager;
import com.nisovin.magicspells.events.SpellTargetEvent;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
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


    @EventHandler(priority = EventPriority.HIGHEST)
    public void onSquadHit(EntityDamageByEntityEvent event) {

    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();

            if (duelManager.getDuelFromUUID(player.getUniqueId()) == null) {
                return;
            }

            if (duelManager.getDuelFromUUID(player.getUniqueId()).isEnded()) {
                event.setCancelled(true);
                return;
            }

            event.setCancelled(false);

            //Player dead?
            if (player.isDead()) {
                event.setCancelled(true);
                return;
            }

            double armorReduction = 0.0;

            //Get DamageReduction values from all peices of currently worn armor.
            for (ItemStack itemStack : player.getInventory().getArmorContents()) {
                if (itemStack.getType().equals(Material.AIR))
                    continue;

                if (gearManager.isCarbyneArmor(itemStack)) {
                    CarbyneArmor carbyneArmor = gearManager.getCarbyneArmor(itemStack);

                    if (carbyneArmor != null) {
                        armorReduction = armorReduction + carbyneArmor.getArmorRating();
                    }
                }

                if (gearManager.isDefaultArmor(itemStack)) {
                    MinecraftArmor minecraftArmor = gearManager.getDefaultArmor(itemStack);

                    if (minecraftArmor != null) {
                        armorReduction = armorReduction + minecraftArmor.getArmorRating();
                    }
                }
            }

            if (armorReduction > 0) {
                double flatDamage = 0.0;

                //Calculation of certain DamageCauses for precise balancing.
                switch (event.getCause()) {
                    case FIRE_TICK:
                        flatDamage = 0.5;
                        break;
                    case LAVA:
                        flatDamage = 2.0;
                        break;
                    case LIGHTNING:
                        flatDamage = 5.0;
                        break;
                    case DROWNING:
                        flatDamage = 2.0;
                        break;
                    case STARVATION:
                        flatDamage = 0.5;
                        break;
                    case VOID:
                        flatDamage = 4.0;
                        break;
                    case POISON:
                        flatDamage = 0.5;
                        break;
                    case WITHER:
                        flatDamage = 0.5;
                        break;
                    case SUFFOCATION:
                        flatDamage = 0.5;
                        break;
                    case FALL:
                        flatDamage = event.getDamage() - event.getDamage() * (armorReduction - 0.40);
                        break;
                }

                double damage = (flatDamage - (flatDamage * (armorReduction > 0.50 ? armorReduction - 0.50 : 0.0)) <= 0 ? (event.getDamage() - (event.getDamage() * (armorReduction + getProtectionReduction(player)))) : flatDamage);

                if (damage >= player.getHealth()) {
                    event.setCancelled(true);
                    player.setHealth(0);
                    //player.damage(damage);
                    return;
                }

                event.setDamage(0);
                player.damage(damage);
            }
        }
    }

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

    @EventHandler(priority = EventPriority.HIGH)
    public void onMagic(SpellTargetEvent event) {
        if (duelManager.getDuelFromUUID(event.getCaster().getUniqueId()) != null && event.getTarget() instanceof Player) {
            Player target = (Player) event.getTarget();

            Squad casterS = Carbyne.getInstance().getSquadManager().getSquad(event.getCaster().getUniqueId());
            Squad targetS = Carbyne.getInstance().getSquadManager().getSquad(target.getUniqueId());

            if (casterS != null && targetS != null && casterS.equals(targetS)) {
                event.setCancelled(true);
                return;
            }

            if (duelManager.getDuelFromUUID(target.getUniqueId()) != null) {
                event.setCancelled(false);
            }
        }
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
            List<ItemStack> drops = new ArrayList<>();
            for (ItemStack itemStack : player.getInventory().getContents()) {
                if (itemStack != null && itemStack.getType() != Material.AIR) {
                    drops.add(itemStack);
                }
            }

            for (ItemStack itemStack : player.getInventory().getArmorContents()) {
                if (itemStack != null && itemStack.getType() != Material.AIR) {
                    drops.add(itemStack);
                }
            }

            for (ItemStack itemStack : drops) {
                Item item = event.getPlayer().getWorld().dropItemNaturally(event.getPlayer().getLocation(), itemStack);
                duel.getDrops().add(item);
            }

            player.getInventory().clear();

            duel.getPlayersAlive().remove(player.getUniqueId());
            duel.check();
        }
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        String[] args = event.getMessage().split(" ");
        Player player = event.getPlayer();
        List<String> commands = Carbyne.getInstance().getConfig().getStringList("duel-disabled-commands");

        for (Arena arena : duelManager.getArenas()) {
            if (arena.getDuelists().contains(event.getPlayer().getUniqueId())) {
                if (commands.contains(args[0])) {
                    event.setCancelled(true);
                    MessageManager.sendMessage(player, "&cYou can not use this command whilst in the duel");
                    return;
                }
            }
        }

        if (event.getMessage().toLowerCase().startsWith("/aac") && !event.getPlayer().hasPermission("carbyne.aac")) {
            event.setCancelled(true);
            return;
        }

        DuelRequest request = DuelRequest.getRequest(player.getUniqueId());
        Duel duel = duelManager.getDuelFromUUID(player.getUniqueId());

        if (duel != null || request != null) {
            if (commands.contains(args[0])) {
                event.setCancelled(true);
                MessageManager.sendMessage(player, "&cYou can not use this command whilst in the duel");
                return;
            }
        }

        /*Squad squad = Carbyne.getInstance().getSquadManager().getSquad(player.getUniqueId());

        if (squad == null) {
            return;
        }

        boolean squadInDuel = false;

        for (UUID uuid : squad.getAllPlayers()) {
            DuelRequest requestTo = DuelRequest.getRequest(uuid);
            Duel duelTo = duelManager.getDuelFromUUID(uuid);

            if (duelTo != null || requestTo != null) {
                squadInDuel = true;
                break;
            }
        }

        if (squadInDuel) {
            if (commands.contains(args[0])) {
                event.setCancelled(true);
                MessageManager.sendMessage(player, "&cYou can not use this command whilst in the duel");
            }
        }*/
    }


    public double getProtectionReduction(Player player) {
        double damageReduction = 0.0;

        for (ItemStack is : player.getInventory().getArmorContents()) {
            if (is.getType().equals(Material.AIR))
                continue;

            switch (is.getEnchantmentLevel(Enchantment.PROTECTION_ENVIRONMENTAL)) {
                case 1:
                    if (is.getType().toString().contains("HELMET")) {
                        damageReduction += 0.015;
                    } else if (is.getType().toString().contains("CHESTPLATE")) {
                        damageReduction += 0.04;
                    } else if (is.getType().toString().contains("LEGGINGS")) {
                        damageReduction += 0.03;
                    } else if (is.getType().toString().contains("BOOTS")) {
                        damageReduction += 0.015;
                    }
                    break;
                case 2:
                    if (is.getType().toString().contains("HELMET")) {
                        damageReduction += 0.03;
                    } else if (is.getType().toString().contains("CHESTPLATE")) {
                        damageReduction += 0.08;
                    } else if (is.getType().toString().contains("LEGGINGS")) {
                        damageReduction += 0.06;
                    } else if (is.getType().toString().contains("BOOTS")) {
                        damageReduction += 0.03;
                    }
                    break;
            }
        }

        return damageReduction;
    }
}