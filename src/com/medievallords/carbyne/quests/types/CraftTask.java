package com.medievallords.carbyne.quests.types;


import com.medievallords.carbyne.quests.Task;
import com.medievallords.carbyne.quests.config.QuestLineConfig;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class CraftTask extends Task {

    private List<String> items = new ArrayList<>();

    public CraftTask(String name, QuestLineConfig line, List<String> commands) {
        super(name, line, commands);

        String split[] = line.getString("items", "STONE:0,").split(",");
        for (String s : split) {
            items.add(s);
        }
    }

    public boolean isMatching(ItemStack itemStack) {
        for (String s : items) {
            String[] split = s.split(":");
            int i = Integer.parseInt(split[1]);
            if (itemStack.getType() == Material.getMaterial(split[0].toUpperCase()) && ((int) itemStack.getDurability()) == i) {
                return true;
            }
        }

        return false;
    }
}
