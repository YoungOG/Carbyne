package com.medievallords.carbyne.crates;

import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.crates.animations.CrateAnimation;
import com.medievallords.carbyne.crates.rewards.Reward;
import com.medievallords.carbyne.crates.rewards.RewardGenerator;
import com.medievallords.carbyne.utils.*;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.io.IOException;
import java.util.*;
import java.util.logging.Level;

@Getter
@Setter
public class Crate {

    private String name;
    private Location location;
    private ArrayList<Reward> rewards = new ArrayList<>();
    private ArrayList<UUID> editors = new ArrayList<>();
    private int rewardsAmount;
    private CrateAnimation animation;
    private int taskID;

    public Crate(String name) {
        this.name = name;
    }

    public void save(FileConfiguration crateFileConfiguration) {
        ConfigurationSection configurationSection = crateFileConfiguration.getConfigurationSection("Crates");

        if (!configurationSection.isSet(name))
            configurationSection.createSection(name);

        if (!configurationSection.isSet(name + ".Locations"))
            configurationSection.createSection(name + ".Location");

        if (!configurationSection.isSet(name + ".Rewards"))
            configurationSection.createSection(name + ".Rewards");

        if (!configurationSection.isSet(name + ".RewardsAmount"))
            configurationSection.createSection(name + ".RewardsAmount");

        if (location != null)
            configurationSection.set(name + ".Location", LocationSerialization.serializeLocation(location));

        if (rewardsAmount > 0)
            configurationSection.set(name + ".RewardsAmount", rewardsAmount);

        try {
            crateFileConfiguration.save(Carbyne.getInstance().getCratesFile());
        } catch (IOException e) {
            e.printStackTrace();
            Carbyne.getInstance().getLogger().log(Level.WARNING, "Failed to save the crate " + name + "!");
        }
    }

    public void editRewards(Player player) {
        Inventory inventory = Bukkit.createInventory(player, 54, ChatColor.AQUA + "" + ChatColor.BOLD + "Edit Crate");

        for (Reward reward : getRewards()) {
            ItemStack itemStack = reward.getItem(true);

            if (reward.getCommands().size() > 0) {
                itemStack = new ItemBuilder(itemStack)
                        .addLore(" ")
                        .addLore("&aCommands:").build();
                for (String command : reward.getCommands())
                    new ItemBuilder(itemStack).addLore("&c" + command);
            }

            itemStack = new ItemBuilder(itemStack)
                    .addLore(" ")
                    .addLore("&aDisplay Item: &c" + reward.isDisplayItemOnly())
                    .addLore(" ")
                    .addLore("&aChance: &c" + reward.getChance() + "%").build();

            inventory.addItem(itemStack);
        }

        editors.add(player.getUniqueId());

        player.openInventory(inventory);
    }

    public void generateRewards(Player player, boolean dailyBonus) {
        if (!dailyBonus)
            if (player.getInventory().getItemInMainHand().getAmount() > 1) {
                player.getInventory().getItemInMainHand().setAmount(player.getInventory().getItemInMainHand().getAmount() - 1);
                player.setItemInHand(player.getInventory().getItemInMainHand());
            } else
                player.setItemInHand(new ItemStack(Material.AIR));

        animation.generateRewards(player);
    }

    public void showRewards(Player player) {
        Inventory inventory = Bukkit.createInventory(player, 54, ChatColor.AQUA + "" + ChatColor.BOLD + "Crate Rewards");
        HashMap<Integer, Boolean> randomGear = new HashMap<>();

        for (Reward reward : rewards) {
            inventory.setItem(reward.getSlot(), new ItemBuilder(reward.getItem(true)).addLore("").build());
            if (reward.getGearCode().startsWith("randomgear"))
                randomGear.put(reward.getSlot(), Boolean.valueOf(reward.getGearCode().split(":")[1]));
        }

        player.openInventory(inventory);

        if (randomGear.isEmpty())
            return;

        new BukkitRunnable() {
            @Override
            public void run() {
                if (player.getOpenInventory() == null) {
                    cancel();
                    return;
                }

                for (int p : randomGear.keySet()) {
                    ItemStack randomCarbyne = StaticClasses.gearManager.getRandomCarbyneGear(randomGear.get(p)).getItem(false);

                    inventory.setItem(p, randomCarbyne);
                }
            }
        }.runTaskTimer(Carbyne.getInstance(), 0, 10);
    }


    public void knockbackPlayer(Player player, Location relative) {
        double xpower = 0.25;
        double ypower = 0.3;
        double zpower = 0.25;
        double x = relative.getX() - player.getLocation().getX();
        double y = relative.getY() - player.getLocation().getY();
        double z = relative.getZ() - player.getLocation().getZ();
        Vector nv = new Vector(x, y, z);
        Vector nv2 = new Vector(nv.getX() * -xpower, ypower, nv.getZ() * -zpower);
        player.setVelocity(new Vector(0, 0, 0));
        player.setVelocity(nv2);
    }

//    public Reward getReward() {
//        int totalPercentage = 0;
//
//        for (Reward reward : getRewards())
//            totalPercentage += reward.getChance();
//
//        int index = -1;
//        double random = Maths.randomNumberBetween(totalPercentage, 0);
//
//        double last = 0;
//        for (int i = 0; i < rewards.size(); i++) {
//            Reward reward = getRewards().get(i);
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
//
//    public ArrayList<Reward> getRewards(int amount) {
//        ArrayList<Reward> rewards = new ArrayList<>();
//
//        int totalPercentage = 0;
//
//        for (Reward reward : getRewards())
//            totalPercentage += reward.getChance();
//
//        for (int a = 0; a < amount; a++) {
//            double random = Maths.randomNumberBetween(totalPercentage, 0);
//            double last = 0;
//            for (int i = 0; i < getRewards().size(); i++) {
//                Reward reward = getRewards().get(i);
//                double value = reward.getChance();
//
//                if (random >= last && random < value + last) {
//                    rewards.add(reward);
//                    break;
//                } else
//                    last = value + last;
//            }
//        }
//
//        return rewards;
//    }
//
//    public Reward getReward(int id) {
//        for (Reward reward : rewards)
//            if (reward.getId() == id)
//                return reward;
//
//        return null;
//    }

    public void runEffect(String crateName) {
        switch (crateName) {
            case "Mystical":
                runMysticalEffect();
                break;
            case "Vicious":
                runViciousEffect();
                break;
        }
    }

    private void runMysticalEffect() {
        this.taskID = Bukkit.getScheduler().scheduleAsyncRepeatingTask(Carbyne.getInstance(), new Runnable() {
            private double theta = 0, radius = 0.55;
            private Location loc = getLocation().clone().add(0.5, 0.5, 0.5);

            @Override
            public void run() {
                ParticleEffect.OrdinaryColor purple = new ParticleEffect.OrdinaryColor(237, 23, 52);

                theta += 0.2;

                double x = Math.cos(theta) * radius;
                double y = Math.cos(theta) * radius;
                double z = Math.sin(theta) * radius;

                loc.add(x, y, z);
                ParticleEffect.REDSTONE.display(purple, loc, 40, false);
                loc.subtract(x, 0, z);
                loc.subtract(x, 0, z);
                ParticleEffect.REDSTONE.display(purple, loc, 40, false);
                loc.add(x, -y, z);
            }
        }, 0, 1);
    }

    private void runViciousEffect() {
        this.taskID = Bukkit.getScheduler().scheduleAsyncRepeatingTask(Carbyne.getInstance(), new Runnable() {
            private double theta = 0, radius = 0.55;
            private Location loc = getLocation().clone().add(0.5, 0.5, 0.5);

            @Override
            public void run() {
                ParticleEffect.OrdinaryColor purple = new ParticleEffect.OrdinaryColor(244, 66, 244);

                theta += 0.2;

                double x = Math.cos(theta) * radius;
                double y = Math.cos(theta) * radius;
                double z = Math.sin(theta) * radius;

                loc.add(x, y, z);
                ParticleEffect.REDSTONE.display(purple, loc, 40, false);
                loc.subtract(x, 0, z);
                loc.subtract(x, 0, z);
                ParticleEffect.REDSTONE.display(purple, loc, 40, false);
                loc.add(x, -y, z);
            }
        }, 0, 1);
    }
}
