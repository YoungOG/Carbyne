package com.medievallords.carbyne.crates.rewards;

import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.crates.Crate;
import com.medievallords.carbyne.utils.InventoryWorkaround;
import com.medievallords.carbyne.utils.MessageManager;
import org.bukkit.Sound;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class RewardGenerator {

    private Carbyne main = Carbyne.getInstance();
    private HashMap<UUID, BukkitTask> inventoryTID = new HashMap<>();
    private HashMap<UUID, BukkitTask> startDelayTID = new HashMap<>();
    private HashMap<UUID, BukkitTask> finishDelayTID = new HashMap<>();

    private Crate crate;
    private Player player;
    private Inventory inventory;
    private List<ItemStack> items;
    private Reward chosenReward;
    private int time;
    private int index;
    private int slot;
    private boolean ran;

    public RewardGenerator(Crate crate, Player player, Inventory inventory, int slot, int delay, int time, List<ItemStack> items, Reward chosenReward) {
        this.crate = crate;
        this.player = player;
        this.inventory = inventory;
        this.slot = slot;
        this.time = time;
        this.index = 0;
        this.items = items;
        this.chosenReward = chosenReward;

        startDelayTID.put(player.getUniqueId(), new BukkitRunnable() {
            @Override
            public void run() {
                startScheduler(player);
            }
        }.runTaskLater(main, delay));
    }

    public void startScheduler(Player player) {
        inventory.setItem(slot, get());

        inventoryTID.put(player.getUniqueId(), new BukkitRunnable() {
            @Override
            public void run() {
                next();

                ItemStack item = inventory.getItem(slot);

                if (item == null || prev().equals(item)) {
                    inventory.setItem(slot, get());

                    if (main.getCrateManager().getSounds().size() > 0) {
                        for (Sound s : main.getCrateManager().getSounds().keySet()) {
                            player.playSound(player.getLocation(), s, main.getCrateManager().getSounds().get(s)[0].floatValue(), main.getCrateManager().getSounds().get(s)[1].floatValue());
                        }
                    }
                }
            }
        }.runTaskTimerAsynchronously(Carbyne.getInstance(), 0L, main.getConfig().getInt("crates.crate-opening-speed")));

        finishDelayTID.put(player.getUniqueId(), new BukkitRunnable() {
            @Override
            public void run() {
                ran = true;
                stopScheduler(player, false);
            }
        }.runTaskLater(main, time * 20L));
    }

    public void stopScheduler(Player player, boolean cancelled) {
        if (inventoryTID.containsKey(player.getUniqueId())) {
            inventoryTID.get(player.getUniqueId()).cancel();
            inventoryTID.remove(player.getUniqueId());
        }

        if (finishDelayTID.containsKey(player.getUniqueId())) {
            finishDelayTID.get(player.getUniqueId()).cancel();
            finishDelayTID.remove(player.getUniqueId());
        }

        if (startDelayTID.containsKey(player.getUniqueId())) {
            startDelayTID.get(player.getUniqueId()).cancel();
            startDelayTID.remove(player.getUniqueId());
        }

        ItemStack rewardItem = chosenReward.getItem(false);

        if (!cancelled) {
            inventory.setItem(slot, rewardItem);

            new BukkitRunnable() {
                @Override
                public void run() {
                    if (!chosenReward.isDisplayItemOnly()) {
                        Map<Integer, ItemStack> leftovers = InventoryWorkaround.addItems(player.getInventory(), rewardItem);

                        if (leftovers.values().size() > 0) {
                            MessageManager.sendMessage(player, "&cAn item could not fit in your inventory, and was dropped to the ground.");

                            for (ItemStack itemStack : leftovers.values()) {
                                Item item = player.getWorld().dropItem(player.getEyeLocation(), itemStack);
                                item.setVelocity(player.getEyeLocation().getDirection().normalize().multiply(1));
                            }

                            return;
                        }
                    } else {
                        if (chosenReward.getCommands().size() > 0) {
                            for (String cmd : chosenReward.getCommands()) {
                                main.getServer().dispatchCommand(main.getServer().getConsoleSender(), cmd.replace("/", "").replace("%player%", player.getName()));
                            }
                        }
                    }

                    if (crate.getCrateOpeners().keySet().contains(player.getUniqueId())) {
                        if (crate.getCrateOpenersAmount().keySet().contains(player.getUniqueId())) {
                            if (crate.getCrateOpenersAmount().get(player.getUniqueId()) > 0) {
                                crate.getCrateOpenersAmount().put(player.getUniqueId(), crate.getCrateOpenersAmount().get(player.getUniqueId()) - 1);
                            } else {
                                crate.getCrateOpeners().remove(player.getUniqueId());
                                crate.getCrateOpenersAmount().remove(player.getUniqueId());
                                player.closeInventory();
                            }
                        }
                    }
                }
            }.runTaskLater(main, 3 * 20L);
        } else {
            inventory.clear();

            if (player.isOnline()) {
                if (!chosenReward.isDisplayItemOnly()) {
                    Map<Integer, ItemStack> leftovers = InventoryWorkaround.addItems(player.getInventory(), rewardItem);

                    if (leftovers.values().size() > 0) {
                        MessageManager.sendMessage(player, "&cAn item could not fit in your inventory, and was dropped to the ground.");

                        for (ItemStack itemStack : leftovers.values()) {
                            Item item = player.getWorld().dropItem(player.getEyeLocation(), itemStack);
                            item.setVelocity(player.getEyeLocation().getDirection().normalize().multiply(1));
                        }

                        return;
                    }

                    player.updateInventory();
                } else {
                    if (chosenReward.getCommands().size() > 0) {
                        for (String cmd : chosenReward.getCommands()) {
                            main.getServer().dispatchCommand(main.getServer().getConsoleSender(), cmd.replace("/", "").replace("%player%", player.getName()));
                        }
                    }
                }

                if (crate.getCrateOpeners().keySet().contains(player.getUniqueId())) {
                    crate.getCrateOpeners().remove(player.getUniqueId());
                }

                if (crate.getCrateOpenersAmount().keySet().contains(player.getUniqueId())) {
                    crate.getCrateOpenersAmount().remove(player.getUniqueId());
                }
            }
        }
    }

    private void next() {
        index++;
        index %= items.size();
    }

    public ItemStack get() {
        return items.get(index);
    }

    public ItemStack prev() {
        return items.get((items.size() + index - 1) % items.size());
    }

    public boolean hasRan() {
        return ran;
    }
}