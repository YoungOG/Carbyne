package com.medievallords.carbyne.duels.duel;

import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.duels.arena.Arena;
import com.medievallords.carbyne.duels.duel.request.DuelRequest;
import com.medievallords.carbyne.gear.GearManager;
import com.medievallords.carbyne.gear.types.CarbyneGear;
import com.medievallords.carbyne.gear.types.carbyne.CarbyneArmor;
import com.medievallords.carbyne.gear.types.carbyne.CarbyneWeapon;
import com.medievallords.carbyne.squads.Squad;
import com.medievallords.carbyne.utils.MessageManager;
import com.nisovin.magicspells.events.SpellTargetEvent;
import org.bukkit.EntityEffect;
import org.bukkit.Location;
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

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static com.medievallords.carbyne.duels.duel.DuelStage.FIGHTING;

/**
 * Created by xwiena22 on 2017-03-14.
 *
 */
public class DuelListeners implements Listener {

    private DuelManager duelManager = Carbyne.getInstance().getDuelManager();
    private GearManager gearManager = Carbyne.getInstance().getGearManager();
    private HashMap<UUID, Location> toSpawn = new HashMap<>();

    @EventHandler(priority = EventPriority.HIGH)
    public void onDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();

            Duel duel = duelManager.getDuelFromUUID(player.getUniqueId());

            if (duel == null) {
                return;
            }

            if (duel.getDuelStage() != FIGHTING) {
                event.setCancelled(true);
                return;
            }

            if (duelManager.getDuelFromUUID(player.getUniqueId()).isEnded()) {
                event.setCancelled(true);
                return;
            }

            event.setCancelled(false);

            if (player.isDead()) {
                event.setCancelled(true);
                return;
            }

            double armorReduction = gearManager.getDamageReduction(player);

            if (armorReduction > 0) {
                float flatDamage = 0.0f;

                //Calculation of certain DamageCauses for precise balancing.
                switch (event.getCause()) {
                    case FIRE_TICK:
                        flatDamage = 0.5f;
                        break;
                    case LAVA:
                        flatDamage = 1.0f;
                        break;
                    case LIGHTNING:
                        flatDamage = 5.0f;
                        break;
                    case DROWNING:
                        flatDamage = 2.0f;
                        break;
                    case STARVATION:
                        flatDamage = 0.5f;
                        break;
                    case VOID:
                        flatDamage = 4.0f;
                        break;
                    case POISON:
                        flatDamage = 0.35f;
                        break;
                    case WITHER:
                        flatDamage = 0.35f;
                        break;
                    case SUFFOCATION:
                        flatDamage = 0.5f;
                        break;
                    case FALL:
                        flatDamage = ((float) (event.getDamage() - event.getDamage() * (armorReduction - 0.10f))) * gearManager.getFeatherFallingCalculation(player);
                        break;
                }

                flatDamage *= 5;

                float eventDamage = (float) event.getDamage() * 5;

                float damage = (float) (flatDamage - (flatDamage * (armorReduction > 0.50 ? armorReduction - 0.50 : 0.0)) <= 0 ? (eventDamage - (eventDamage * (armorReduction + gearManager.getProtectionReduction(player)))) : flatDamage);


                //event.setDamage(damage);
                event.setDamage(0);

                damage = gearManager.calculatePotionEffects(damage, player);

                if (player.getHealth() <= damage) {
                    event.setCancelled(true);
                    player.setHealth(0);
                } else {
                    System.out.print("Damage: " + (player.getHealth() - damage));
                    player.setHealth(player.getHealth() - damage);
                    player.playEffect(EntityEffect.HURT);
                    //player.damage(damage);
                }
            } else {
                event.setDamage(event.getDamage() * 5);
            }
        }
    }

    @EventHandler()
    public void onEntityDamagebyEntity(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player) {

            Player damaged = (Player) event.getEntity();
            if (duelManager.getDuelFromUUID(damaged.getUniqueId()) == null) {
                return;
            }

            for (ItemStack itemStack : damaged.getInventory().getArmorContents()) {
                if (gearManager.isCarbyneArmor(itemStack) || gearManager.isCarbyneWeapon(itemStack)) {
                    CarbyneGear carbyneGear = gearManager.getCarbyneGear(itemStack);

                    if (carbyneGear != null) {
                        if (carbyneGear instanceof CarbyneArmor) {
                            CarbyneArmor carbyneArmor = (CarbyneArmor) carbyneGear;

                            //if (itemStack.getType().equals(Material.LEATHER_CHESTPLATE) || itemStack.getType().equals(Material.LEATHER_LEGGINGS)) {
                            if (carbyneArmor.getDefensivePotionEffects().size() > 0) {
                                carbyneArmor.applyDefensiveEffect(damaged);
                            }

                            if (carbyneArmor.getOffensivePotionEffects().size() > 0) {
                                if (event.getDamager() != null && event.getDamager() instanceof Player) {
                                    carbyneArmor.applyOffensiveEffect((Player) event.getDamager());
                                }
                            }

                            carbyneArmor.damageItem(damaged, itemStack);
                        }

                        if (carbyneGear instanceof CarbyneWeapon) {
                            CarbyneWeapon carbyneWeapon = (CarbyneWeapon) carbyneGear;

                            carbyneWeapon.damageItem(damaged, itemStack);
                        }
                    }
                }

                /*if (gearManager.isDefaultArmor(itemStack) || gearManager.isDefaultWeapon(itemStack)) {
                    if (gearManager.isDefaultArmor(itemStack)) {
                        MinecraftArmor minecraftArmor = gearManager.getDefaultArmor(itemStack);

                        minecraftArmor.damageItem(damaged, itemStack);
                    }

                    if (gearManager.isDefaultWeapon(itemStack)) {
                        MinecraftWeapon minecraftWeapon = gearManager.getDefaultWeapon(itemStack);

                        minecraftWeapon.damageItem(damaged, itemStack);
                    }
                }*/
            }
        }

        if (event.getDamager() instanceof Player) {
            Player damager = (Player) event.getDamager();
            ItemStack itemStack = damager.getItemInHand();

            if (gearManager.isCarbyneWeapon(itemStack)) {
                CarbyneWeapon carbyneWeapon = gearManager.getCarbyneWeapon(itemStack);

                if (carbyneWeapon != null) {
                    if (event.getEntity() != null && event.getEntity() instanceof Player) {
                        if (carbyneWeapon.getOffensivePotionEffects().size() > 0) {
                            carbyneWeapon.applyOffensiveEffect((Player) event.getEntity());
                        }
                    }

                    if (carbyneWeapon.getDefensivePotionEffects().size() > 0) {
                        carbyneWeapon.applyDefensiveEffect(damager);
                    }

                    carbyneWeapon.damageItem(damager, itemStack);
                }
            }

            /*if (gearManager.isDefaultWeapon(itemStack)) {
                MinecraftWeapon minecraftWeapon = gearManager.getDefaultWeapon(itemStack);

                minecraftWeapon.damageItem(damager, itemStack);
            }*/
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
            player.setHealth(0);
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
}