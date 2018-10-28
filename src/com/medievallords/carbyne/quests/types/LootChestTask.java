package com.medievallords.carbyne.quests.types;

import com.medievallords.carbyne.gear.types.CarbyneGear;
import com.medievallords.carbyne.quests.Task;
import com.medievallords.carbyne.quests.config.QuestLineConfig;
import com.medievallords.carbyne.utils.StaticClasses;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class LootChestTask extends Task {

    private List<String> requiredItems = new ArrayList<>();

    public LootChestTask(String name, QuestLineConfig line, List<String> commands) {
        super(name, line, commands);

        String split[] = line.getString("items", "STONE,").split(",");
        for (String s : split) {
            requiredItems.add(s);
        }
    }

    public boolean isMatching(ItemStack itemStack) {
        for (String s : requiredItems) {
            CarbyneGear gear = StaticClasses.gearManager.getCarbyneGear(itemStack);
            CarbyneGear gear2 = StaticClasses.gearManager.getCarbyneGear(s);
            if (gear != null && gear2 != null && gear == gear2) {
                return true;
            }

            if (itemStack.hasItemMeta() && itemStack.getItemMeta().hasDisplayName()) {
                if (ChatColor.translateAlternateColorCodes('&', s).equals(itemStack.getItemMeta().getDisplayName())) {
                    return true;
                }
            } else {
                if (itemStack.getType().name().equals(s.toUpperCase())) {
                    return true;
                }
            }

        }

        return false;
    }

//    public class RequiredItem {
//
//        private Material material;
//        private String item;
//
//        public RequiredItem(Material material, String item) {
//            this.item = item;
//            this.material = material;
//        }
//
//        public Material getMaterial() {
//            return material;
//        }
//
//        public String getItem() {
//            return item;
//        }
//    }
}
