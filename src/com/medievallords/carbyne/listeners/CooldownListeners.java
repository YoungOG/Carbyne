package com.medievallords.carbyne.listeners;

import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.utils.Cooldowns;
import com.medievallords.carbyne.utils.MessageManager;
import com.medievallords.carbyne.utils.PlayerUtility;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

public class CooldownListeners implements Listener {

    public CooldownListeners() {
        new BukkitRunnable() {
            public void run() {
                for (Player p : PlayerUtility.getOnlinePlayers()) {
                    if (Cooldowns.getCooldown(p.getUniqueId(), "EnderPearlCooldown") > 0L) {
                        p.playSound(p.getLocation(), Sound.NOTE_STICKS, 1.0F, 0.0F);
                        for (ItemStack i : p.getInventory().getContents()) {
                            if (i != null) {
                                if (i.getType() == Material.ENDER_PEARL) {
                                    ItemMeta im = i.getItemMeta();
                                    im.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&cRemaining Time: " + Cooldowns.getCooldown(p.getUniqueId(), "EnderPearlCooldown") / 1000L));
                                    i.setItemMeta(im);
                                }
                            }
                        }
                    } else {
                        for (ItemStack i : p.getInventory().getContents()) {
                            if (i != null) {
                                if (i.getType() == Material.ENDER_PEARL) {
                                    if (i.getItemMeta().getDisplayName() != null) {
                                        if (i.getItemMeta().getDisplayName().contains("Time")) {
                                            ItemMeta im = i.getItemMeta();
                                            im.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&3Ender Pearl"));
                                            i.setItemMeta(im);
                                        }
                                    }
                                }
                                if (i.getType() == Material.GOLDEN_APPLE && i.getDurability() == 1) {
                                    if (i.getItemMeta().getDisplayName() != null) {
                                        if (i.getItemMeta().getDisplayName().contains("Time")) {
                                            ItemMeta im = i.getItemMeta();
                                            im.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&3Golden Apple"));
                                            i.setItemMeta(im);
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if (Cooldowns.getCooldown(p.getUniqueId(), "GodAppleCooldown") > 0L) {
                        for (ItemStack i : p.getInventory().getContents()) {
                            if (i != null) {
                                if (i.getType() == Material.GOLDEN_APPLE && i.getDurability() == 1) {
                                    ItemMeta im = i.getItemMeta();
                                    im.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&cRemaining Time: " + Cooldowns.getCooldown(p.getUniqueId(), "GodAppleCooldown") / 1000L));
                                    i.setItemMeta(im);
                                }
                            }
                        }
                    } else {
                        for (ItemStack i : p.getInventory().getContents()) {
                            if (i != null) {
                                if (i.getType() == Material.GOLDEN_APPLE && i.getDurability() == 1) {
                                    if (i.getItemMeta().getDisplayName() != null) {
                                        if (i.getItemMeta().getDisplayName().contains("Time")) {
                                            ItemMeta im = i.getItemMeta();
                                            im.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&3Golden Apple"));
                                            i.setItemMeta(im);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }.runTaskTimerAsynchronously(Carbyne.getInstance(), 0L, 20L);
    }

    @EventHandler
    public void onPearl(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        if (e.hasItem()) {
            if (e.getItem().getType() == Material.ENDER_PEARL) {
                if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
                    if (!Cooldowns.tryCooldown(p.getUniqueId(), "EnderPearlCooldown", 15000L)) {
                        e.setCancelled(true);
                        p.updateInventory();
                        MessageManager.sendMessage(p, "&eYou cannot throw another Enderpearl for &6" + Cooldowns.getCooldown(p.getUniqueId(), "EnderPearlCooldown") / 1000L + " &eseconds!");
                    } else {
                        for (ItemStack i : p.getInventory().getContents()) {
                            if (i != null) {
                                if (i.getType() == Material.ENDER_PEARL) {
                                    ItemMeta im = i.getItemMeta();
                                    im.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&cRemaining Time: 15"));
                                    i.setItemMeta(im);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onEat(PlayerItemConsumeEvent e) {
        Player p = e.getPlayer();
        if (e.getItem() != null) {
            if (e.getItem().getType() == Material.GOLDEN_APPLE && e.getItem().getDurability() == 1) {
                if (!Cooldowns.tryCooldown(p.getUniqueId(), "GodAppleCooldown", 300000L)) {
                    e.setCancelled(true);
                    p.updateInventory();
                    MessageManager.sendMessage(p, "&eYou cannot eat another God Apple for &6" + Cooldowns.getCooldown(p.getUniqueId(), "GodAppleCooldown") / 1000L + " &eseconds!");
                } else {
                    for (ItemStack i : p.getInventory().getContents()) {
                        if (i != null) {
                            if (i.getType() == Material.GOLDEN_APPLE && i.getDurability() == 1) {
                                ItemMeta im = i.getItemMeta();
                                im.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&cRemaining Time: 300"));
                                i.setItemMeta(im);
                            }
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent e) {
        for (ItemStack drops : e.getDrops()) {
            if (drops.getType() == Material.ENDER_PEARL) {
                if (!drops.hasItemMeta() || !drops.getItemMeta().hasDisplayName() || !drops.getItemMeta().getDisplayName().contains(ChatColor.DARK_AQUA + "Ender Pearl") || !drops.getItemMeta().getDisplayName().contains("Remaining Time")) {
                    ItemMeta im = drops.getItemMeta();
                    im.setDisplayName(ChatColor.DARK_AQUA + "Ender Pearl");
                    drops.setItemMeta(im);
                }
            }
            if (drops.getType() == Material.GOLDEN_APPLE) {
                if (!drops.hasItemMeta() || !drops.getItemMeta().hasDisplayName() || !drops.getItemMeta().getDisplayName().contains(ChatColor.DARK_AQUA + "Golden Apple") || !drops.getItemMeta().getDisplayName().contains("Remaining Time")) {
                    ItemMeta im = drops.getItemMeta();
                    im.setDisplayName(ChatColor.DARK_AQUA + "Golden Apple");
                    drops.setItemMeta(im);
                }
            }
        }
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent e) {
        ItemStack item = e.getItemDrop().getItemStack();

        if (item.getType() == Material.ENDER_PEARL) {
            if (!item.hasItemMeta() || !item.getItemMeta().hasDisplayName() || !item.getItemMeta().getDisplayName().contains(ChatColor.DARK_AQUA + "Ender Pearl") || !item.getItemMeta().getDisplayName().contains("Remaining Time")) {
                ItemMeta im = item.getItemMeta();
                im.setDisplayName(ChatColor.DARK_AQUA + "Ender Pearl");
                item.setItemMeta(im);
            }
        }
        if (item.getType() == Material.GOLDEN_APPLE) {
            if (!item.hasItemMeta() || !item.getItemMeta().hasDisplayName() || !item.getItemMeta().getDisplayName().contains(ChatColor.DARK_AQUA + "Golden Apple") || !item.getItemMeta().getDisplayName().contains("Remaining Time")) {
                ItemMeta im = item.getItemMeta();
                im.setDisplayName(ChatColor.DARK_AQUA + "Golden Apple");
                item.setItemMeta(im);
            }
        }
    }
}
