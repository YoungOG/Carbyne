package com.medievallords.carbyne.crates.animations;

import com.boydti.fawe.util.TaskManager;
import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.crates.rewards.Reward;
import com.medievallords.carbyne.utils.InstantFirework;
import com.medievallords.carbyne.utils.InventoryWorkaround;
import com.medievallords.carbyne.utils.ItemBuilder;
import com.medievallords.carbyne.utils.MessageManager;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;

@Getter
@Setter
public class MemoryData {

    private Player player;
    private Inventory inventory;
    private HashMap<Integer, Reward> memoryRewards = new HashMap<>();
    private int chosenRewards = 0, allowed;
    private Set<Integer> clickedSlots = new HashSet<>();
    private List<Reward> chosenItems = new ArrayList<>();
    private List<Reward> rewardsToGive = new ArrayList<>();
    private boolean await = false;

    public MemoryData(Player player, Inventory inventory, int allowed) {
        this.player = player;
        this.inventory = inventory;
        this.allowed = allowed;
    }

    public void handleClick(int slot, MemoryAnimation ma) {
        if (clickedSlots.contains(slot)) {
            return;
        }

        if (await) {
            return;
        }

        Reward reward = memoryRewards.get(slot);
        inventory.setItem(slot, reward.getItem(false));
        clickedSlots.add(slot);

        chosenRewards++;
        InstantFirework.spawn(player.getLocation(), FireworkEffect.builder().withColor(Color.BLUE).withColor(Color.RED).withColor(Color.GREEN).withColor(Color.YELLOW).withColor(Color.PURPLE).withFade(Color.TEAL).build());

        if (chosenItems.contains(reward)) {
            rewardsToGive.add(reward);
        } else {
            chosenItems.add(reward);
        }

        if (chosenRewards < allowed) {
            ItemStack pane = new ItemBuilder(Material.STAINED_GLASS_PANE).durability(2).addEnchantment(Enchantment.DURABILITY, 10).name("&6&lSelect &7&l" + (allowed - chosenRewards) + " &6&lmore item(s).")
                    .addLore("").addLore("&7If you pair two of the").addLore("&7item, you will get that item.").build();
            for (int i = 0; i < inventory.getSize(); i++) {
                if (inventory.getItem(i).getType().equals(Material.STAINED_GLASS_PANE)) {
                    inventory.setItem(i, pane);
                }
            }
        } else {
            ItemStack pane = new ItemBuilder(Material.STAINED_GLASS_PANE).durability(2).addEnchantment(Enchantment.DURABILITY, 10).name("&c&lYou may not select more items.").build();
            for (int i = 0; i < inventory.getSize(); i++) {
                if (inventory.getItem(i).getType().equals(Material.STAINED_GLASS_PANE)) {
                    inventory.setItem(i, pane);
                }
            }
        }

        if (chosenRewards >= allowed) {
            TaskManager.IMP.later(new Runnable() {
                @Override
                public void run() {
                    if (!rewardsToGive.isEmpty()) {
                        MessageManager.sendMessage(player, "&aYou have paired an item!");
                        for (Reward toGive : rewardsToGive) {
                            if (!toGive.isDisplayItemOnly()) {
                                Map<Integer, ItemStack> leftovers = InventoryWorkaround.addItems(player.getInventory(), toGive.getItem(false));

                                if (leftovers.values().size() > 0) {
                                    MessageManager.sendMessage(player, "&cAn item could not fit in your inventory, and was dropped to the ground.");

                                    for (ItemStack itemStack : leftovers.values()) {
                                        Item item = player.getWorld().dropItem(player.getEyeLocation(), itemStack);
                                        item.setVelocity(player.getEyeLocation().getDirection().normalize().multiply(1));
                                    }
                                }
                            }

                            ItemStack itemStack = toGive.getItem(false);
                            for (String cmd : toGive.getCommands()) {
                                Carbyne.getInstance().getServer().dispatchCommand(Carbyne.getInstance().getServer().getConsoleSender(), cmd.replace("/", "").replace("%player%", player.getName()).replace("%item%", (itemStack.getItemMeta().hasDisplayName() ? itemStack.getItemMeta().getDisplayName() : itemStack.getType().name())));
                            }
                        }

                        for (int i = 0; i < inventory.getSize(); i++) {
                            inventory.setItem(i, memoryRewards.get(i).getItem(false));
                        }

                        if (MemoryData.this.equals(ma.getMemoryDataMap().get(player.getUniqueId()))) {
                            ma.getMemoryDataMap().remove(player.getUniqueId());
                        }
                    } else {
                        MessageManager.sendMessage(player, "&aBetter luck next time!");
                        for (int i = 0; i < inventory.getSize(); i++) {
                            inventory.setItem(i, memoryRewards.get(i).getItem(false));
                        }

                        if (MemoryData.this.equals(ma.getMemoryDataMap().get(player.getUniqueId()))) {
                            ma.getMemoryDataMap().remove(player.getUniqueId());
                        }
                    }
                }
            }, 30);
        }
    }
}
