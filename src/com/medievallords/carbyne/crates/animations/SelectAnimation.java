package com.medievallords.carbyne.crates.animations;

import com.medievallords.carbyne.crates.rewards.Reward;
import com.medievallords.carbyne.utils.ItemBuilder;
import com.medievallords.carbyne.utils.Maths;
import com.medievallords.carbyne.utils.StaticClasses;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@Getter
public class SelectAnimation extends CrateAnimation {

    private int slots;
    private List<Reward> rewards;
    private HashMap<UUID, SelectData> selectDataMap = new HashMap<>();
    private int rewardsAmount;

    public SelectAnimation(int slots, List<Reward> rewards, int rewardsAmount) {
        this.slots = slots;
        this.rewards = rewards;
        this.rewardsAmount = rewardsAmount;
    }

    @Override
    public void generateRewards(Player player) {
        Inventory inventory = Bukkit.createInventory(null, slots, ChatColor.AQUA + "" + ChatColor.BOLD + "Crate Rewards");
        ItemStack pane = new ItemBuilder(Material.STAINED_GLASS_PANE).durability(2).addEnchantment(Enchantment.DURABILITY, 10).name("&6&lSelect &7&l" + rewardsAmount + " &6&litems.")
                .addLore("").build();
        for (int i = 0; i < slots; i++) {
            inventory.setItem(i, pane);
        }

        SelectData selectData = new SelectData(player, inventory, rewardsAmount, this);
        selectDataMap.put(player.getUniqueId(), selectData);
        List<Reward> chosenRewards = getRewards(slots);
        for (int i = 0; i < slots; i++) {
            selectData.getSelectRewards().put(i, chosenRewards.get(i));
        }

        player.openInventory(inventory);
    }

//    public Reward getReward() {
//        int totalPercentage = 0;
//
//        for (Reward reward : rewards)
//            totalPercentage += reward.getChance();
//
//        int index = -1;
//        double random = Maths.randomNumberBetween(totalPercentage, 0);
//
//        double last = 0;
//        for (int i = 0; i < rewards.size(); i++) {
//            Reward reward = rewards.get(i);
//            double value = reward.getChance();
//
//            if (random > last && random < value + last) {
//                index = i;
//                break;
//            } else
//                last = last + value;
//        }
//
//        return rewards.get(index);
//    }

    public ArrayList<Reward> getRewards(int amount) {
        ArrayList<Reward> rewards = new ArrayList<>();

        int totalPercentage = 0;

        for (Reward reward : this.rewards)
            totalPercentage += reward.getChance();

        for (int a = 0; a < amount; a++) {
            double random = Maths.randomNumberBetween(totalPercentage, 0);
            double last = 0;
            for (int i = 0; i < this.rewards.size(); i++) {
                Reward reward = this.rewards.get(i);
                double value = reward.getChance();

                if (random >= last && random < value + last) {
                    if (StaticClasses.gearManager.getCarbyneGear(reward.getItem(false)) != null) {
                        int itemId = reward.getItemId();
                        int itemData = reward.getItemData();
                        int itemAmount = reward.getAmount();
                        String displayName = reward.getDisplayName();
                        List<String> lore = reward.getLore();
                        List<String> commands = reward.getCommands();
                        boolean displayItemOnly = reward.isDisplayItemOnly();
                        HashMap<Enchantment, Integer> enchantments = reward.getEnchantments();
                        int chance = reward.getChance();
                        int slot = reward.getSlot();

                        reward = new Reward(reward.getId(), itemId, itemData, itemAmount, StaticClasses.gearManager.getGearCode(reward.getItem(false)));
                        reward.setDisplayName(displayName);
                        reward.setLore(lore);
                        reward.setEnchantments(enchantments);
                        reward.setCommands(commands);
                        reward.setDisplayItemOnly(displayItemOnly);
                        reward.setChance(chance);
                        reward.setSlot(slot);
                    }

                    rewards.add(reward);
                    break;
                } else
                    last = value + last;
            }
        }

        return rewards;
    }
}
