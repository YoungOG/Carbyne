package com.medievallords.carbyne.gear;

import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.gear.types.CarbyneGear;
import com.medievallords.carbyne.gear.types.carbyne.CarbyneArmor;
import com.medievallords.carbyne.gear.types.carbyne.CarbyneWeapon;
import com.medievallords.carbyne.utils.HiddenStringUtils;
import com.medievallords.carbyne.utils.ItemBuilder;
import com.medievallords.carbyne.utils.MessageManager;
import com.medievallords.carbyne.utils.PlayerUtility;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GearGuiManager {

    private Carbyne carbyne = Carbyne.getInstance();
    private GearManager gearManager;

    private final Inventory storeGui = Bukkit.createInventory(null, InventoryType.HOPPER, ChatColor.translateAlternateColorCodes('&', "&a&lCarbyne Forge"));
    private final Inventory weaponsGui = Bukkit.createInventory(null, 27, ChatColor.translateAlternateColorCodes('&', "&4&lWeapons Section"));
    private final Inventory armorGui = Bukkit.createInventory(null, 9, ChatColor.translateAlternateColorCodes('&', "&5&lArmor Section"));
    private final HashMap<String, Inventory> armorGuiList = new HashMap<>();

    public GearGuiManager(GearManager gearManager) {
        this.gearManager = gearManager;

        setupStoreGui();
        setupWeaponsGui();
        setupArmorGui();
        setupFillerGui();
    }

    public void setupStoreGui() {
        List<CarbyneArmor> carbyneArmorList = new ArrayList<>();
        for (CarbyneGear carbyneGear : gearManager.getCarbyneGear()) {
            if (carbyneGear instanceof CarbyneArmor) {
                CarbyneArmor carbyneArmor = (CarbyneArmor) carbyneGear;

                if (carbyneArmor.getItem(true).getType() == Material.LEATHER_CHESTPLATE) {
                    carbyneArmorList.add(carbyneArmor);
                }
            }
        }

        storeGui.setItem(1, new ItemBuilder(Material.DIAMOND_SWORD).amount(1).name("&4&lWeapons").addLore("&ePurchase Carbyne weapons.").build());
        storeGui.setItem(3, new ItemBuilder(Material.LEATHER_CHESTPLATE).amount(1).color(Color.fromRGB(51, 0, 0)).name("&5&lArmor").clearLore().addLore("&ePurchase Carbyne armor.").build());

        new BukkitRunnable() {
            int i = 0;

            @Override
            public void run() {
                if (i >= carbyneArmorList.size()) {
                    i = 0;
                }

                i++;
            }
        }.runTaskTimerAsynchronously(carbyne, 0L, 30L);
    }

    public void setupWeaponsGui() {
        for (CarbyneWeapon carbyneWeapon : gearManager.getCarbyneWeapon()) {
            weaponsGui.addItem(new ItemBuilder(carbyneWeapon.getItem(true)).addLore(" ").addLore((!carbyneWeapon.isHidden() ? "&eCost: " + carbyneWeapon.getCost() : "&cThis is not a purchasable weapon.")).build());
        }

        weaponsGui.setItem(26, new ItemBuilder(Material.BARRIER).name("&c&lGo Back").build());
    }

    public void setupArmorGui() {
        for (CarbyneArmor carbyneArmor : gearManager.getCarbyneArmor()) {
            double ar = 0.0;
            for (CarbyneArmor set : gearManager.getCarbyneArmorByColor(carbyneArmor.getColor())) {
                ar += set.getArmorRating();
            }

            List<String> loreCopy = new ArrayList<>();
            for (String s : carbyneArmor.getLore()) {
                loreCopy.add(ChatColor.translateAlternateColorCodes('&', s));
            }

            if (carbyneArmor.isHidden()) {
                loreCopy.add(0, "&cThis is not a purchasable set.");
                loreCopy.add(0, " ");
            }

            loreCopy.add(0, "&aDamage Reduction&7: &b" + (int) (ar * 100) + "%");
            loreCopy.add(0, HiddenStringUtils.encodeString(carbyneArmor.getGearCode()));

            armorGui.addItem(new ItemBuilder(carbyneArmor.getItem(true)).setLore(loreCopy).build());
        }

        armorGui.setItem(8, new ItemBuilder(Material.BARRIER).name("&c&lGo Back").build());
    }

    public void setupFillerGui() {
        if (gearManager.getCarbyneArmor().size() > 0) {
            for (CarbyneArmor carbyneArmor : gearManager.getCarbyneArmor()) {
                Inventory inventory = Bukkit.createInventory(null, 9, ChatColor.translateAlternateColorCodes('&', "&5&lPurchase Armor"));

                inventory.setItem(8, new ItemBuilder(Material.BARRIER).name("&c&lGo Back").build());

                for (CarbyneArmor set : gearManager.getCarbyneArmorByColor(carbyneArmor.getColor())) {
                    int slot = 0;

                    if (set.getItem(true).getType().equals(Material.LEATHER_HELMET)) {
                        slot = 0;
                    } else if (set.getItem(true).getType().equals(Material.LEATHER_CHESTPLATE)) {
                        slot = 1;
                    } else if (set.getItem(true).getType().equals(Material.LEATHER_LEGGINGS)) {
                        slot = 2;
                    } else if (set.getItem(true).getType().equals(Material.LEATHER_BOOTS)) {
                        slot = 3;
                    }

                    inventory.setItem(slot, new ItemBuilder(set.getItem(true)).addLore(" ").addLore((!set.isHidden() ? "&eCost: " + set.getCost() : "&cThis is not a purchasable armor set.")).build());
                }

                armorGuiList.put(carbyneArmor.getDisplayName(), inventory);
            }
        }
    }

    public void reloadStoreGuis() {
        for (Player player : PlayerUtility.getOnlinePlayers()) {
            if (player.getOpenInventory() != null) {
                if (isCustomInventory(player.getOpenInventory().getTopInventory())) {
                    player.closeInventory();
                    MessageManager.sendMessage(player, "&cThe Carbyne store has been reloaded.");
                }
            }
        }

        storeGui.clear();
        armorGui.clear();
        weaponsGui.clear();
        armorGuiList.clear();

        setupStoreGui();
        setupWeaponsGui();
        setupArmorGui();
        setupFillerGui();
    }

    public boolean isCustomInventory(Inventory inventory) {
        boolean custom = false;

        if (inventory.getTitle().equalsIgnoreCase(storeGui.getTitle()) || inventory.getTitle().equalsIgnoreCase(weaponsGui.getTitle()) || inventory.getTitle().equalsIgnoreCase(armorGui.getTitle())) {
            custom = true;
        }

        for (Inventory key : armorGuiList.values()) {
            if (key.getTitle().equalsIgnoreCase(inventory.getTitle())) {
                custom = true;
            }
        }

        return custom;
    }

    public Inventory getStoreGui() {
        return storeGui;
    }

    public Inventory getWeaponsGui() {
        return weaponsGui;
    }

    public Inventory getArmorGui() {
        return armorGui;
    }

    public HashMap<String, Inventory> getArmorGuiList() {
        return armorGuiList;
    }
}