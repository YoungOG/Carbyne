package com.medievallords.carbyne.utils;

import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.gear.types.carbyne.CarbyneArmor;
import com.medievallords.carbyne.gear.types.minecraft.MinecraftArmor;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.TownyUniverse;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
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

    public static double calculateDamageReduction(Player player, double originalDamage, EntityDamageEvent.DamageCause cause) {
        double armorReduction = 0.0;

        //Get DamageReduction values from all pieces of currently worn armor.
        for (ItemStack itemStack : player.getInventory().getArmorContents()) {
            if (itemStack.getType().equals(Material.AIR))
                continue;

            if (Carbyne.getInstance().getGearManager().isCarbyneArmor(itemStack)) {
                CarbyneArmor carbyneArmor = Carbyne.getInstance().getGearManager().getCarbyneArmor(itemStack);

                if (carbyneArmor != null) {
                    armorReduction = armorReduction + carbyneArmor.getArmorRating();
                }
            }

            if (Carbyne.getInstance().getGearManager().isDefaultArmor(itemStack)) {
                MinecraftArmor minecraftArmor = Carbyne.getInstance().getGearManager().getDefaultArmor(itemStack);

                if (minecraftArmor != null) {
                    armorReduction = armorReduction + minecraftArmor.getArmorRating();
                }
            }
        }

        if (armorReduction > 0) {
            double flatDamage = 0.0;

            //Calculation of certain DamageCauses for precise balancing.
            switch (cause) {
                case FIRE_TICK:
                    flatDamage = 0.5;
                    break;
                case LAVA:
                    flatDamage = 4.0;
                    break;
                case LIGHTNING:
                    flatDamage = 5.0;
                    break;
                case DROWNING:
                    flatDamage = 2.0;
                    break;
                case STARVATION:
                    flatDamage = 0.5;
                    break;
                case VOID:
                    flatDamage = 4.0;
                    break;
                case POISON:
                    flatDamage = 0.5;
                    break;
                case WITHER:
                    flatDamage = 0.5;
                    break;
                case SUFFOCATION:
                    flatDamage = 0.5;
                    break;
                case FALL:
                    flatDamage = originalDamage - originalDamage * (armorReduction - 0.40);
                    break;
            }

            return (flatDamage - (flatDamage * (armorReduction > 0.50 ? armorReduction - 0.50 : 0.0)) <= 0 ? (originalDamage - (originalDamage * (armorReduction + getProtectionReduction(player)))) : flatDamage);
        }

        return 0.0;
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

    public static double getProtectionReduction(Player player) {
        double damageReduction = 0.0;

        for (ItemStack is : player.getInventory().getArmorContents()) {
            if (is.getType().equals(Material.AIR))
                continue;

            switch (is.getEnchantmentLevel(Enchantment.PROTECTION_ENVIRONMENTAL)) {
                case 1:
                    if (is.getType().toString().contains("HELMET")) {
                        damageReduction += 0.015;
                    } else if (is.getType().toString().contains("CHESTPLATE")) {
                        damageReduction += 0.04;
                    } else if (is.getType().toString().contains("LEGGINGS")) {
                        damageReduction += 0.03;
                    } else if (is.getType().toString().contains("BOOTS")) {
                        damageReduction += 0.015;
                    }
                    break;
                case 2:
                    if (is.getType().toString().contains("HELMET")) {
                        damageReduction += 0.03;
                    } else if (is.getType().toString().contains("CHESTPLATE")) {
                        damageReduction += 0.08;
                    } else if (is.getType().toString().contains("LEGGINGS")) {
                        damageReduction += 0.06;
                    } else if (is.getType().toString().contains("BOOTS")) {
                        damageReduction += 0.03;
                    }
                    break;
            }
        }

        return damageReduction;
    }
}