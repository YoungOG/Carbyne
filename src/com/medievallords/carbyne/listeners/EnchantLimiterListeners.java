package com.medievallords.carbyne.listeners;

import org.bukkit.GameMode;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.PrepareItemEnchantEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * Created by Calvin on 6/4/2017
 * for the Carbyne project.
 */
public class EnchantLimiterListeners implements Listener {

    @EventHandler
    public void onItemEnchant(PrepareItemEnchantEvent event) {

    }

    public void deductExperience(Player player, int amount) {
        if (player.getGameMode() == GameMode.CREATIVE) {
            return;
        }

        if (player.getLevel() < amount) {
            player.sendMessage("&cYou do not have sufficient amount experience to enchant this.");
            return ;
        }

        player.setLevel(player.getLevel() - amount);
    }

    public int getRequiredExperienceLevels(ItemStack itemstack) {
        int amount = 0;
        ItemMeta itemMeta = itemstack.getItemMeta();

        if (!itemMeta.hasEnchants()) {
            return 0;
        }

        for (Enchantment enchantment : itemMeta.getEnchants().keySet()) {
            amount += itemMeta.getEnchantLevel(enchantment);
        }

        return amount;
    }
}
