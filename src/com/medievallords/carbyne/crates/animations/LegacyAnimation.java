package com.medievallords.carbyne.crates.animations;

import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.crates.rewards.Reward;
import com.medievallords.carbyne.crates.rewards.RewardGenerator;
import com.medievallords.carbyne.utils.InstantFirework;
import com.medievallords.carbyne.utils.ItemBuilder;
import com.medievallords.carbyne.utils.Maths;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

@Getter
@Setter
public class LegacyAnimation extends CrateAnimation {

    private HashMap<UUID, Inventory> crateOpeners = new HashMap<>();
    private HashMap<UUID, Integer> crateOpenersAmount = new HashMap<>();
    private List<Reward> rewards;
    private int rewardsAmount;

    public LegacyAnimation(List<Reward> rewards, int rewardsAmount) {
        this.rewards = rewards;
        this.rewardsAmount = rewardsAmount;
    }

    public void generateRewards(Player player) {
        int openTime = Carbyne.getInstance().getConfig().getInt("crates.crate-opening-time");
        int fillerId = Carbyne.getInstance().getConfig().getInt("crates.filler-itemid");
        int fillerPeriod = Carbyne.getInstance().getConfig().getInt("crates.filler-period");

        Inventory inventory = Bukkit.createInventory(player, 27, ChatColor.AQUA + "" + ChatColor.BOLD + "Crate Rewards");

        InstantFirework.spawn(player.getLocation(), FireworkEffect.builder().withColor(Color.BLUE).withColor(Color.RED).withColor(Color.GREEN).withColor(Color.YELLOW).withColor(Color.PURPLE).withFade(Color.TEAL).build());

        crateOpeners.put(player.getUniqueId(), inventory);
        crateOpenersAmount.put(player.getUniqueId(), rewardsAmount - 1);

        new BukkitRunnable() {
            int runTime = openTime * fillerPeriod;

            @Override
            public void run() {
                if (runTime > 0)
                    runTime--;
                else {
                    cancel();
                    return;
                }

                if (rewardsAmount == 1)
                    for (int i = 0; i < inventory.getSize(); i++) {
                        if (i == 13)
                            continue;

                        inventory.setItem(i, new ItemBuilder(Material.getMaterial(fillerId)).name("").durability(new Random().nextInt(16)).build());
                    }
                else if (rewardsAmount == 2)
                    for (int i = 0; i < inventory.getSize(); i++) {
                        if (i == 12 || i == 14)
                            continue;

                        inventory.setItem(i, new ItemBuilder(Material.getMaterial(fillerId)).name("").durability(new Random().nextInt(16)).build());
                    }
                else if (rewardsAmount == 3)
                    for (int i = 0; i < inventory.getSize(); i++) {
                        if (i >= 12 && i <= 14)
                            continue;

                        inventory.setItem(i, new ItemBuilder(Material.getMaterial(fillerId)).name("").durability(new Random().nextInt(16)).build());
                    }
                else if (rewardsAmount == 5)
                    for (int i = 0; i < inventory.getSize(); i++) {
                        if (i >= 11 && i <= 15)
                            continue;

                        inventory.setItem(i, new ItemBuilder(Material.getMaterial(fillerId)).name("").durability(new Random().nextInt(16)).build());
                    }
                else
                    for (int i = 0; i < inventory.getSize(); i++) {
                        if (i == 13)
                            continue;

                        inventory.setItem(i, new ItemBuilder(Material.getMaterial(fillerId)).name("").durability(new Random().nextInt(16)).build());
                    }
            }
        }.runTaskTimerAsynchronously(Carbyne.getInstance(), 0L, fillerPeriod);

        ArrayList<RewardGenerator> rewardGenerators = new ArrayList<>();

        new BukkitRunnable() {
            @Override
            public void run() {
                if (!crateOpeners.containsKey(player.getUniqueId())) {
                    for (RewardGenerator rewardGenerator : rewardGenerators)
                        if (!rewardGenerator.hasRan())
                            rewardGenerator.stopScheduler(player, true);

                    cancel();
                }
            }
        }.runTaskTimer(Carbyne.getInstance(), 0L, 1L);

        List<ItemStack> rewardItems = new ArrayList<>();
        for (Reward reward : rewards)
            rewardItems.add(reward.getItem(false));


        List<Reward> chosenRewards = getRewards(rewardsAmount, rewards);

        if (rewardsAmount == 1)
            rewardGenerators.add(new RewardGenerator(this, player, inventory, 13, 0, openTime, rewardItems, chosenRewards.get(0)));
        else if (rewardsAmount == 2) {
            rewardGenerators.add(new RewardGenerator(this, player, inventory, 12, 0, openTime, rewardItems, chosenRewards.get(0)));
            rewardGenerators.add(new RewardGenerator(this, player, inventory, 14, 10, openTime, rewardItems, chosenRewards.get(1)));
        } else if (rewardsAmount == 3) {
            rewardGenerators.add(new RewardGenerator(this, player, inventory, 12, 0, openTime, rewardItems, chosenRewards.get(0)));
            rewardGenerators.add(new RewardGenerator(this, player, inventory, 13, 10, openTime, rewardItems, chosenRewards.get(1)));
            rewardGenerators.add(new RewardGenerator(this, player, inventory, 14, 20, openTime, rewardItems, chosenRewards.get(2)));
        } else if (rewardsAmount == 5) {
            rewardGenerators.add(new RewardGenerator(this, player, inventory, 11, 0, openTime, rewardItems, chosenRewards.get(0)));
            rewardGenerators.add(new RewardGenerator(this, player, inventory, 12, 10, openTime, rewardItems, chosenRewards.get(1)));
            rewardGenerators.add(new RewardGenerator(this, player, inventory, 13, 20, openTime, rewardItems, chosenRewards.get(2)));
            rewardGenerators.add(new RewardGenerator(this, player, inventory, 14, 30, openTime, rewardItems, chosenRewards.get(3)));
            rewardGenerators.add(new RewardGenerator(this, player, inventory, 15, 40, openTime, rewardItems, chosenRewards.get(4)));
        } else
            rewardGenerators.add(new RewardGenerator(this, player, inventory, 13, 0, openTime, rewardItems, chosenRewards.get(0)));

        player.openInventory(inventory);
    }

    public ArrayList<Reward> getRewards(int amount, List<Reward> allRewards) {
        ArrayList<Reward> rewards = new ArrayList<>();

        int totalPercentage = 0;

        for (Reward reward : allRewards)
            totalPercentage += reward.getChance();

        for (int a = 0; a < amount; a++) {
            double random = Maths.randomNumberBetween(totalPercentage, 0);
            double last = 0;
            for (int i = 0; i < allRewards.size(); i++) {
                Reward reward = allRewards.get(i);
                double value = reward.getChance();

                if (random >= last && random < value + last) {
                    rewards.add(reward);
                    break;
                } else
                    last = value + last;
            }
        }

        return rewards;
    }
}
