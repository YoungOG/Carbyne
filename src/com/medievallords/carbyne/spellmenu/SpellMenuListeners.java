package com.medievallords.carbyne.spellmenu;

import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.utils.ItemBuilder;
import com.medievallords.carbyne.utils.MessageManager;
import com.medievallords.carbyne.utils.PlayerUtility;
import com.nisovin.magicspells.MagicSpells;
import com.nisovin.magicspells.Spell;
import com.nisovin.magicspells.Spellbook;
import com.nisovin.magicspells.util.CastItem;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class SpellMenuListeners implements Listener {

    @Getter
    private static Set<UUID> spellSubMenuUsers = new HashSet<>();
    @Getter
    private static HashMap<UUID, String> spellConfigurationMenuUsers = new HashMap<>();
    private Carbyne main = Carbyne.getInstance();
    private Spell forget;

    public SpellMenuListeners() {
        forget = MagicSpells.getSpellByInternalName("forget");
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        Inventory inventory = event.getInventory();
        Player player = (Player) event.getWhoClicked();
        ItemStack item = event.getCurrentItem();

        if (inventory.getTitle().contains("Spell Menu")) {
            event.setCancelled(true);

            if (item != null) {
                if (item.hasItemMeta() && item.getItemMeta() != null) {
                    String spellName = ChatColor.stripColor(item.getItemMeta().getDisplayName());
                    Spellbook spellbook = MagicSpells.getSpellbook(player);
                    Spell spell = MagicSpells.getSpellByInGameName(spellName);

                    if (spellbook.hasSpell(spell)) {
                        main.getSpellMenuManager().openSpellSubMenu(player, spellName);
                        player.playSound(player.getLocation(), Sound.CHEST_OPEN, 1.0F, 1.0F);
                    } else {
                        if (!spellbook.canLearn(spell)) {
                            player.closeInventory();
                            player.playSound(player.getLocation(), Sound.ITEM_PICKUP, 1.0F, 1.0F);
                            MessageManager.sendMessage(player, "&cYou cannot learn this spell.");
                            return;
                        } else if (spellbook.hasSpell(spell)) {
                            player.closeInventory();
                            player.playSound(player.getLocation(), Sound.ITEM_PICKUP, 1.0F, 1.0F);
                            MessageManager.sendMessage(player, "&cYou have already learnt this spell.");
                            return;
                        } else {
                            spellbook.addSpell(spell);
                            spellbook.save();

                            inventory.setItem(event.getSlot(), new ItemBuilder(item).setLore(0, "&b&lLearnt").setLore(1, "&8Click to manage this spell.").addGlow().build());
                            player.playSound(player.getLocation(), Sound.NOTE_PLING, 1.0F, 1.0F);
                        }
                    }
                }
            }
        }

        if (inventory.getTitle().contains("Spell Configuration")) {
            event.setCancelled(true);

            String spellName = inventory.getTitle().split(": ")[1];

            if (item != null) {
                switch (item.getType()) {
                    case ENCHANTMENT_TABLE:
                        spellSubMenuUsers.remove(player.getUniqueId());
                        main.getSpellMenuManager().openSpellConfigurationMenu(player, spellName);
                        break;
                    case REDSTONE:
                        if (player.getInventory().containsAtLeast(new ItemStack(Material.DIAMOND), 5)) {
                            forget.castSpell(player, Spell.SpellCastState.NORMAL, 1, new String[]{spellName + ""});
                            PlayerUtility.removeItems(player.getInventory(), Material.DIAMOND, 0, 5);
                            player.playSound(player.getLocation(), Sound.CLICK, 1.0F, 1.0F);
                        } else {
                            spellSubMenuUsers.remove(player.getUniqueId());
                            main.getSpellMenuManager().openSpellSubMenu(player, spellName);
                            player.playSound(player.getLocation(), Sound.ITEM_PICKUP, 1.0F, 1.0F);
                            MessageManager.sendMessage(player, "&cYou need to have at least 5 diamonds in your inventory to forget this spell.");
                            return;
                        }
                        break;
                }
            }
        }

        int slot = event.getSlot();

        if (inventory.getTitle().contains("Bind/Unbind")) {
            event.setCancelled(true);

            Spellbook spellbook = MagicSpells.getSpellbook(player);
            Spell spell = MagicSpells.getSpellByInGameName(ChatColor.stripColor(inventory.getTitle().split(": ")[1]));

            if (item != null && item.getType() != Material.AIR) {
                ItemBuilder itemBuilder = new ItemBuilder(item.clone());
                itemBuilder.removeLore(4);

                if (spellbook == null)
                    MessageManager.sendMessage(player, "&cYour spellbook could not be accessed.");
                else if (!spellbook.hasSpell(spell))
                    MessageManager.sendMessage(player, "&cYou do not know this spell.");
                else if (!spell.canCastWithItem())
                    MessageManager.sendMessage(player, "&cThis spell can not be bound.");
                else {
                    CastItem castItem = new CastItem(player.getInventory().getContents()[event.getSlot()]);

                    if (!spell.canBind(castItem)) {
                        MessageManager.sendMessage(player, "&cThis spell cannot be bound to that item");
                        return;
                    }

                    boolean removed = spellbook.removeCastItem(spell, castItem);

                    if (!removed) {
                        spellbook.addCastItem(spell, castItem);
                        spellbook.save();

                        itemBuilder
                                .addLore("")
                                .addLore("&bThe &5" + spell.getName() + " &bspell is bound to this item.")
                                .addLore("")
                                .addLore("&7&lClick this item to &c&lunbind &7&lthe &5&l" + spell.getName() + " &7&lspell from this item.");

                        inventory.setItem(slot, new ItemBuilder(itemBuilder.build()).addGlow().build());
                        ItemStack[] items = inventory.getContents();
                        for (int i = 0; i < 36; i++) {
                            ItemStack otherItem = items[i];
                            if (otherItem != null & slot != i) {
                                if (item.getDurability() == otherItem.getDurability() && item.getType() == otherItem.getType()) {
                                    ItemBuilder copy = new ItemBuilder(otherItem);
                                    copy.removeLore(4);
                                    copy
                                            .addLore("")
                                            .addLore("&bThe &5" + spell.getName() + " &bspell is bound to this item.")
                                            .addLore("")
                                            .addLore("&7&lClick this item to &c&lunbind &7&lthe &5&l" + spell.getName() + " &7&lspell from this item.");

                                    inventory.setItem(i, copy.addGlow().build());
                                }
                            }
                        }
                    } else {
                        spellbook.save();

                        itemBuilder
                                .addLore("")
                                .addLore("&bThe &5" + spell.getName() + " &bspell is not bound to this item.")
                                .addLore("")
                                .addLore("&7&lClick this item to &a&lbind &7&lthe &5&l" + spell.getName() + " &7&lspell from this item.");

                        inventory.setItem(slot, new ItemBuilder(itemBuilder.build()).hideGlow().build());

                        ItemStack[] items = inventory.getContents();
                        for (int i = 0; i < 36; i++) {
                            ItemStack otherItem = items[i];

                            if (otherItem != null && slot != i) {
                                if (item.getDurability() == otherItem.getDurability() && item.getType() == otherItem.getType()) {
                                    ItemBuilder copy = new ItemBuilder(otherItem);
                                    copy.removeLore(4);
                                    copy
                                            .addLore("")
                                            .addLore("&bThe &5" + spell.getName() + " &bspell is not bound to this item.")
                                            .addLore("")
                                            .addLore("&7&lClick this item to &a&lbind &7&lthe &5&l" + spell.getName() + " &7&lspell from this item.");

                                    inventory.setItem(i, copy.hideGlow().build());
                                }
                            }
                        }
                    }


                    player.playSound(player.getLocation(), Sound.CLICK, 1.0F, 1.0F);
                }
            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();

        if (spellSubMenuUsers.contains(player.getUniqueId())) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    spellSubMenuUsers.remove(player.getUniqueId());
                    main.getSpellMenuManager().openSpellsMenu(player);
                    player.playSound(player.getLocation(), Sound.CHEST_OPEN, 1.0F, 1.0F);
                }
            }.runTaskLater(main, 1L);
        } else if (spellConfigurationMenuUsers.containsKey(player.getUniqueId())) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    spellConfigurationMenuUsers.remove(player.getUniqueId());
                    main.getSpellMenuManager().openSpellsMenu(player);
                    player.playSound(player.getLocation(), Sound.CHEST_OPEN, 1.0F, 1.0F);
                }
            }.runTaskLater(main, 1L);
        }
    }
}
