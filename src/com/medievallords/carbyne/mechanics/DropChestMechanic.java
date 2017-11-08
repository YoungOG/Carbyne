package com.medievallords.carbyne.mechanics;

import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.utils.ItemBuilder;
import io.lumine.xikage.mythicmobs.adapters.AbstractEntity;
import io.lumine.xikage.mythicmobs.io.MythicLineConfig;
import io.lumine.xikage.mythicmobs.skills.ITargetedEntitySkill;
import io.lumine.xikage.mythicmobs.skills.SkillMechanic;
import io.lumine.xikage.mythicmobs.skills.SkillMetadata;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;


/**
 * Created by Williams on 2017-09-09
 * for the Carbyne project.
 */
public class DropChestMechanic extends SkillMechanic implements ITargetedEntitySkill {

    private ItemStack[] items = new ItemStack[]{
            new ItemBuilder(Material.DIAMOND_BOOTS).addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2).build(),
            new ItemBuilder(Material.DIAMOND_CHESTPLATE).addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2).build(),
            new ItemBuilder(Material.DIAMOND_LEGGINGS).addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2).build(),
            new ItemBuilder(Material.DIAMOND_HELMET).addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2).build(),
            new ItemBuilder(Material.DIAMOND_SWORD).addEnchantment(Enchantment.DAMAGE_ALL, 3).build(),
            new ItemBuilder(Material.STICK).addEnchantment(Enchantment.DURABILITY, 10).name("&5Magic Wand")
                    .addLore("&eDiscover the power of magic.").addLore("&eGo to the &4Magic Library &eat").addLore("&espawn to learn about spells.").build(),
            new ItemBuilder(Material.NETHER_STAR).amount(5).build(),
            new ItemBuilder(Material.REDSTONE).amount(32).build(),
            new ItemBuilder(Material.DIAMOND).amount(7).build(),
            new ItemBuilder(Material.GOLD_NUGGET).amount(100).build()

    };

    private Location location = new Location(Bukkit.getWorld("world"), 0, 0, 0);

    public DropChestMechanic(String skill, MythicLineConfig mlc, int interval) {
        super(skill, mlc, interval);

        location = new Location(Bukkit.getWorld("world"), mlc.getDouble("x"), mlc.getDouble("y"), mlc.getDouble("z"));
    }

    @Override
    public boolean castAtEntity(SkillMetadata skillMetadata, AbstractEntity abstractEntity) {
        Entity entity = abstractEntity.getBukkitEntity();
        if (!(entity instanceof Player)) {
            return false;
        }

        Player player = (Player) entity;
        player.getInventory().addItem(items);
        new BukkitRunnable() {
            @Override
            public void run() {
                player.teleport(location);
            }
        }.runTaskLater(Carbyne.getInstance(), 60);

        return false;
    }
}
