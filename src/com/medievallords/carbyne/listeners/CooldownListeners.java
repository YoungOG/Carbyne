package com.medievallords.carbyne.listeners;

import com.bizarrealex.aether.scoreboard.Board;
import com.bizarrealex.aether.scoreboard.cooldown.BoardCooldown;
import com.bizarrealex.aether.scoreboard.cooldown.BoardFormat;
import com.medievallords.carbyne.utils.MessageManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;

public class CooldownListeners implements Listener {

    public CooldownListeners() {
//        new BukkitRunnable() {
//            public void run() {
//                for (Player p : PlayerUtility.getOnlinePlayers()) {
//                    Board board = Board.getByPlayer(p);
//
//                    if (board != null) {
//                        BoardCooldown enderpearlCooldown = board.getCooldown("enderpearl");
//
//                        if (enderpearlCooldown != null) {
//                            if (enderpearlCooldown.getDuration() > 0.0D) {
//                                p.playSound(p.getLocation(), Sound.NOTE_STICKS, 1.0F, 0.0F);
//
//                                for (ItemStack i : p.getInventory().getContents()) {
//                                    if (i != null) {
//                                        if (i.getType() == Material.ENDER_PEARL) {
//                                            ItemMeta im = i.getItemMeta();
//                                            im.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&cRemaining Time: " + enderpearlCooldown.getFormattedString(BoardFormat.SECONDS)));
//                                            i.setItemMeta(im);
//                                        }
//                                    }
//                                }
//                            } else {
//                                for (ItemStack i : p.getInventory().getContents()) {
//                                    if (i != null) {
//                                        if (i.getType() == Material.ENDER_PEARL) {
//                                            if (i.getItemMeta().getDisplayName() != null) {
//                                                if (i.getItemMeta().getDisplayName().contains("Time")) {
//                                                    ItemMeta im = i.getItemMeta();
//                                                    im.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&3Ender Pearl"));
//                                                    i.setItemMeta(im);
//                                                }
//                                            }
//                                        }
//                                    }
//                                }
//                            }
//
//                            BoardCooldown godappleCooldown = board.getCooldown("godapple");
//
//                            if (godappleCooldown != null) {
//                                if (godappleCooldown.getDuration() > 0.0D) {
//                                    for (ItemStack i : p.getInventory().getContents()) {
//                                        if (i != null) {
//                                            if (i.getType() == Material.GOLDEN_APPLE && i.getDurability() == 1) {
//                                                ItemMeta im = i.getItemMeta();
//                                                im.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&cRemaining Time: " + godappleCooldown.getFormattedString(BoardFormat.MINUTES)));
//                                                i.setItemMeta(im);
//                                            }
//                                        }
//                                    }
//                                } else {
//                                    for (ItemStack i : p.getInventory().getContents()) {
//                                        if (i != null) {
//                                            if (i.getType() == Material.GOLDEN_APPLE && i.getDurability() == 1) {
//                                                if (i.getItemMeta().getDisplayName() != null) {
//                                                    if (i.getItemMeta().getDisplayName().contains("Time")) {
//                                                        ItemMeta im = i.getItemMeta();
//                                                        im.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&3Golden Apple"));
//                                                        i.setItemMeta(im);
//                                                    }
//                                                }
//                                            }
//                                        }
//                                    }
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//        }.runTaskTimerAsynchronously(Carbyne.getInstance(), 0L, 5L);
    }

    @EventHandler
    public void onPearl(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        if (e.hasItem()) {
            if (e.getItem().getType() == Material.ENDER_PEARL) {
                if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
                    Board board = Board.getByPlayer(p);

                    if (board != null) {
                        BoardCooldown enderpearlCooldown = board.getCooldown("enderpearl");

                        if (enderpearlCooldown != null) {
                            e.setCancelled(true);
                            p.updateInventory();
                            MessageManager.sendMessage(p, "&eYou cannot throw another Enderpearl for &6" + enderpearlCooldown.getFormattedString(BoardFormat.SECONDS) + " &eseconds!");
                        } else {
                            new BoardCooldown(board, "enderpearl", 15.0D);

//                            for (ItemStack i : p.getInventory().getContents()) {
//                                if (i != null) {
//                                    if (i.getType() == Material.ENDER_PEARL) {
//                                        ItemMeta im = i.getItemMeta();
//                                        im.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&cRemaining Time: " + enderpearlCooldown.getFormattedString(BoardFormat.SECONDS)));
//                                        i.setItemMeta(im);
//                                    }
//                                }
//                            }
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
                Board board = Board.getByPlayer(p);

                if (board != null) {
                    BoardCooldown godappleCooldown = board.getCooldown("godapple");

                    if (godappleCooldown != null) {
                        e.setCancelled(true);
                        p.updateInventory();
                        MessageManager.sendMessage(p, "&eYou cannot eat another God Apple for &6" + godappleCooldown.getFormattedString(BoardFormat.MINUTES) + " &eseconds!");
                    } else {
                        new BoardCooldown(board, "godapple", 300.0D);

//                        for (ItemStack i : p.getInventory().getContents()) {
//                            if (i != null) {
//                                if (i.getType() == Material.GOLDEN_APPLE && i.getDurability() == 1) {
//                                    ItemMeta im = i.getItemMeta();
//                                    im.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&cRemaining Time: " + godappleCooldown.getFormattedString(BoardFormat.MINUTES)));
//                                    i.setItemMeta(im);
//                                }
//                            }
//                        }
                    }
                }
            }
        }
    }

//    @EventHandler
//    public void onEntityDeath(EntityDeathEvent e) {
//        for (ItemStack drops : e.getDrops()) {
//            if (drops.getType() == Material.ENDER_PEARL) {
//                if (!drops.hasItemMeta() || !drops.getItemMeta().hasDisplayName() || !drops.getItemMeta().getDisplayName().contains(ChatColor.DARK_AQUA + "Ender Pearl") || !drops.getItemMeta().getDisplayName().contains("Remaining Time")) {
//                    ItemMeta im = drops.getItemMeta();
//                    im.setDisplayName(ChatColor.DARK_AQUA + "Ender Pearl");
//                    drops.setItemMeta(im);
//                }
//            }
//            if (drops.getType() == Material.GOLDEN_APPLE) {
//                if (!drops.hasItemMeta() || !drops.getItemMeta().hasDisplayName() || !drops.getItemMeta().getDisplayName().contains(ChatColor.DARK_AQUA + "Golden Apple") || !drops.getItemMeta().getDisplayName().contains("Remaining Time")) {
//                    ItemMeta im = drops.getItemMeta();
//                    im.setDisplayName(ChatColor.DARK_AQUA + "Golden Apple");
//                    drops.setItemMeta(im);
//                }
//            }
//        }
//    }
//
//    @EventHandler
//    public void onDrop(PlayerDropItemEvent e) {
//        ItemStack item = e.getItemDrop().getItemStack();
//
//        if (item.getType() == Material.ENDER_PEARL) {
//            if (!item.hasItemMeta() || !item.getItemMeta().hasDisplayName() || !item.getItemMeta().getDisplayName().contains(ChatColor.DARK_AQUA + "Ender Pearl") || !item.getItemMeta().getDisplayName().contains("Remaining Time")) {
//                ItemMeta im = item.getItemMeta();
//                im.setDisplayName(ChatColor.DARK_AQUA + "Ender Pearl");
//                item.setItemMeta(im);
//            }
//        }
//        if (item.getType() == Material.GOLDEN_APPLE) {
//            if (!item.hasItemMeta() || !item.getItemMeta().hasDisplayName() || !item.getItemMeta().getDisplayName().contains(ChatColor.DARK_AQUA + "Golden Apple") || !item.getItemMeta().getDisplayName().contains("Remaining Time")) {
//                ItemMeta im = item.getItemMeta();
//                im.setDisplayName(ChatColor.DARK_AQUA + "Golden Apple");
//                item.setItemMeta(im);
//            }
//        }
//    }
}
