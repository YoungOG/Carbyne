package com.medievallords.carbyne.packages;

import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.utils.HiddenStringUtils;
import com.medievallords.carbyne.utils.ItemBuilder;
import com.medievallords.carbyne.utils.MessageManager;
import com.medievallords.carbyne.utils.Namer;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.github.paperspigot.Title;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

/**
 * Created by WE on 2017-08-03.
 */
@Getter
@Setter
public class Package {

    public static List<Package> packages = new ArrayList<>();

    private List<PackageItem> rewards = new ArrayList<>();
    private String name;
    private String packageCode;

    private String displayName;
    private List<String> lore = new ArrayList<>();
    private String material;
    private int data;

    private boolean randomItem;

    public Package(List<PackageItem> rewards, String name, String packageCode, String displayName, String material, int data, boolean randomItem, List<String> addLore) {
        this.rewards = rewards;
        this.name = name;
        this.packageCode = packageCode;
        this.displayName = displayName;
        this.material = material;
        this.data = data;
        this.randomItem = randomItem;

        lore.add(HiddenStringUtils.encodeString(packageCode));
        lore.add(ChatColor.YELLOW + "Use " + ChatColor.RED + "/pack open" + ChatColor.YELLOW + " to open this package");
        lore.add(ChatColor.YELLOW + "Use " + ChatColor.RED + "/pack preview" + ChatColor.YELLOW + " to preview this package");
        lore.add("");

        lore.addAll(addLore);
        rewards.sort(Comparator.comparingDouble(PackageItem::getChance));
    }

    public ItemStack getItem(int amount) {
        return new ItemBuilder(Material.getMaterial(material)).addEnchantment(Enchantment.DURABILITY, 10).setLore(lore).name(displayName).amount(amount).build();
    }

    public void previewPackage(Player player) {
        Inventory inv = Bukkit.createInventory(null, 54, ChatColor.translateAlternateColorCodes('&', displayName));

        for (PackageItem item : rewards) {
            ItemStack setItem = item.getItem();

            if (setItem.getType() == Material.ENCHANTED_BOOK) {
                EnchantmentStorageMeta enchantmentStorageMeta = (EnchantmentStorageMeta) setItem.getItemMeta();

                for (Enchantment enchantment : item.getEnchantments().keySet()) {
                    enchantmentStorageMeta.addStoredEnchant(enchantment, item.getEnchantments().get(enchantment), true);
                }

                setItem.setItemMeta(enchantmentStorageMeta);
            }

            setItem = new ItemBuilder(setItem).addLore("").addLore(item.getRarity().getColor() + item.getRarity().toString()).build();
            inv.setItem(item.getSlot(), setItem);
        }

        player.openInventory(inv);
    }

    public void openPackage(Player player) {
        double chance = Math.random();

        if (randomItem) {
            for (int i = 0; i < rewards.size(); i++) {
                int random = new Random().nextInt(rewards.size());
                PackageItem item = rewards.get(random);

                if (Math.random() >= item.getChance() && i != rewards.size() - 1) {
                } else {
                    ItemStack reward = item.getItem();
                    if (reward.getType() == Material.ENCHANTED_BOOK) {
                        EnchantmentStorageMeta enchantmentStorageMeta = (EnchantmentStorageMeta) reward.getItemMeta();
                        for (Enchantment enchantment : item.getEnchantments().keySet()) {
                            enchantmentStorageMeta.addStoredEnchant(enchantment, item.getEnchantments().get(enchantment), true);
                        }

                        reward.setItemMeta(enchantmentStorageMeta);
                    }

                    Inventory inv = Bukkit.getServer().createInventory(null, 27, "§b§lRewards");
                    if (reward.getType().getMaxStackSize() == 1) {
                        for (int am = 1; am <= reward.getAmount(); am++) {
                            inv.addItem(reward);
                        }
                    } else {
                        inv.addItem(reward);
                    }

                    if (!item.getCommands().isEmpty()) {
                        for (String cmd : item.getCommands()) {
                            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), cmd.replace("%player%", player.getName()));
                        }

                        inv = null;
                        player.sendTitle(new Title.Builder().title(ChatColor.translateAlternateColorCodes('&', "&6You found"))
                                .subtitle(ChatColor.translateAlternateColorCodes('&', (item.getItem().hasItemMeta() ?
                                        ((item.getItem().getItemMeta().hasDisplayName() ? item.getItem().getItemMeta().getDisplayName() :
                                                item.getDisplayName())) : item.getDisplayName()) + "!")).stay(60).build());
                    }

                    if (inv != null) {
                        player.openInventory(inv);
                        runFireworks(player.getLocation());
                    }

                    if (item.getRarity() == PackageItemRarity.LEGENDARY) {
                        MessageManager.broadcastMessage("&6&l" + player.getName() + " &d&lhas opened " + displayName + " &d&land got " + (item.getItem().hasItemMeta() ? ((item.getItem().getItemMeta().hasDisplayName() ? item.getItem().getItemMeta().getDisplayName() : item.getDisplayName())) : item.getDisplayName()) + "!");
                    }

                    break;
                }
            }
        } else {

            for (int i = 0; i < rewards.size(); i++) {
                PackageItem item = rewards.get(i);
                if (chance < item.getChance()) {
                    ItemStack reward = item.getItem();
                    if (reward.getType() == Material.ENCHANTED_BOOK) {
                        EnchantmentStorageMeta enchantmentStorageMeta = (EnchantmentStorageMeta) reward.getItemMeta();
                        for (Enchantment enchantment : item.getEnchantments().keySet()) {
                            enchantmentStorageMeta.addStoredEnchant(enchantment, item.getEnchantments().get(enchantment), true);
                        }

                        reward.setItemMeta(enchantmentStorageMeta);
                    }

                    Inventory inv = Bukkit.getServer().createInventory(null, 27, "§b§lRewards");
                    if (reward.getType().getMaxStackSize() == 1) {
                        for (int am = 0; am <= reward.getAmount(); am++) {
                            inv.addItem(reward);
                        }
                    } else {
                        inv.addItem(reward);
                    }

                    if (!item.getCommands().isEmpty()) {
                        for (String cmd : item.getCommands()) {
                            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), cmd.replace("%player%", player.getName()));
                        }

                        inv = null;
                        player.sendTitle(new Title.Builder().title(ChatColor.translateAlternateColorCodes('&', "&6You found"))
                                .subtitle(ChatColor.translateAlternateColorCodes('&', (item.getItem().hasItemMeta() ?
                                        ((item.getItem().getItemMeta().hasDisplayName() ? item.getItem().getItemMeta().getDisplayName() :
                                                item.getDisplayName())) : item.getDisplayName()) + "!")).stay(60).build());
                    }

                    if (inv != null) {
                        player.openInventory(inv);
                        runFireworks(player.getLocation());
                    }

                    if (item.getRarity() == PackageItemRarity.LEGENDARY) {
                        MessageManager.broadcastMessage("&6&l" + player.getName() + " &d&lhas opened " + displayName + " &d&land got " + (item.getItem().hasItemMeta() ? ((item.getItem().getItemMeta().hasDisplayName() ? item.getItem().getItemMeta().getDisplayName() : item.getDisplayName())) : item.getDisplayName()) + "!");
                    }

                    break;
                } else {
                    if (i == rewards.size() - 1) {
                        ItemStack reward = item.getItem();
                        if (reward.getType() == Material.ENCHANTED_BOOK) {
                            EnchantmentStorageMeta enchantmentStorageMeta = (EnchantmentStorageMeta) reward.getItemMeta();
                            for (Enchantment enchantment : item.getEnchantments().keySet()) {
                                enchantmentStorageMeta.addStoredEnchant(enchantment, item.getEnchantments().get(enchantment), true);
                            }

                            reward.setItemMeta(enchantmentStorageMeta);
                        }

                        Inventory inv = Bukkit.getServer().createInventory(null, 27, "§b§lRewards");
                        if (reward.getType().getMaxStackSize() == 1) {
                            for (int am = 0; am <= reward.getAmount(); am++) {
                                inv.addItem(reward);
                            }
                        } else {
                            inv.addItem(reward);
                        }

                        if (!item.getCommands().isEmpty()) {
                            for (String cmd : item.getCommands()) {
                                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), cmd.replace("%player%", player.getName()));
                            }

                            inv = null;
                            player.sendTitle(new Title.Builder().title(ChatColor.translateAlternateColorCodes('&', "&6You found"))
                                    .subtitle(ChatColor.translateAlternateColorCodes('&', (item.getItem().hasItemMeta() ?
                                            ((item.getItem().getItemMeta().hasDisplayName() ? item.getItem().getItemMeta().getDisplayName() :
                                                    item.getDisplayName())) : item.getDisplayName()) + "!")).stay(60).build());
                        }

                        if (inv != null) {
                            player.openInventory(inv);
                            runFireworks(player.getLocation());
                        }

                        if (item.getRarity() == PackageItemRarity.LEGENDARY) {
                            MessageManager.broadcastMessage("&6&l" + player.getName() + " &d&lhas opened " + displayName + " &d&land got " + (item.getItem().hasItemMeta() ? ((item.getItem().getItemMeta().hasDisplayName() ? item.getItem().getItemMeta().getDisplayName() : item.getDisplayName())) : item.getDisplayName()) + "!");
                        }

                        break;
                    }
                }
            }
        }
    }

    public static Package getPackage(String name) {
        for (Package p : packages) {
            if (p.getName().equalsIgnoreCase(name)) {
                return p;
            }
        }

        return null;
    }

    public static Package getPackage(ItemStack is) {
        if (is.getItemMeta() == null) {
            return null;
        }

        List<String> lore = Namer.getLore(is);

        if (lore == null || lore.isEmpty()) {
            return null;
        }

        for (Package p : packages) {
            if (p.getPackageCode().equalsIgnoreCase(HiddenStringUtils.extractHiddenString(lore.get(0)))) {
                return p;
            }
        }

        return null;
    }

    public void runFireworks(Location location) {
        Firework firework = (Firework) location.getWorld().spawnEntity(location, EntityType.FIREWORK);
        FireworkEffect effect = FireworkEffect.builder().with(getRandomType()).withColor(getRandomColor()).withColor(getRandomColor()).build();
        FireworkMeta meta = firework.getFireworkMeta();
        meta.clearEffects();
        meta.addEffect(effect);
        firework.setFireworkMeta(meta);

        new BukkitRunnable() {
            @Override
            public void run() {
                firework.detonate();
            }
        }.runTaskLater(Carbyne.getInstance(), 2);
    }

    public FireworkEffect.Type getRandomType() {
        int i = new Random().nextInt(Effect.Type.values().length);
        return FireworkEffect.Type.values()[i];
    }

    public Color getRandomColor() {
        Random random = new Random();
        int r = random.nextInt(256);
        int g = random.nextInt(256);
        int b = random.nextInt(256);
        return Color.fromRGB(r, g, b);
    }
}
