package com.medievallords.carbyne.gear;


import com.medievallords.carbyne.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class GearGuiManager {

    //private final Inventory storeGui = Bukkit.createInventory(null, InventoryType.HOPPER, ChatColor.translateAlternateColorCodes('&', "&a&lCarbyne Forge"));
    //private final Inventory weaponsGui = Bukkit.createInventory(null, 27, ChatColor.translateAlternateColorCodes('&', "&4&lWeapons Section"));
    //private final Inventory armorGui = Bukkit.createInventory(null, 9, ChatColor.translateAlternateColorCodes('&', "&5&lArmor Section"));
    //private final HashMap<String, Inventory> armorGuiList = new HashMap<>();
    private GearManager gearManager;
    public static final String TITLE = "§5§lCarbyne Forge";

    public GearGuiManager(GearManager gearManager) {
        this.gearManager = gearManager;

//        setupStoreGui();
//        setupWeaponsGui();
//        setupArmorGui();
//        setupFillerGui();
    }


    public Inventory getForgeInventory() {
        Inventory inventory = Bukkit.createInventory(null, 45, TITLE); //Create the inventory itself with 45 slots.

        int size = inventory.getSize(); //Get the inventory size in slots.
        for (int i = 0; i < size; i++) { // Go through all slots to set a glass pane to make it look prettier.
            inventory.setItem(i, new ItemBuilder(Material.STAINED_GLASS_PANE).durability(15).name("&7").build());
        }

        inventory.setItem(19, null); // Clear the artifact-spot.
        inventory.setItem(12, null); // Clear the crafting-space so that you know where to put the ingredients.
        inventory.setItem(13, null);
        inventory.setItem(14, null);
        inventory.setItem(21, null);
        inventory.setItem(22, null);
        inventory.setItem(23, null);
        inventory.setItem(30, null);
        inventory.setItem(31, null);
        inventory.setItem(32, null);

        inventory.setItem(25, null); // Clear the recipe-result slot.


        // Set a few more panes for aesthetics.
        ItemStack pane = new ItemBuilder(Material.STAINED_GLASS_PANE).durability(14)
                .name("&4Place Artifact in the &7empty &4slot.")
                .addLore("Placeholder lore message")
                .build();

        inventory.setItem(1, pane);
        inventory.setItem(10, pane);
        inventory.setItem(28, pane);
        inventory.setItem(37, pane);

        inventory.setItem(3, pane);
        inventory.setItem(4, pane);
        inventory.setItem(5, pane);
        inventory.setItem(39, pane);
        inventory.setItem(40, pane);
        inventory.setItem(41, pane);

        inventory.setItem(7, pane);
        inventory.setItem(16, pane);
        inventory.setItem(34, pane);
        inventory.setItem(43, pane);

        return inventory; // Return the inventory that we created.
    }

//    public void openCarbyneForge(Player player) {
//        forges.add(new CarbyneForge(player));
//    }
//
//    public CarbyneForge getCarbyneForge(Player player) {
//        for (CarbyneForge forge : forges)
//            if (forge.getPlayer().equals(player))
//                return forge;
//
//            return null;
//    }
//
//    public CarbyneForge getCarbyneForge(UUID uuid) {
//        for (CarbyneForge forge : forges)
//            if (forge.getPlayer().getUniqueId().equals(uuid))
//                return forge;
//
//        return null;
//    }
//
//    @Getter
//    @Setter
//    public class CarbyneForge {
//
//        private Player player;
//        private Inventory inventory;
//        private HashMap<Integer, ItemStack> ingredients = new HashMap<>();
//        private CarybneForgeState state = CarybneForgeState.ONE;
//
//        public CarbyneForge(Player player) {
//            this.player = player;
//
//            open();
//        }
//
//        public void open() {
//            inventory = Bukkit.createInventory(player, 45, ChatColor.translateAlternateColorCodes('&', "&5&lCarbyne Forge"));
//
//            for (int i = 0; i < inventory.getSize(); i++)
//                inventory.setItem(i, new ItemBuilder(Material.STAINED_GLASS_PANE).data(15).name("").build());
//
//            inventory.setItem(19, null);
//            inventory.setItem(12, null);
//            inventory.setItem(13, null);
//            inventory.setItem(14, null);
//            inventory.setItem(21, null);
//            inventory.setItem(22, null);
//            inventory.setItem(23, null);
//            inventory.setItem(31, null);
//            inventory.setItem(32, null);
//            inventory.setItem(33, null);
//
//            inventory.setItem(25, null);
//        }
//
//        public void update() {
//            new BukkitRunnable() {
//                @Override
//                public void run() {
//                    if (interval > 0) interval = 0; else interval++;
//                }
//            }.runTaskTimerAsynchronously(Carbyne.getInstance(), 0L, 4 * 20);
//
//            new BukkitRunnable() {
//                boolean colorState = false;
//
//                @Override
//                public void run() {
//                    colorState = interval == 0;
//
//                    switch (state) {
//                        case ONE:
//                            //Alternate between red and black.
//                            inventory.setItem(1, new ItemBuilder(Material.STAINED_GLASS_PANE).data(colorState ? 14 : 15)
//                                    .name(colorState ? "&4Place Artifact in the &7empty &4slot." : "")
//                                    .addLore(colorState ? "Placeholder lore message" : "")
//                                    .build());
//                            inventory.setItem(10, new ItemBuilder(Material.STAINED_GLASS_PANE).data(colorState ? 14 : 15)
//                                    .name(colorState ? "&4Place Artifact in the &7empty &4slot." : "")
//                                    .addLore(colorState ? "Placeholder lore message" : "")
//                                    .build());
//
//                            inventory.setItem(28, new ItemBuilder(Material.STAINED_GLASS_PANE).data(colorState ? 14 : 15)
//                                    .name(colorState ? "&4Place Artifact in the &7empty &4slot." : "")
//                                    .addLore(colorState ? "Placeholder lore message" : "")
//                                    .build());
//                            inventory.setItem(37, new ItemBuilder(Material.STAINED_GLASS_PANE).data(colorState ? 14 : 15)
//                                    .name(colorState ? "&4Place Artifact in the &7empty &4slot." : "")
//                                    .addLore(colorState ? "Placeholder lore message" : "")
//                                    .build());
//
//                            inventory.setItem(3, new ItemBuilder(Material.STAINED_GLASS_PANE).data(14)
//                                    .name("&4Place Artifact in the &7empty &4slot.")
//                                    .addLore("Placeholder lore message")
//                                    .build());
//                            inventory.setItem(4, new ItemBuilder(Material.STAINED_GLASS_PANE).data(14)
//                                    .name("&4Place Artifact in the &7empty &4slot.")
//                                    .addLore("Placeholder lore message")
//                                    .build());
//                            inventory.setItem(5, new ItemBuilder(Material.STAINED_GLASS_PANE).data(14)
//                                    .name("&4Place Artifact in the &7empty &4slot.")
//                                    .addLore("Placeholder lore message")
//                                    .build());
//
//                            //17, 18, 37, 45
//                            //41, 42. 43
//                            break;
//                        case TWO:
//                            inventory.setItem(1, new ItemBuilder(Material.STAINED_GLASS_PANE).data(5)
//                                    .name("Placeholder name")
//                                    .addLore("Placeholder lore")
//                                    .build());
//                            inventory.setItem(10, new ItemBuilder(Material.STAINED_GLASS_PANE).data(5)
//                                    .name("Placeholder name")
//                                    .addLore("Placeholder lore")
//                                    .name("")
//                                    .build());
//
//                            inventory.setItem(28, new ItemBuilder(Material.STAINED_GLASS_PANE).data(5)
//                                    .name("Placeholder name")
//                                    .addLore("Placeholder lore")
//                                    .name("")
//                                    .build());
//                            inventory.setItem(37, new ItemBuilder(Material.STAINED_GLASS_PANE).data(5)
//                                    .name("Placeholder name")
//                                    .addLore("Placeholder lore")
//                                    .name("")
//                                    .build());
//                        case THREE:
//                            inventory.setItem(1, new ItemBuilder(Material.STAINED_GLASS_PANE).data(5)
//                                    .name("Placeholder name")
//                                    .addLore("Placeholder lore")
//                                    .build());
//                            inventory.setItem(10, new ItemBuilder(Material.STAINED_GLASS_PANE).data(5)
//                                    .name("Placeholder name")
//                                    .addLore("Placeholder lore")
//                                    .name("")
//                                    .build());
//
//                            inventory.setItem(28, new ItemBuilder(Material.STAINED_GLASS_PANE).data(5)
//                                    .name("Placeholder name")
//                                    .addLore("Placeholder lore")
//                                    .name("")
//                                    .build());
//                            inventory.setItem(37, new ItemBuilder(Material.STAINED_GLASS_PANE).data(5)
//                                    .name("Placeholder name")
//                                    .addLore("Placeholder lore")
//                                    .name("")
//                                    .build());
//                        case FOUR:
//                            inventory.setItem(1, new ItemBuilder(Material.STAINED_GLASS_PANE).data(5)
//                                    .name("Placeholder name")
//                                    .addLore("Placeholder lore")
//                                    .build());
//                            inventory.setItem(10, new ItemBuilder(Material.STAINED_GLASS_PANE).data(5)
//                                    .name("Placeholder name")
//                                    .addLore("Placeholder lore")
//                                    .name("")
//                                    .build());
//
//                            inventory.setItem(28, new ItemBuilder(Material.STAINED_GLASS_PANE).data(5)
//                                    .name("Placeholder name")
//                                    .addLore("Placeholder lore")
//                                    .name("")
//                                    .build());
//                            inventory.setItem(37, new ItemBuilder(Material.STAINED_GLASS_PANE).data(5)
//                                    .name("Placeholder name")
//                                    .addLore("Placeholder lore")
//                                    .name("")
//                                    .build());
//                        case FIVE:
//                            inventory.setItem(1, new ItemBuilder(Material.STAINED_GLASS_PANE).data(5)
//                                    .name("Placeholder name")
//                                    .addLore("Placeholder lore")
//                                    .build());
//                            inventory.setItem(10, new ItemBuilder(Material.STAINED_GLASS_PANE).data(5)
//                                    .name("Placeholder name")
//                                    .addLore("Placeholder lore")
//                                    .name("")
//                                    .build());
//
//                            inventory.setItem(28, new ItemBuilder(Material.STAINED_GLASS_PANE).data(5)
//                                    .name("Placeholder name")
//                                    .addLore("Placeholder lore")
//                                    .name("")
//                                    .build());
//                            inventory.setItem(37, new ItemBuilder(Material.STAINED_GLASS_PANE).data(5)
//                                    .name("Placeholder name")
//                                    .addLore("Placeholder lore")
//                                    .name("")
//                                    .build());
//                    }
//                }
//            }.runTaskTimerAsynchronously(Carbyne.getInstance(), 0L, 10L);
//        }
//
//        public void close() {
//
//        }
//    }
//
//    public enum CarybneForgeState {
//        ONE, TWO, THREE, FOUR, FIVE
//    }
//
//    public void setupStoreGui() {
//        List<CarbyneArmor> carbyneArmorList = new ArrayList<>();
//        for (CarbyneGear carbyneGear : gearManager.getCarbyneGear())
//            if (carbyneGear instanceof CarbyneArmor) {
//                CarbyneArmor carbyneArmor = (CarbyneArmor) carbyneGear;
//
//                if (carbyneArmor.getItem(true).getType() == Material.LEATHER_CHESTPLATE)
//                    carbyneArmorList.add(carbyneArmor);
//            }
//
//        storeGui.setItem(1, new ItemBuilder(Material.DIAMOND_SWORD).amount(1).name("&4&lWeapons").addLore("&ePurchase Carbyne weapons.").build());
//        storeGui.setItem(3, new ItemBuilder(Material.LEATHER_CHESTPLATE).amount(1).color(Color.fromRGB(51, 0, 0)).name("&5&lArmor").clearLore().addLore("&ePurchase Carbyne armor.").build());
//
////        new BukkitRunnable() {
////            int i = 0;
////
////            @Override
////            public void run() {
////                if (i >= carbyneArmorList.size())
////                    i = 0;
////
////                i++;
////            }
////        }.runTaskTimerAsynchronously(carbyne, 0L, 30L);
//    }
//
//    public void setupWeaponsGui() {
//        for (CarbyneWeapon carbyneWeapon : gearManager.getCarbyneWeapon())
//            if (carbyneWeapon.getState() == GearState.VISIBLE) {
//                if (carbyneWeapon.getLore().isEmpty())
//                    weaponsGui.addItem(new ItemBuilder(carbyneWeapon.getItem(true)).addLore(" ").addLore("&aCost&7: &b" + carbyneWeapon.getCost()).build());
//                else
//                    weaponsGui.addItem(new ItemBuilder(carbyneWeapon.getItem(true)).addLore(" ").addLore("&aCost&7: &b" + carbyneWeapon.getCost()).build());
//            }
//
//        weaponsGui.setItem(26, new ItemBuilder(Material.BARRIER).name("&c&lGo Back").build());
//    }
//
//    public void setupArmorGui() {
//        for (CarbyneArmor carbyneArmor : gearManager.getCarbyneArmor()) {
//            double health = 0.0;
//
//            for (CarbyneArmor set : gearManager.getCarbyneArmorByColor(carbyneArmor.getBaseColor()))
//                health += set.getHealth();
//
//            List<String> loreCopy = new ArrayList<>();
//
//            loreCopy.add(HiddenStringUtils.encodeString(carbyneArmor.getGearCode() + "," + carbyneArmor.getMaxDurability()));
//            loreCopy.add("&aHealth&7: &c" + health);
//
//            if (carbyneArmor.getLore().size() > 0) {
//                loreCopy.add(" ");
//
//                for (String s : carbyneArmor.getLore())
//                    loreCopy.add(ChatColor.translateAlternateColorCodes('&', s));
//            }
//
//            armorGui.addItem(new ItemBuilder(carbyneArmor.getItem(true)).setLore(loreCopy).build());
//        }
//
//        armorGui.setItem(8, new ItemBuilder(Material.BARRIER).name("&c&lGo Back").build());
//    }
//
//    public void setupFillerGui() {
//        if (gearManager.getCarbyneArmor().size() > 0) {
//            for (CarbyneArmor carbyneArmor : gearManager.getCarbyneArmor()) {
//                Inventory inventory = Bukkit.createInventory(null, 9, ChatColor.translateAlternateColorCodes('&', "&5&lPurchase Armor"));
//
//                inventory.setItem(8, new ItemBuilder(Material.BARRIER).name("&c&lGo Back").build());
//
//                for (CarbyneArmor set : gearManager.getCarbyneArmorByColor(carbyneArmor.getBaseColor())) {
//                    int slot = 0;
//
//                    if (set.getItem(true).getType().equals(Material.LEATHER_CHESTPLATE))
//                        slot = 1;
//                    else if (set.getItem(true).getType().equals(Material.LEATHER_LEGGINGS))
//                        slot = 2;
//                    else if (set.getItem(true).getType().equals(Material.LEATHER_BOOTS))
//                        slot = 3;
//
//                    inventory.setItem(slot, new ItemBuilder(set.getItem(true)).addLore(" ").addLore("&aCost&7: &b" + set.getCost()).build());
//                }
//
//                armorGuiList.put(carbyneArmor.getDisplayName(), inventory);
//            }
//        }
//    }
//
//    public void reloadStoreGuis() {
//        for (Player player : PlayerUtility.getOnlinePlayers())
//            if (player.getOpenInventory() != null)
//                if (isCustomInventory(player.getOpenInventory().getTopInventory())) {
//                    player.closeInventory();
//                    MessageManager.sendMessage(player, "&cThe Carbyne store has been reloaded.");
//                }
//
//        storeGui.clear();
//        armorGui.clear();
//        weaponsGui.clear();
//        armorGuiList.clear();
//
//        setupStoreGui();
//        setupWeaponsGui();
//        setupArmorGui();
//        setupFillerGui();
//    }
//
//    public boolean isCustomInventory(Inventory inventory) {
//        boolean custom = false;
//
//        if (inventory.getTitle().equalsIgnoreCase(storeGui.getTitle()) || inventory.getTitle().equalsIgnoreCase(weaponsGui.getTitle()) || inventory.getTitle().equalsIgnoreCase(armorGui.getTitle()))
//            custom = true;
//
//        for (Inventory key : armorGuiList.values())
//            if (key.getTitle().equalsIgnoreCase(inventory.getTitle()))
//                custom = true;
//
//        return custom;
//    }
//
//    public Inventory getStoreGui() {
//        return storeGui;
//    }
//
//    public Inventory getWeaponsGui() {
//        return weaponsGui;
//    }
//
//    public Inventory getArmorGui() {
//        return armorGui;
//    }
//
//    public HashMap<String, Inventory> getArmorGuiList() {
//        return armorGuiList;
//    }
}