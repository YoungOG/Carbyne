package com.medievallords.carbyne.quests.types;

import com.medievallords.carbyne.quests.Task;
import com.medievallords.carbyne.quests.config.QuestLineConfig;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EnchantTask extends Task {

    private final List<String> items = new ArrayList<>();
    private final List<String> enchantments = new ArrayList<>();

    public EnchantTask(String name, QuestLineConfig line, List<String> commands) {
        super(name, line, commands);

        String split[] = line.getString("items", "STONE:0,").split(",");
        for (String s : split) {
            items.add(s);
        }

        String split2[] = line.getString("enchantments", "DURABILITY:0,").split(",");
        for (String s : split2) {
            enchantments.add(s);
        }
    }

    public boolean isMatching(ItemStack itemStack, Map<Enchantment, Integer> enchantments) {
        boolean matching = false;
        for (String s : items) {
            String[] split = s.split(":");
            int i = Integer.parseInt(split[1]);
            if (itemStack.getType() == Material.getMaterial(split[0].toUpperCase()) && ((int) itemStack.getDurability()) == i) {
                matching = true;
                break;
            }
        }

        if (matching) {
            for (String s : this.enchantments) {
                String[] split = s.split(":");
                String name = split[0].toUpperCase();
                int i = Integer.parseInt(split[1]);

                try {
                    Enchantment enchantmentFound = Enchantment.getByName(name);
                    if (enchantments.containsKey(enchantmentFound) && enchantments.get(enchantmentFound) == i) {
                        return true;
                    }
                } catch (IllegalArgumentException e) {
                    System.out.println("Could not find enchantment " + name + ". Please chest the quest config.");
                    return false;
                }
            }

            return false;
        } else {
            return false;
        }
    }
}
