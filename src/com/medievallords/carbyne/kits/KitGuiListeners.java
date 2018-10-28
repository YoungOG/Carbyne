package com.medievallords.carbyne.kits;

import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.utils.StaticClasses;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class KitGuiListeners implements Listener {

    private KitManager kitManager = StaticClasses.kitManager;

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        Inventory inventory = event.getInventory();
        Player player = (Player) event.getWhoClicked();
        ItemStack item = event.getCurrentItem();

        if (inventory.getTitle().contains("Kit Selection")) {
            event.setCancelled(true);

            if (item != null && item.getType() != Material.STAINED_GLASS_PANE) {
                if (item.hasItemMeta() && item.getItemMeta() != null) {
                    String itemName = ChatColor.stripColor(item.getItemMeta().getDisplayName());
                    Kit kit = kitManager.getKit(itemName);

                    if (event.isRightClick()) {
                        kitManager.openKitPreviewGui(player, kit);
                        player.playSound(player.getLocation(), Sound.BLOCK_CHEST_OPEN, 1.0F, 1.0F);
                        return;
                    }

                    kit.apply(player);
                }
            }
        }

        if (ChatColor.stripColor(inventory.getTitle()).startsWith("Preview Kit"))
            event.setCancelled(true);
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();

        if (ChatColor.stripColor(event.getInventory().getTitle()).startsWith("Preview Kit")) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    kitManager.openKitMenuGui(player);
                    player.playSound(player.getLocation(), Sound.BLOCK_CHEST_OPEN, 1.0F, 1.0F);
                }
            }.runTaskLater(Carbyne.getInstance(), 1L);
        }
    }
}
