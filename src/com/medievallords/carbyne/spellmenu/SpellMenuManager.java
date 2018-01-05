package com.medievallords.carbyne.spellmenu;

import com.medievallords.carbyne.utils.ItemBuilder;
import com.medievallords.carbyne.utils.MessageManager;
import com.medievallords.carbyne.utils.PlayerUtility;
import com.nisovin.magicspells.MagicSpells;
import com.nisovin.magicspells.Spell;
import com.nisovin.magicspells.Spellbook;
import com.nisovin.magicspells.util.CastItem;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Calvin on 11/8/2017
 * for the Carbyne project.
 */
public class SpellMenuManager {

    private ItemStack[] spellItems;

    public SpellMenuManager() {
        spellItems = new ItemStack[]{
                new ItemBuilder(Material.FEATHER)
                        .name("&5Blink")
                        .addLore("")
                        .addLore("")
                        .addLore("")
                        .addLore("&aRange&b: 50 Blocks")
                        .addLore("&aReagent Cost&c: 2 Redstone Dust")
                        .addLore("&aCooldown&d: 20 seconds")
                        .addLore("")
                        .addLore("&aDescription&7: ")
                        .addLore("&7  Allows you to teleport 50 blocks in your")
                        .addLore("&7direction.")
                        .build(),
                new ItemBuilder(Material.ENDER_PEARL)
                        .name("&5Leap")
                        .addLore("")
                        .addLore("")
                        .addLore("")
                        .addLore("&aReagent Cost&c: 2 Redstone Dust")
                        .addLore("&aCooldown&d: 20 seconds")
                        .addLore("")
                        .addLore("&aDescription&7: ")
                        .addLore("&7  Allows you to leap a short distance.").build(),
                new ItemBuilder(Material.IRON_FENCE)
                        .name("&5Entomb")
                        .addLore("")
                        .addLore("")
                        .addLore("")
                        .addLore("&aDuration&b: 5 seconds")
                        .addLore("&aRange&b: 30 Blocks")
                        .addLore("&aReagent Cost&c: 3 Redstone Dust")
                        .addLore("&aCooldown&d: 35 seconds")
                        .addLore("")
                        .addLore("&aDescription&7: ")
                        .addLore("&7  Traps the target entity in a cage.")
                        .build(),
                new ItemBuilder(Material.BONE)
                        .name("&5Cripple")
                        .addLore("")
                        .addLore("")
                        .addLore("")
                        .addLore("&aDuration&b: 5 seconds")
                        .addLore("&aRange&b: 30 Blocks")
                        .addLore("&aReagent Cost&c: 3 Redstone Dust")
                        .addLore("&aCooldown&d: 35 seconds")
                        .addLore("")
                        .addLore("&aDescription&7: ")
                        .addLore("&7  Slows the target entity's movement")
                        .addLore("&7substantially.")
                        .build(),
                new ItemBuilder(Material.SLIME_BALL)
                        .name("&5Poison")
                        .addLore("")
                        .addLore("")
                        .addLore("")
                        .addLore("&aEffect&b: Poison II")
                        .addLore("&aDuration&b: 10 seconds")
                        .addLore("&aRange&b: 20 Blocks")
                        .addLore("&aReagent Cost&c: 2 Redstone Dust")
                        .addLore("&aCooldown&d: 25 seconds")
                        .addLore("")
                        .addLore("&aDescription&7: ")
                        .addLore("&7  Applies a lethal poisonous dose")
                        .addLore("&7to the target.")
                        .build(),
                new ItemBuilder(Material.EYE_OF_ENDER)
                        .name("&5ShadowStep")
                        .addLore("")
                        .addLore("")
                        .addLore("")
                        .addLore("&aRange&b: 28 Blocks")
                        .addLore("&aReagent Cost&c: 2 Redstone Dust")
                        .addLore("&aCooldown&d: 20 seconds")
                        .addLore("")
                        .addLore("&aDescription&7: ")
                        .addLore("&7  Teleports you behind your target.")
                        .build(),
                new ItemBuilder(Material.LEASH)
                        .name("&5ForceToss")
                        .addLore("")
                        .addLore("")
                        .addLore("")
                        .addLore("&aRange&b: 15 Blocks")
                        .addLore("&aReagent Cost&c: 7 Redstone Dust")
                        .addLore("&aCooldown&d: 20 seconds")
                        .addLore("")
                        .addLore("&aDescription&7: ")
                        .addLore("&7  Pushes your target back with")
                        .addLore("&7a substantial amount of force.")
                        .build(),
                new ItemBuilder(Material.NETHER_STAR)
                        .name("&5Silence")
                        .addLore("")
                        .addLore("")
                        .addLore("")
                        .addLore("&aDuration&b: 10 seconds")
                        .addLore("&aRange&b: 25 Blocks")
                        .addLore("&aReagent Cost&c: 2 Redstone Dust")
                        .addLore("&aCooldown&d: 35 seconds")
                        .addLore("")
                        .addLore("&aDescription&7: ")
                        .addLore("&7  Prevents the target entity")
                        .addLore("&7from casting spells, and commands.")
                        .build(),
        };
    }

    public void openSpellsMenu(Player player) {
        Inventory inventory = Bukkit.createInventory(player, 9, ChatColor.translateAlternateColorCodes('&', "&5&lSpell Menu"));
        ItemStack[] items = spellItems.clone();

        Spellbook spellbook = MagicSpells.getSpellbook(player);

        for (ItemStack itemStack : items) {
            ItemBuilder builder = new ItemBuilder(itemStack);
            if (spellbook.hasSpell(MagicSpells.getSpellByInGameName(ChatColor.stripColor(builder.build().getItemMeta().getDisplayName().toLowerCase())))) {
                builder.setLore(0, "&b&lLearnt").setLore(1, "&8Click to manage this spell.").addGlow();
                inventory.addItem(builder.build());
            }
        }

        for (ItemStack itemStack : items) {
            ItemBuilder builder = new ItemBuilder(itemStack);
            if (!spellbook.hasSpell(MagicSpells.getSpellByInGameName(ChatColor.stripColor(builder.build().getItemMeta().getDisplayName().toLowerCase())))) {
                builder.setLore(0, "&cUnlearnt").setLore(1, "&8Click to learn this spell.").hideGlow();
                inventory.addItem(builder.build());
            }
        }

        player.openInventory(inventory);
        player.playSound(player.getLocation(), Sound.CHEST_OPEN, 1, .8f);
    }

    public void openSpellSubMenu(Player player, String spellName) {
        Inventory inventory = Bukkit.createInventory(player, InventoryType.HOPPER, ChatColor.translateAlternateColorCodes('&', "&aSpell Configuration&5: " + spellName));

        for (int i = 0; i < inventory.getSize(); i++)
            inventory.setItem(i, new ItemBuilder(Material.STAINED_GLASS_PANE).durability(15).name(" ").build());

        inventory.setItem(0, new ItemBuilder(Material.ENCHANTMENT_TABLE)
                .name("&a&lConfigure")
                .addLore("&7Click to bind or unbind the &5" + spellName + " &7spell to an item")
                .addLore("&7in your inventory.")
                .addLore("")
                .addLore("&c&lPlease note that you can only have a max of &4&l5 &c&lspells at any given time.")
                .build());

        inventory.setItem(4, new ItemBuilder(Material.REDSTONE)
                .name("&c&lForget")
                .addLore("&7Click to forget the &5" + spellName + " &7spell.")
                .addLore("")
                .addLore("&c&lPlease note this action costs 5 diamonds.")
                .build());

        player.closeInventory();

        SpellMenuListeners.getSpellSubMenuUsers().add(player.getUniqueId());

        player.openInventory(inventory);
        player.playSound(player.getLocation(), Sound.CHEST_OPEN, 1, .8f);
    }

    public void openSpellConfigurationMenu(Player player, String spellName) {
        Inventory inventory = Bukkit.createInventory(player, 36, ChatColor.translateAlternateColorCodes('&', "&aBind/Unbind: &5" + spellName));

        if (PlayerUtility.isInventoryEmpty(player.getInventory())) {
            MessageManager.sendMessage(player, "&cYou need to have items in your inventory to bind/unbind this spell.");
            openSpellSubMenu(player, spellName);
            return;
        }

        Spellbook spellbook = MagicSpells.getSpellbook(player);
        Spell spell = MagicSpells.getSpellByInGameName(spellName);

        if (spell == null) {
            MessageManager.sendMessage(player, "&cAn error has occurred.");
            openSpellConfigurationMenu(player, spellName);
            return;
        }

        ItemStack[] items = player.getInventory().getContents();
        for (int i = 0; i < 36; i++) {
            ItemStack item = items[i];

            if (item != null && item.getType() != Material.AIR) {
                CastItem castItem = new CastItem(item);
                ItemBuilder itemBuilder = new ItemBuilder(item.clone())
                        .addLore("");

                boolean removed = spellbook.removeCastItem(spell, castItem);
                if (!removed) {
                    itemBuilder
                            .addLore("")
                            .addLore("&bThe &5" + spell.getName() + " &bspell is not bound to this item.")
                            .addLore("")
                            .addLore("&7&lClick this item to &c&lunbind &7&lthe &5&l" + spell.getName() + " &7&lspell from this item.");

                    inventory.setItem(i, new ItemBuilder(itemBuilder.build()).hideGlow().build());
                } else {
                    spellbook.addCastItem(spell, castItem);
                    spellbook.save();

                    itemBuilder
                            .addLore("")
                            .addLore("&bThe &5" + spell.getName() + " &bspell is bound to this item.")
                            .addLore("")
                            .addLore("&7&lClick this item to &a&lbind &7&lthe &5&l" + spell.getName() + " &7&lspell from this item.");

                    inventory.setItem(i, new ItemBuilder(itemBuilder.build()).addGlow().build());
                }
            }
        }

        player.closeInventory();

        SpellMenuListeners.getSpellConfigurationMenuUsers().put(player.getUniqueId(), spellName);

        player.openInventory(inventory);
        player.playSound(player.getLocation(), Sound.CHEST_OPEN, 1, .8f);
    }
}
