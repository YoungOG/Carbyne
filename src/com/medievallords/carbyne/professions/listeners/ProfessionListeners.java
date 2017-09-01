package com.medievallords.carbyne.professions.listeners;

import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.profiles.Profile;
import com.medievallords.carbyne.utils.ItemBuilder;
import com.medievallords.carbyne.utils.MessageManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Williams on 2017-08-13
 * for the Carbyne project.
 */
public class ProfessionListeners implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getInventory().getTitle().equals("§b§lChoose your profession..")) {
            if (event.getCurrentItem() == null || !event.getCurrentItem().hasItemMeta() || !event.getCurrentItem().getItemMeta().hasDisplayName())
                return;

            event.setCancelled(true);

            if (!(event.getWhoClicked() instanceof Player))
                return;

            Player player = (Player) event.getWhoClicked();

            Profile profile = Carbyne.getInstance().getProfileManager().getProfile(player.getUniqueId());
            if (profile == null)
                return;

            switch (ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName())) {
                case "Farming":
                    profile.setProfession(Carbyne.getInstance().getProfessionManager().getProfession("Farming"));
                    MessageManager.sendMessage(player, "&aYou have choosen the &7Farming &aprofession");
                    break;
                case "Smelting":
                    profile.setProfession(Carbyne.getInstance().getProfessionManager().getProfession("Smelting"));
                    MessageManager.sendMessage(player, "&aYou have choosen the &7Smelting &aprofession");
                    break;
                case "Fishing":
                    profile.setProfession(Carbyne.getInstance().getProfessionManager().getProfession("Fishing"));
                    MessageManager.sendMessage(player, "&aYou have choosen the &7Fishing &aprofession");
                    break;
                case "Alchemy":
                    profile.setProfession(Carbyne.getInstance().getProfessionManager().getProfession("Alchemy"));
                    MessageManager.sendMessage(player, "&aYou have choosen the &7Alchemy &aprofession");
                    break;
                case "Crafting":
                    profile.setProfession(Carbyne.getInstance().getProfessionManager().getProfession("Crafting"));
                    MessageManager.sendMessage(player, "&aYou have choosen the &7Crafting &aprofession");
                    break;
                case "Repairing":
                    profile.setProfession(Carbyne.getInstance().getProfessionManager().getProfession("Repairing"));
                    MessageManager.sendMessage(player, "&aYou have choosen the &7Repairing &aprofession");
                    break;
                case "Taming":
                    profile.setProfession(Carbyne.getInstance().getProfessionManager().getProfession("Taming"));
                    MessageManager.sendMessage(player, "&aYou have choosen the &7Taming &aprofession");
                    break;
                case "Enchanting":
                    profile.setProfession(Carbyne.getInstance().getProfessionManager().getProfession("Enchanting"));
                    MessageManager.sendMessage(player, "&aYou have choosen the &7Enchanting &aprofession");
                    break;
                case "Hunting":
                    profile.setProfession(Carbyne.getInstance().getProfessionManager().getProfession("Hunting"));
                    MessageManager.sendMessage(player, "&aYou have choosen the &7Hunting &aprofession");
                    break;
                case "Prospecting":
                    profile.setProfession(Carbyne.getInstance().getProfessionManager().getProfession("Prospecting"));
                    MessageManager.sendMessage(player, "&aYou have choosen the &7Prospecting &aprofession");
                    break;
            }
        } else if (event.getInventory().getTitle().equals("§b§lProfessions Menu")) {
            event.setCancelled(true);
            if (!(event.getWhoClicked() instanceof Player))
                return;

            Player player = (Player) event.getWhoClicked();

            if (event.getCurrentItem() == null || !event.getCurrentItem().hasItemMeta() || !event.getCurrentItem().getItemMeta().hasDisplayName())
                return;


            if (ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName()).equalsIgnoreCase("View Professions")) {
                openViewProfessions(player);
            }
        } else if (event.getInventory().getTitle().equals("§b§lProfessions")) {
            event.setCancelled(true);
        }
    }

    private void openViewProfessions(Player player) {
        Inventory inventory = Bukkit.getServer().createInventory(null, 18, "§b§lProfessions");
        inventory.setContents(new ItemStack[]{
                new ItemBuilder(Material.IRON_SWORD).name("&eHunting").setLore(getHuntingDescription()).hideFlags().build(),
                new ItemBuilder(Material.BOOK).name("&4Enchanting").addEnchantment(Enchantment.DURABILITY, 10).setLore(getEnchantingDescription()).build(),
                new ItemBuilder(Material.WORKBENCH).name("&aCrafting").setLore(getCraftingDescription()).build(),
                new ItemBuilder(Material.GOLD_HOE).name("&dFarming").setLore(getFarmingDescription()).build(),
                new ItemBuilder(Material.FISHING_ROD).name("&9Fishing").setLore(getFishingDescription()).build(),
                new ItemBuilder(Material.ANVIL).name("&7Repairing").setLore(getRepairingDescription()).build(),
                new ItemBuilder(Material.FURNACE).name("&eSmelting").setLore(getSmeltingDescription()).build(),
                new ItemBuilder(Material.LEASH).name("&3Taming").setLore(getTamingDescription()).build(),
                new ItemBuilder(Material.BREWING_STAND_ITEM).name("&cAlchemy").setLore(getAlchemyDescription()).build(),
                null, null, null, null,
                new ItemBuilder(Material.GOLD_PICKAXE).name("&2Prospecting").setLore(getProspectingDescription()).build()
        });

        player.openInventory(inventory);
    }

    public List<String> getAlchemyDescription() {
        List<String> lore = new ArrayList<>();
        lore.add("&aThis profession allows you to");
        lore.add("&agather materials to brew potions");
        lore.add("&aand earn &6gold nuggets&a.");
        lore.add("&aYou get more gold the higher profession");
        lore.add("&alevel you are.");
        lore.add("");
        lore.add("&aYou can then deposit the &6gold nuggets");
        lore.add("&ainto your account using &7/deposit <amount>");
        lore.add("&aand/or use them for other purposes");
        lore.add("");
        lore.add("&cYou can't choose this profession");
        return lore;
    }

    public List<String> getCraftingDescription() {
        List<String> lore = new ArrayList<>();
        lore.add("&aThis profession allows you to");
        lore.add("&acraft materials and gear to earn");
        lore.add("&6gold nuggets&a. You get more gold the");
        lore.add("&ahigher profession level you are.");
        lore.add("");
        lore.add("&aYou can then deposit the &6gold nuggets");
        lore.add("&ainto your account using &7/deposit <amount>");
        lore.add("&aand/or use them for other purposes");
        lore.add("");
        lore.add("&cYou can't choose this profession");
        return lore;
    }

    public List<String> getFarmingDescription() {
        List<String> lore = new ArrayList<>();
        lore.add("&aThis profession allows you to");
        lore.add("&afarm and harvest crops to earn");
        lore.add("&6gold nuggets&a. You get more gold the");
        lore.add("&ahigher profession level you are.");
        lore.add("");
        lore.add("&aYou can then deposit the &6gold nuggets");
        lore.add("&ainto your account using &7/deposit <amount>");
        lore.add("&aand/or use them for other purposes");
        lore.add("");
        lore.add("&cYou can't choose this profession");
        return lore;
    }

    public List<String> getFishingDescription() {
        List<String> lore = new ArrayList<>();
        lore.add("&aThis profession allows you to");
        lore.add("&acatch fish and other discoveries to earn");
        lore.add("&6gold nuggets&a. You get more gold the");
        lore.add("&ahigher profession level you are.");
        lore.add("");
        lore.add("&aYou can then deposit the &6gold nuggets");
        lore.add("&ainto your account using &7/deposit <amount>");
        lore.add("&aand/or use them for other purposes");
        lore.add("");
        lore.add("&cYou can't choose this profession");
        return lore;
    }

    public List<String> getRepairingDescription() {
        List<String> lore = new ArrayList<>();
        lore.add("&aThis profession allows you to");
        lore.add("&arepair tools and gear to earn");
        lore.add("&6gold nuggets. &aYou get more gold the");
        lore.add("&ahigher profession level you are.");
        lore.add("");
        lore.add("&aYou can then deposit the &6gold nuggets");
        lore.add("&ainto your account using &7/deposit <amount>");
        lore.add("&aand/or use them for other purposes");
        lore.add("");
        lore.add("&cYou can't choose this profession");
        return lore;
    }

    public List<String> getSmeltingDescription() {
        List<String> lore = new ArrayList<>();
        lore.add("&aThis profession allows you to");
        lore.add("&asmelt ores and minerals to earn");
        lore.add("&6gold nuggets&a. You get more gold the");
        lore.add("&ahigher profession level you are.");
        lore.add("");
        lore.add("&aYou can then deposit the &6gold nuggets");
        lore.add("&ainto your account using &7/deposit <amount>");
        lore.add("&aand/or use them for other purposes");
        lore.add("");
        lore.add("&cYou can't choose this profession");
        return lore;
    }

    public List<String> getTamingDescription() {
        List<String> lore = new ArrayList<>();
        lore.add("&aThis profession allows you to");
        lore.add("&atame animals to earn");
        lore.add("&6gold nuggets&a. You get more gold the");
        lore.add("&ahigher profession level you are.");
        lore.add("");
        lore.add("&aYou can then deposit the &6gold nuggets");
        lore.add("&ainto your account using &7/deposit <amount>");
        lore.add("&aand/or use them for other purposes");
        lore.add("");
        lore.add("&cYou can't choose this profession");
        return lore;
    }

    public List<String> getEnchantingDescription() {
        List<String> lore = new ArrayList<>();
        lore.add("&aThis profession allows you to");
        lore.add("&aenchant armors, tools and weapons to earn");
        lore.add("&6gold nuggets&a. You get more gold the");
        lore.add("&ahigher profession level you are.");
        lore.add("");
        lore.add("&aYou can then deposit the &6gold nuggets");
        lore.add("&ainto your account using &7/deposit <amount>");
        lore.add("&aand/or use them for other purposes");
        lore.add("");
        lore.add("&cYou can't choose this profession");
        return lore;
    }

    public List<String> getProspectingDescription() {
        List<String> lore = new ArrayList<>();
        lore.add("&aThis profession allows you to");
        lore.add("&amine ores and minerals to earn");
        lore.add("&6gold nuggets&a. You get more gold the");
        lore.add("&ahigher profession level you are.");
        lore.add("");
        lore.add("&aYou can then deposit the &6gold nuggets");
        lore.add("&ainto your account using &7/deposit <amount>");
        lore.add("&aand/or use them for other purposes");
        lore.add("");
        lore.add("&cYou can't choose this profession");
        return lore;
    }

    public List<String> getHuntingDescription() {
        List<String> lore = new ArrayList<>();
        lore.add("&aThis profession allows you to");
        lore.add("&ahunt monsters and animals to earn");
        lore.add("&6gold nuggets&a. You get more gold the");
        lore.add("&ahigher profession level you are.");
        lore.add("");
        lore.add("&aYou can then deposit the &6gold nuggets");
        lore.add("&ainto your account using &7/deposit <amount>");
        lore.add("&aand/or use them for other purposes");
        lore.add("");
        lore.add("&cYou can't choose this profession");
        return lore;
    }
}
