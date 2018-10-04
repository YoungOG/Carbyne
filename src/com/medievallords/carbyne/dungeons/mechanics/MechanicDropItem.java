package com.medievallords.carbyne.dungeons.mechanics;

import com.medievallords.carbyne.dungeons.mechanics.targeters.instances.ITargetLocation;
import com.medievallords.carbyne.utils.DungeonLineConfig;
import com.medievallords.carbyne.utils.ItemBuilder;
import io.lumine.xikage.mythicmobs.MythicMobs;
import io.lumine.xikage.mythicmobs.adapters.bukkit.BukkitAdapter;
import io.lumine.xikage.mythicmobs.items.MythicItem;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class MechanicDropItem extends Mechanic implements ITargetLocation {

    private List<ItemStack> items = new ArrayList<>();

    public MechanicDropItem(String type, DungeonLineConfig lineConfig) {
        super(type,lineConfig);

        String[] splitItems = lineConfig.getString("items", "DIAMOND:1,").split(",");
        for (String key : splitItems) {
            int amount = 1;
            String[] amountSplit = key.split(":");
            String itemName = amountSplit[0];
            if (amountSplit.length == 2) {
                try {
                    amount = Integer.parseInt(amountSplit[1]);
                } catch (NumberFormatException e) {

                }
            }


            Optional<MythicItem> mythicItem = MythicMobs.inst().getItemManager().getItem(itemName);
            if (mythicItem.isPresent()) {
                items.add(BukkitAdapter.adapt(mythicItem.get().generateItemStack(amount)));
            } else {
                Material material = Material.getMaterial(itemName.toUpperCase());
                if (Arrays.asList(Material.values()).contains(material)) {
                    items.add(new ItemBuilder(material).amount(amount).build());
                }
            }
        }
    }

    @Override
    public boolean cast(Location location) {
        World world = location.getWorld();
        for (ItemStack itemStack : items) {
            world.dropItem(location, itemStack);
        }

        return true;
    }
}
