package com.medievallords.carbyne.crates.animations;

import com.boydti.fawe.util.TaskManager;
import com.medievallords.carbyne.crates.rewards.Reward;
import com.medievallords.carbyne.gear.types.CarbyneGear;
import com.medievallords.carbyne.gear.types.GearState;
import com.medievallords.carbyne.utils.*;
import lombok.Getter;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;

@Getter
public class SelectData {

    public SelectAnimation sa;

    private Player player;
    private Inventory inventory;
    private HashMap<Integer, Reward> selectRewards = new HashMap<>();
    private int chosenRewards = 0, allowed;
    private Set<Integer> clickedSlots = new HashSet<>();
    private List<ItemStack> chosenItems = new ArrayList<>();
    private boolean await = false;
    private double velD = 0;
    private double adder = 0.7 / (double) StaticClasses.gearManager.getHiddenCarbyneGear().size();

    public SelectData(Player player, Inventory inventory, int allowed, SelectAnimation sa) {
        this.player = player;
        this.inventory = inventory;
        this.allowed = allowed;
        this.sa = sa;
    }

    private void updateRare(int slot, double delay, ItemStack itemStack, Iterator<CarbyneGear> i) {
        if (delay < 30) {
            velD += adder;
        } else {
            velD = 0;
        }

        if (i.hasNext()) {
            TaskManager.IMP.later(new Runnable() {
                @Override
                public void run() {
                    if (delay > 7) {
                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_PLING, 1, 1);
                    }

                    inventory.setItem(slot, itemStack);
                    updateRare(slot, delay + velD, i.next().getItem(false), i);
                }
            }, (int) delay);
        } else {
            inventory.setItem(slot, itemStack);
            await = false;
            handleFinished();
            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&8[&d&lCrate&8] &a" + player.getName() + " &7has found " + itemStack.getItemMeta().getDisplayName() + " &7in a crate!"));
        }
    }

    public void handleClick(int slot) {
        if (clickedSlots.contains(slot)) {
            return;
        }

        if (await) {
            return;
        }

        clickedSlots.add(slot);
        InstantFirework.spawn(player.getLocation(), FireworkEffect.builder().withColor(Color.BLUE).withColor(Color.RED).withColor(Color.GREEN).withColor(Color.YELLOW).withColor(Color.PURPLE).withFade(Color.TEAL).build());
        chosenRewards++;
        Reward reward = selectRewards.get(slot);
        ItemStack rewardItem = reward.getItem(false);
        CarbyneGear gear = StaticClasses.gearManager.getCarbyneGear(rewardItem);
        chosenItems.add(rewardItem);
        if (gear != null && gear.getState().equals(GearState.HIDDEN)) {
            await = true;
            List<CarbyneGear> listOfGear = StaticClasses.gearManager.getHiddenCarbyneGear();
            Collections.shuffle(listOfGear);
            listOfGear.remove(gear);
            listOfGear.add(gear);
            Iterator<CarbyneGear> iterator = listOfGear.iterator();
            velD = 0;
            updateRare(slot, 3, iterator.next().getItem(false), iterator);
        } else {
            inventory.setItem(slot, rewardItem);
            handleFinished();
        }

    }

    private void handleFinished() {
        if (chosenRewards < allowed) {
            ItemStack pane = new ItemBuilder(Material.STAINED_GLASS_PANE).durability(2).addEnchantment(Enchantment.DURABILITY, 10).name("&6&lSelect &7&l" + (allowed - chosenRewards) + " &6&lmore item(s).")
                    .addLore("").build();
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
                    if (!chosenItems.isEmpty()) {
                        for (ItemStack toGive : chosenItems) {
                            Map<Integer, ItemStack> leftovers = InventoryWorkaround.addItems(player.getInventory(), toGive);
                            if (leftovers.values().size() > 0) {
                                MessageManager.sendMessage(player, "&cAn item could not fit in your inventory, and was dropped to the ground.");
                                for (ItemStack itemStack : leftovers.values()) {
                                    Item item = player.getWorld().dropItem(player.getEyeLocation(), itemStack);
                                    item.setVelocity(player.getEyeLocation().getDirection().normalize().multiply(1));
                                }
                            }
                        }

                        for (int i = 0; i < inventory.getSize(); i++) {
                            if (inventory.getItem(i).getType().equals(Material.STAINED_GLASS_PANE)) {
                                inventory.setItem(i, selectRewards.get(i).getItem(false));
                            }
                        }

                        if (SelectData.this.equals(sa.getSelectDataMap().get(player.getUniqueId()))) {
                            sa.getSelectDataMap().remove(player.getUniqueId());
                        }
                    }
                }
            }, 30);
        }
    }

}
