package com.medievallords.carbyne.lootchests;

import com.medievallords.carbyne.customevents.LootChestLootEvent;
import com.medievallords.carbyne.utils.MessageManager;
import com.medievallords.carbyne.utils.StaticClasses;
import com.medievallords.carbyne.zones.Zone;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;


public class LootChestListeners implements Listener {

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        if (e.getPlayer().getWorld().getName().equalsIgnoreCase("world"))
            if (e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
                if (e.getClickedBlock().getType().equals(Material.CHEST) || e.getClickedBlock().getType() == Material.TRAPPED_CHEST)
                    if (StaticClasses.lootChestManager.getLootChests().containsKey(e.getClickedBlock().getLocation())) {
                        LootChest lc = StaticClasses.lootChestManager.getLootChests().get(e.getClickedBlock().getLocation());
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

                        LootChestLootEvent lootEvent = new LootChestLootEvent(e.getPlayer(), (Chest) e.getClickedBlock().getState(), lc.getLootLoot());
                        Bukkit.getPluginManager().callEvent(lootEvent);
                        if (lootEvent.isCancelled()) {
                            return;
                        }

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
                    } else {
                        Block block = e.getClickedBlock();
                        Location location = block.getLocation();
                        if (StaticClasses.lootChestManager.isIgnored(location)) {
                            return;
                        }

                        if (StaticClasses.lootChestManager.isOnCooldown(location)) {
                            return;
                        }

                        Zone zone = StaticClasses.zoneManager.getZone(location);
                        if (zone == null) {
                            return;
                        }

                        zone.giveLoot(e.getPlayer(), (Chest) block.getState());
                        StaticClasses.lootChestManager.putOnCooldown(location, System.currentTimeMillis() + zone.getCooldownForChests());
                    }
            } else if (e.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
                if (e.getClickedBlock().getType() == Material.CHEST || e.getClickedBlock().getType() == Material.TRAPPED_CHEST) {
                    if (e.getPlayer().hasPermission("carbyne.commands.lootchest") || e.getPlayer().isOp()) {
                        if (!StaticClasses.lootChestManager.getLootChests().containsKey(e.getClickedBlock().getLocation()))
                            return;

                        LootChest lc = StaticClasses.lootChestManager.getLootChests().get(e.getClickedBlock().getLocation());

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
            if (StaticClasses.lootChestManager.getLootChests().containsKey(l))
                e.setCancelled(true);
    }
}
