package com.medievallords.carbyne.gear.listeners;


import com.codingforcookies.armorequip.ArmorEquipEvent;
import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.customevents.CarbyneRepairedEvent;
import com.medievallords.carbyne.dungeons.player.DPlayer;
import com.medievallords.carbyne.gear.GearManager;
import com.medievallords.carbyne.gear.effects.WeakenedEffect;
import com.medievallords.carbyne.gear.types.CarbyneGear;
import com.medievallords.carbyne.gear.types.carbyne.CarbyneArmor;
import com.medievallords.carbyne.gear.types.carbyne.CarbyneWeapon;
import com.medievallords.carbyne.gear.types.minecraft.MinecraftArmor;
import com.medievallords.carbyne.gear.types.minecraft.MinecraftWeapon;
import com.medievallords.carbyne.region.RegionUser;
import com.medievallords.carbyne.region.Selection;
import com.medievallords.carbyne.utils.*;
import com.medievallords.carbyne.utils.scoreboard.Board;
import com.medievallords.carbyne.utils.scoreboard.BoardCooldown;
import com.medievallords.carbyne.utils.scoreboard.BoardFormat;
import com.medievallords.carbyne.zones.Zone;
import com.palmergames.bukkit.towny.event.PlayerChangePlotEvent;
import com.palmergames.bukkit.towny.object.TownyUniverse;
import org.apache.commons.lang.WordUtils;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class GearListeners implements Listener {

    private HashMap<UUID, WeakenedEffect> weakEffects = new HashMap<>();
    private HashMap<UUID, LastData> lastHit = new HashMap<>();
    private Set<Hologram> activeHolograms = new HashSet<>();
    private DecimalFormat df = new DecimalFormat("#.#");

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();

            PlayerHealth playerHealth = PlayerHealth.getPlayerHealth(player.getUniqueId());

            if (playerHealth.getHealth() < playerHealth.getMaxHealth() * 0.1 && !weakEffects.containsKey(player.getUniqueId())) {
                WeakenedEffect weakenedEffect = new WeakenedEffect(player);
                weakenedEffect.runTaskTimerAsynchronously(Carbyne.getInstance(), 0, 1);
                weakEffects.put(player.getUniqueId(), weakenedEffect);
            }

            if (player.isDead()) {
                event.setCancelled(true);
                return;
            }

//            Duel duel = StaticClasses.duelManager.getDuelFromUUID(player.getUniqueId());
//            if (duel != null && duel.getDuelStage().equals(DuelStage.FIGHTING)) {
//                event.setCancelled(false);
//            } else
            if (TownyUniverse.getTownBlock(player.getLocation()) != null && !TownyUniverse.getTownBlock(player.getLocation()).getPermissions().pvp) {
                event.setCancelled(true);
                return;
            } else if (event.isCancelled()) {
                return;
            }

            if (event.getCause() == EntityDamageEvent.DamageCause.VOID)
                if (playerHealth.getHealth() <= GearManager.VOID_DAMAGE * playerHealth.getMaxHealth())
                    handleVoidDeath(player);

            StaticClasses.gearManager.calculateDamage(event, playerHealth, player);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void preventVoidDamage(EntityDamageEvent event) {
        if (!event.getCause().equals(EntityDamageEvent.DamageCause.VOID)) {
            return;
        }

        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();

            if (TownyUniverse.getTownBlock(player.getLocation()) != null && !TownyUniverse.getTownBlock(player.getLocation()).getPermissions().pvp) {
                event.setCancelled(true);
                Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "spawn " + player.getName());
            }
        }

    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        new BukkitRunnable() {
            @Override
            public void run() {
                double maxHealth = StaticClasses.gearManager.calculateMaxHealth(event.getPlayer(), false);
                PlayerHealth playerHealth = PlayerHealth.getPlayerHealth(event.getPlayer().getUniqueId());
                playerHealth.setMaxHealth(maxHealth);
                playerHealth.setHealth(maxHealth, event.getPlayer());
            }
        }.runTaskLater(Carbyne.getInstance(), 5);
    }

    @EventHandler
    public void onEquip(ArmorEquipEvent event) {
        Player player = event.getPlayer();
        player.playSound(player.getLocation(), Sound.ENTITY_ENDERDRAGON_FLAP, 1, (float) Math.random() * 3);

        if (event.getOldArmorPiece() != null && event.getOldArmorPiece().getType() != Material.AIR) {
            ItemStack item = event.getOldArmorPiece();

            if (StaticClasses.gearManager.isCarbyneArmor(item)) {
                CarbyneArmor armor = StaticClasses.gearManager.getCarbyneArmor(item);

                if (armor.isPolished(item)) {
                    ItemMeta meta = item.getItemMeta();
                    LeatherArmorMeta lameta = (LeatherArmorMeta) meta;
                    lameta.setColor(armor.getMinFadeColor());
                    item.setItemMeta(lameta);
                } else {
                    ItemMeta meta = item.getItemMeta();
                    LeatherArmorMeta lameta = (LeatherArmorMeta) meta;
                    lameta.setColor(armor.getBaseColor());
                    item.setItemMeta(lameta);
                }
            }
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                Zone zone = StaticClasses.zoneManager.getZone(player.getLocation());
                double maxHealth = StaticClasses.gearManager.calculateMaxHealth(player, zone != null && zone.isNerfedZone());
                PlayerHealth playerHealth = PlayerHealth.getPlayerHealth(player.getUniqueId());
                double toSet = playerHealth.getHealth();
                if (TownyUniverse.getTownBlock(player.getLocation()) != null && !TownyUniverse.getTownBlock(player.getLocation()).getPermissions().pvp) {
                    toSet = (playerHealth.getHealth() / playerHealth.getMaxHealth()) * maxHealth;
                }

                playerHealth.setMaxHealth(maxHealth);
                playerHealth.setHealth(toSet, player);

                MessageManager.sendMessage(player, "&7[&aCarbyne&7]: &7Your new max health is &5" + maxHealth + "&7.");
            }
        }.runTaskLater(Carbyne.getInstance(), 10);
    }

    @EventHandler
    public void onVoidQuit(PlayerQuitEvent event) {
        if (getBlockBelowLoc(event.getPlayer().getLocation().clone()) == null)
            if (lastHit.containsKey(event.getPlayer().getUniqueId())) {
                handleVoidDeath(event.getPlayer());
                PlayerHealth.getPlayerHealth(event.getPlayer().getUniqueId()).setHealth(0, event.getPlayer());
            }
    }

    @EventHandler
    public void onHealthRegain(EntityRegainHealthEvent event) {
        if (event.getEntityType() == EntityType.PLAYER) {
            Player player = (Player) event.getEntity();
            PlayerHealth playerHealth = PlayerHealth.getPlayerHealth(player.getUniqueId());

            if (event.getRegainReason() == EntityRegainHealthEvent.RegainReason.SATIATED) {
                event.setCancelled(true);
                double health = playerHealth.getMaxHealth() / 25.0;
                playerHealth.setHealth(playerHealth.getHealth() + health, player);
            } else if (event.getRegainReason() == EntityRegainHealthEvent.RegainReason.MAGIC) {
                event.setCancelled(true);
                playerHealth.setHealth(playerHealth.getHealth() + GearManager.POTION_HEALING, player);
                displayIndicator(player.getLocation().add(0, 2.2D, 0), (GearManager.POTION_HEALING), false);
            } else if (event.getRegainReason() == EntityRegainHealthEvent.RegainReason.MAGIC_REGEN) {
                event.setCancelled(true);
                double modifier = 0;

                if (player.hasPotionEffect(PotionEffectType.REGENERATION))
                    for (PotionEffect effect : player.getActivePotionEffects())
                        if (effect.getType().equals(PotionEffectType.REGENERATION)) {
                            modifier = (GearManager.REGENERATION * (effect.getAmplifier() + 1));
                            break;
                        }

                playerHealth.setHealth(playerHealth.getHealth() + (modifier * playerHealth.getMaxHealth()), player);

                displayIndicator(player.getLocation().add(0, 2.2D, 0), (modifier * playerHealth.getMaxHealth()), false);
            }
        }
    }

    @EventHandler
    public void onRodThrow(PlayerFishEvent event) {
        if (event.getCaught() != null && event.getCaught() instanceof Player) {
            Player caught = (Player) event.getCaught();
            Vector direction = caught.getLocation().toVector().subtract(event.getPlayer().getLocation().toVector()).normalize().multiply(1.3);
            direction.setX(direction.getX() * -1);
            direction.setY(direction.getY() * -1);
            direction.setZ(direction.getZ() * -1);
            caught.setVelocity(direction);
        }
    }

    @EventHandler
    public void onArrow(EntityShootBowEvent event) {
        double damage = 200.0;

        if (event.getBow() != null)
            damage += event.getBow().getEnchantmentLevel(Enchantment.ARROW_DAMAGE) * (GearManager.POWER_DAMAGE);

        damage *= (double) event.getForce();

        event.getProjectile().setMetadata("damage", new FixedMetadataValue(Carbyne.getInstance(), damage));
    }

    @EventHandler
    public void onLastHit(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();

            if (player.isDead()) {
                event.setCancelled(true);
                return;
            }

//            Duel duel = StaticClasses.duelManager.getDuelFromUUID(player.getUniqueId());
//            if (duel != null && duel.getDuelStage().equals(DuelStage.FIGHTING)) {
//                event.setCancelled(false);
//            } else
            if (TownyUniverse.getTownBlock(player.getLocation()) != null && !TownyUniverse.getTownBlock(player.getLocation()).getPermissions().pvp) {
                event.setCancelled(true);
                return;
            } else if (event.isCancelled()) {
                return;
            }

            double damage = 0.0;

            boolean inDungeon = false;
            PlayerHealth playerHealth = PlayerHealth.getPlayerHealth(player.getUniqueId());
            DPlayer dPlayer = StaticClasses.dungeonHandler.getDPlayer(player.getUniqueId());
            if (dPlayer != null) {
                inDungeon = true;
            }

            if (event.getDamager() instanceof Player) {
                if (inDungeon && !dPlayer.getInstance().getDungeon().isPvp()) {
                    event.setCancelled(true);
                    return;
                }

                Player playerDamager = (Player) event.getDamager();
                if (TownyUniverse.getTownBlock(playerDamager.getLocation()) != null && !TownyUniverse.getTownBlock(playerDamager.getLocation()).getPermissions().pvp) {
                    event.setCancelled(true);
                    return;
                }

                lastHit.put(player.getUniqueId(), new LastData(playerDamager.getUniqueId(), System.currentTimeMillis()));
                double calculatedDamage = StaticClasses.gearManager.calculateDamage(playerDamager);
                damage += calculatedDamage + (ThreadLocalRandom.current().nextDouble(calculatedDamage * 0.8));
                damage = StaticClasses.gearManager.calculateWeakness(damage, playerDamager);
            } else if (event.getDamager() instanceof Arrow) {
                Arrow arrow = (Arrow) event.getDamager();

                if (arrow.hasMetadata("damage"))
                    damage += arrow.getMetadata("damage").get(0).asDouble();
                else
                    damage += 200.0;

                if (arrow.getShooter() != null && arrow.getShooter() instanceof Player) {
                    Player shooter = (Player) arrow.getShooter();
                    if (TownyUniverse.getTownBlock(shooter.getLocation()) != null && !TownyUniverse.getTownBlock(shooter.getLocation()).getPermissions().pvp) {
                        event.setCancelled(true);
                        return;
                    }

                    if (inDungeon && !dPlayer.getInstance().getDungeon().isPvp()) {
                        event.setCancelled(true);
                        return;
                    }

                    double landY = arrow.getLocation().getY(),
                            damagedPlayerY = player.getLocation().getY(),
                            landingLocY = landY - damagedPlayerY;

                    if (landingLocY > 1.5) {
                        if (Cooldowns.getCooldown(player.getUniqueId(), "HeadshotCooldown") > 0)
                            return;

                        damage *= 1.5;
                        player.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 3 * 20, 4, false, false));
                        player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 3 * 20, 1, false, false));

                        for (int i = 0; i < 10; ++i)
                            ParticleEffect.BLOCK_CRACK.display(new ParticleEffect.BlockData(Material.REDSTONE_BLOCK, (byte) 0), 0.5F, 0.1F, 0.5F, 1.0F, 25, player.getLocation(), 50, false);

                        shooter.playSound(shooter.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 10.0f, ThreadLocalRandom.current().nextFloat());
                        MessageManager.sendMessage(shooter, "&aYou have shot &5" + (player.getCustomName() != null ? player.getCustomName() : player.getName()) + " &ain the head and dealt bonus damage!");

                        Cooldowns.setCooldown(player.getUniqueId(), "HeadshotCooldown", 18000);
                    } else if (landingLocY > 0.1 && landingLocY < 0.75) {
                        if (Cooldowns.getCooldown(player.getUniqueId(), "KneeShotCooldown") > 0)
                            return;

                        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 4 * 20, 0, false, false));

                        for (int i = 0; i < 10; ++i)
                            ParticleEffect.BLOCK_CRACK.display(new ParticleEffect.BlockData(Material.REDSTONE_BLOCK, (byte) 0), 0.5F, 0.1F, 0.5F, 1.0F, 25, player.getLocation(), 50, false);

                        shooter.playSound(shooter.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 10.0f, ThreadLocalRandom.current().nextFloat());
                        MessageManager.sendMessage(shooter, "&aYou have shot &5" + (player.getCustomName() != null ? player.getCustomName() : player.getName()) + " &ain the knee and crippled them!");

                        Cooldowns.setCooldown(player.getUniqueId(), "KneeShotCooldown", 3000);
                    } else if (landingLocY > 0.1 && landingLocY < 1.0) {
                        if (Cooldowns.getCooldown(player.getUniqueId(), "LegShotCooldown") > 0)
                            return;

                        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 6 * 20, 1, false, false));

                        for (int i = 0; i < 10; ++i)
                            ParticleEffect.BLOCK_CRACK.display(new ParticleEffect.BlockData(Material.REDSTONE_BLOCK, (byte) 0), 0.5F, 0.1F, 0.5F, 1.0F, 25, player.getLocation(), 50, false);

                        shooter.playSound(shooter.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 10.0f, ThreadLocalRandom.current().nextFloat());
                        MessageManager.sendMessage(shooter, "&aYou have shot &5" + (player.getCustomName() != null ? player.getCustomName() : player.getName()) + " &ain the leg and crippled them!");

                        Cooldowns.setCooldown(player.getUniqueId(), "LegShotCooldown", 3000);
                    }
                }
            } else if (event.getDamager() instanceof Projectile) {
                switch (event.getDamager().getType()) {
                    case FIREBALL:
                        damage += 400.0;
                        break;
                    case SMALL_FIREBALL:
                        damage += 150.0;
                        break;
                    case WITHER_SKULL:
                        damage += 350.0;
                        break;
                }

            } else
                damage = event.getDamage();

            damage = StaticClasses.gearManager.calculateDamageResistance(damage, player);

            if (player.getNoDamageTicks() <= 0) {
                playerHealth.setHealth(playerHealth.getHealth() - damage, player);
                playerHealth.setLastDamage(damage);
                displayIndicator(player.getLocation().add(0, 2.2D, 0), damage, true);
            } else if (player.getNoDamageTicks() <= 10 && player.getNoDamageTicks() >= 1) {
                if (playerHealth.getLastDamage() >= damage) {
                    event.setCancelled(true);
                    return;
                }

                playerHealth.setLastDamage(damage);
                double toDamage = (damage / 1.3);
                playerHealth.setHealth(playerHealth.getHealth() - toDamage, player);
                displayIndicator(player.getLocation().add(0, 2.2D, 0), toDamage, true);
            } else {
                event.setCancelled(true);
                return;
            }

            event.setDamage(0);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onDamageItem(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player) {
            Player player = (Player) event.getDamager();
            if (player.getGameMode() != GameMode.CREATIVE) {
                CarbyneWeapon gear = StaticClasses.gearManager.getCarbyneWeapon(player.getInventory().getItemInMainHand());

                if (gear != null)
                    gear.damageItem(player, player.getInventory().getItemInMainHand());
                else {
                    MinecraftWeapon weapon = StaticClasses.gearManager.getDefaultWeapon(player.getInventory().getItemInMainHand());
                    if (weapon != null)
                        weapon.damageItem(player, player.getInventory().getItemInMainHand());
                }
            }
        }

        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            if (player.getGameMode() != GameMode.CREATIVE) {
                for (ItemStack itemStack : player.getInventory().getArmorContents()) {
                    if (itemStack == null)
                        continue;

                    CarbyneArmor gear = StaticClasses.gearManager.getCarbyneArmor(itemStack);
                    if (gear != null)
                        gear.damageItem(player, itemStack);
                    else {
                        MinecraftArmor armor = StaticClasses.gearManager.getDefaultArmor(itemStack);
                        if (armor != null)
                            armor.damageItem(player, itemStack);
                    }
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onEntityDamagebyEntity(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player) {
            Player damaged = (Player) event.getEntity();

            if (TownyUniverse.getTownBlock(damaged.getLocation()) != null && !TownyUniverse.getTownBlock(damaged.getLocation()).getPermissions().pvp) {
                return;
            }

            for (ItemStack itemStack : damaged.getInventory().getArmorContents()) {
                if (StaticClasses.gearManager.isCarbyneArmor(itemStack)) {
                    CarbyneArmor carbyneArmor = StaticClasses.gearManager.getCarbyneArmor(itemStack);

                    if (carbyneArmor.getDefensivePotionEffects().size() > 0)

                        carbyneArmor.applyDefensiveEffect(damaged);
                    if (carbyneArmor.getOffensivePotionEffects().size() > 0)
                        if (event.getDamager() != null && event.getDamager() instanceof Player)
                            carbyneArmor.applyOffensiveEffect((Player) event.getDamager());
                }
            }
        }

        if (event.getDamager() instanceof Player) {
            Player damager = (Player) event.getDamager();
            ItemStack itemStack = damager.getInventory().getItemInMainHand();

            if (StaticClasses.gearManager.isCarbyneWeapon(itemStack)) {
                CarbyneWeapon carbyneWeapon = StaticClasses.gearManager.getCarbyneWeapon(itemStack);

                if (event.getEntity() != null && event.getEntity() instanceof Player)
                    if (carbyneWeapon.getOffensivePotionEffects().size() > 0)
                        carbyneWeapon.applyOffensiveEffect((Player) event.getEntity());

                if (carbyneWeapon.getDefensivePotionEffects().size() > 0)
                    carbyneWeapon.applyDefensiveEffect(damager);
            }
        }
    }

    @EventHandler
    public void onDurabilityLoss(PlayerItemDamageEvent event) {
        ItemStack item = event.getItem();

        if (StaticClasses.gearManager.getCarbyneGear(item) != null || StaticClasses.gearManager.isDefaultArmor(item) || StaticClasses.gearManager.isDefaultWeapon(item))
            event.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        ItemStack itemStack = player.getInventory().getItemInMainHand();

        if (itemStack == null || itemStack.getType() == Material.AIR)
            return;

        CarbyneGear gear = StaticClasses.gearManager.getCarbyneGear(itemStack);

        if (gear == null)
            return;

        if (gear instanceof CarbyneWeapon) {
            CarbyneWeapon carbyneWeapon = (CarbyneWeapon) gear;

            carbyneWeapon.damageItem(player, itemStack);
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        PlayerUtility.checkForIllegalItems(event.getPlayer(), event.getPlayer().getInventory());

        if (!event.getAction().equals(Action.RIGHT_CLICK_AIR) && !event.getAction().equals(Action.RIGHT_CLICK_BLOCK))
            return;

        if (!event.getPlayer().isSneaking())
            return;

        ItemStack itemStack = event.getPlayer().getInventory().getItemInMainHand();
        if (itemStack == null)
            return;

        CarbyneWeapon carbyneWeapon = StaticClasses.gearManager.getCarbyneWeapon(itemStack);
        if (carbyneWeapon == null)
            return;

//        Duel duel = StaticClasses.duelManager.getDuelFromUUID(event.getPlayer().getUniqueId());
//        if (duel != null)
//            return;

        if (TownyUniverse.getTownBlock(event.getPlayer().getLocation()) != null && !TownyUniverse.getTownBlock(event.getPlayer().getLocation()).getPermissions().pvp)
            return;

        if (carbyneWeapon.getSpecial() != null) {
            if (carbyneWeapon.getSpecialCharge(itemStack) >= carbyneWeapon.getSpecial().getRequiredCharge() || event.getPlayer().hasPermission("carbyne.specials.override")) {

                Board board = Board.getByPlayer(event.getPlayer().getUniqueId());
                if (board != null) {
                    BoardCooldown boardCooldown = board.getCooldown("special");

                    if (boardCooldown == null) {
                        carbyneWeapon.setSpecialCharge(itemStack, 0);
                        carbyneWeapon.getSpecial().callSpecial(event.getPlayer());

                        if (!event.getPlayer().hasPermission("carbyne.specials.override"))
                            new BoardCooldown(board, "special", 60.0D);
                    } else
                        MessageManager.sendMessage(event.getPlayer(), "&eYou cannot use another weapon special for another &6" + boardCooldown.getFormattedString(BoardFormat.SECONDS) + " &eseconds.");
                }
            } else
                MessageManager.sendMessage(event.getPlayer(), "&cYour weapon must be fully charged.");
        }
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        LivingEntity livingEntity = event.getEntity();

        if (livingEntity.getKiller() != null) {
            ItemStack itemStack = livingEntity.getKiller().getInventory().getItemInMainHand();

            if (StaticClasses.gearManager.isCarbyneWeapon(itemStack)) {
                CarbyneWeapon carbyneWeapon = StaticClasses.gearManager.getCarbyneWeapon(itemStack);

                if (carbyneWeapon == null)
                    return;

                if (carbyneWeapon.getSpecial() == null)
                    return;

                int specialCharge = carbyneWeapon.getSpecialCharge(itemStack);

                if (specialCharge >= carbyneWeapon.getSpecial().getRequiredCharge()) {
                    carbyneWeapon.setSpecialCharge(itemStack, carbyneWeapon.getSpecial().getRequiredCharge());
                    return;
                }

                if (livingEntity instanceof Player) {
                    if (Cooldowns.tryCooldown(livingEntity.getKiller().getUniqueId(), livingEntity.getUniqueId().toString() + ":charge", 300000))
                        specialCharge += 10;
                } else if (livingEntity instanceof Monster)
                    specialCharge += 5;

                carbyneWeapon.setSpecialCharge(itemStack, specialCharge);
            }
        }
    }

//    @EventHandler
//    public void onClick(InventoryClickEvent event) {
//        if (event.getCurrentItem() != null && event.getCurrentItem().getType() != Material.AIR)
//            if (event.getCurrentItem().getType() == Material.NETHER_STAR && !event.getInventory().getTitle().contains("Spell"))
//                event.setCurrentItem(new ItemBuilder(StaticClasses.gearManager.getTokenItem()).amount(event.getCurrentItem().getAmount()).build());
//    }

    @EventHandler
    public void onCraft(CraftItemEvent event) {
        ItemStack itemStack = event.getCurrentItem();
        Player player = (Player) event.getWhoClicked();

        if (itemStack.getType().name().contains("LEATHER_")) {
            LeatherArmorMeta meta = (LeatherArmorMeta) itemStack.getItemMeta();
            if (!meta.getColor().equals(Color.fromRGB(160, 101, 64))) {
                event.setCancelled(true);
                MessageManager.sendMessage(player, "&cYou cannot color leather armor.");
                return;
            }
        }

        if (StaticClasses.gearManager.isCarbyneArmor(itemStack) || StaticClasses.gearManager.isCarbyneWeapon(itemStack)) {
            event.setCancelled(true);
            MessageManager.sendMessage(player, "&cYou cannot craft with Carbyne gear.");
        }
    }

    @EventHandler
    public void onEnchant(EnchantItemEvent event) {
        ItemStack itemStack = event.getItem();
        Player player = event.getEnchanter();

        if (StaticClasses.gearManager.isCarbyneArmor(itemStack) || StaticClasses.gearManager.isCarbyneWeapon(itemStack)) {
            event.setCancelled(true);
            MessageManager.sendMessage(player, "&cYou cannot enchant Carbyne gear.");
        }

        StaticClasses.gearManager.updateHealth(itemStack);
        StaticClasses.gearManager.updateDamage(itemStack);
    }

    @EventHandler
    public void onAnvilUse(InventoryClickEvent event) {
        Inventory inv = event.getInventory();

        if (inv instanceof AnvilInventory)
            if (inv.getItem(0) != null) {
                if (StaticClasses.gearManager.isCarbyneArmor(inv.getItem(0)) || StaticClasses.gearManager.isCarbyneWeapon(inv.getItem(0))) {
                    event.setCancelled(true);
                    MessageManager.sendMessage(event.getWhoClicked(), "&cYou cannot enchant or re-name Carbyne gear.");
                }

                StaticClasses.gearManager.updateHealth(inv.getItem(0));
                StaticClasses.gearManager.updateDamage(inv.getItem(0));
            }
    }

    @EventHandler
    public void onRepairCarbyne(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Block block = event.getClickedBlock();

            if (block.getType() != Material.ANVIL)
                return;

            Player player = event.getPlayer();

            ItemStack itemStack = player.getInventory().getItemInMainHand();

            if (itemStack == null)
                return;

            CarbyneGear gear = StaticClasses.gearManager.getCarbyneGear(itemStack);

            if (gear != null) {
                repairGear(player, itemStack, gear, event, block, 1);
                return;
            }

            MinecraftArmor minecraftArmor = StaticClasses.gearManager.getDefaultArmor(itemStack);
            if (minecraftArmor != null) {
                repairGear(player, itemStack, minecraftArmor, event, block, 2);
                return;
            }

            MinecraftWeapon minecraftWeapon = StaticClasses.gearManager.getDefaultWeapon(itemStack);
            if (minecraftWeapon != null) {
                repairGear(player, itemStack, minecraftWeapon, event, block, 2);
            }
        }
    }

    private void repairGear(Player player, ItemStack itemStack, CarbyneGear gear, PlayerInteractEvent event, Block block, int divider) {
        if (gear.getDurability(itemStack) >= gear.getMaxDurability()) {
            player.closeInventory();
            MessageManager.sendMessage(player, "&aThis item is already fully repaired.");
            event.setCancelled(true);
            return;
        }

        if (!containAtleast(player.getInventory(), gear.getRepairType(), gear.getRepairData(), 1)) {
            MessageManager.sendMessage(player, "&cYou need &4" + (gear.getRepairType() == Material.NETHER_STAR ? "Carbyne Ingots" : (WordUtils.capitalizeFully(gear.getRepairType().name().replace("_", " ")) + "s")) + " &cin your inventory to repair this item.");
            event.setCancelled(true);
            return;
        }

        int repairCost = gear.getRepairCost(itemStack);
        if (!containAtleast(player.getInventory(), gear.getRepairType(), gear.getRepairData(), repairCost)) {
            int amountOfIngots = getAmountOfIngots(player.getInventory(), gear.getRepairType(), gear.getRepairData());

            removeItems(player.getInventory(), gear.getRepairType(), gear.getRepairData(), amountOfIngots);

            event.setCancelled(true);
            Item item = player.getWorld().dropItem(block.getLocation().add(0.5, 1.15, 0.5), player.getInventory().getItemInMainHand());
            item.setVelocity(new Vector(0, 0, 0));
            item.teleport(block.getLocation().add(0.5, 1.1, 0.5));
            ParticleEffect.LAVA.display(0, 0, 0, 0, 2, block.getLocation().add(0.5, 0.15, 0.5), 40, false);
            player.getWorld().playSound(block.getLocation(), Sound.ENTITY_FIREWORK_BLAST_FAR, 10f, 1f);
            item.setPickupDelay(1000000000);
            player.updateInventory();
            int breakTime = 0;

            if (Math.random() < 0.05) {
                if ((repairCost * repairCost) - 3 > 0) {
                    Random random = new Random();
                    breakTime = random.nextInt((repairCost * repairCost) - 3) + 3;
                }
            }

            breakTime /= divider;

            repairItem(player, player.getInventory().getItemInMainHand(), item, amountOfIngots, gear, block.getLocation().add(0.5, 1.12, 0.5), breakTime);
            player.setItemInHand(null);
        } else {
            removeItems(player.getInventory(), gear.getRepairType(), gear.getRepairData(), repairCost);

            event.setCancelled(true);
            Item item = player.getWorld().dropItem(block.getLocation().add(0.5, 1.15, 0.5), player.getInventory().getItemInMainHand());
            item.setVelocity(new Vector(0, 0, 0));
            item.teleport(block.getLocation().add(0.5, 1.1, 0.5));
            ParticleEffect.LAVA.display(0, 0, 0, 0, 2, block.getLocation().add(0.5, 1.12, 0.5), 40, false);
            player.getWorld().playSound(block.getLocation(), Sound.ENTITY_FIREWORK_BLAST_FAR, 10f, 1f);
            item.setPickupDelay(1000000000);
            player.updateInventory();
            int breakTime = 0;

            if (Math.random() < 0.05) {
                if ((repairCost * repairCost) - 3 > 0) {
                    Random random = new Random();
                    breakTime = random.nextInt((repairCost * repairCost) - 3) + 3;
                }
            }

            breakTime /= divider;

            repairItem(player, player.getInventory().getItemInMainHand(), item, repairCost, gear, block.getLocation().add(0.5, 1.12, 0.5), breakTime);
            player.setItemInHand(null);
        }
    }

    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent event) {
        Player player = (Player) event.getPlayer();
        Inventory inventory = event.getInventory();

        PlayerUtility.checkForIllegalItems(player, inventory);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        Inventory inventory = event.getInventory();

        PlayerUtility.checkForIllegalItems(player, inventory);
    }

    @EventHandler
    public void itemDrop(ItemSpawnEvent event) {
        StaticClasses.gearManager.convertToMoneyItem(event.getEntity().getItemStack());
    }

    @EventHandler
    public void onPick(PlayerPickupItemEvent event) {
        new BukkitRunnable() {
            public void run() {
                PlayerUtility.checkForIllegalItems(event.getPlayer(), event.getPlayer().getInventory());
            }
        }.runTaskLater(Carbyne.getInstance(), 5L);
    }

    @EventHandler
    public void onConsume(PlayerItemConsumeEvent event) {
        Player player = event.getPlayer();
        ItemStack itemStack = event.getItem();

        if (itemStack.getType() == Material.POTION) {
            event.setCancelled(true);

            player.setItemInHand(new ItemStack(Material.GLASS_BOTTLE));

            Potion potion = Potion.fromItemStack(itemStack);

            for (PotionEffect effect : potion.getEffects())
                player.addPotionEffect(new PotionEffect(effect.getType(), effect.getDuration(), effect.getAmplifier(), false, false), true);
        }
    }

    @EventHandler
    public void onPotionSplash(PotionSplashEvent event) {
        event.setCancelled(true);

        for (Entity affectedEntity : event.getAffectedEntities()) {
            if (affectedEntity instanceof Player) {
                Player affectedPlayer = (Player) affectedEntity;

                for (PotionEffect effect : event.getPotion().getEffects())
                    affectedPlayer.addPotionEffect(new PotionEffect(effect.getType(), effect.getDuration(), effect.getAmplifier(), false, false), true);
            }
        }
    }

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        if (event.getCause() == PlayerTeleportEvent.TeleportCause.COMMAND || event.getCause() == PlayerTeleportEvent.TeleportCause.NETHER_PORTAL || event.getCause() == PlayerTeleportEvent.TeleportCause.END_PORTAL) {
            StaticClasses.gearManager.getGearEffects().effectTeleport(event.getPlayer(), event.getFrom());
            StaticClasses.gearManager.getGearEffects().effectTeleport(event.getPlayer(), event.getTo());
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        player.setMaxHealth(20);

        player.getAttribute(Attribute.GENERIC_ATTACK_SPEED).setBaseValue(100);

        if (StaticClasses.gearManager.getPlayerGearFadeSchedulers().containsKey(player.getUniqueId())) {
            StaticClasses.gearManager.getPlayerGearFadeSchedulers().get(player.getUniqueId()).cancel();
            StaticClasses.gearManager.getPlayerGearFadeSchedulers().remove(player.getUniqueId());
        }

        StaticClasses.gearManager.getPlayerGearFadeSchedulers().put(player.getUniqueId(), new BukkitRunnable() {
            boolean shouldFadeUp = false;
            int prizedHue = 0;

            @Override
            public void run() {
                if (!player.isOnline()) {
                    StaticClasses.gearManager.getPlayerGearFadeSchedulers().remove(player.getUniqueId());
                    cancel();
                }

                if (player.getInventory().getArmorContents().length > 0)
                    for (ItemStack itemStack : player.getInventory().getArmorContents()) {
                        boolean isPrized;

                        if (itemStack == null || itemStack.getType() == Material.AIR)
                            continue;

                        if (StaticClasses.gearManager.isCarbyneArmor(itemStack)) {
                            CarbyneArmor armor = StaticClasses.gearManager.getCarbyneArmor(itemStack);

                            if (armor == null)
                                continue;

                            if (armor.isPolished(itemStack)) {
                                isPrized = armor.getGearCode().equalsIgnoreCase("PH") || armor.getGearCode().equalsIgnoreCase("PC") || armor.getGearCode().equalsIgnoreCase("PL") || armor.getGearCode().equalsIgnoreCase("PB");

                                LeatherArmorMeta laMeta = (LeatherArmorMeta) itemStack.getItemMeta();

                                int r = laMeta.getColor().getRed(),
                                        g = laMeta.getColor().getGreen(),
                                        b = laMeta.getColor().getBlue();

                                if (r >= armor.getMaxFadeColor().getRed() && g >= armor.getMaxFadeColor().getGreen() && b >= armor.getMaxFadeColor().getBlue())
                                    shouldFadeUp = false;
                                else if (r <= armor.getMinFadeColor().getRed() && g <= armor.getMinFadeColor().getGreen() && b <= armor.getMinFadeColor().getBlue())
                                    shouldFadeUp = true;

                                if (isPrized) {
                                    if (shouldFadeUp)
                                        prizedHue++;
                                    else
                                        prizedHue--;

                                    r = HSLColor.toRGB(prizedHue, 100, 50).getRed();
                                    g = HSLColor.toRGB(prizedHue, 100, 50).getGreen();
                                    b = HSLColor.toRGB(prizedHue, 100, 50).getBlue();
                                } else if (shouldFadeUp) {
                                    r += armor.getTickFadeColor()[0];
                                    g += armor.getTickFadeColor()[1];
                                    b += armor.getTickFadeColor()[2];
                                } else {
                                    r -= armor.getTickFadeColor()[0];
                                    g -= armor.getTickFadeColor()[1];
                                    b -= armor.getTickFadeColor()[2];
                                }

                                if (r < 0)
                                    r = 0;
                                else if (r > 255)
                                    r = 255;

                                if (g < 0)
                                    g = 0;
                                else if (g > 255)
                                    g = 255;

                                if (b < 0)
                                    b = 0;
                                else if (b > 255)
                                    b = 255;

                                Color color = Color.fromRGB(r, g, b);

                                laMeta.setColor(color);
                                itemStack.setItemMeta(laMeta);
                            }
                        }
                    }
            }
        }.runTaskTimerAsynchronously(Carbyne.getInstance(), 0L, 5L));
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        if (StaticClasses.gearManager.getPlayerGearFadeSchedulers().containsKey(player.getUniqueId())) {
            StaticClasses.gearManager.getPlayerGearFadeSchedulers().get(player.getUniqueId()).cancel();
            StaticClasses.gearManager.getPlayerGearFadeSchedulers().remove(player.getUniqueId());
        }

        if (player.getInventory().getArmorContents().length > 0)
            for (ItemStack itemStack : player.getInventory().getArmorContents()) {
                if (itemStack == null)
                    continue;

                if (StaticClasses.gearManager.isCarbyneArmor(itemStack)) {
                    CarbyneArmor armor = StaticClasses.gearManager.getCarbyneArmor(itemStack);

                    if (!armor.isPolished(itemStack)) {
                        LeatherArmorMeta laMeta = (LeatherArmorMeta) itemStack.getItemMeta();
                        laMeta.setColor(armor.getBaseColor());
                        itemStack.setItemMeta(laMeta);
                    }
                }
            }
    }

    @EventHandler
    public void onDraw(EntityShootBowEvent event) {
        if (event.getEntity() instanceof Player)
            if (event.getForce() < 0.5) {
                event.setCancelled(true);
                MessageManager.sendMessage(event.getEntity(), "&cYou must draw your bow back harder!");
            }
    }

    @EventHandler
    public void onWandInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (event.getItem() != null && event.getItem().getType() == Material.BONE && event.getClickedBlock() != null && player.hasPermission("carbyne.wand") && player.getGameMode() == GameMode.CREATIVE) {
            RegionUser user = RegionUser.getRegionUser(player);

            if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
                event.setCancelled(true);
                Selection selection = (user.getSelection() != null) ? user.getSelection() : new Selection(null, null);
                Location location = event.getClickedBlock().getLocation();
                location.setY(0.0);
                selection.setLocation1(location);
                user.setSelection(selection);
                MessageManager.sendMessage(player, "&aSelection 1: &b" + location.getWorld().getName() + "&a, &b" + location.getBlockX() + "&a, &b" + location.getBlockY() + "&a, &b" + location.getBlockZ() + "&a.");
            } else if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                event.setCancelled(true);
                Selection selection = (user.getSelection() != null) ? user.getSelection() : new Selection(null, null);
                Location location = event.getClickedBlock().getLocation();
                location.setY((double) location.getWorld().getMaxHeight());
                selection.setLocation2(location);
                user.setSelection(selection);
                MessageManager.sendMessage(player, "&aSelection 2: &b" + location.getWorld().getName() + "&a, &b" + location.getBlockX() + "&a, &b" + location.getBlockY() + "&a, &b" + location.getBlockZ() + "&a.");
            }
        }
    }

    @EventHandler
    public void onPlotChange(PlayerChangePlotEvent event) {
        Player player = event.getPlayer();

        if (StaticClasses.gearManager.isWearingCarbyne(player)) {
            Zone from = StaticClasses.zoneManager.getZone(event.getMoveEvent().getFrom());
            Zone to = StaticClasses.zoneManager.getZone(event.getMoveEvent().getTo());
            if (from == null && to == null) {
                return;
            }

            if (((from != null && !from.isNerfedZone()) || from == null) && (to != null && to.isNerfedZone())) {
                MessageManager.sendMessage(player, "&8[&c&lWARNING&8] &eYou have entered a Carbyne Nerfed Zone.");
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_PLING, 1, 1);
                double maxHealth = StaticClasses.gearManager.calculateMaxHealth(player, true);
                PlayerHealth playerHealth = PlayerHealth.getPlayerHealth(player.getUniqueId());
                double toSet = (playerHealth.getHealth() / playerHealth.getMaxHealth()) * maxHealth;
                playerHealth.setMaxHealth(maxHealth);
                playerHealth.setHealth(toSet, player);
            } else if (from != null && from.isNerfedZone() && ((to != null && !to.isNerfedZone()) || to == null)) {
                MessageManager.sendMessage(player, "&8[&d&lALERT&8] &aYou have left a Carbyne Nerfed Zone.");
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_PLING, 1, 1);
                double maxHealth = StaticClasses.gearManager.calculateMaxHealth(player, false);
                PlayerHealth playerHealth = PlayerHealth.getPlayerHealth(player.getUniqueId());
                double toSet = (playerHealth.getHealth() / playerHealth.getMaxHealth()) * maxHealth;
                playerHealth.setMaxHealth(maxHealth);
                playerHealth.setHealth(toSet, player);
            }
        }
    }

    public void removeItems(Inventory inventory, Material type, int data, int amount) {
        if (amount <= 0)
            return;

        int size = inventory.getSize();

        for (int slot = 0; slot < size; slot++) {
            ItemStack is = inventory.getItem(slot);

            if (is == null)
                continue;

            if (type == is.getType() && is.getDurability() == data) {
                int newAmount = is.getAmount() - amount;

                if (newAmount > 0) {
                    is.setAmount(newAmount);
                    break;
                } else {
                    inventory.clear(slot);
                    amount = -newAmount;

                    if (amount == 0)
                        break;
                }
            }
        }
    }

    public boolean containAtleast(Inventory inventory, Material type, int data, int amount) {
        int size = inventory.getSize();
        int totalAmount = 0;

        for (int slot = 0; slot < size; slot++) {
            ItemStack is = inventory.getItem(slot);

            if (is == null)
                continue;

            if (type == is.getType() && is.getDurability() == data)
                totalAmount += is.getAmount();
        }

        return totalAmount >= amount;
    }

    public int getAmountOfIngots(Inventory inventory, Material type, int data) {
        int amount = 0;
        int size = inventory.getSize();

        for (int slot = 0; slot < size; slot++) {
            ItemStack is = inventory.getItem(slot);

            if (is == null)
                continue;

            if (type == is.getType() && is.getDurability() == data)
                amount += is.getAmount();
        }

        return amount;
    }

    public void repairItem(Player player, ItemStack itemStack, Item item, int repairCost, CarbyneGear gear, Location location, int breakTime) {
        StaticClasses.gearManager.getRepairItems().add(item);

        new BukkitRunnable() {
            int i = -1;
            boolean far = false;

            @Override
            public void run() {
                i++;

                if (breakTime > 0 && i >= breakTime) {
                    cancel();
                    MessageManager.sendMessage(player, "&cYour item broke!");

                    ParticleEffect.SMOKE_LARGE.display(0f, 0f, 0f, 0.002f, 4, location, 40, true);
                    player.getWorld().playSound(location, Sound.BLOCK_ANVIL_BREAK, 1f, (float) Math.random() * 2.5f);

                    if (item != null) {
                        StaticClasses.gearManager.getRepairItems().remove(item);
                        item.remove();
                    }

                    return;
                }

                if ((!player.getWorld().getName().equals(location.getWorld().getName()) && !far) || ((player.getWorld().getName().equals(location.getWorld().getName()) && (player.getLocation().distance(location) >= 9 && !far)))) {
                    far = true;
                    MessageManager.sendMessage(player, "&cYou are too far from the anvil, your item will be dropped on the ground.");
                }

                if (player.getWorld().getName().equals(location.getWorld().getName()) && (far && player.getLocation().distance(location) < 9)) {
                    far = false;
                    MessageManager.sendMessage(player, "&aYou are no longer too far away.");
                }

                if (i >= repairCost * 4) {
                    cancel();

                    //ItemStack itemStack = gear.getItem(false).clone();
                    if (StaticClasses.gearManager.isCarbyneArmor(itemStack)) {
                        CarbyneArmor armor = (CarbyneArmor) gear;
                        int per = armor.getMaxDurability() / armor.getRepairCost();
                        armor.setDurability(itemStack, (armor.getDurability(itemStack) + (per * repairCost)));
                    }

                    if (StaticClasses.gearManager.isCarbyneWeapon(itemStack)) {
                        CarbyneWeapon weapon = (CarbyneWeapon) gear;
                        int per = weapon.getMaxDurability() / weapon.getRepairCost();
                        weapon.setDurability(itemStack, (weapon.getDurability(itemStack) + (per * repairCost)));
                    }

                    if (StaticClasses.gearManager.isDefaultArmor(itemStack)) {
                        MinecraftArmor armor = (MinecraftArmor) gear;
                        int per = armor.getMaxDurability() / armor.getRepairCost();
                        armor.setDurability(itemStack, (armor.getDurability(itemStack) + (per * repairCost)));
                    }

                    if (StaticClasses.gearManager.isDefaultWeapon(itemStack)) {
                        MinecraftWeapon weapon = (MinecraftWeapon) gear;
                        int per = weapon.getMaxDurability() / weapon.getRepairCost();
                        weapon.setDurability(itemStack, (weapon.getDurability(itemStack) + (per * repairCost)));
                    }

                    if (!player.isOnline() || far) {
                        location.getWorld().dropItem(location, itemStack);

                        if (item != null) {
                            StaticClasses.gearManager.getRepairItems().remove(item);
                            item.remove();
                        }

                        if (player.isOnline()) {
                            MessageManager.sendMessage(player, "&aYour item has been repaired.");
                            Bukkit.getServer().getPluginManager().callEvent(new CarbyneRepairedEvent(player, gear));
                        }
                    } else {
                        if (player.getInventory().getItemInMainHand() != null) {
                            if (player.getInventory().firstEmpty() == -1) {
                                player.getWorld().dropItem(player.getLocation(), itemStack);
                                return;
                            }

                            player.getInventory().addItem(itemStack);
                        } else
                            player.setItemInHand(itemStack);

                        MessageManager.sendMessage(player, "&aYour item has been repaired.");

                        if (item != null) {
                            StaticClasses.gearManager.getRepairItems().remove(item);
                            item.remove();
                        }

                        Bukkit.getServer().getPluginManager().callEvent(new CarbyneRepairedEvent(player, gear));
                    }
                }

                ParticleEffect.FLAME.display(0f, 0f, 0f, 0.075f, 20, location, 40, true);
                player.getWorld().playSound(location, Sound.BLOCK_ANVIL_USE, 1f, (float) Math.random() * 2.5f);
            }
        }.runTaskTimer(Carbyne.getInstance(), 0, 20);
    }

    public int cleanupDamageIndicators() {
        for (Hologram hologram : activeHolograms)
            hologram.delete();

        int i = activeHolograms.size();
        activeHolograms.clear();
        return i;
    }

    public static Double random(double min, double max) {
        return ThreadLocalRandom.current().nextDouble(min, max);
    }

    public void displayIndicator(final Location location, final double value, final boolean isDamage) {
        ChatColor color;
        if (isDamage)
            color = ChatColor.RED;
        else
            color = ChatColor.GREEN;

        displayIndicator(location, value, isDamage, color);
    }

    public void displayIndicator(final Location location, final double value, final boolean isDamage, ChatColor color) {
        double x = random(-0.3D, 0.3D);
        double z = random(-0.3D, 0.3D);
//        long duration = ((long) value / 2) + 20L; //Increase display duration by a second per 40 hearts of damage.

        //long duration = 15L;

//        if (duration > 15L)
//            duration = 15L;

        Hologram hologram = HologramsAPI.createHologram(Carbyne.getInstance(), location.add(x, 1D, z));

        if (isDamage)
            hologram.appendTextLine(color + "-" + df.format(value));
        else
            hologram.appendTextLine(color + "+" + df.format(value));

        activeHolograms.add(hologram);

        new BukkitRunnable() {
            int phase = 0;

            public void run() {
                phase++;
                if (phase >= 2) {
                    hologram.delete();
                    activeHolograms.remove(hologram);
                    this.cancel();
                    return;
                }

                hologram.teleport(hologram.getLocation().add(0D, 1D, 0D));
            }
        }.runTaskTimer(Carbyne.getInstance(), 1L, 15L);
    }


    public Location getBlockBelowLoc(Location loc) {
        if (loc.getY() <= 0)
            return null;

        Location locBelow = loc.subtract(0, 1, 0);
        if (locBelow.getBlock().getType() == Material.AIR)
            locBelow = getBlockBelowLoc(locBelow);

        return locBelow;
    }

    private void handleVoidDeath(Player player) {
        if (lastHit.containsKey(player.getUniqueId())) {
            LastData data = lastHit.get(player.getUniqueId());
            long time = data.time;
            Player damager = Bukkit.getPlayer(data.uuid);

            if (System.currentTimeMillis() - time < 30000) {
                for (ItemStack item : player.getInventory().getContents()) {
                    if (item == null || item.getType() == Material.AIR) continue;

                    damager.getWorld().dropItemNaturally(damager.getLocation(), item);
                }

                for (ItemStack item : player.getInventory().getArmorContents()) {
                    if (item == null || item.getType() == Material.AIR) continue;
                    damager.getWorld().dropItemNaturally(damager.getLocation(), item);
                }

                player.getInventory().clear();
                player.getInventory().setArmorContents(null);
            }
        }
    }

    public void removeFromExhaust(Player player) {
        weakEffects.get(player.getUniqueId()).cancel();
        weakEffects.remove(player.getUniqueId());
    }

    public class LastData {

        public UUID uuid;
        public long time;

        public LastData(UUID uuid, long time) {
            this.time = time;
            this.uuid = uuid;
        }
    }
}