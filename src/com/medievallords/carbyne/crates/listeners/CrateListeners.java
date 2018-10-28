package com.medievallords.carbyne.crates.listeners;

import com.boydti.fawe.util.TaskManager;
import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.crates.Crate;
import com.medievallords.carbyne.crates.animations.*;
import com.medievallords.carbyne.crates.keys.Key;
import com.medievallords.carbyne.utils.Cooldowns;
import com.medievallords.carbyne.utils.MessageManager;
import com.medievallords.carbyne.utils.StaticClasses;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class CrateListeners implements Listener {

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        Player player = e.getPlayer();

        if (e.hasItem() && e.getItem() != null && e.getItem().getType() != Material.AIR) {
            Key key = StaticClasses.crateManager.getKey(e.getItem());

            if (key != null)
                e.setCancelled(true);
        }

        if (e.hasBlock() && e.getClickedBlock() != null && e.getClickedBlock().getType() != Material.AIR) {
            Block block = e.getClickedBlock();

            if (StaticClasses.crateManager.getCrate(block.getLocation()) == null)
                return;

            e.setCancelled(true);

            if (StaticClasses.crateManager.isOpeningCrate(player)) {
                MessageManager.sendMessage(player, "&cYou are already opening a crate.");
                return;
            }

            Crate crate = StaticClasses.crateManager.getCrate(block.getLocation());

            if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
                if (player.getInventory().getItemInMainHand() != null && player.getInventory().getItemInMainHand().getType() != Material.AIR) {
                    ItemStack itemStack = player.getInventory().getItemInMainHand();
                    Key key = StaticClasses.crateManager.getKey(itemStack);

                    if (key == null) {
                        MessageManager.sendMessage(player, "&cYou must be holding a key to open this crate.");
                        crate.knockbackPlayer(player, crate.getLocation());
                        return;
                    }

                    if (!key.getCrate().equalsIgnoreCase(crate.getName())) {
                        MessageManager.sendMessage(player, "&cYou must be holding a key to open this crate.");
                        crate.knockbackPlayer(player, crate.getLocation());
                        return;
                    }
                } else {
                    MessageManager.sendMessage(player, "&cYou must be holding a key to open this crate.");
                    crate.knockbackPlayer(player, crate.getLocation());
                    return;
                }

                if (Cooldowns.tryCooldown(player.getUniqueId(), "Crate-Cooldown", 1000)) {
                    crate.generateRewards(player, false);
                } else
                    MessageManager.sendMessage(player, "&cYou cannot use this for another " + (Cooldowns.getCooldown(player.getUniqueId(), "Crate-Cooldown") / 1000) + " seconds.");
            } else if (e.getAction() == Action.LEFT_CLICK_BLOCK)
                crate.showRewards(player);

        }
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        Player player = (Player) e.getWhoClicked();

        ItemStack itemStack = e.getCurrentItem();
        if (itemStack == null) {
            return;
        }

        if (e.getInventory().getTitle().contains("Crate Rewards")) {
            e.setCancelled(true);

            Crate crate = StaticClasses.crateManager.getCrate(player.getUniqueId());
            if (crate == null) {
                return;
            }

            if (crate.getAnimation() instanceof MemoryAnimation) {
                MemoryAnimation ma = (MemoryAnimation) crate.getAnimation();
                if (!ma.getMemoryDataMap().containsKey(player.getUniqueId())) {
                    return;
                }

                MemoryData memoryData = ma.getMemoryDataMap().get(player.getUniqueId());
                if (memoryData.getChosenRewards() >= memoryData.getAllowed()) {
                    MessageManager.sendMessage(player, "&cYou cannot select more items.");
                    return;
                }

                if (!itemStack.getType().equals(Material.STAINED_GLASS_PANE)) {
                    return;
                }

                memoryData.handleClick(e.getSlot(), ma);
            } else if (crate.getAnimation() instanceof SelectAnimation) {
                SelectAnimation sa = (SelectAnimation) crate.getAnimation();
                if (!sa.getSelectDataMap().containsKey(player.getUniqueId())) {
                    return;
                }

                SelectData selectData = sa.getSelectDataMap().get(player.getUniqueId());
                if (selectData.getChosenRewards() >= selectData.getAllowed()) {
                    MessageManager.sendMessage(player, "&cYou cannot select more items.");
                    return;
                }

                if (!itemStack.getType().equals(Material.STAINED_GLASS_PANE)) {
                    return;
                }

                selectData.handleClick(e.getSlot());
            }
        }
        //else if (e.getInventory().getTitle().contains("Edit Crate")) {
//            if (e.getCurrentItem() != null && e.getCurrentItem().getType() != Material.AIR) {
//                Crate crate = StaticClasses.crateManager.getCrate(player.getUniqueId());
//
//                if (crate == null) {
//                    e.setCancelled(true);
//                    return;
//                }
//
//                MessageManager.sendMessage(player, "&fEditing Crate");
//
//            }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent e) {
        Player player = (Player) e.getPlayer();

        for (Crate crate : StaticClasses.crateManager.getCrates()) {
            if (crate.getAnimation() instanceof LegacyAnimation) {
                LegacyAnimation la = (LegacyAnimation) crate.getAnimation();
                if (la.getCrateOpeners().keySet().contains(player.getUniqueId())) {
                    la.getCrateOpeners().remove(player.getUniqueId());
                    la.getCrateOpenersAmount().remove(player.getUniqueId());

                    Cooldowns.setCooldown(player.getUniqueId(), "Crate-Cooldown", 0);
                }
            } else if (crate.getAnimation() instanceof MemoryAnimation) {
                MemoryAnimation ma = (MemoryAnimation) crate.getAnimation();
                if (ma.getMemoryDataMap().containsKey(player.getUniqueId())) {
                    TaskManager.IMP.later(new Runnable() {
                        @Override
                        public void run() {
                            if (player.isOnline()) {
                                if (ma.getMemoryDataMap().containsKey(player.getUniqueId())) {
                                    MessageManager.sendMessage(player, "&6You must select &7" + (ma.getRewardsAmount() - ma.getMemoryDataMap().get(player.getUniqueId()).getChosenRewards()) + "&6 item(s)!");
                                    player.openInventory(ma.getMemoryDataMap().get(player.getUniqueId()).getInventory());
                                }
                            }
                        }
                    }, 10);
                }
            } else if (crate.getAnimation() instanceof SelectAnimation) {
                SelectAnimation sa = (SelectAnimation) crate.getAnimation();
                if (sa.getSelectDataMap().containsKey(player.getUniqueId())) {
                    SelectData selectData = sa.getSelectDataMap().get(player.getUniqueId());
                    TaskManager.IMP.later(new Runnable() {
                        @Override
                        public void run() {
                            if (player.isOnline()) {
                                if (sa.getSelectDataMap().containsKey(player.getUniqueId()) && (selectData.isAwait() || selectData.getChosenRewards() < selectData.getAllowed())) {
                                    MessageManager.sendMessage(player, "&6You must select &7" + (sa.getRewardsAmount() - sa.getSelectDataMap().get(player.getUniqueId()).getChosenRewards()) + "&6 item(s)!");
                                    player.openInventory(sa.getSelectDataMap().get(player.getUniqueId()).getInventory());
                                }
                            }
                        }
                    }, 10);
                }
            }
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();

        for (Crate crate : StaticClasses.crateManager.getCrates())
            if (crate.getAnimation() instanceof LegacyAnimation) {
                LegacyAnimation la = (LegacyAnimation) crate.getAnimation();
                if (la.getCrateOpeners().keySet().contains(player.getUniqueId()))
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            player.openInventory(la.getCrateOpeners().get(player.getUniqueId()));
                        }
                    }.runTaskLaterAsynchronously(Carbyne.getInstance(), 3L);
            } else if (crate.getAnimation() instanceof MemoryAnimation) {
                MemoryAnimation ma = (MemoryAnimation) crate.getAnimation();
                if (ma.getMemoryDataMap().containsKey(player.getUniqueId())) {
                    MemoryData memoryData = ma.getMemoryDataMap().get(player.getUniqueId());
                    TaskManager.IMP.later(new Runnable() {
                        @Override
                        public void run() {
                            player.openInventory(memoryData.getInventory());
                        }
                    }, 10);
                }
            } else if (crate.getAnimation() instanceof SelectAnimation) {
                SelectAnimation ds = (SelectAnimation) crate.getAnimation();
                if (ds.getSelectDataMap().containsKey(player.getUniqueId())) {
                    SelectData selectData = ds.getSelectDataMap().get(player.getUniqueId());
                    TaskManager.IMP.later(new Runnable() {
                        @Override
                        public void run() {
                            player.openInventory(selectData.getInventory());
                        }
                    }, 10);
                }
            }
    }
}
