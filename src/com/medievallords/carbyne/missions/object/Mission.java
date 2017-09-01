package com.medievallords.carbyne.missions.object;

import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.economy.account.Account;
import com.medievallords.carbyne.missions.MissionsManager;
import com.medievallords.carbyne.missions.enums.Difficulty;
import com.medievallords.carbyne.utils.DateUtil;
import com.medievallords.carbyne.utils.ItemBuilder;
import com.medievallords.carbyne.utils.Maths;
import com.medievallords.carbyne.utils.MessageManager;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.reflect.ConstructorUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * Created by Dalton on 8/8/2017.
 */
@Getter
@Setter
public abstract class Mission {

    private static MissionsManager missionsManager = Carbyne.getInstance().getMissionsManager();

    private boolean active, passed, failed;

    private String name;
    private String[] description;
    private String timeLimit;
    private Difficulty difficulty;
    private int objectiveCount;
    private int objectiveGoal;
    private double reward;

    private long activeTimeLimit = -1;

    private List<String> lootData;

    /**
     * SPECIAL PRECONDITION: The subclass of this class MUST contain a constructor with the single arg UUID!
     *
     * @param name          Name of the mission
     * @param description   Description of the mission
     * @param timeLimit     How long a player has to complete a mission in String format ex. "1d4h2m54s"
     * @param objectiveGoal The amount of the objective goal you need to complete the mission.
     * @param reward        The cash reward for completing the mission.
     */
    public Mission(String name, String[] description, String timeLimit, int objectiveGoal, double reward, List<String> lootData) {
        this.name = name;
        this.description = description;
        this.timeLimit = timeLimit;
        this.objectiveCount = 0;
        this.difficulty = randomDifficulty();
        this.objectiveGoal = (int) (objectiveGoal * difficulty.getModifier());
        this.objectiveGoal *= difficulty.getModifier();
        this.reward = Math.floor(reward * difficulty.getModifier());
        this.lootData = lootData;
    }

    /**
     * Alternative constructor where Difficulty is set manually.
     */
    public Mission(Difficulty difficulty, String name, String[] description, String timeLimit, int objectiveGoal, double reward, List<String> lootData) {
        this.name = name;
        this.description = description;
        this.timeLimit = timeLimit;
        this.objectiveCount = 0;
        this.difficulty = difficulty;
        this.objectiveGoal = (int) (objectiveGoal * difficulty.getModifier());
        this.objectiveGoal *= difficulty.getModifier();
        this.reward = Math.floor(reward * difficulty.getModifier());
        this.lootData = lootData;
    }

    /**
     * This method is called every time a player clicks on their Mission Inventory
     *
     * @param clicker The player who clicked.
     * @param inv     The inventory.
     * @param slot    The slot in the inventory clicked.
     */
    public void clickMission(Player clicker, Inventory inv, int slot) {
        UUID uuid = clicker.getUniqueId();
        if (this.difficulty.equals(Difficulty.CRAZY)) {
            Mission[] missions = missionsManager.getUuidMissions().get(clicker.getUniqueId()).getCurrentMissions();
            if (slot == 10 && missions[0].passed && missions[1].passed && missions[2].passed) {
                if (!active) {
                    activateMission(inv, slot, uuid);
                    MessageManager.sendMessage(clicker, "&bThe daily challenge " + name + "&b has been activated! Good luck!");
                } else if (active && failed) {
                    MessageManager.sendMessage(clicker, name + "&b has been &4&lFAILED&r&b!");
                } else if (active && passed) {
                    MessageManager.sendMessage(clicker, name + "&b has been &l&2COMPLETED&r&b!");
                }
                return;
            } else {
                MessageManager.sendMessage(clicker, "&bYou must complete the three daily missions to unlock the daily challenge!");
                return;
            }

        }

        if (!active && !passed && !failed) {
            activateMission(inv, slot, uuid);
            MessageManager.sendMessage(clicker, "&bThe mission " + name + "&b has been activated!");
        } else if (active && failed) {
            MessageManager.sendMessage(clicker, name + "&b has been &4&lFAILED&r&b!");
        } else if (active && passed) {
            MessageManager.sendMessage(clicker, name + "&b has been &l&2COMPLETED&r&b!");
        }
    }

    /**
     * Called when a mission needs to be activated. Parses time into long. This can run Async.
     *
     * @param inv  PRECONMDITION: Must be the Mission Inventory.
     * @param slot Slot which contains the mission itemstack to activate.
     */
    private void activateMission(Inventory inv, int slot, UUID uuid) {
        if (passed || failed) return;
        active = true;
        if (timeLimit != null) try {
            this.activeTimeLimit = DateUtil.parseDateDiff(timeLimit, true);
        } catch (Exception e) {
        }
        new BukkitRunnable() {
            public void run() {
                inv.setItem(slot, getMissionTicket());
            }
        }.runTask(Carbyne.getInstance());

        new BukkitRunnable() {
            public void run() {
                if (passed || failed) cancel();
                else if (activeTimeLimit <= System.currentTimeMillis()) failMission(uuid);
            }
        }.runTaskTimerAsynchronously(Carbyne.getInstance(), 0L, 20L);
    }

    /**
     * A method that calls to properly fail a mission by setting pass to false and sends the player a message if they are online.
     */
    public void failMission(UUID uuid) {
        failed = true;
        try {
            OfflinePlayer player = Bukkit.getPlayer(uuid);
            if (player.isOnline())
                MessageManager.sendMessage((Player) player, "&cYou have failed to complete the " + name + "&c mission in time!");
        } catch (Exception ex) {
        }
        active = false;
    }

    public void completeMission(UUID uuid) {
        passed = true;
        Player player = Bukkit.getPlayer(uuid);
        MessageManager.sendMessage(player, "&bYou have completed the " + name + "&b mission and have been rewarded with " + reward + "&b!");
        if (reward != -1) {
            Account account = Account.getAccount(uuid);
            account.setBalance(account.getBalance() + reward);
        }
        if (lootData != null && lootData.size() > 0) {
            for (int i = 0; i < lootData.size(); i++) {
                String data = lootData.get(i).toLowerCase();
                if (MissionsManager.chance.containsKey(data) && Math.random() < MissionsManager.chance.get(data) * difficulty.getModifier()) {
                    if (player.getInventory().firstEmpty() == -1) {
                        player.getWorld().dropItemNaturally(player.getLocation(), MissionsManager.lootItems.get(data));
                    } else player.getInventory().addItem(MissionsManager.lootItems.get(data));
                }
            }
        }
        active = false;
    }

    /**
     * Increment the objective goal while also making mission checks. This should always been called instead of incrmeenting the objectiveCount variable.
     *
     * @param amount > 0
     */
    public void incrementObjectiveCount(UUID uuid, int amount) {
        if (!active || passed || failed) return;
        objectiveCount += amount;
        if (objectiveCount >= objectiveGoal) {
            objectiveCount = objectiveGoal;
            completeMission(uuid);
        } else if (objectiveCount % 100 == 0)
            MessageManager.sendMessage(Bukkit.getPlayer(uuid), "&bProgress on " + name + "&b: &e" + objectiveCount + "&b/&e" + objectiveGoal);
    }

    public void adminPassMission(UUID uuid) {
        if (passed == true) return;
        active = false;
        passed = true;
        failed = false;
        objectiveCount = objectiveGoal;
        completeMission(uuid);
    }

    private Difficulty randomDifficulty() {
        return Difficulty.values()[Maths.randomNumberBetween(Difficulty.values().length - 2, 0)]; // -2 to exclude crazy and boss
    }

    public ItemStack getMissionTicket() {
        List<String> lore = new ArrayList<>(Arrays.asList(description));
        lore.add("");
        lore.add("&aProgress&e: " + objectiveCount + "&a/&e" + objectiveGoal);
        lore.add(ChatColor.translateAlternateColorCodes('&', "&aDifficulty&r" + difficulty.getColorCode() + ": " + difficulty.name()));
        lore.add("&aTime Left" + ((passed) ? "&2: &lPASSED" : ((failed) ? "&4: &lFAILED" : getTimeLeft())));
        lore.add("&aReward&e: " + reward);
        lore.add("");
        if (getLootData() != null && getLootData().size() >= 1) {
            lore.add("&6Drop Rewards and Chances: ");

            for (String name : getLootData()) {
                ItemStack is = MissionsManager.lootItems.get(name.toLowerCase());
                double chance = MissionsManager.chance.get(name.toLowerCase());
                lore.add("&r&6\u2022 &b" + ((is.getItemMeta().getDisplayName() != null) ? is.getItemMeta().getDisplayName() : is.getType().toString()) + " &r&6- &b" + Math.floor((difficulty.getModifier() * chance) * 100) + "%");
            }
        }


        ItemStack is = new ItemBuilder(
                ((passed) ? Material.EMERALD : ((failed) ? Material.COAL : ((active) ? Material.PAPER : Material.BOOK)))
        ).name(name)
                .setLore(lore)
                .build();
        return is;
    }

    public String getTimeLeft() {
        if (activeTimeLimit == -1) return "&b: &bNot Active";
        long diff = activeTimeLimit - System.currentTimeMillis();
        String hoursLeft = String.valueOf((int) (diff / (60 * 60 * 1000)));
        String minutesLeft = String.valueOf((int) ((diff % (60 * 60 * 1000)) / (60 * 1000)));
        String secondsLeft = String.valueOf((int) ((diff % (60 * 1000)) / (1000)));
        if (hoursLeft.length() == 1) hoursLeft = 0 + hoursLeft;
        if (secondsLeft.length() == 1) secondsLeft = 0 + secondsLeft;
        if (minutesLeft.length() == 1) minutesLeft = 0 + minutesLeft;
        return new String("&b: " + hoursLeft + ":" + minutesLeft + ":" + secondsLeft);
    }

    public static <T> T instanitate(Class<?> input, Object[] objects) {
        Class[] clazzes = new Class[objects.length];
        for (int i = 0; i < objects.length; i++) clazzes[i] = ((objects[i] == null) ? null : objects[i].getClass());
        Constructor<?> ctor = ConstructorUtils.getMatchingAccessibleConstructor(input, clazzes);
        try {
            return (T) ctor.newInstance(objects);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void changeDifficulty(MissionData data, Difficulty difficulty) {
        this.difficulty = difficulty;
        this.objectiveGoal = (int) (data.getObjectiveGoal() * difficulty.getModifier());
        this.objectiveGoal *= difficulty.getModifier();
        this.reward = Math.floor(data.getReward() * difficulty.getModifier());
    }

}
