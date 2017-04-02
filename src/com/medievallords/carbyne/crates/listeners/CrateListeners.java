package com.medievallords.carbyne.crates.listeners;

import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.crates.Crate;
import com.medievallords.carbyne.crates.CrateManager;
import com.medievallords.carbyne.crates.keys.Key;
import com.medievallords.carbyne.utils.Cooldowns;
import com.medievallords.carbyne.utils.MessageManager;
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

    private Carbyne main = Carbyne.getInstance();
    private CrateManager crateManager = main.getCrateManager();

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        Player player = e.getPlayer();

        if (e.hasItem() && e.getItem() != null && e.getItem().getType() != Material.AIR) {
            Key key = crateManager.getKey(e.getItem());

            if (key != null) {
                e.setCancelled(true);
            }
        }

        if (e.hasBlock() && e.getClickedBlock() != null && e.getClickedBlock().getType() != Material.AIR) {
            Block block = e.getClickedBlock();

            if (crateManager.getCrate(block.getLocation()) == null) {
                return;
            }

            e.setCancelled(true);

            if (crateManager.isOpeningCrate(player)) {
                MessageManager.sendMessage(player, "&cYou are already opening a crate.");
                return;
            }

            Crate crate = crateManager.getCrate(block.getLocation());

            if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
                if (player.getItemInHand() != null && player.getItemInHand().getType() != Material.AIR) {
                    ItemStack itemStack = player.getItemInHand();
                    Key key = crateManager.getKey(itemStack);

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
                    crate.generateRewards(player);
                } else {
                    MessageManager.sendMessage(player, "&cYou cannot use this for another " + (Cooldowns.getCooldown(player.getUniqueId(), "Crate-Cooldown") / 1000) + " seconds.");
                }
            } else if (e.getAction() == Action.LEFT_CLICK_BLOCK) {
                crate.showRewards(player);
            }
        }
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        Player player = (Player) e.getWhoClicked();

        if (e.getInventory().getTitle().contains("Crate Rewards")) {
            e.setCancelled(true);
        } else if (e.getInventory().getTitle().contains("Edit Crate")) {
            if (e.getCurrentItem() != null && e.getCurrentItem().getType() != Material.AIR) {
                Crate crate = crateManager.getCrate(player.getUniqueId());

                if (crate == null) {
                    e.setCancelled(true);
                    return;
                }

                MessageManager.sendMessage(player, "&fEditing Crate");

            }
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent e) {
        Player player = (Player) e.getPlayer();

        for (Crate crate : crateManager.getCrates()) {
            if (crate.getCrateOpeners().keySet().contains(player.getUniqueId())) {
                crate.getCrateOpeners().remove(player.getUniqueId());
                crate.getCrateOpenersAmount().remove(player.getUniqueId());

                Cooldowns.setCooldown(player.getUniqueId(), "Crate-Cooldown", 0);
            }
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();

        for (Crate crate : crateManager.getCrates()) {
            if (crate.getCrateOpeners().keySet().contains(player.getUniqueId())) {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        player.openInventory(crate.getCrateOpeners().get(player.getUniqueId()));
                    }
                }.runTaskLaterAsynchronously(main, 3L);
            }
        }
    }
}
