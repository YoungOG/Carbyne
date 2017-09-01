package com.medievallords.carbyne.professions.commands;

import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.profiles.Profile;
import com.medievallords.carbyne.utils.ItemBuilder;
import com.medievallords.carbyne.utils.MessageManager;
import com.medievallords.carbyne.utils.command.BaseCommand;
import com.medievallords.carbyne.utils.command.Command;
import com.medievallords.carbyne.utils.command.CommandArgs;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Williams on 2017-08-12
 * for the Carbyne project.
 */
public class ProfessionChooseCommand extends BaseCommand {

    @Command(name = "profession", aliases = {"prof"}, inGameOnly = true)
    public void onCommand(CommandArgs commandArgs) {
        Player player = commandArgs.getPlayer();
        String[] args = commandArgs.getArgs();

        Profile profile = Carbyne.getInstance().getProfileManager().getProfile(player.getUniqueId());
        if (profile == null) {
            MessageManager.sendMessage(player, "&cAn error has occurred");
            return;
        }

        if (profile.getProfession() == null) {
            openProfessionChooser(player);
        } else {
            openProfessionStats(player, profile);
        }
    }

    public void openProfessionChooser(Player player) {
        Inventory inventory = Bukkit.getServer().createInventory(null, 18, "§b§lChoose your profession..");
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

    public void openProfessionStats(Player player, Profile profile) {
        Inventory inventory = Bukkit.getServer().createInventory(null, InventoryType.HOPPER, "§b§lProfessions Menu");
        List<String> statsLore = new ArrayList<>();
        statsLore.add("&aLevel&7: &b" + profile.getProfessionLevel());
        statsLore.add("&aProgression&7: &b" + profile.getProfessionProgress() + "&7/&b" + profile.getRequiredProfessionProgress());
        String professionName = profile.getProfession().getName();
        ItemStack stats = new ItemBuilder(Material.GOLD_INGOT).name("&d" + professionName).setLore(statsLore).build();
        inventory.setItem(1, stats);

        ItemStack professions = new ItemBuilder(Material.PAPER).name("&aView Professions").addLore("&eClick to view the professions.").build();
        inventory.setItem(3, professions);

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
        return lore;
    }
}
