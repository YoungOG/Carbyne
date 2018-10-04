package com.medievallords.carbyne.gear.listeners;

import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.gear.GearGuiManager;
import com.medievallords.carbyne.gear.GearManager;
import com.medievallords.carbyne.gear.artifacts.Artifact;
import com.medievallords.carbyne.gear.types.CarbyneGear;
import com.medievallords.carbyne.recipes.CustomCarbyneRecipe;
import com.medievallords.carbyne.utils.ItemBuilder;
import com.medievallords.carbyne.utils.MessageManager;
import com.medievallords.carbyne.utils.ParticleEffect;
import com.medievallords.carbyne.utils.StaticClasses;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class GearGuiListeners implements Listener {

    private GearGuiManager gearGuiManager;
    private final int[] numbers = {
            12,
            13,
            14,
            21,
            22,
            23,
            30,
            31,
            32,
            25,
            19
    };
    public GearGuiListeners(GearManager gearManager) {
        this.gearGuiManager = gearManager.getGearGuiManager();
    }

    @EventHandler
    public void onInt(InventoryDragEvent event) {
        if (!event.getInventory().getTitle().equals(GearGuiManager.TITLE)) {
            return;
        }

        final Player player = (Player) event.getWhoClicked();

        new BukkitRunnable() {
            @Override
            public void run() {

                final ItemStack[] ingredients = new ItemStack[9];

                ingredients[0] = event.getInventory().getItem(12);
                ingredients[1] = event.getInventory().getItem(13);
                ingredients[2] = event.getInventory().getItem(14);
                ingredients[3] = event.getInventory().getItem(21);
                ingredients[4] = event.getInventory().getItem(22);
                ingredients[5] = event.getInventory().getItem(23);
                ingredients[6] = event.getInventory().getItem(30);
                ingredients[7] = event.getInventory().getItem(31);
                ingredients[8] = event.getInventory().getItem(32);

                for (int i = 0; i < 9; i++) {
                    if (ingredients[i] == null) {
                        ingredients[i] = new ItemStack(Material.AIR);
                    }
                }

                final ItemStack artifact = event.getInventory().getItem(19);

                final CustomCarbyneRecipe recipe = CustomCarbyneRecipe.getRecipe(ingredients, artifact);

                if (artifact != null && artifact.getType() != Material.AIR) {
                    Artifact actualArtifact = StaticClasses.gearManager.getArtifact(artifact);
                    if (actualArtifact != null) {
                        ItemStack pane = new ItemBuilder(Material.STAINED_GLASS_PANE).durability(5)
                                .name("&4Artifact found")
                                .addLore("Placeholder lore message")
                                .build();
                        event.getInventory().setItem(1, pane);
                        event.getInventory().setItem(10, pane);
                        event.getInventory().setItem(28, pane);
                        event.getInventory().setItem(37, pane);
                    } else {
                        ItemStack pane = new ItemBuilder(Material.STAINED_GLASS_PANE).durability(14)
                                .name("&4Artifact found")
                                .addLore("Placeholder lore message")
                                .build();
                        event.getInventory().setItem(1, pane);
                        event.getInventory().setItem(10, pane);
                        event.getInventory().setItem(28, pane);
                        event.getInventory().setItem(37, pane);
                    }
                } else {
                    ItemStack pane = new ItemBuilder(Material.STAINED_GLASS_PANE).durability(14)
                            .name("&4Artifact found")
                            .addLore("Placeholder lore message")
                            .build();
                    event.getInventory().setItem(1, pane);
                    event.getInventory().setItem(10, pane);
                    event.getInventory().setItem(28, pane);
                }

                if (recipe == null) {
                    event.getInventory().setItem(25, null);
                    ItemStack pane = new ItemBuilder(Material.STAINED_GLASS_PANE).durability(14)
                            .name("&4Place Artifact in the &7empty &4slot.")
                            .addLore("Placeholder lore message")
                            .build();
                    event.getInventory().setItem(3, pane);
                    event.getInventory().setItem(4, pane);
                    event.getInventory().setItem(5, pane);
                    event.getInventory().setItem(39, pane);
                    event.getInventory().setItem(40, pane);
                    event.getInventory().setItem(41, pane);

                    event.getInventory().setItem(7, pane);
                    event.getInventory().setItem(16, pane);
                    event.getInventory().setItem(34, pane);
                    event.getInventory().setItem(43, pane);
                    return;
                }


                event.getInventory().setItem(25, recipe.getResult());
                ItemStack pane = new ItemBuilder(Material.STAINED_GLASS_PANE).durability(5)
                        .name("&4Yeah")
                        .addLore("Placeholder lore message")
                        .build();
                event.getInventory().setItem(3, pane);
                event.getInventory().setItem(4, pane);
                event.getInventory().setItem(5, pane);
                event.getInventory().setItem(39, pane);
                event.getInventory().setItem(40, pane);
                event.getInventory().setItem(41, pane);

                event.getInventory().setItem(7, pane);
                event.getInventory().setItem(16, pane);
                event.getInventory().setItem(34, pane);
                event.getInventory().setItem(43, pane);

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        player.updateInventory();
                    }
                }.runTaskLater(Carbyne.getInstance(), 1);
            }
        }.runTaskLater(Carbyne.getInstance(), 1);
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (event.getClickedInventory() == null) {
            return;
        }

        if (!event.getClickedInventory().getTitle().equals(GearGuiManager.TITLE)) {
            return;
        }

        if (!contains(numbers, event.getSlot())) {
            event.setCancelled(true);
            return;
        }

        final Player player = (Player) event.getWhoClicked();

        if (event.getSlot() == 25 && event.getCurrentItem().getType() != Material.AIR) {
            event.setCancelled(true);

            final ItemStack[] items = new ItemStack[10];
            items[0] = event.getInventory().getItem(12);
            items[1] = event.getInventory().getItem(13);
            items[2] = event.getInventory().getItem(14);
            items[3] = event.getInventory().getItem(21);
            items[4] = event.getInventory().getItem(22);
            items[5] = event.getInventory().getItem(23);
            items[6] = event.getInventory().getItem(30);
            items[7] = event.getInventory().getItem(31);
            items[8] = event.getInventory().getItem(32);
            items[9] = event.getInventory().getItem(19);

            for (int i = 0; i < 10; i++) {
                if (items[i] != null) {
                    items[i].setAmount(items[i].getAmount() - 1);
                }
            }


            Location location = StaticClasses.gearManager.getForgeByDistance(player.getLocation()).clone().add(0.5, 1.15, 0.5);
            forgeGear(player, StaticClasses.gearManager.getCarbyneGear(event.getCurrentItem()), location);
            event.getClickedInventory().setItem(25, null);
            //player.closeInventory();
            //return;
        }

        new BukkitRunnable() {
            @Override
            public void run() {

                final ItemStack[] ingredients = new ItemStack[9];

                ingredients[0] = event.getInventory().getItem(12);
                ingredients[1] = event.getInventory().getItem(13);
                ingredients[2] = event.getInventory().getItem(14);
                ingredients[3] = event.getInventory().getItem(21);
                ingredients[4] = event.getInventory().getItem(22);
                ingredients[5] = event.getInventory().getItem(23);
                ingredients[6] = event.getInventory().getItem(30);
                ingredients[7] = event.getInventory().getItem(31);
                ingredients[8] = event.getInventory().getItem(32);

                for (int i = 0; i < 9; i++) {
                    if (ingredients[i] == null) {
                        ingredients[i] = new ItemStack(Material.AIR);
                    }
                }

                final ItemStack artifact = event.getInventory().getItem(19);

                final CustomCarbyneRecipe recipe = CustomCarbyneRecipe.getRecipe(ingredients, artifact);

                if (artifact != null && artifact.getType() != Material.AIR) {
                    Artifact actualArtifact = StaticClasses.gearManager.getArtifact(artifact);
                    if (actualArtifact != null) {
                        ItemStack pane = new ItemBuilder(Material.STAINED_GLASS_PANE).durability(5)
                                .name("&4Artifact found")
                                .addLore("Placeholder lore message")
                                .build();
                        event.getInventory().setItem(1, pane);
                        event.getInventory().setItem(10, pane);
                        event.getInventory().setItem(28, pane);
                        event.getInventory().setItem(37, pane);
                    } else {
                        ItemStack pane = new ItemBuilder(Material.STAINED_GLASS_PANE).durability(14)
                                .name("&4Artifact found")
                                .addLore("Placeholder lore message")
                                .build();
                        event.getInventory().setItem(1, pane);
                        event.getInventory().setItem(10, pane);
                        event.getInventory().setItem(28, pane);
                        event.getInventory().setItem(37, pane);
                    }
                } else {
                    ItemStack pane = new ItemBuilder(Material.STAINED_GLASS_PANE).durability(14)
                            .name("&4Artifact found")
                            .addLore("Placeholder lore message")
                            .build();
                    event.getInventory().setItem(1, pane);
                    event.getInventory().setItem(10, pane);
                    event.getInventory().setItem(28, pane);
                    event.getInventory().setItem(37, pane);
                }

                if (recipe == null) {
                    event.getInventory().setItem(25, null);
                    ItemStack pane = new ItemBuilder(Material.STAINED_GLASS_PANE).durability(14)
                            .name("&4Place Artifact in the &7empty &4slot.")
                            .addLore("Placeholder lore message")
                            .build();
                    event.getInventory().setItem(3, pane);
                    event.getInventory().setItem(4, pane);
                    event.getInventory().setItem(5, pane);
                    event.getInventory().setItem(39, pane);
                    event.getInventory().setItem(40, pane);
                    event.getInventory().setItem(41, pane);

                    event.getInventory().setItem(7, pane);
                    event.getInventory().setItem(16, pane);
                    event.getInventory().setItem(34, pane);
                    event.getInventory().setItem(43, pane);
                    return;
                }

                event.getInventory().setItem(25, recipe.getResult());
                ItemStack pane = new ItemBuilder(Material.STAINED_GLASS_PANE).durability(5)
                        .name("&4Yeah")
                        .addLore("Placeholder lore message")
                        .build();
                event.getInventory().setItem(3, pane);
                event.getInventory().setItem(4, pane);
                event.getInventory().setItem(5, pane);
                event.getInventory().setItem(39, pane);
                event.getInventory().setItem(40, pane);
                event.getInventory().setItem(41, pane);

                event.getInventory().setItem(7, pane);
                event.getInventory().setItem(16, pane);
                event.getInventory().setItem(34, pane);
                event.getInventory().setItem(43, pane);

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        player.updateInventory();
                    }
                }.runTaskLater(Carbyne.getInstance(), 1);
            }
        }.runTaskLater(Carbyne.getInstance(), 1);
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        if (!event.getInventory().getTitle().equals(GearGuiManager.TITLE)) {
            return;
        }

        final Player player = (Player) event.getPlayer();

        final ItemStack[] ingredients = new ItemStack[10];
        ingredients[0] = event.getInventory().getItem(12);
        ingredients[1] = event.getInventory().getItem(13);
        ingredients[2] = event.getInventory().getItem(14);
        ingredients[3] = event.getInventory().getItem(21);
        ingredients[4] = event.getInventory().getItem(22);
        ingredients[5] = event.getInventory().getItem(23);
        ingredients[6] = event.getInventory().getItem(30);
        ingredients[7] = event.getInventory().getItem(31);
        ingredients[8] = event.getInventory().getItem(32);
        ingredients[9] = event.getInventory().getItem(19);

        for (int i = 0; i < 10; i++) {
            if (ingredients[i] != null) {
                player.getInventory().addItem(ingredients[i]);
            }
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        if (event.getClickedBlock() == null || event.getClickedBlock().getType() == Material.AIR) {
            return;
        }

        Location clicked = event.getClickedBlock().getLocation();
        Location forge = StaticClasses.gearManager.getForge(clicked);
        if (forge != null) {
            event.getPlayer().openInventory(gearGuiManager.getForgeInventory());
            event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.BLOCK_CHEST_OPEN, 1, .8f);
            event.setCancelled(true);
        }

    }

    private boolean contains(int[] n, int i) {
        for (int nu : n) {
            if (nu == i) {
                return true;
            }
        }

        return false;
    }

    private void forgeGear(final Player player, final CarbyneGear gear, final Location location) {

        final Item item = player.getWorld().dropItem(location, gear.getItem(false));
        item.setPickupDelay(Integer.MAX_VALUE);

        MessageManager.sendMessage(player, "&8[&5&lForge&8] &cYour item is now being forged.");
        new BukkitRunnable() {
            int i = -1;
            boolean far = false;

            @Override
            public void run() {
                i++;

                if ((!player.getWorld().getName().equals(location.getWorld().getName()) && !far) || ((player.getWorld().getName().equals(location.getWorld().getName()) && (player.getLocation().distance(location) >= 9 && !far)))) {
                    far = true;
                    MessageManager.sendMessage(player, "&8[&5&lForge&8] &cYou are too far from the anvil, your item will be dropped on the ground.");
                }

                if (player.getWorld().getName().equals(location.getWorld().getName()) && (far && player.getLocation().distance(location) < 9)) {
                    far = false;
                    MessageManager.sendMessage(player, "&8[&5&lForge&8] &aYou are no longer too far away.");
                }

                if (i >= 20) {
                    cancel();

                    //ItemStack itemStack = gear.getItem(false).clone();
                    if (!player.isOnline() || far) {
                        location.getWorld().dropItem(location, gear.getItem(false));

                        if (item != null) {
                            item.remove();
                        }

                        if (player.isOnline()) {
                            MessageManager.sendMessage(player, "&8[&5&lForge&8] &aYour item has been forged.");
                        }
                    } else {
                        if (player.getInventory().getItemInMainHand() != null) {
                            if (player.getInventory().firstEmpty() == -1) {
                                player.getWorld().dropItem(player.getLocation(), gear.getItem(false));
                            } else {
                                player.getInventory().addItem(gear.getItem(false));
                            }
                        } else
                            player.setItemInHand(gear.getItem(false));

                        MessageManager.sendMessage(player, "&8[&5&lForge&8] &aYour item has been forged.");

                        if (item != null) {
                            item.remove();
                        }
                    }
                }

                ParticleEffect.FLAME.display(0f, 0f, 0f, 0.075f, 20, location, 40, true);
                player.getWorld().playSound(location, Sound.BLOCK_ANVIL_USE, 1f, (float) Math.random() * 2.5f);
            }
        }.runTaskTimer(Carbyne.getInstance(), 0, 20);
    }

//    @EventHandler
//    public void onClick(InventoryClickEvent e) {
//        Player p = (Player) e.getWhoClicked();
//
//        if (gearGuiManager.isCustomInventory(e.getInventory()))
//            e.setCancelled(true);
//
//
//        if (e.getInventory().getSize() == 9) {
//            if (!(e.getRawSlot() < e.getView().getTopInventory().getSize()))
//                return;
//        } else if (e.getRawSlot() != e.getSlot())
//                return;
//
//        if (e.getCurrentItem() == null)
//            return;
//
//        if (e.getInventory().getTitle().equalsIgnoreCase(gearGuiManager.getStoreGui().getTitle())) {
//            e.setCancelled(true);
//
//            if (e.getCurrentItem().getType() == Material.DIAMOND_SWORD)
//                p.openInventory(gearGuiManager.getWeaponsGui());
//            else if (e.getCurrentItem().getType() == Material.LEATHER_CHESTPLATE)
//                p.openInventory(gearGuiManager.getArmorGui());
//        } else if (e.getInventory().getTitle().equalsIgnoreCase(gearGuiManager.getWeaponsGui().getTitle())) {
//            e.setCancelled(true);
//
//            if (e.getCurrentItem().getType() == Material.BARRIER)
//                p.openInventory(gearGuiManager.getStoreGui());
//            else {
//                if (e.getCurrentItem() != null && e.getCurrentItem().getType() != Material.AIR) {
//                    CarbyneWeapon carbyneWeapon = StaticClasses.gearManager.getCarbyneWeapon(e.getCurrentItem());
//
//                    if (carbyneWeapon == null) {
//                        MessageManager.sendMessage(p, "&cAn error has occurred while purchasing this item. (Error code: 1)");
//                        p.closeInventory();
//                        return;
//                    }
//
//                    if (carbyneWeapon.getState() == GearState.HIDDEN && (!p.hasPermission("carbyne.administrator") || !p.isOp())) {
//                        MessageManager.sendMessage(p, "&cThis weapon is not purchasable.");
//                        p.closeInventory();
//                        return;
//                    }
//
//                    if (p.getInventory().firstEmpty() == -1) {
//                        MessageManager.sendMessage(p, "&cYour inventory is full.");
//                        p.closeInventory();
//                        return;
//                    }
//
//                    if (p.hasPermission("carbyne.administrator") || p.isOp()) {
//                        p.getInventory().addItem(carbyneWeapon.getItem(false).clone());
//                        StringUtils.logToFile("[Carbyne OP] " + new Date().toString() + " --> " + p.getName() + " has purchased " + carbyneWeapon.getDisplayName(), "carbyneItemLog.txt");
//                        p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
//                        MessageManager.sendMessage(p, "&aSuccessfully purchased a &4" + carbyneWeapon.getDisplayName() + "&a.");
//                        return;
//                    }
//
//                    if (canBuy(p, carbyneWeapon.getCost())) {
//                        int total = 0;
//
//                        for (ItemStack item : p.getInventory().all(StaticClasses.gearManager.getTokenMaterial()).values())
//                            if (item.hasItemMeta() && item.getItemMeta().hasLore())
//                                total += item.getAmount();
//
//                        if (total < carbyneWeapon.getCost()) {
//                            MessageManager.sendMessage(p, "&cYou do not have enough armor tokens to purchase this item.");
//                            p.closeInventory();
//                            return;
//                        }
//
//                        PlayerUtility.removeItems(p.getInventory(), StaticClasses.gearManager.getTokenItem(), carbyneWeapon.getCost());
//
//                        p.getInventory().addItem(carbyneWeapon.getItem(false).clone());
//                        StringUtils.logToFile("[Carbyne] " + new Date().toString() + " --> " + p.getName() + " has purchased " + carbyneWeapon.getDisplayName(), "carbyneItemLog.txt");
//                        p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
//                        MessageManager.sendMessage(p, "&aSuccessfully purchased a &5" + carbyneWeapon.getDisplayName() + " &afor &c" + carbyneWeapon.getCost() + " &aof &b" + ChatColor.stripColor(StaticClasses.gearManager.getTokenItem().getItemMeta().getDisplayName()) + "&a.");
//                    } else {
//                        MessageManager.sendMessage(p, "&cYou do not have enough armor tokens to purchase this item.");
//                        p.closeInventory();
//                    }
//                }
//            }
//        } else if (e.getInventory().getTitle().equalsIgnoreCase(gearGuiManager.getArmorGui().getTitle())) {
//            e.setCancelled(true);
//
//            if (e.getCurrentItem().getType() == Material.BARRIER)
//                p.openInventory(gearGuiManager.getStoreGui());
//            else {
//                if (e.getCurrentItem() != null && e.getCurrentItem().getType() != Material.AIR) {
//                    CarbyneArmor carbyneArmor = StaticClasses.gearManager.getCarbyneArmor(e.getCurrentItem());
//
//                    if (carbyneArmor == null) {
//                        MessageManager.sendMessage(p, "&cAn error has occurred while purchasing this item. (Error code: 1)");
//                        p.closeInventory();
//                        return;
//                    }
//
//                    if (gearGuiManager.getArmorGuiList().keySet().contains(carbyneArmor.getDisplayName()))
//                        p.openInventory(gearGuiManager.getArmorGuiList().get(carbyneArmor.getDisplayName()));
//                }
//            }
//        } else if (ChatColor.stripColor(e.getInventory().getTitle()).contains("Purchase Armor") || ChatColor.stripColor(e.getInventory().getTitle()).equalsIgnoreCase("Purchase Armor")) {
//            e.setCancelled(true);
//
//            if (e.getCurrentItem().getType() == Material.BARRIER)
//                p.openInventory(gearGuiManager.getArmorGui());
//            else {
//                if (e.getCurrentItem() != null && e.getCurrentItem().getType() != Material.AIR) {
//                    CarbyneArmor carbyneArmor = StaticClasses.gearManager.getCarbyneArmor(e.getCurrentItem());
//
//                    if (carbyneArmor == null) {
//                        MessageManager.sendMessage(p, "&cAn error has occurred while purchasing this item. (Error code: 1)");
//                        p.closeInventory();
//                        return;
//                    }
//
//                    if (carbyneArmor.getState() == GearState.HIDDEN && (!p.hasPermission("carbyne.administrator") || !p.isOp())) {
//                        MessageManager.sendMessage(p, "&cThis armor set is not purchasable.");
//                        p.closeInventory();
//                        return;
//                    }
//
//                    if (p.getInventory().firstEmpty() == -1) {
//                        MessageManager.sendMessage(p, "&cYour inventory is full.");
//                        p.closeInventory();
//                        return;
//                    }
//
//                    if (p.hasPermission("carbyne.administrator") || p.isOp()) {
//                        p.getInventory().addItem(carbyneArmor.getItem(false).clone());
//                        p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
//                        StringUtils.logToFile("[Carbyne OP] " + new Date().toString() + " --> " + p.getName() + " has purchased " + carbyneArmor.getDisplayName(), "carbyneItemLog.txt");
//                        MessageManager.sendMessage(p, "&aSuccessfully purchased a &5" + carbyneArmor.getDisplayName() + "&a.");
//                        return;
//                    }
//
//                    if (canBuy(p, carbyneArmor.getCost())) {
//                        int total = 0;
//
//                        for (ItemStack item : p.getInventory().all(StaticClasses.gearManager.getTokenMaterial()).values())
//                            if (item.hasItemMeta() && item.getItemMeta().hasLore())
//                                total += item.getAmount();
//
//                        if (total < carbyneArmor.getCost()) {
//                            MessageManager.sendMessage(p, "&cYou do not have enough armor tokens to purchase this item.");
//                            p.closeInventory();
//                            return;
//                        }
//
//                        PlayerUtility.removeItems(p.getInventory(), StaticClasses.gearManager.getTokenItem(), carbyneArmor.getCost());
//
//                        p.getInventory().addItem(carbyneArmor.getItem(false).clone());
//                        StringUtils.logToFile("[Carbyne] " + new Date().toString() + " --> " + p.getName() + " has purchased " + carbyneArmor.getDisplayName(), "carbyneItemLog.txt");
//                        p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
//                        MessageManager.sendMessage(p, "&aSuccessfully purchased a &5" + carbyneArmor.getDisplayName() + " &afor &c" + carbyneArmor.getCost() + " &aof &b" + ChatColor.stripColor(StaticClasses.gearManager.getTokenItem().getItemMeta().getDisplayName()) + "&a.");
//                    } else {
//                        MessageManager.sendMessage(p, "&cYou do not have enough armor tokens to purchase this item.");
//                        p.closeInventory();
//                    }
//                }
//            }
//        }
//    }
//
//    public boolean canBuy(Player player, int cost) {
//        return player.getInventory().containsAtLeast(StaticClasses.gearManager.getTokenItem(), cost);
//    }
}
