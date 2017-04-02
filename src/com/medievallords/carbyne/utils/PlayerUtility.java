package com.medievallords.carbyne.utils;

import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.TownyUniverse;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collection;

public class PlayerUtility {

    public static Collection<? extends Player> getOnlinePlayers() {
        return Bukkit.getOnlinePlayers();
    }

    public static Resident getResident(Player player) {
        try {
            return TownyUniverse.getDataSource().getResident(player.getName());
        } catch (NotRegisteredException e) {
            return null;
        }
    }

    public static boolean hasClickedTop(InventoryClickEvent event) {
        return event.getRawSlot() == event.getSlot();
    }

    public static void checkForIllegalItems(Player player, Inventory inventory) {
        for (ItemStack item : inventory.getContents()) {
            if (item != null && item.getType() != Material.AIR) {
                if (item.hasItemMeta()) {
                    if (item.getItemMeta().hasEnchants()) {
                        boolean hasIllegalItem = false;

                        for (Enchantment enchantment : item.getEnchantments().keySet()) {
                            if (item.getEnchantments().get(enchantment) > 10) {
                                hasIllegalItem = true;
                            }
                        }

                        if (hasIllegalItem) {
                            inventory.remove(item);

                            JSONMessage message = JSONMessage.create(ChatColor.translateAlternateColorCodes('&', "&f[&cWARNNING&f]: &cAn illegal item has been confiscated from &b" + player.getName() + "&c.\n"))
                                    .tooltip(ChatColor.translateAlternateColorCodes('&', "&cClick to teleport to &b" + player.getName() + "&c."))
                                    .runCommand("/tp " + player.getName())
                                    .then(ChatColor.translateAlternateColorCodes('&', "  &cItem Type: &b" + item.getType().name()) + "\n")
                                    .then(ChatColor.translateAlternateColorCodes('&', "  &cAmount: &b" + item.getAmount()) + "\n")
                                    .then(ChatColor.translateAlternateColorCodes('&', "  &cEnchantments: &b&nShow Enchantments"));

                            StringBuilder stringBuilder = new StringBuilder();

                            for (Enchantment enchantment : item.getEnchantments().keySet()) {
                                stringBuilder.append("&c").append(enchantment.getName()).append(" &7(&b").append(enchantment.getId()).append("&7): &b").append(item.getEnchantmentLevel(enchantment)).append("\n");
                            }

                            message.tooltip(ChatColor.translateAlternateColorCodes('&', stringBuilder.toString()));

                            for (Player all : PlayerUtility.getOnlinePlayers()) {
                                if (all.hasPermission("carbyne.illegalweapon")) {
                                    message.send(all);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public static ArrayList<Player> getPlayersInRadius(Location radiusCenter, int radius) {
        ArrayList<Player> playerList = new ArrayList<>();
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (onlinePlayer.getWorld().equals(radiusCenter.getWorld())) {
                if (onlinePlayer.getLocation().distance(radiusCenter) < radius) {
                    playerList.add(onlinePlayer);
                }
            }
        }
        return playerList;
    }
}