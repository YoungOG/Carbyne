package com.medievallords.carbyne.utils;

import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Namer {

    public static ItemStack setName(ItemStack item, String name) {
        ItemMeta im = item.getItemMeta();
        im.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
        item.setItemMeta(im);
        return item;
    }

    public static String getName(ItemStack item) {
        return item.getItemMeta().getDisplayName();
    }

    public static ItemStack setLore(ItemStack item, List<String> lore) {
        ItemMeta im = item.getItemMeta();
        im.setLore(addChatColor(lore));
        item.setItemMeta(im);

        return item;
    }

    public static ItemStack setLore(ItemStack item, String lore) {
        ItemMeta im = item.getItemMeta();
        List<String> loreList = new ArrayList<String>();
        loreList.add(lore);
        im.setLore(addChatColor(loreList));
        item.setItemMeta(im);

        return item;
    }

    public static ItemStack setLore(ItemStack item, String lore, int index) {
        ItemMeta im = item.getItemMeta();
        List<String> loreList = im.getLore();
        loreList.set(index, lore);
        im.setLore(addChatColor(loreList));
        item.setItemMeta(im);

        return item;
    }

    public static ItemStack addLore(ItemStack item, String lore) {
        ItemMeta im = item.getItemMeta();
        List<String> temp = im.getLore();
        temp.add(ChatColor.translateAlternateColorCodes('&', lore));
        im.setLore(temp);
        item.setItemMeta(im);

        return item;
    }

    public static ItemStack addLore(ItemStack item, List<String> lore) {
        ItemMeta im = item.getItemMeta();
        List<String> temp = im.getLore();
        Iterator<String> itr = lore.iterator();
        while (itr.hasNext()) {
            temp.add(ChatColor.translateAlternateColorCodes('&', itr.next()));
        }
        im.setLore(temp);
        item.setItemMeta(im);

        return item;
    }

    public static List<String> getLore(ItemStack item) {
        if (item == null)
            return null;
        if (item.getItemMeta() == null)
            return null;
        return item.getItemMeta().getLore();
    }

    public static String[] getLoreAsArray(ItemStack item) {
        if (item.getItemMeta().getLore() == null)
            return new String[0];
        String[] temp = new String[item.getItemMeta().getLore().size()];
        int i = 0;
        for (String s : item.getItemMeta().getLore()) {
            temp[i] = s;
            ++i;
        }
        return temp;
    }

    public static ChatColor getChatColor(String s) {
        if (s.contains("&0")) {
            s = s.replace("&0", "");
            return ChatColor.BLACK;
        } else if (s.contains("&1")) {
            s = s.replace("&1", "");
            return ChatColor.DARK_BLUE;
        } else if (s.contains("&2")) {
            s = s.replace("&2", "");
            return ChatColor.DARK_GREEN;
        } else if (s.contains("&3")) {
            s = s.replace("&3", "");
            return ChatColor.DARK_AQUA;
        } else if (s.contains("&4")) {
            s = s.replace("&4", "");
            return ChatColor.DARK_RED;
        } else if (s.contains("&5")) {
            s = s.replace("&5", "");
            return ChatColor.DARK_PURPLE;
        } else if (s.contains("&6")) {
            s = s.replace("&6", "");
            return ChatColor.GOLD;
        } else if (s.contains("&7")) {
            s = s.replace("&7", "");
            return ChatColor.GRAY;
        } else if (s.contains("&8")) {
            s = s.replace("&8", "");
            return ChatColor.DARK_GRAY;
        } else if (s.contains("&9")) {
            s = s.replace("&9", "");
            return ChatColor.BLUE;
        } else if (s.contains("&a")) {
            s = s.replace("&a", "");
            return ChatColor.GREEN;
        } else if (s.contains("&b")) {
            s = s.replace("&b", "");
            return ChatColor.AQUA;
        } else if (s.contains("&c")) {
            s = s.replace("&c", "");
            return ChatColor.RED;
        } else if (s.contains("&d")) {
            s = s.replace("&d", "");
            return ChatColor.LIGHT_PURPLE;
        } else if (s.contains("&e")) {
            s = s.replace("&e", "");
            return ChatColor.YELLOW;
        } else if (s.contains("&f")) {
            s = s.replace("&f", "");
            return ChatColor.WHITE;
        }
        return ChatColor.RESET;
    }

    public static String removeTag(String s) {
        if (s.contains("&0")) {
            s = s.replace("&0", "");

        }
        if (s.contains("&1")) {
            s = s.replace("&1", "");

        }
        if (s.contains("&2")) {
            s = s.replace("&2", "");

        }
        if (s.contains("&3")) {
            s = s.replace("&3", "");

        }
        if (s.contains("&4")) {
            s = s.replace("&4", "");

        }
        if (s.contains("&5")) {
            s = s.replace("&5", "");

        }
        if (s.contains("&6")) {
            s = s.replace("&6", "");

        }
        if (s.contains("&7")) {
            s = s.replace("&7", "");

        }
        if (s.contains("&8")) {
            s = s.replace("&8", "");

        }
        if (s.contains("&9")) {
            s = s.replace("&9", "");

        }
        if (s.contains("&a")) {
            s = s.replace("&a", "");

        }
        if (s.contains("&b")) {
            s = s.replace("&b", "");

        }
        if (s.contains("&c")) {
            s = s.replace("&c", "");

        }
        if (s.contains("&d")) {
            s = s.replace("&d", "");

        }
        if (s.contains("&e")) {
            s = s.replace("&e", "");

        }
        if (s.contains("&f")) {
            s = s.replace("&f", "");

        }
        return s;
    }

    public static String[] addChatColor(String[] lore) {
        String[] temp = new String[lore.length];
        int i = 0;
        for (String s : lore) {
            if (s.contains("&0")) {
                s = s.replace("&0", "" + ChatColor.BLACK);
            }
            if (s.contains("&1")) {
                s = s.replace("&1", "" + ChatColor.DARK_BLUE);
            }
            if (s.contains("&2")) {
                s = s.replace("&2", "" + ChatColor.DARK_GREEN);
            }
            if (s.contains("&3")) {
                s = s.replace("&3", "" + ChatColor.DARK_AQUA);
            }
            if (s.contains("&4")) {
                s = s.replace("&4", "" + ChatColor.DARK_RED);
            }
            if (s.contains("&5")) {
                s = s.replace("&5", "" + ChatColor.DARK_PURPLE);
            }
            if (s.contains("&6")) {
                s = s.replace("&6", "" + ChatColor.GOLD);
            }
            if (s.contains("&7")) {
                s = s.replace("&7", "" + ChatColor.GRAY);
            }
            if (s.contains("&8")) {
                s = s.replace("&8", "" + ChatColor.DARK_GRAY);
            }
            if (s.contains("&9")) {
                s = s.replace("&9", "" + ChatColor.BLUE);
            }
            if (s.contains("&a")) {
                s = s.replace("&a", "" + ChatColor.GREEN);
            }
            if (s.contains("&b")) {
                s = s.replace("&b", "" + ChatColor.AQUA);
            }
            if (s.contains("&c")) {
                s = s.replace("&c", "" + ChatColor.RED);
            }
            if (s.contains("&d")) {
                s = s.replace("&d", "" + ChatColor.LIGHT_PURPLE);
            }
            if (s.contains("&e")) {
                s = s.replace("&e", "" + ChatColor.YELLOW);
            }
            if (s.contains("&f")) {
                s = s.replace("&f", "" + ChatColor.WHITE);
            }
            if (s.contains("&k")) {
                s = s.replace("&k", "" + ChatColor.MAGIC);
            }
            temp[i] = s;
            ++i;
        }

        return temp;
    }

    public static List<String> addChatColor(List<String> lore) {
        List<String> temp = new ArrayList<>(lore.size());
        for (String s : lore) {
            temp.add(ChatColor.translateAlternateColorCodes('&', s));
        }

        return temp;
    }

    public static String getPotionEffectName(PotionEffect effect) {
        String name = "Unknown";

        switch (effect.getType().getName().toLowerCase()) {
            case "absorption":
                name = "Absorption";
                break;
            case "blindness":
                name = "Blindness";
                break;
            case "confusion":
                name = "Confusion";
                break;
            case "damage_resistance":
                name = "Damage Resistance";
                break;
            case "fast_digging":
                name = "Haste";
                break;
            case "fire_resistance":
                name = "Fire Resistance";
                break;
            case "hunger":
                name = "Hunger";
                break;
            case "increase_damage":
                name = "Strength";
                break;
            case "invisibility":
                name = "Invisibility";
                break;
            case "jump":
                name = "Jump Boost";
                break;
            case "night_vision":
                name = "Night Vision";
                break;
            case "poison":
                name = "Poison";
                break;
            case "regeneration":
                name = "Regeneration";
                break;
            case "saturation":
                name = "Saturation";
                break;
            case "slow":
                name = "Slowness";
                break;
            case "slow_digging":
                name = "Fatigue";
                break;
            case "speed":
                name = "Speed";
                break;
            case "weakness":
                name = "Weakness";
                break;
            case "wither":
                name = "Wither";
                break;
        }

        return name + getAmplifierName(effect.getAmplifier());
    }

    public static String getAmplifierName(int amplifier) {
        String name = " " + amplifier;

        switch (amplifier) {
            case 0:
                name = " I";
                break;
            case 1:
                name = " II";
                break;
            case 2:
                name = " III";
                break;
            case 3:
                name = " IV";
                break;
            case 4:
                name = " V";
                break;
        }

        return name;
    }

    public static String capitalizeFirstLetter(String original) {
        if (original == null || original.length() == 0) {
            return original;
        }
        return original.substring(0, 1).toUpperCase() + original.substring(1);
    }
}