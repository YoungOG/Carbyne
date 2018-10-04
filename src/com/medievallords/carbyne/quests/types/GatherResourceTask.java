package com.medievallords.carbyne.quests.types;

import com.medievallords.carbyne.quests.Task;
import com.medievallords.carbyne.quests.config.QuestLineConfig;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.ArrayList;
import java.util.List;

@Getter
public class GatherResourceTask extends Task {

    private final List<String> materials = new ArrayList<>();

    public GatherResourceTask(String name, QuestLineConfig line, List<String> commands) {
        super(name, line, commands);
        String[] mats = line.getString("materials", "LOG:0,").split(",");
        for (String mat : mats) {
            this.materials.add(mat);
        }
    }

    public boolean isMatching(Block block) {
        for (String s : this.materials) {
            String[] split = s.split(":");
            String name = split[0].toUpperCase();
            int i = Integer.parseInt(split[1]);

            try {
                Material material = Material.getMaterial(name);
                if (block.getType() == material && (int) block.getData() == i) {
                    return true;
                }
            } catch (IllegalArgumentException e) {
                System.out.println("Could not find material " + name + ". Check quest config.");
            }
        }

        return false;
    }
}
