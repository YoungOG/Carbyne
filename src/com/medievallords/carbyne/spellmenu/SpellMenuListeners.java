package com.medievallords.carbyne.spellmenu;

import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.utils.ItemBuilder;
import com.medievallords.carbyne.utils.MessageManager;
import com.medievallords.carbyne.utils.PlayerUtility;
import com.nisovin.magicspells.MagicSpells;
import com.nisovin.magicspells.Spell;
import com.nisovin.magicspells.Spellbook;
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

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SpellMenuListeners implements Listener {

    @Getter
    private static List<UUID> users = new ArrayList<>();
    private Carbyne main = Carbyne.getInstance();
    private Spell bind, unbind, forget;

    public SpellMenuListeners() {
        bind = MagicSpells.getSpellByInternalName("bind");
        unbind = MagicSpells.getSpellByInternalName("unbind");
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
                        main.getSpellMenuManager().openSpellOptions(player, spellName);
                        player.playSound(player.getLocation(), Sound.CHEST_OPEN, 1.0F, 1.0F);
                    } else {
                        if (!spellbook.canLearn(spell)) {
                            users.remove(player.getUniqueId());
                            player.closeInventory();
                            player.playSound(player.getLocation(), Sound.ITEM_PICKUP, 1.0F, 1.0F);
                            MessageManager.sendMessage(player, "&cYou cannot learn this spell.");
                            return;
                        } else if (spellbook.hasSpell(spell)) {
                            users.remove(player.getUniqueId());
                            player.closeInventory();
                            player.playSound(player.getLocation(), Sound.ITEM_PICKUP, 1.0F, 1.0F);
                            MessageManager.sendMessage(player, "&cYou have already learnt this spell.");
                            return;
                        } else {
                            spellbook.addSpell(spell);
                            spellbook.save();

                            ItemBuilder builder = new ItemBuilder(item);
                            builder.setLore(0, "&b&lLearnt").setLore(1, "&8Click to manage this spell.").addGlow();
                            inventory.setItem(event.getSlot(), builder.build());
                            player.playSound(player.getLocation(), Sound.NOTE_PLING, 1.0F, 1.0F);
                        }
                    }
                }
            }
        }

        if (inventory.getTitle().contains("Spell Options")) {
            event.setCancelled(true);

            String spellName = inventory.getTitle().split(": ")[1];

            if (item != null) {
                switch (item.getType()) {
                    case INK_SACK:
                        if (item.getDurability() == 10) {
                            if (player.getItemInHand() != null && player.getItemInHand().getType() != Material.AIR) {
                                bind.castSpell(player, Spell.SpellCastState.NORMAL, 1, new String[]{spellName + ""});
                                player.playSound(player.getLocation(), Sound.CLICK, 1.0F, 1.0F);
                            } else {
                                users.remove(player.getUniqueId());
                                player.closeInventory();
                                player.playSound(player.getLocation(), Sound.ITEM_PICKUP, 1.0F, 1.0F);
                                MessageManager.sendMessage(player, "&cYou must be holding an item to bind this spell.");
                                return;
                            }
                        } else if (item.getDurability() == 8) {
                            if (player.getItemInHand() != null && player.getItemInHand().getType() != Material.AIR) {
                                unbind.castSpell(player, Spell.SpellCastState.NORMAL, 1, new String[]{spellName + ""});
                                player.playSound(player.getLocation(), Sound.CLICK, 1.0F, 1.0F);
                            } else {
                                users.remove(player.getUniqueId());
                                player.closeInventory();
                                player.playSound(player.getLocation(), Sound.ITEM_PICKUP, 1.0F, 1.0F);
                                MessageManager.sendMessage(player, "&cYou must be holding an item to unbind this spell.");
                                return;
                            }
                        }
                        break;
                    case REDSTONE:
                        if (player.getInventory().containsAtLeast(new ItemStack(Material.DIAMOND), 5)) {
                            forget.castSpell(player, Spell.SpellCastState.NORMAL, 1, new String[]{spellName + ""});
                            PlayerUtility.removeItems(player.getInventory(), Material.DIAMOND, 0, 5);
                            player.playSound(player.getLocation(), Sound.CLICK, 1.0F, 1.0F);
                        } else {
                            users.remove(player.getUniqueId());
                            player.closeInventory();
                            player.playSound(player.getLocation(), Sound.ITEM_PICKUP, 1.0F, 1.0F);
                            MessageManager.sendMessage(player, "&cYou need to have at least 5 diamonds in your inventory to forget this spell.");
                            return;
                        }

                        break;
                }

                main.getSpellMenuManager().openSpellsMenu(player);
            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();

        if (users.contains(player.getUniqueId())) {
            users.remove(player.getUniqueId());
            main.getSpellMenuManager().openSpellsMenu(player);
            player.playSound(player.getLocation(), Sound.CHEST_OPEN, 1.0F, 1.0F);
        }
    }
}
