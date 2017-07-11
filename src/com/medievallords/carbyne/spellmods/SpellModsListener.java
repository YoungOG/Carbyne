package com.medievallords.carbyne.spellmods;

import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.utils.ItemBuilder;
import com.medievallords.carbyne.utils.MessageManager;
import com.nisovin.magicspells.MagicSpells;
import com.nisovin.magicspells.Spell;
import com.nisovin.magicspells.Spellbook;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

/**
 * Created by Dalton on 6/11/2017.
 */
public class SpellModsListener implements Listener
{

    private Carbyne main = Carbyne.getInstance();
    private static String inventoryName = ChatColor.translateAlternateColorCodes('&', "&5&lMagic Spells");
    private static HashMap<String, ItemStack> spellItemRepresentations; // K: Spell Name V: Item Representation
    private Spell bind, unbind, forget;
    private static ItemStack bindItem, unbindItem, forgetItem, infoItem, nullItem;

    private HashMap<Player, String> currentSelection;

    public SpellModsListener()
    {
        spellItemRepresentations = new HashMap<>();
        spellItemRepresentations.put("blink", new ItemBuilder(Material.RED_MUSHROOM).amount(1).name("&4Blink").addLore("&cTeleport a short distance!").build());
        spellItemRepresentations.put("leap", new ItemBuilder(Material.BROWN_MUSHROOM).amount(1).name("&eLeap").addLore("&6Leap a short distance!").build());
        spellItemRepresentations.put("poison", new ItemBuilder(Material.POISONOUS_POTATO).amount(1).name("&2Poison").addLore("&aCripple foes with powerful poison!").build());
        spellItemRepresentations.put("cripple", new ItemBuilder(Material.STRING).amount(1).name("&9Cripple").addLore("&bEncumber your foes with concentrated magic!").build());
        spellItemRepresentations.put("entomb", new ItemBuilder(Material.IRON_FENCE).amount(1).name("&4Entomb").addLore("&5Trap your foe behind bars!").build());
        spellItemRepresentations.put("shadowstep", new ItemBuilder(Material.COAL).amount(1).name("&4ShadowStep").addLore("&0Travel to your foes shadow!").build());
        spellItemRepresentations.put("silence", new ItemBuilder(Material.WEB).amount(1).name("&3Silence").addLore("&bSuppress magic for a short time!").build());
        spellItemRepresentations.put("haze", new ItemBuilder(Material.CAKE).amount(1).name("&7Haze").addLore("&dConfused your target with strange magic!").build());

        bind = MagicSpells.getSpellByInternalName("bind");
        unbind = MagicSpells.getSpellByInternalName("unbind");
        forget = MagicSpells.getSpellByInternalName("forget");

        bindItem = new ItemBuilder(Material.WOOL).durability(10).name("Bind").addLore("Bind a spell to the current item in your hand!").build();
        unbindItem = new ItemBuilder(Material.WOOL).durability(15).name("Unbind").addLore("Unbind a spell from the current item in your hand!").build();
        forgetItem = new ItemBuilder(Material.WOOL).durability(14).name("&4Forget").addLore("Select a spell to forget! Costs 5 diamonds!").build();
        infoItem = new ItemBuilder(Material.BOOK).name("&2Learn Spell").addLore("Travel to the Spell Library at spawn to learn spells!").build();
        nullItem = new ItemBuilder(Material.WOOL).name("No Selection").build();

        currentSelection = new HashMap<>();
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e)
    {
        if(e.getInventory().getName().equalsIgnoreCase(inventoryName))
            currentSelection.remove(e.getPlayer());
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e)
    {
        if(e.getInventory().getName().equalsIgnoreCase(inventoryName) && e.getWhoClicked() instanceof Player)
        {
            e.setCancelled(true);

            Player player = (Player)e.getWhoClicked();
            ItemStack is = e.getCurrentItem();
            Inventory inv = e.getInventory();

            String spellName;
            try {
                spellName = ChatColor.stripColor(is.getItemMeta().getDisplayName()).toLowerCase();
            } catch(NullPointerException ex) { return; } //This spell is not coded into the gui, an admin prbably used a spell that is not in the gui.

            switch(spellName)
            {
                case "bind":
                {
                    currentSelection.put(player, "bind");
                    MessageManager.sendMessage(player, "&cYou have selected bind!");
                    inv.setItem(25, inv.getItem(19));
                    break;
                }
                case "unbind":
                {
                    currentSelection.put(player, "unbind");
                    MessageManager.sendMessage(player, "&cYou have selected unbind!");
                    inv.setItem(25, inv.getItem(18));
                    break;
                }
                case "forget":
                {
                    currentSelection.put(player, "forget");
                    MessageManager.sendMessage(player, "&4You have selected forget!");
                    inv.setItem(25, inv.getItem(20));
                    break;
                }
                default:
                {
                    if(currentSelection.containsKey(player))
                        switch(currentSelection.get(player))
                        {
                            case "bind":
                            {
                                bind.castSpell(player, Spell.SpellCastState.NORMAL, 1, new String[] { spellName + "" });
                                currentSelection.remove(player);
                                inv.setItem(25, nullItem);
                                break;
                            }
                            case "unbind":
                            {
                                unbind.castSpell(player, Spell.SpellCastState.NORMAL, 1, new String[] { spellName + "" });
                                currentSelection.remove(player);
                                inv.setItem(25, nullItem);
                                break;
                            }
                            case "forget":
                            {
                                forget.castSpell(player, Spell.SpellCastState.NORMAL, 1, new String[] { spellName + "" });
                                currentSelection.remove(player);
                                Spellbook spellBook = MagicSpells.getSpellbook(player);

                                for(int j = 0; j < 9; j++)
                                    inv.setItem(j, new ItemStack(Material.AIR));

                                int i = 2;
                                for(Spell spell : spellBook.getSpells())
                                {
                                    String name = spell.getName();
                                    if(spellItemRepresentations.containsKey(name))
                                    {
                                        inv.setItem(i, spellItemRepresentations.get(name));
                                        i++;
                                    }
                                }

                                inv.setItem(25, nullItem);
                                break;
                            }
                        }
                    break;
                }
            }
        }
    }

    public static Inventory generateGUI(Player player)
    {
        Inventory gui = Bukkit.createInventory(null, 27, inventoryName);
        Spellbook spellBook = MagicSpells.getSpellbook(player);

        int i = 2;
        for(Spell spell : spellBook.getSpells())
        {
            String name = spell.getName();
            if(spellItemRepresentations.containsKey(name))
            {
                gui.setItem(i, spellItemRepresentations.get(name));
                i++;
            }
        }

        gui.setItem(26, infoItem);
        gui.setItem(25, nullItem);
        gui.setItem(20, forgetItem);
        gui.setItem(19, bindItem);
        gui.setItem(18, unbindItem);

        return gui;
    }

}
