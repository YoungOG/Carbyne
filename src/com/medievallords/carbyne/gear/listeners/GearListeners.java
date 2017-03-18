package com.medievallords.carbyne.gear.listeners;

import com.bizarrealex.aether.scoreboard.Board;
import com.bizarrealex.aether.scoreboard.cooldown.BoardCooldown;
import com.bizarrealex.aether.scoreboard.cooldown.BoardFormat;
import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.gear.GearManager;
import com.medievallords.carbyne.gear.types.CarbyneGear;
import com.medievallords.carbyne.gear.types.carbyne.CarbyneArmor;
import com.medievallords.carbyne.gear.types.carbyne.CarbyneWeapon;
import com.medievallords.carbyne.gear.types.minecraft.MinecraftArmor;
import com.medievallords.carbyne.gear.types.minecraft.MinecraftWeapon;
import com.medievallords.carbyne.utils.MessageManager;
import com.medievallords.carbyne.utils.PlayerUtility;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class GearListeners implements Listener {

    private Carbyne carbyne = Carbyne.getInstance();
    private GearManager gearManager = carbyne.getGearManager();

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();

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
                        flatDamage = 4.0;
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

                event.setDamage(0);
                player.damage(damage);
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityDamagebyEntity(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player) {
            Player damaged = (Player) event.getEntity();

            for (ItemStack itemStack : damaged.getInventory().getArmorContents()) {
                if (gearManager.isCarbyneArmor(itemStack) || gearManager.isCarbyneWeapon(itemStack)) {
                    CarbyneGear carbyneGear = gearManager.getCarbyneGear(itemStack);

                    if (carbyneGear != null) {
                        if (carbyneGear instanceof CarbyneArmor) {
                            CarbyneArmor carbyneArmor = (CarbyneArmor) carbyneGear;

                            if (itemStack.getType().equals(Material.LEATHER_CHESTPLATE) || itemStack.getType().equals(Material.LEATHER_LEGGINGS)) {
                                if (carbyneArmor.getDefensivePotionEffects().size() > 0) {
                                    carbyneArmor.applyDefensiveEffect(damaged);
                                }

                                if (carbyneArmor.getOffensivePotionEffects().size() > 0) {
                                    if (event.getDamager() != null && event.getDamager() instanceof Player) {
                                        carbyneArmor.applyOffensiveEffect((Player) event.getDamager());
                                    }
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

                if (gearManager.isDefaultArmor(itemStack) || gearManager.isDefaultWeapon(itemStack)) {
                    if (gearManager.isDefaultArmor(itemStack)) {
                        MinecraftArmor minecraftArmor = gearManager.getDefaultArmor(itemStack);

                        minecraftArmor.damageItem(damaged, itemStack);
                    }

                    if (gearManager.isDefaultWeapon(itemStack)) {
                        MinecraftWeapon minecraftWeapon = gearManager.getDefaultWeapon(itemStack);

                        minecraftWeapon.damageItem(damaged, itemStack);
                    }
                }
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

            if (gearManager.isDefaultWeapon(itemStack)) {
                MinecraftWeapon minecraftWeapon = gearManager.getDefaultWeapon(itemStack);

                minecraftWeapon.damageItem(damager, itemStack);
            }
        }
    }

    @EventHandler
    public void onDurabilityLoss(PlayerItemDamageEvent event) {
        ItemStack item = event.getItem();

        if (gearManager.isCarbyneWeapon(item) || gearManager.isCarbyneArmor(item)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        PlayerUtility.checkForIllegalItems(event.getPlayer(), event.getPlayer().getInventory());

        if (!event.getAction().equals(Action.RIGHT_CLICK_AIR) && !event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            return;
        }

        if (!event.getPlayer().isSneaking()) {
            return;
        }

        ItemStack itemStack = event.getPlayer().getInventory().getItemInHand();

        if (itemStack == null) {
            return;
        }

        CarbyneWeapon carbyneWeapon = gearManager.getCarbyneWeapon(itemStack);

        if (carbyneWeapon == null) {
            return;
        }

        if (carbyneWeapon.getSpecial() != null) {
            if (carbyneWeapon.getSpecialCharge(itemStack) >= carbyneWeapon.getSpecial().getRequiredCharge() || event.getPlayer().hasPermission("carbyne.specials.bypass")) {
                carbyneWeapon.getSpecial().callSpecial(event.getPlayer());
                carbyneWeapon.setSpecialCharge(itemStack, 0);

                if (!event.getPlayer().hasPermission("carbyne.specials.bypass")) {
                    Board board = Board.getByPlayer(event.getPlayer());

                    if (board != null) {
                        BoardCooldown specialCooldown = board.getCooldown("special");

                        if (specialCooldown != null) {
                            MessageManager.sendMessage(event.getPlayer(), "&eYou cannot use another special for &6" + specialCooldown.getFormattedString(BoardFormat.SECONDS) + " &eseconds.");
                        } else {
                            new BoardCooldown(board, "special", 60.0D);
                        }
                    }
                }
            } else {
                MessageManager.sendMessage(event.getPlayer(), "&cYour weapon must be fully charged.");
            }
        }
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        LivingEntity livingEntity = event.getEntity();

        if (livingEntity.getKiller() != null) {
            ItemStack itemStack = livingEntity.getKiller().getItemInHand();

            if (gearManager.isCarbyneWeapon(itemStack)) {
                CarbyneWeapon carbyneWeapon = gearManager.getCarbyneWeapon(itemStack);

                if (carbyneWeapon == null) {
                    return;
                }

                if (carbyneWeapon.getSpecial() == null) {
                    return;
                }

                int specialCharge = carbyneWeapon.getSpecialCharge(itemStack);

                if (specialCharge >= carbyneWeapon.getSpecial().getRequiredCharge()) {
                    MessageManager.sendMessage(livingEntity.getKiller(), "&7[&aCarbyne&7]: &aYour &b" + carbyneWeapon.getSpecial().getSpecialName() + " &aweapon special is fully charged!");
                    carbyneWeapon.setSpecialCharge(itemStack, carbyneWeapon.getSpecial().getRequiredCharge());
                    return;
                }

                if (livingEntity instanceof Player) {
                    specialCharge += 5;
                } else if (livingEntity instanceof Monster) {
                    specialCharge += 1;
                }

                carbyneWeapon.setSpecialCharge(itemStack, specialCharge);
            }
        }
    }

    @EventHandler
    public void onCraftItem(CraftItemEvent event) {
        if (event.getCurrentItem() != null && event.getCurrentItem().getType() != Material.AIR) {
            if (gearManager.isDefaultArmor(event.getCurrentItem())) {
                MinecraftArmor minecraftArmor = gearManager.getDefaultArmor(event.getCurrentItem());

                if (minecraftArmor == null) {
                    return;
                }

                event.setCurrentItem(minecraftArmor.getItem(false));
            } else if (gearManager.isDefaultWeapon(event.getCurrentItem())) {
                MinecraftWeapon minecraftWeapon = gearManager.getDefaultWeapon(event.getCurrentItem());

                if (minecraftWeapon == null) {
                    return;
                }

                event.setCurrentItem(minecraftWeapon.getItem(false));
            }
        }
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (event.getCurrentItem() != null && event.getCurrentItem().getType() != Material.AIR) {
            if (gearManager.isDefaultArmor(event.getCurrentItem())) {
                if (event.getInventory() != null) {
                    if (gearManager.getGearGuiManager().isCustomInventory(event.getInventory())) {
                        return;
                    }
                }

                MinecraftArmor minecraftArmor = gearManager.getDefaultArmor(event.getCurrentItem());

                if (minecraftArmor == null) {
                    return;
                }

                event.setCurrentItem(gearManager.convertDefaultItem(event.getCurrentItem()));
            } else if (gearManager.isDefaultWeapon(event.getCurrentItem())) {
                if (event.getInventory() != null) {
                    if (gearManager.getGearGuiManager().isCustomInventory(event.getInventory())) {
                        return;
                    }
                }

                MinecraftWeapon minecraftWeapon = gearManager.getDefaultWeapon(event.getCurrentItem());

                if (minecraftWeapon == null) {
                    return;
                }

                event.setCurrentItem(gearManager.convertDefaultItem(event.getCurrentItem()));
            }
        }
    }

    @EventHandler
    public void onCraft(CraftItemEvent event) {
        ItemStack itemStack = event.getCurrentItem();
        Player player = (Player) event.getWhoClicked();

        if (gearManager.isCarbyneArmor(itemStack) || gearManager.isCarbyneWeapon(itemStack)) {
            event.setCancelled(true);
            MessageManager.sendMessage(player, "&cYou cannot craft with carbyne gear.");
        }
    }

    @EventHandler
    public void onEnchant(EnchantItemEvent event) {
        ItemStack itemStack = event.getItem();
        Player player = event.getEnchanter();

        if (gearManager.isCarbyneArmor(itemStack) || gearManager.isCarbyneWeapon(itemStack)) {
            event.setCancelled(true);
            MessageManager.sendMessage(player, "&cYou cannot enchant carbyne gear.");
            return;
        }

        for (Enchantment enchantment : event.getEnchantsToAdd().keySet()) {
            if (enchantment == Enchantment.PROTECTION_ENVIRONMENTAL) {
                if (event.getEnchantsToAdd().get(enchantment) > 2) {
                    event.getEnchantsToAdd().put(enchantment, 2);
                }
            }
        }
    }

    @EventHandler
    public void onAnvilUse(InventoryClickEvent event) {
        Inventory inv = event.getInventory();

        if (inv instanceof AnvilInventory) {
            if (gearManager.isCarbyneArmor(inv.getItem(0)) || gearManager.isCarbyneWeapon(inv.getItem(0))) {
                event.setCancelled(true);
                MessageManager.sendMessage(event.getWhoClicked(), "&cYou cannot enchant carbyne gear.");
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
    public void onPick(final PlayerPickupItemEvent event) {
        new BukkitRunnable() {
            public void run() {
                PlayerUtility.checkForIllegalItems(event.getPlayer(), event.getPlayer().getInventory());
            }
        }.runTaskLater(Carbyne.getInstance(), 5L);
    }

//    @EventHandler
//    public void onInventoryClose(InventoryCloseEvent event) {
//        double[] a = {0, 0, 0, 0};
//        ItemStack[] ac = e.getPlayer().getInventory().getArmorContents();
//
//        for (int i = 0; i < 4; i++) {
//            if (ac[i].getType().equals(Material.AIR)) {
//                continue;
//            }
//
//            if (!ac[i].hasItemMeta() && ac[i].getItemMeta() == null) {
//                continue;
//            }
//
//            if (!ac[i].getItemMeta().hasLore()) {
//                continue;
//            }
//
//            if (ac[i].getItemMeta().getLore() == null) {
//                continue;
//            }
//
//            if (ac[i].getItemMeta().getLore().size() < 1) {
//                continue;
//            }
//
//            if (ac[i].getItemMeta().getLore().get(1) == null) {
//                continue;
//            }
//
//            if (ac[i].getItemMeta().getLore().get(1).split("\\s+")[1] == null) {
//                continue;
//            }
//
//            if (gearManager.isDefaultArmor(ac[i]) || gearManager.isCarbyneArmor(ac[i])) {
//                a[i] = Double.parseDouble(ChatColor.stripColor(ac[i].getItemMeta().getLore().get(1).split("\\s+")[1]));
//            }
//        }
//
////        if (ac[0] != null && ac[1] != null && ac[2] != null && ac[3] != null) {
////            if (ac[0].hasItemMeta() && ac[1].hasItemMeta() && ac[2].hasItemMeta() && ac[3].hasItemMeta()) {
////                if (ac[0].getItemMeta().hasDisplayName() && ac[1].getItemMeta().hasDisplayName() && ac[2].getItemMeta().hasDisplayName() && ac[3].getItemMeta().hasDisplayName()) {
////                    if (ac[0].getItemMeta().getDisplayName().equalsIgnoreCase(ac[3].getItemMeta().getDisplayName()) && ac[1].getItemMeta().getDisplayName().equalsIgnoreCase(ac[3].getItemMeta().getDisplayName()) && ac[2].getItemMeta().getDisplayName().equalsIgnoreCase(ac[3].getItemMeta().getDisplayName())) {
////                        EffectsTask.addPlayer((Player) e.getPlayer());
////                    } else {
////                        EffectsTask.removePlayer((Player) e.getPlayer());
////                    }
////                }
////            }
//    }

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