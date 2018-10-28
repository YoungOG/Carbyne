package com.medievallords.carbyne.quests.rewards;

import com.medievallords.carbyne.gear.artifacts.Artifact;
import com.medievallords.carbyne.gear.types.CarbyneGear;
import com.medievallords.carbyne.utils.ItemBuilder;
import com.medievallords.carbyne.utils.StaticClasses;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Random;

@Getter
@Setter
public class QuestRewardItem {

    private String displayName, rewardDisplayName;
    private Material material;
    private int data, id;
    private List<String> lore;
    private HashMap<Enchantment, Integer> enchantments = new HashMap<>();
    private int amount;

    public QuestRewardItem(int id, Material material, String displayName, List<String> lore, int amount, int data, String rewardDisplayName) {
        this.id = id;
        this.material = material;
        this.displayName = displayName;
        this.lore = lore;
        this.amount = amount;
        this.data = data;
        this.rewardDisplayName = rewardDisplayName;
    }

    public ItemStack getItem() {
        if (displayName != null && !displayName.equals("")) {
            String[] gear = displayName.split(",");

            if (gear.length > 1) {
                int random = new Random().nextInt(gear.length);
                CarbyneGear carbyneGear = StaticClasses.gearManager.getCarbyneGear(gear[random]);

                if (carbyneGear != null) {
                    return carbyneGear.getItem(false);
                }
            }

            if (StaticClasses.gearManager.getCarbyneGear(displayName) != null) {
                return StaticClasses.gearManager.getCarbyneGear(displayName).getItem(false);
            } else if (displayName.contains("randomGear")) {
                String name = displayName;
                String[] split = name.split(":");

                if (split.length > 1) {
                    return StaticClasses.gearManager.getRandomCarbyneGear(Boolean.parseBoolean(split[1])).getItem(false);
                }
            } else if (displayName.contains("randomartifact")) {
                String name = displayName;
                String[] split = name.split(":");

                if (split.length > 1) {
                    return StaticClasses.gearManager.getRandomArtifact(Boolean.parseBoolean(split[1])).getCustomRecipe().getResult();
                }
            }

            Artifact artifact = StaticClasses.gearManager.getArtifact(displayName);
            if (artifact != null) {
                return artifact.getCustomRecipe().getResult();
            }
        }

        return new ItemBuilder(material).durability(data).amount(amount).name(displayName).setLore(lore).addEnchantments(enchantments).build();
    }
}
