package com.medievallords.carbyne.lootchests;

import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.utils.MessageManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * Created by Dalton on 6/5/2017.
 */
public class LootChestListeners implements Listener {

    private Carbyne main = Carbyne.getInstance();

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        if (e.getPlayer().getWorld().getName().equalsIgnoreCase("world"))
            if (e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
                if (e.getClickedBlock().getType().equals(Material.CHEST))
                    if (main.getLootChestManager().getLootChests().containsKey(e.getClickedBlock().getLocation())) {
                        LootChest lc = main.getLootChestManager().getLootChests().get(e.getClickedBlock().getLocation());
                        List<ItemStack> loot = lc.getLoot();
                        Inventory playerInv = e.getPlayer().getInventory();

                        lc.hideChest();

                        if (loot.size() == 0) {
                            MessageManager.sendMessage(e.getPlayer(), "&cSorry! That chest was &4empty&c!");
                            return;
                        }

                        StringBuilder sb = new StringBuilder();
                        sb.append("&aYou have looted ");

                        int maxItems = lc.getMaxItems();
                        int itemsSpawned = 0;

                        for (int i = 0; i < loot.size(); i++) {
                            if (itemsSpawned >= maxItems && maxItems > 0) {
                                break;
                            }

                            if (playerInv.firstEmpty() == -1) {


                                sb.append("&a!");
                                World world = e.getPlayer().getWorld();

                                for (; i < loot.size(); i++) {
                                    if (itemsSpawned >= maxItems && maxItems > 0) {
                                        break;
                                    }

                                    world.dropItemNaturally(e.getClickedBlock().getLocation(), loot.get(i));
                                    itemsSpawned++;
                                }

                                break;
                            } else {
                                ItemStack is = loot.get(i);
                                playerInv.setItem(playerInv.firstEmpty(), is);

                                String itemName;

                                if (is.getItemMeta().getDisplayName() != null && !is.getItemMeta().getDisplayName().isEmpty())
                                    itemName = is.getItemMeta().getDisplayName();
                                else
                                    itemName = is.getType().name();

                                if (playerInv.firstEmpty() == -1 || i + 1 < loot.size())
                                    sb.append(itemName).append("&a!");
                                else
                                    sb.append(itemName).append("&a, ");

                                itemsSpawned++;
                            }
                        }

                        MessageManager.sendMessage(e.getPlayer(), sb.toString());
                    }
            } else if (e.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
                if (e.getClickedBlock().getType() == Material.CHEST || e.getClickedBlock().getType() == Material.TRAPPED_CHEST) {
                    if (e.getPlayer().hasPermission("carbyne.commands.lootchest") || e.getPlayer().isOp()) {
                        if (!main.getLootChestManager().getLootChests().containsKey(e.getClickedBlock().getLocation()))
                            return;

                        LootChest lc = main.getLootChestManager().getLootChests().get(e.getClickedBlock().getLocation());

                        if (lc != null) {
                            MessageManager.sendMessage(e.getPlayer(), "&aThis LootChest is named &b" + lc.getChestConfigName() + "&a.");
                        }
                    }
                }
            }
    }

    /**
     * Stops chests that are loot chests from being broken.
     *
     * @param e
     */
    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        Location l = e.getBlock().getLocation();
        if (l.getWorld().getName().equalsIgnoreCase("world"))
            if (main.getLootChestManager().getLootChests().containsKey(l))
                e.setCancelled(true);
    }
}
