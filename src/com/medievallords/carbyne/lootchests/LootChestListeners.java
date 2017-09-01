package com.medievallords.carbyne.lootchests;

import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.utils.MessageManager;
import io.lumine.xikage.mythicmobs.MythicMobs;
import io.lumine.xikage.mythicmobs.api.bukkit.events.MythicMobDeathEvent;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Created by Dalton on 6/5/2017.
 */
public class LootChestListeners implements Listener {

    private Carbyne main = Carbyne.getInstance();

    @EventHandler
    public void onInteractChest(PlayerInteractEvent event) {
        if ((event.getAction() == Action.LEFT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_BLOCK) && event.getPlayer().getWorld().getName().equalsIgnoreCase("world")) {

            Block block = event.getClickedBlock();
            if (block.getType() == Material.CHEST || block.getType() == Material.TRAPPED_CHEST) {

                LootChest lootChest = main.getLootChestManager().getByLocation(block.getLocation());

                if (lootChest != null) {
                    if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                        event.setCancelled(true);
                    }

                    if (!lootChest.allMobsDead()) {
                        MessageManager.sendMessage(event.getPlayer(), "&cYou need to kill the mobs first");
                        return;
                    }

                    lootChest.setHealth(lootChest.getHealth() - 1);

                    if (lootChest.getHealth() <= 0) {
                        lootChest.hideChest();
                        lootChest.getHologram().removeLine(0);
                        lootChest.getHologram().appendTextLine(ChatColor.LIGHT_PURPLE + "Looted");
                        lootChest.dropLoot();
                        return;
                    }

                    lootChest.getHologram().removeLine(0);
                    lootChest.getHologram().appendTextLine(ChatColor.translateAlternateColorCodes('&', "&a" + ((int) lootChest.getHealth() + " &7/ &a" + (int) lootChest.getMaxHealth())));
                }
            }
        }
    }

    @EventHandler
    public void onMobDeath(MythicMobDeathEvent event) {
        for (int i = 0; i < main.getLootChestManager().getLootChests().size(); i++) {
            LootChest lc = main.getLootChestManager().getLootChests().get(i);
            if (lc.getMobsAlive().contains(event.getMob())) {
                lc.getMobsAlive().remove(event.getMob());
            }
        }
    }

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event) {
        for (int i = 0; i < main.getLootChestManager().getLootChests().size(); i++) {
            LootChest lc = main.getLootChestManager().getLootChests().get(i);
            if (lc.getLocation().getChunk().equals(event.getChunk())) {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (!lc.getMobs().isEmpty()) {
                            lc.getMobsAlive().clear();
                            for (String mobName : lc.getMobs()) {
                                lc.getMobsAlive().add(MythicMobs.inst().getMobManager().spawnMob(mobName, lc.getLocation().clone().add(0.5, 1.05, 0.5)));
                            }
                        }
                    }
                }.runTaskLater(main, 45);
            }
        }
    }

    /*@EventHandler
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
    }*/

    /**
     * Stops chests that are loot chests from being broken.
     *
     * @param e
     */
    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        Location l = e.getBlock().getLocation();
        if (l.getWorld().getName().equalsIgnoreCase("world"))
            if (main.getLootChestManager().getByLocation(l) != null)
                e.setCancelled(true);
    }
}
