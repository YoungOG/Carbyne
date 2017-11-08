package com.medievallords.carbyne.spellmenu;

import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.utils.ItemBuilder;
import com.nisovin.magicspells.MagicSpells;
import com.nisovin.magicspells.Spellbook;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Calvin on 11/8/2017
 * for the Carbyne project.
 */
public class SpellMenuManager {

    private Carbyne main = Carbyne.getInstance();
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
    }

    public void openSpellOptions(Player player, String spellName) {
        Inventory inventory = Bukkit.createInventory(player, InventoryType.HOPPER, ChatColor.translateAlternateColorCodes('&', "&aSpell Options&5: " + spellName));

        inventory.setItem(0, new ItemBuilder(Material.INK_SACK).durability(10).name("&a&lBind")
                .addLore("&7Click to bind the &5" + spellName + " &7spell to your currently")
                .addLore("&7held item.")
                .build());

        inventory.setItem(2, new ItemBuilder(Material.INK_SACK).durability(8).name("&7&lUnbind")
                .addLore("&7Click to  unbind the &5" + spellName + " &7spell from your currently")
                .addLore("&7held item.")
                .build());

        inventory.setItem(4, new ItemBuilder(Material.REDSTONE).name("&c&lForget")
                .addLore("&7Click to forget the &5" + spellName + " &7spell.")
                .addLore("")
                .addLore("&cPlease note this action costs 5 diamonds.")
                .build());

        SpellMenuListeners.getUsers().add(player.getUniqueId());

        player.openInventory(inventory);
    }
}
