package com.medievallords.carbyne.gear.listeners;

import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.gear.GearManager;
import com.medievallords.carbyne.gear.types.CarbyneGear;
import com.medievallords.carbyne.gear.types.carbyne.CarbyneArmor;
import com.medievallords.carbyne.gear.types.carbyne.CarbyneWeapon;
import com.medievallords.carbyne.gear.types.minecraft.MinecraftArmor;
import com.medievallords.carbyne.gear.types.minecraft.MinecraftWeapon;
import com.medievallords.carbyne.utils.MessageManager;
import com.medievallords.carbyne.utils.Namer;
import com.medievallords.carbyne.utils.PlayerUtility;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Animals;
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
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.Set;

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
            for (ItemStack is : player.getInventory().getArmorContents()) {
                if (is.getType().equals(Material.AIR))
                    continue;

                if (gearManager.isCarbyneArmor(is)) {
                    CarbyneArmor ca = gearManager.getCarbyneArmor(is);

                    if (ca != null) {
                        armorReduction = armorReduction + ca.getArmorRating();
                    }
                }

                if (gearManager.isDefaultArmor(is)) {
                    MinecraftArmor ca = gearManager.getDefaultArmor(is);

                    if (ca != null) {
                        armorReduction = armorReduction + ca.getArmorRating();
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

    @EventHandler
    public void onEntityDeath(EntityDeathEvent deathEvent) {
        if (deathEvent.getEntity().getKiller() == null || deathEvent.getEntity().getKiller().getInventory().getItemInHand() == null) {
            return;
        }
        CarbyneWeapon carbyneWeapon = gearManager.getCarbyneWeapon(deathEvent.getEntity().getKiller().getInventory().getItemInHand());
        if(carbyneWeapon.getSpecial() == null) {
            return;
        }
        if (deathEvent.getEntity() instanceof Player) {
           carbyneWeapon.setCharge(carbyneWeapon.getCharge() + 10);
        }
        else if (deathEvent.getEntity() instanceof Monster || deathEvent.getEntity() instanceof Animals) {
            carbyneWeapon.setCharge(carbyneWeapon.getCharge() + 1);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityDamagebyEntity(EntityDamageByEntityEvent e) {
        if (e.getEntity() instanceof Player) {
            Player attacked = (Player) e.getEntity();

            for (ItemStack is : attacked.getInventory().getArmorContents()) {
                if (is.getType().equals(Material.LEATHER_CHESTPLATE) || is.getType().equals(Material.LEATHER_LEGGINGS)) {
                    CarbyneArmor ca = gearManager.getCarbyneArmor(is);

                    if (ca != null) {
                        if (ca.getDefensivePotionEffects().size() > 0) {
                            ca.applyDefensiveEffect(attacked);
                        }

                        if (ca.getOffensivePotionEffects().size() > 0) {
                            if (e.getDamager() != null && e.getDamager() instanceof Player) {
                                ca.applyOffensiveEffect((Player) e.getDamager());
                            }
                        }
                    }
                }

                CarbyneGear cg = gearManager.getCarbyneGear(is);

                if (cg == null) {
                    continue;
                }

                int durability = gearManager.getDurability(is);

                if (durability == -1) {
                    return;
                }

                if (durability >= 1) {
                    durability--;
                    List<String> old = is.getItemMeta().getLore();
                    old.remove(1);
                    old.add(1, ChatColor.GREEN + "Durability" + ChatColor.GRAY + ": " + ChatColor.RED + durability);
                    Namer.setLore(is, old);
                } else {
                    attacked.getInventory().remove(is);
                    attacked.playSound(attacked.getLocation(), Sound.ITEM_BREAK, 1, 1);
                }
            }
        }

        if (e.getDamager() instanceof Player) {
            Player damager = (Player) e.getDamager();
            ItemStack is = damager.getItemInHand();
            CarbyneWeapon cw = gearManager.getCarbyneWeapon(is);

            if (cw != null) {
                if (e.getEntity() != null && e.getEntity() instanceof Player) {
                    if (cw.getOffensivePotionEffects().size() > 0) {
                        cw.applyOffensiveEffect((Player) e.getEntity());
                    }
                }

                if (cw.getDefensivePotionEffects().size() > 0) {
                    cw.applyDefensiveEffect(damager);
                }

                int durability = gearManager.getDurability(is);

                if (durability == -1) {
                    return;
                }

                if (durability >= 1) {
                    durability--;
                    List<String> old = is.getItemMeta().getLore();
                    old.remove(1);
                    old.add(1, ChatColor.GREEN + "Durability" + ChatColor.GRAY + ": " + ChatColor.RED + durability);
                    Namer.setLore(is, old);
                } else {
                    damager.setItemInHand(new ItemStack(Material.AIR));
                    damager.getWorld().playSound(damager.getLocation(), Sound.ITEM_BREAK, 1.0F, 1.0F);
                }
            } else {
                int durability = gearManager.getDurability(is);

                if (durability == -1) {
                    return;
                }

                if (durability >= 1) {
                    durability--;
                    List<String> old = is.getItemMeta().getLore();
                    old.remove(1);
                    old.add(1, ChatColor.GREEN + "Durability" + ChatColor.GRAY + ": " + ChatColor.RED + durability);
                    Namer.setLore(is, old);
                } else {
                    damager.setItemInHand(new ItemStack(Material.AIR));
                    damager.getWorld().playSound(damager.getLocation(), Sound.ITEM_BREAK, 1.0F, 1.0F);
                }
            }
        }
    }


    @EventHandler
    public  void onInteract(PlayerInteractEvent interactEvent) {
        if ((interactEvent.getAction() == Action.RIGHT_CLICK_AIR || interactEvent.getAction() == Action.RIGHT_CLICK_BLOCK) && interactEvent.getPlayer().isSneaking()) {
            CarbyneWeapon carbyneWeapon = gearManager.getCarbyneWeapon(interactEvent.getPlayer().getInventory().getItemInHand());
            if (carbyneWeapon == null || carbyneWeapon.getSpecial() == null)
                return;

            if (carbyneWeapon.getCharge() >= carbyneWeapon.getSpecial().getRequiredCharge()) {
                carbyneWeapon.getSpecial().callSpecial(interactEvent.getPlayer(), interactEvent.getPlayer().getTargetBlock((Set<Material>) null, 10).getLocation(), carbyneWeapon);
            }
        }
    }



//    /**
//     * If a player right clicks air and is sneaking, they are checked if they can use a special. If they can they use the special.
//     *
//     * @param e
//     */
//    @EventHandler
//    public void onInteract(PlayerInteractEvent e) {
//        PlayerUtility.checkForIllegalItems(e.getPlayer(), e.getPlayer().getInventory());
//
//        if (!e.getAction().equals(Action.RIGHT_CLICK_AIR) && !e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
//            return;
//        }
//
//        if (!e.getPlayer().isSneaking()) {
//            return;
//        }
//
//        ItemStack is = e.getPlayer().getInventory().getItemInHand();
//
//        if (is == null) {
//            return;
//        }
//
//        CarbyneWeapon cw = gearManager.getCarbyneWeapon(is);
//
//        if (cw == null) {
//            return;
//        }
//
//        if (cw.getSpecialName() != null) {
//            MessageManager.sendMessage(e.getPlayer(), "&cSpecials have been disabled.");
////                cw.useSpecial(cw, cpm.getCPByName(e.getPlayer().getName()));
//        }
//    }
//
//    /**
//     * On an entities death, if the killer is a player, his special charges will increase and update.
//     *
//     * @param e
//     */
//    @EventHandler
//    public void onEntityDeath(EntityDeathEvent e) {
//        LivingEntity le = e.getEntity();
//
//        if (le.getKiller() != null) {
//            ItemStack is = le.getKiller().getItemInHand();
//
//            if (gearManager.isCarbyneWeapon(is)) {
//                CarbyneWeapon cw = gearManager.getCarbyneWeapon(is);
//
//                if (cw == null) {
//                    return;
//                }
//
//                String[] chargeLine = is.getItemMeta().getLore().get(2).split("\\s+");
//                int am = Integer.parseInt(chargeLine[1]);
//
//                if (am == cw.getSpecialCost()) {
//                    return;
//                }
//
//                am++;
//                Namer.setLore(is, "Charge: " + am + " / " + cw.getSpecialCost(), 2);
//                cw.setSpecialCharges(am);
//            }
//        }
//    }

    /**
     * This method checks if the player is crafting a minecraft armor peice (excluding chain). The peice will be replaced
     * by a MinecraftArmor getItem() itemstack. If the ItemStack is null a warning will display.
     *
     * @param e the craft item event in question
     */
    @EventHandler
    public void onCraftItem(CraftItemEvent e) {
        if (e.getCurrentItem() != null && e.getCurrentItem().getType() != Material.AIR) {
            if (gearManager.isDefaultArmor(e.getCurrentItem())) {
                MinecraftArmor ma = gearManager.getDefaultArmor(e.getCurrentItem());

                if (ma == null) {
                    return;
                }

                e.setCurrentItem(ma.getItem(false));
            } else if (gearManager.isDefaultWeapon(e.getCurrentItem())) {
                MinecraftWeapon mw = gearManager.getDefaultWeapon(e.getCurrentItem());

                if (mw == null) {
                    return;
                }

                e.setCurrentItem(mw.getItem(false));
            }
        }
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (e.getCurrentItem() != null && e.getCurrentItem().getType() != Material.AIR) {
            if (gearManager.isDefaultArmor(e.getCurrentItem())) {
                if (e.getInventory() != null) {
                    if (gearManager.getGearGuiManager().isCustomInventory(e.getInventory())) {
                        return;
                    }
                }

                MinecraftArmor ma = gearManager.getDefaultArmor(e.getCurrentItem());

                if (ma == null) {
                    return;
                }

                e.setCurrentItem(gearManager.convertDefaultItem(e.getCurrentItem()));
            } else if (gearManager.isDefaultWeapon(e.getCurrentItem())) {
                if (e.getInventory() != null) {
                    if (gearManager.getGearGuiManager().isCustomInventory(e.getInventory())) {
                        return;
                    }
                }

                MinecraftWeapon mw = gearManager.getDefaultWeapon(e.getCurrentItem());

                if (mw == null) {
                    return;
                }

                e.setCurrentItem(gearManager.convertDefaultItem(e.getCurrentItem()));
            }
        }
    }

    @EventHandler
    public void onCraft(CraftItemEvent e) {
        ItemStack item = e.getCurrentItem();
        Player player = (Player) e.getWhoClicked();

        if (gearManager.isCarbyneArmor(item) || gearManager.isCarbyneWeapon(item)) {
            e.setCancelled(true);
            MessageManager.sendMessage(player, "&cYou cannot craft with carbyne gear.");
        }
    }

    @EventHandler
    public void onEnchant(EnchantItemEvent e) {
        ItemStack item = e.getItem();
        Player player = e.getEnchanter();

        if (gearManager.isCarbyneArmor(item) || gearManager.isCarbyneWeapon(item)) {
            e.setCancelled(true);
            MessageManager.sendMessage(player, "&cYou cannot enchant carbyne gear.");
            return;
        }

        for (Enchantment enchantment : e.getEnchantsToAdd().keySet()) {
            if (enchantment == Enchantment.PROTECTION_ENVIRONMENTAL) {
                if (e.getEnchantsToAdd().get(enchantment) > 2) {
                    e.getEnchantsToAdd().put(enchantment, 2);
                }
            }
        }
    }

    @EventHandler
    public void onAnvilUse(InventoryClickEvent e) {
        Inventory inv = e.getInventory();

        if (inv instanceof AnvilInventory) {
            if (gearManager.isCarbyneArmor(inv.getItem(0)) || gearManager.isCarbyneWeapon(inv.getItem(0))) {
                e.setCancelled(true);
                MessageManager.sendMessage(e.getWhoClicked(), "&cYou cannot enchant carbyne gear.");
            }
        }
    }

    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent e) {
        Player player = (Player) e.getPlayer();
        Inventory inventory = e.getInventory();

        PlayerUtility.checkForIllegalItems(player, inventory);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        Player player = (Player) e.getWhoClicked();
        Inventory inventory = e.getInventory();

        PlayerUtility.checkForIllegalItems(player, inventory);
    }

    @EventHandler
    public void itemDrop(ItemSpawnEvent e) {
        gearManager.convertToMoneyItem(e.getEntity().getItemStack());
    }

    @EventHandler
    public void onPick(final PlayerPickupItemEvent e) {
        new BukkitRunnable() {
            public void run() {
                PlayerUtility.checkForIllegalItems(e.getPlayer(), e.getPlayer().getInventory());
            }
        }.runTaskLater(Carbyne.getInstance(), 5L);
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {
        double[] a = {0, 0, 0, 0};
        ItemStack[] ac = e.getPlayer().getInventory().getArmorContents();

        for (int i = 0; i < 4; i++) {
            if (ac[i].getType().equals(Material.AIR)) {
                continue;
            }

            if (!ac[i].hasItemMeta() && ac[i].getItemMeta() == null) {
                continue;
            }

            if (!ac[i].getItemMeta().hasLore()) {
                continue;
            }

            if (ac[i].getItemMeta().getLore() == null) {
                continue;
            }

            if (ac[i].getItemMeta().getLore().size() < 1) {
                continue;
            }

            if (ac[i].getItemMeta().getLore().get(1) == null) {
                continue;
            }

            if (ac[i].getItemMeta().getLore().get(1).split("\\s+")[1] == null) {
                continue;
            }

            if (gearManager.isDefaultArmor(ac[i]) || gearManager.isCarbyneArmor(ac[i])) {
                a[i] = Double.parseDouble(ChatColor.stripColor(ac[i].getItemMeta().getLore().get(1).split("\\s+")[1]));
            }
        }

//        if (ac[0] != null && ac[1] != null && ac[2] != null && ac[3] != null) {
//            if (ac[0].hasItemMeta() && ac[1].hasItemMeta() && ac[2].hasItemMeta() && ac[3].hasItemMeta()) {
//                if (ac[0].getItemMeta().hasDisplayName() && ac[1].getItemMeta().hasDisplayName() && ac[2].getItemMeta().hasDisplayName() && ac[3].getItemMeta().hasDisplayName()) {
//                    if (ac[0].getItemMeta().getDisplayName().equalsIgnoreCase(ac[3].getItemMeta().getDisplayName()) && ac[1].getItemMeta().getDisplayName().equalsIgnoreCase(ac[3].getItemMeta().getDisplayName()) && ac[2].getItemMeta().getDisplayName().equalsIgnoreCase(ac[3].getItemMeta().getDisplayName())) {
//                        EffectsTask.addPlayer((Player) e.getPlayer());
//                    } else {
//                        EffectsTask.removePlayer((Player) e.getPlayer());
//                    }
//                }
//            }
    }

    public double getProtectionReduction(Player player) {
        double reduction = 0.0;

        for (ItemStack is : player.getInventory().getArmorContents()) {
            if (is.getType().equals(Material.AIR))
                continue;

            switch (is.getEnchantmentLevel(Enchantment.PROTECTION_ENVIRONMENTAL)) {
                case 1:
                    if (is.getType().toString().contains("HELMET")) {
                        reduction += 0.015;
                    } else if (is.getType().toString().contains("CHESTPLATE")) {
                        reduction += 0.04;
                    } else if (is.getType().toString().contains("LEGGINGS")) {
                        reduction += 0.03;
                    } else if (is.getType().toString().contains("BOOTS")) {
                        reduction += 0.015;
                    }
                    break;
                case 2:
                    if (is.getType().toString().contains("HELMET")) {
                        reduction += 0.03;
                    } else if (is.getType().toString().contains("CHESTPLATE")) {
                        reduction += 0.08;
                    } else if (is.getType().toString().contains("LEGGINGS")) {
                        reduction += 0.06;
                    } else if (is.getType().toString().contains("BOOTS")) {
                        reduction += 0.03;
                    }
                    break;
            }
        }

        return reduction;
    }
}