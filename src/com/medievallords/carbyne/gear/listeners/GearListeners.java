package com.medievallords.carbyne.gear.listeners;


import com.codingforcookies.armorequip.ArmorEquipEvent;
import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.customevents.CarbyneRepairedEvent;
import com.medievallords.carbyne.duels.duel.Duel;
import com.medievallords.carbyne.gear.GearManager;
import com.medievallords.carbyne.gear.effects.ExhaustEffect;
import com.medievallords.carbyne.gear.types.CarbyneGear;
import com.medievallords.carbyne.gear.types.carbyne.CarbyneArmor;
import com.medievallords.carbyne.gear.types.carbyne.CarbyneWeapon;
import com.medievallords.carbyne.gear.types.minecraft.MinecraftArmor;
import com.medievallords.carbyne.gear.types.minecraft.MinecraftWeapon;
import com.medievallords.carbyne.utils.*;
import com.medievallords.carbyne.utils.scoreboard.Board;
import com.medievallords.carbyne.utils.scoreboard.BoardCooldown;
import com.medievallords.carbyne.utils.scoreboard.BoardFormat;
import com.palmergames.bukkit.towny.object.TownyUniverse;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
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
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Random;
import java.util.UUID;

public class GearListeners implements Listener {

    private Carbyne carbyne = Carbyne.getInstance();
    private GearManager gearManager = carbyne.getGearManager();
    private HashMap<UUID, ExhaustEffect> exhaustEffects = new HashMap<>();

    public void removeFromExhaust(Player player) {
        if (exhaustEffects.containsKey(player.getUniqueId()))
            exhaustEffects.remove(player.getUniqueId());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();

            if (player.getHealth() <= 32 && !exhaustEffects.containsKey(player.getUniqueId())) {
                ExhaustEffect exhaustEffect = new ExhaustEffect(player);
                exhaustEffect.runTaskTimerAsynchronously(carbyne, 0, 1);
                exhaustEffects.put(player.getUniqueId(), exhaustEffect);
            }

            if (carbyne.getDuelManager().getDuelFromUUID(player.getUniqueId()) != null)
                return;

            //Player dead?
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
                    player.setHealth(player.getHealth() - damage);
                    player.playEffect(EntityEffect.HURT);
                    //player.damage(damage);
                }
            } else
                event.setDamage(event.getDamage() * 5);
        }
    }

    @EventHandler
    public void onHealthRegain(EntityRegainHealthEvent event) {
        if (event.getEntityType() == EntityType.PLAYER)
            event.setAmount(event.getAmount() * 5);
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

    @EventHandler(ignoreCancelled = true)
    public void onEntityDamagebyEntity(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player) {
            Player damager = (Player) event.getDamager();

            if (damager.hasPotionEffect(PotionEffectType.INCREASE_DAMAGE))
                for (PotionEffect effect : damager.getActivePotionEffects())
                    if (effect.getType().equals(PotionEffectType.INCREASE_DAMAGE)) {
                        event.setDamage(event.getDamage() * (1 + (0.1 * effect.getAmplifier())));
                        break;
                    }
        }

        if (event.getEntity() instanceof Player) {
            Player damaged = (Player) event.getEntity();

            if (TownyUniverse.getTownBlock(damaged.getLocation()) != null && !TownyUniverse.getTownBlock(damaged.getLocation()).getPermissions().pvp) {
                if (carbyne.getDuelManager().getDuelFromUUID(damaged.getUniqueId()) != null)
                    return;

                return;
            }

            for (ItemStack itemStack : damaged.getInventory().getArmorContents()) {
                if (gearManager.isCarbyneArmor(itemStack) || gearManager.isCarbyneWeapon(itemStack)) {
                    CarbyneGear carbyneGear = gearManager.getCarbyneGear(itemStack);

                    if (carbyneGear != null) {
                        if (carbyneGear instanceof CarbyneArmor) {
                            CarbyneArmor carbyneArmor = (CarbyneArmor) carbyneGear;

                            //if (itemStack.getType().equals(Material.LEATHER_CHESTPLATE) || itemStack.getType().equals(Material.LEATHER_LEGGINGS)) {
                            if (carbyneArmor.getDefensivePotionEffects().size() > 0)
                                carbyneArmor.applyDefensiveEffect(damaged);

                            if (carbyneArmor.getOffensivePotionEffects().size() > 0)
                                if (event.getDamager() != null && event.getDamager() instanceof Player)
                                    carbyneArmor.applyOffensiveEffect((Player) event.getDamager());

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
                    if (event.getEntity() != null && event.getEntity() instanceof Player)
                        if (carbyneWeapon.getOffensivePotionEffects().size() > 0)
                            carbyneWeapon.applyOffensiveEffect((Player) event.getEntity());

                    if (carbyneWeapon.getDefensivePotionEffects().size() > 0)
                        carbyneWeapon.applyDefensiveEffect(damager);

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
    public void onDurabilityLoss(PlayerItemDamageEvent event) {
        ItemStack item = event.getItem();

        if (gearManager.isCarbyneWeapon(item) || gearManager.isCarbyneArmor(item))
            event.setCancelled(true);
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        PlayerUtility.checkForIllegalItems(event.getPlayer(), event.getPlayer().getInventory());

        if (!event.getAction().equals(Action.RIGHT_CLICK_AIR) && !event.getAction().equals(Action.RIGHT_CLICK_BLOCK))
            return;

        if (!event.getPlayer().isSneaking())
            return;

        ItemStack itemStack = event.getPlayer().getInventory().getItemInHand();
        if (itemStack == null)
            return;


        CarbyneWeapon carbyneWeapon = gearManager.getCarbyneWeapon(itemStack);
        if (carbyneWeapon == null)
            return;


        Duel duel = Carbyne.getInstance().getDuelManager().getDuelFromUUID(event.getPlayer().getUniqueId());
        if (duel != null)
            return;


        if (TownyUniverse.getTownBlock(event.getPlayer().getLocation()) != null && !TownyUniverse.getTownBlock(event.getPlayer().getLocation()).getPermissions().pvp)
            return;


        if (carbyneWeapon.getSpecial() != null) {
            if (carbyneWeapon.getSpecialCharge(itemStack) >= carbyneWeapon.getSpecial().getRequiredCharge() || event.getPlayer().hasPermission("carbyne.specials.override")) {

                Board board = Board.getByPlayer(event.getPlayer());
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
            ItemStack itemStack = livingEntity.getKiller().getItemInHand();

            if (gearManager.isCarbyneWeapon(itemStack)) {
                CarbyneWeapon carbyneWeapon = gearManager.getCarbyneWeapon(itemStack);

                if (carbyneWeapon == null)
                    return;

                if (carbyneWeapon.getSpecial() == null)
                    return;

                int specialCharge = carbyneWeapon.getSpecialCharge(itemStack);

                if (specialCharge >= carbyneWeapon.getSpecial().getRequiredCharge()) {
//                    MessageManager.sendMessage(livingEntity.getKiller(), "&7[&aCarbyne&7]: &aYour &b" + carbyneWeapon.getSpecial().getSpecialName() + " &aweapon special is fully charged!");
                    carbyneWeapon.setSpecialCharge(itemStack, carbyneWeapon.getSpecial().getRequiredCharge());
                    return;
                }

                if (livingEntity instanceof Player)
                    if (Cooldowns.tryCooldown(livingEntity.getKiller().getUniqueId(), livingEntity.getUniqueId().toString() + ":charge", 300000))
                        specialCharge += 10;
                    else if (livingEntity instanceof Monster)
                        specialCharge += 5;

                carbyneWeapon.setSpecialCharge(itemStack, specialCharge);
            }
        }
    }

    /*@EventHandler
    public void onCraftItem(CraftItemEvent event) {
        if (event.getCurrentItem() != null && event.getCurrentItem().getType() != Material.AIR) {
            if (gearManager.isDefaultArmor(event.getCurrentItem())) {
                MinecraftArmor minecraftArmor = gearManager.getDefaultArmor(event.getCurrentItem());

                if (minecraftArmor != null) {
                    event.setCurrentItem(gearManager.convertDefaultItem(event.getCurrentItem()));
                }
            } else if (gearManager.isDefaultWeapon(event.getCurrentItem())) {
                MinecraftWeapon minecraftWeapon = gearManager.getDefaultWeapon(event.getCurrentItem());

                if (minecraftWeapon != null) {
                    event.setCurrentItem(gearManager.convertDefaultItem(event.getCurrentItem()));
                }
            }
        }
    }*/

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (event.getCurrentItem() != null && event.getCurrentItem().getType() != Material.AIR) {
            if (gearManager.isDefaultArmor(event.getCurrentItem())) {
                if (event.getInventory() != null)
                    if (gearManager.getGearGuiManager().isCustomInventory(event.getInventory()))
                        return;

                MinecraftArmor minecraftArmor = gearManager.getDefaultArmor(event.getCurrentItem());

                if (minecraftArmor != null)
                    event.setCurrentItem(gearManager.convertDefaultItem(event.getCurrentItem()));
            } else if (gearManager.isDefaultWeapon(event.getCurrentItem())) {
                if (event.getInventory() != null)
                    if (gearManager.getGearGuiManager().isCustomInventory(event.getInventory()))
                        return;

                MinecraftWeapon minecraftWeapon = gearManager.getDefaultWeapon(event.getCurrentItem());

                if (minecraftWeapon != null)
                    event.setCurrentItem(gearManager.convertDefaultItem(event.getCurrentItem()));

            } else if (event.getCurrentItem().getType() == Material.NETHER_STAR && !event.getInventory().getTitle().contains("Spell")) {
                event.setCurrentItem(new ItemBuilder(gearManager.getTokenItem()).amount(event.getCurrentItem().getAmount()).build());
            }
        }
    }

    @EventHandler
    public void onCraft(CraftItemEvent event) {
        ItemStack itemStack = event.getCurrentItem();
        Player player = (Player) event.getWhoClicked();

        if (gearManager.isCarbyneArmor(itemStack) || gearManager.isCarbyneWeapon(itemStack)) {
            event.setCancelled(true);
            MessageManager.sendMessage(player, "&cYou cannot craft with Carbyne gear.");
        }
    }

    @EventHandler
    public void onEnchant(EnchantItemEvent event) {
        ItemStack itemStack = event.getItem();
        Player player = event.getEnchanter();

        if (gearManager.isCarbyneArmor(itemStack) || gearManager.isCarbyneWeapon(itemStack)) {
            event.setCancelled(true);
            MessageManager.sendMessage(player, "&cYou cannot enchant Carbyne gear.");
        }
    }

    @EventHandler
    public void onAnvilUse(InventoryClickEvent event) {
        Inventory inv = event.getInventory();

        if (inv instanceof AnvilInventory)
            if (inv.getItem(0) != null)
                if (gearManager.isCarbyneArmor(inv.getItem(0)) || gearManager.isCarbyneWeapon(inv.getItem(0))) {
                    event.setCancelled(true);
                    MessageManager.sendMessage(event.getWhoClicked(), "&cYou cannot enchant or re-name Carbyne gear.");
                }
    }

    @EventHandler
    public void onRepairCarbyne(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Block block = event.getClickedBlock();

            if (block.getType() != Material.ANVIL)
                return;


            Player player = event.getPlayer();

            ItemStack itemStack = player.getItemInHand();

            if (itemStack == null)
                return;

            CarbyneGear gear = gearManager.getCarbyneGear(itemStack);

            if (gear == null)
                return;


            if (gear.getDurability(itemStack) >= gear.getMaxDurability()) {
                player.closeInventory();
                MessageManager.sendMessage(player, "&aThis item is already fully repaired.");
                event.setCancelled(true);
                return;
            }
            if (!player.getInventory().containsAtLeast(gearManager.getTokenItem(), 1)) {
                MessageManager.sendMessage(player, "&cYou do not have enough carbyne ingots.");
                event.setCancelled(true);
                return;
            }

            int repairCost = gear.getRepairCost(itemStack);

            if (!player.getInventory().containsAtLeast(gearManager.getTokenItem(), repairCost)) {
                int amountOfIngots = getAmountOfIngots(player.getInventory(), gearManager.getTokenMaterial(), gearManager.getTokenData());

                removeItems(player.getInventory(), gearManager.getTokenMaterial(), gearManager.getTokenData(), repairCost);

                event.setCancelled(true);
                Item item = player.getWorld().dropItem(block.getLocation().add(0.5, 1.15, 0.5), player.getItemInHand());
                item.setVelocity(new Vector(0, 0, 0));
                item.teleport(block.getLocation().add(0.5, 1.1, 0.5));
                ParticleEffect.LAVA.display(0, 0, 0, 0, 2, block.getLocation().add(0.5, 0.15, 0.5), 40, false);
                player.getWorld().playSound(block.getLocation(), Sound.FIREWORK_BLAST2, 10f, 1f);
                item.setPickupDelay(1000000000);
                player.setItemInHand(null);
                player.updateInventory();
                int breakTime = 0;

                if (Math.random() < 0.05) {
                    if ((repairCost * repairCost) - 3 < 0)
                        breakTime = 0;
                    else {
                        Random random = new Random();
                        breakTime = random.nextInt((repairCost * repairCost) - 3) + 3;
                    }
                }

                repairItem(player, item, amountOfIngots, gear, block.getLocation().add(0.5, 1.12, 0.5), breakTime);
            } else {
                removeItems(player.getInventory(), gearManager.getTokenMaterial(), gearManager.getTokenData(), repairCost);

                event.setCancelled(true);
                Item item = player.getWorld().dropItem(block.getLocation().add(0.5, 1.15, 0.5), player.getItemInHand());
                item.setVelocity(new Vector(0, 0, 0));
                item.teleport(block.getLocation().add(0.5, 1.1, 0.5));
                ParticleEffect.LAVA.display(0, 0, 0, 0, 2, block.getLocation().add(0.5, 1.12, 0.5), 40, false);
                player.getWorld().playSound(block.getLocation(), Sound.FIREWORK_BLAST2, 10f, 1f);
                item.setPickupDelay(1000000000);
                player.setItemInHand(null);
                player.updateInventory();
                int breakTime = 0;

                if (Math.random() < 0.05) {
                    if ((repairCost * repairCost) - 3 < 0)
                        breakTime = 0;
                    else {
                        Random random = new Random();
                        breakTime = random.nextInt((repairCost * repairCost) - 3) + 3;
                    }
                }

                repairItem(player, item, repairCost, gear, block.getLocation().add(0.5, 1.12, 0.5), breakTime);
            }
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
        gearManager.convertToMoneyItem(event.getEntity().getItemStack());
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
            gearManager.getGearEffects().effectTeleport(event.getPlayer(), event.getFrom());
            gearManager.getGearEffects().effectTeleport(event.getPlayer(), event.getTo());
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        player.setMaxHealth(100);
        player.setHealthScale(20);

        if (gearManager.getPlayerGearFadeSchedulers().containsKey(player.getUniqueId())) {
            gearManager.getPlayerGearFadeSchedulers().get(player.getUniqueId()).cancel();
            gearManager.getPlayerGearFadeSchedulers().remove(player.getUniqueId());
        }

        gearManager.getPlayerGearFadeSchedulers().put(player.getUniqueId(), new BukkitRunnable() {
            boolean shouldFadeUp = false;
            int prizedHue = 0;

            @Override
            public void run() {
                if (!player.isOnline()) {
                    gearManager.getPlayerGearFadeSchedulers().remove(player.getUniqueId());
                    cancel();
                }

                if (player.getInventory().getArmorContents().length > 0)
                    for (ItemStack itemStack : player.getInventory().getArmorContents()) {
                        boolean isPrized;

                        if (itemStack == null)
                            continue;

                        if (gearManager.isCarbyneArmor(itemStack)) {
                            CarbyneArmor armor = gearManager.getCarbyneArmor(itemStack);

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
                                } else {
                                    if (shouldFadeUp) {
                                        r += armor.getTickFadeColor()[0];
                                        g += armor.getTickFadeColor()[1];
                                        b += armor.getTickFadeColor()[2];
                                    } else {
                                        r -= armor.getTickFadeColor()[0];
                                        g -= armor.getTickFadeColor()[1];
                                        b -= armor.getTickFadeColor()[2];
                                    }
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

        if (gearManager.getPlayerGearFadeSchedulers().containsKey(player.getUniqueId())) {
            gearManager.getPlayerGearFadeSchedulers().get(player.getUniqueId()).cancel();
            gearManager.getPlayerGearFadeSchedulers().remove(player.getUniqueId());
        }

        if (player.getInventory().getArmorContents().length > 0)
            for (ItemStack itemStack : player.getInventory().getArmorContents()) {
                if (itemStack == null)
                    continue;

                if (gearManager.isCarbyneArmor(itemStack)) {
                    CarbyneArmor armor = gearManager.getCarbyneArmor(itemStack);

                    if (!armor.isPolished(itemStack)) {
                        LeatherArmorMeta laMeta = (LeatherArmorMeta) itemStack.getItemMeta();
                        laMeta.setColor(armor.getBaseColor());
                        itemStack.setItemMeta(laMeta);
                    }
                }
            }
    }

    @EventHandler
    public void onEquip(ArmorEquipEvent event) {
        Player player = event.getPlayer();
        player.playSound(player.getLocation(), Sound.ENDERDRAGON_WINGS, 1, (float) Math.random() * 3);
        if (event.getOldArmorPiece() != null && event.getOldArmorPiece().getType() != Material.AIR) {
            ItemStack item = event.getOldArmorPiece();

            if (gearManager.isCarbyneArmor(item)) {
                CarbyneArmor armor = gearManager.getCarbyneArmor(item);

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
    }

    public void removeItems(Inventory inventory, Material type, int data, int amount) {
        if (amount <= 0) {
            return;
        }

        int size = inventory.getSize();

        for (int slot = 0; slot < size; slot++) {
            ItemStack is = inventory.getItem(slot);

            if (is == null) {
                continue;
            }

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

    public int getAmountOfIngots(Inventory inventory, Material type, int data) {
        int amount = 0;
        int size = inventory.getSize();

        for (int slot = 0; slot < size; slot++) {
            ItemStack is = inventory.getItem(slot);

            if (is == null) {
                continue;
            }

            if (type == is.getType() && is.getDurability() == data) {
                amount += is.getAmount();
            }
        }
        return amount;
    }

    public void repairItem(Player player, Item item, int repairCost, CarbyneGear gear, Location location, int breakTime) {
        gearManager.getRepairItems().add(item);

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
                    player.getWorld().playSound(location, Sound.ANVIL_BREAK, 10f, (float) Math.random() * 2.5f);

                    if (item != null) {
                        gearManager.getRepairItems().remove(item);
                        item.remove();
                    }

                    return;
                }

                if ((!player.getWorld().getName().equals(location.getWorld().getName()) && !far) || ((player.getWorld().getName().equals(location.getWorld().getName()) && (player.getLocation().distance(location) >= 9 && !far)))) {
                    far = true;
                    MessageManager.sendMessage(player, "&cYou are too far from the anvil, your item will be dropped on the ground");
                }

                if (player.getWorld().getName().equals(location.getWorld().getName()) && (far && player.getLocation().distance(location) < 9)) {
                    far = false;
                    MessageManager.sendMessage(player, "&aYou are no longer too far away");
                }

                if (i >= repairCost * 4) {
                    cancel();

                    ItemStack itemStack = gear.getItem(false).clone();

                    if (!player.isOnline() || far) {
                        location.getWorld().dropItem(location, itemStack);

                        if (item != null) {
                            gearManager.getRepairItems().remove(item);
                            item.remove();
                        }

                        if (player.isOnline()) {
                            MessageManager.sendMessage(player, "&aYour item has been repaired.");
                            Bukkit.getServer().getPluginManager().callEvent(new CarbyneRepairedEvent(player, gear));
                        }
                    } else {
                        if (player.getItemInHand() != null) {
                            if (player.getInventory().firstEmpty() == -1) {
                                player.getWorld().dropItem(player.getLocation(), itemStack);
                                return;
                            }

                            player.getInventory().addItem(itemStack);
                        } else {
                            player.setItemInHand(itemStack);
                        }

                        MessageManager.sendMessage(player, "&aYour item has been repaired.");

                        if (item != null) {
                            gearManager.getRepairItems().remove(item);
                            item.remove();
                        }

                        Bukkit.getServer().getPluginManager().callEvent(new CarbyneRepairedEvent(player, gear));
                    }
                }

                ParticleEffect.FLAME.display(0f, 0f, 0f, 0.075f, 20, location, 40, true);
                player.getWorld().playSound(location, Sound.ANVIL_USE, 10f, (float) Math.random() * 2.5f);
            }
        }.runTaskTimer(Carbyne.getInstance(), 0, 20);
    }
}