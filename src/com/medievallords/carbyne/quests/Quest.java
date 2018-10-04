package com.medievallords.carbyne.quests;

import com.comphenix.protocol.PacketType;
import com.medievallords.carbyne.profiles.Profile;
import com.medievallords.carbyne.quests.config.QuestLineConfig;
import com.medievallords.carbyne.quests.rewards.QuestReward;
import com.medievallords.carbyne.quests.rewards.QuestRewardItem;
import com.medievallords.carbyne.utils.*;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

@Getter
@Setter
public class Quest {

    // 10-15 dormantQuests per day. 10 (default), 15 for donators
    // You can have 2 dormantQuests at once (default), 4 for donators.

    protected HashMap<UUID, Long> players = new HashMap<>();
    private List<Task> tasks;
    private String name, displayName, completeMessage, takenMessage;
    private QuestReward reward;
    private boolean forcedQuest;
    private int totalProgress;
    private long timeFrame;
    private Difficulty difficulty;

    // Display Item
    private List<String> informationLore;

    public Quest(String name, List<String> informationLore, QuestLineConfig line, List<Task> tasks) {
        this.name = name;
        this.informationLore = informationLore;
        this.difficulty = Difficulty.valueOf(line.getString("difficulty", "EASY").toUpperCase());
        this.timeFrame = line.getInt("timeFrame", 0);
        this.displayName = line.getString("displayName", name);
        this.completeMessage = line.getString("completeMessage", "&aYou have completed the quest!");
        this.takenMessage = line.getString("takenMessage", "&aYou have taken the quest!");
        this.tasks = tasks;
        this.forcedQuest = line.getBoolean("forcedQuest", false);
        for (Task task : tasks) {
            totalProgress += task.requiredProgress;
        }
    }

    public void takeQuest(Player player) {
        Profile profile = StaticClasses.profileManager.getProfile(player.getUniqueId());
        profile.checkQuestNext();
        if (profile.getQuestsLeft() <= 0){
            MessageManager.sendMessage(player, "&cYou can more take more quests in &7" + profile.getQuestNextString());
            return;
        }

        players.put(player.getUniqueId(), System.currentTimeMillis());
        for (Task task : tasks) {
            task.addPlayer(player);
        }

        profile.setQuestsLeft(profile.getQuestsLeft() - 1);

        MessageManager.sendMessage(player, takenMessage);
    }

    public void skipQuest(Player player) {
        Profile profile = StaticClasses.profileManager.getProfile(player.getUniqueId());

        if (players.containsKey(player.getUniqueId())) {
            for (Task task : tasks) {
                task.removePlayer(player);
            }

            players.remove(player.getUniqueId());
            profile.setQuestsLeft(profile.getQuestsLeft() + 1);
        }

        MessageManager.sendMessage(player, "&cYou have skipped the quest!");

        profile.setQuestSkipsLeft(profile.getQuestSkipsLeft() - 1);
        profile.getDormantQuests().add(name);
    }

    public void removePlayer(Player player) {
        for (Task task : tasks) {
            task.removePlayer(player);
        }
        players.remove(player.getUniqueId());
    }

    public void addPlayer(UUID uuid, long time) {
        players.put(uuid, time);
    }

    public boolean completeQuest(Player player) {
        if (!players.containsKey(player.getUniqueId())) {
            return false;
        }

        if (!completedAllTasks(player.getUniqueId())) {
            MessageManager.sendMessage(player, "&cYou have not completed the quest!");
            return false;
        }

        for (Task task : tasks) {
            task.removePlayer(player);
        }

        MessageManager.sendMessage(player, completeMessage);
        players.remove(player.getUniqueId());
        Profile profile = StaticClasses.profileManager.getProfile(player.getUniqueId());
        if (forcedQuest) {
            profile.getForcedQuests().remove(name);
        } else {
            profile.getDormantQuests().add(name);
        }

        giveReward(player);
        player.openInventory(StaticClasses.questHandler.getQuestInventory(player));
        return true;
    }

    public boolean isComplete(Player player) {
        if (!players.containsKey(player.getUniqueId())) {
            return false;
        }

        if (!completedAllTasks(player.getUniqueId())) {
            return false;
        }

        return true;
    }

    private void giveReward(Player player) {
        reward.giveReward(player);
    }

    public String getRemainingTimeString(Player player) {
        if (timeFrame <= 0) {
            return "\u221e";
        }

        if (!players.containsKey(player.getUniqueId())) {
            return "";
        }

        long timePassed = System.currentTimeMillis() - players.get(player.getUniqueId());
        long timeLeft = timeFrame - timePassed;
        return DateUtil.readableTime(timeLeft, false);
    }

    public ItemStack getDisplayItem(Player player, boolean questTaken) {
        ItemBuilder builder = new ItemBuilder(Material.INK_SACK);
        builder.addLore(HiddenStringUtils.encodeString(name));
        if (questTaken) {
            builder.durability(8);
        } else {
            builder.durability(difficulty.getData());
        }

        builder.name(difficulty.getColor() + "&l" + displayName);
        builder.addLore("&dDifficulty&7: " + difficulty.getColor() + "" +WordUtils.capitalizeFully(difficulty.name()));
        builder.addLore("");
        if (!questTaken) {
            for (String s : informationLore) {
                builder.addLore("&7" + s);
            }

            builder.addLore("");

            builder.addLore("&dTime Frame&7: " + getTimeFrameString());
            builder.addLore("&dRewards&7:");
            for (QuestRewardItem rewardItem : reward.getRewardItems()) {
                String name;
                if (rewardItem.getDisplayName().isEmpty()) {
                    name = "&8- &b" + WordUtils.capitalizeFully(rewardItem.getMaterial().name().replace("_", ""));
                } else {
                    name = "&8- " + rewardItem.getDisplayName();
                }

                name += " &7" + rewardItem.getAmount();
                builder.addLore(name);
                if (!rewardItem.getEnchantments().isEmpty()) {
                    for (Enchantment enchantment : rewardItem.getEnchantments().keySet()) {
                        String enchantmentName = MessageManager.getEnchantmentFriendlyName(enchantment);
                        String loreAdd = "   &6" + enchantmentName + " &7" + rewardItem.getEnchantments().get(enchantment);
                        builder.addLore(loreAdd);
                    }
                }
            }

            builder.addLore("");
        } else {
            if (isComplete(player)) {
                builder.addLore("&7&l--- &d&lCompleted &7&l---");
                builder.addLore("");
            } else {
                builder.addLore("&dProgress&7: " + getOverallProgress(player));
                builder.addLore("&dTime&7: " + getRemainingTimeString(player));
                builder.addLore("&dRewards&7:");
                for (QuestRewardItem rewardItem : reward.getRewardItems()) {
                    String name;
                    if (rewardItem.getDisplayName().isEmpty()) {
                        name = "&8- &b" + WordUtils.capitalizeFully(rewardItem.getMaterial().name().replace("_", " "));
                    } else {
                        name = "&8- " + rewardItem.getDisplayName();
                    }

                    name += " &7" + rewardItem.getAmount();
                    builder.addLore(name);
                    if (!rewardItem.getEnchantments().isEmpty()) {
                        for (Enchantment enchantment : rewardItem.getEnchantments().keySet()) {
                            String enchantmentName = MessageManager.getEnchantmentFriendlyName(enchantment);
                            String loreAdd = "&a   &6" + enchantmentName + " &7" + rewardItem.getEnchantments().get(enchantment);
                            builder.addLore(loreAdd);
                        }
                    }
                }

                builder.addLore("");
            }
        }

        builder.addLore("&dInfo&7: /quest info");

        builder.addLore("&7(Right Click To Skip)");

        return builder.build();
    }

    private String getTimeFrameString() {
        if (timeFrame <= 0) {
            return "\u221e";
        }

        return DateUtil.readableTime(timeFrame, true);
    }

    private String getOverallProgress(Player player) {
        double progress = 0;
        for (Task task : tasks) {
            progress += task.getPlayers().get(player.getUniqueId());
        }

        return formatProgress(progress / (double) totalProgress);
    }

    public String formatProgress(double progress) {
        StringBuilder s = new StringBuilder();

        int req;

        if (progress >= 0.7) {
            s.append("§a");
            req = 3;
        } else if (progress >= 0.3) {
            s.append("§e");
            req = 7;
        } else {
            s.append("§c");
            req = 10 - ((int) (10 * progress));
        }


        int oReq = (int) (progress * 10);

        for (int i = 0; i < oReq; i++)
            s.append("\u2758");

        s.append("§7");

        for (int i = 0; i < req; i++)
            s.append("\u2758");

        return s.toString();
    }

    private boolean completedAllTasks(UUID uuid) {
        for (Task task : tasks) {
            if (!task.isCompleted(uuid)) {
                return false;
            }
        }

        return true;
    }
}
