package com.medievallords.carbyne.quests.rewards;

import com.medievallords.carbyne.utils.InventoryWorkaround;
import com.medievallords.carbyne.utils.MessageManager;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;

public class QuestReward {

    private List<String> commands;
    @Getter
    private List<QuestRewardItem> rewardItems;

    public QuestReward(List<String> commands, List<QuestRewardItem> rewardItems) {
        this.rewardItems = rewardItems;
        this.commands = commands;
    }

    public void giveReward(Player player) {
        for (QuestRewardItem questRewardItem : rewardItems) {
            ItemStack toGive = questRewardItem.getItem();
            Map<Integer, ItemStack> leftovers = InventoryWorkaround.addItems(player.getInventory(), toGive);
            if (leftovers.values().size() > 0) {
                MessageManager.sendMessage(player, "&cAn item could not fit in your inventory, and was dropped to the ground.");
                for (ItemStack itemStack : leftovers.values()) {
                    Item item = player.getWorld().dropItem(player.getEyeLocation(), itemStack);
                    item.setVelocity(player.getEyeLocation().getDirection().normalize().multiply(1));
                }
            }
        }

        for (String command : commands) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("%player%", player.getName()));
        }
    }
}
