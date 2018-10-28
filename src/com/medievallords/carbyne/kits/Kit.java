package com.medievallords.carbyne.kits;

import com.medievallords.carbyne.profiles.Profile;
import com.medievallords.carbyne.profiles.ProfileManager;
import com.medievallords.carbyne.utils.DateUtil;
import com.medievallords.carbyne.utils.MessageManager;
import com.medievallords.carbyne.utils.PlayerUtility;
import com.medievallords.carbyne.utils.StaticClasses;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Kit {

    private static final ProfileManager profileManager = StaticClasses.profileManager;
    private String name, materialData;
    private List<ItemStack> contents = new ArrayList<>();
    private int cost, delay;
    private boolean isHidden = false;

    public Kit(String name) {
        this.name = name;
    }

    public void apply(Player player) {
        if (!player.hasPermission("carbyne.kits." + getName()) && !player.hasPermission("carbyne.kits.*")) {
            MessageManager.sendMessage(player, "&cYou do not have permission to use this kit.");
            return;
        }

        Profile profile = profileManager.getProfile(player.getUniqueId());
        long nextUse = getNextUse(profile);

        if (nextUse == 0L) {
            if (getContents() == null || getContents().isEmpty())
                return;

            if (!player.hasPermission("carbyne.bypasskitcost"))
                if (cost > 0 && profile.getKitPoints() < cost) {
                    MessageManager.sendMessage(player, "&cYou do not have enough kit points to purchase this kit.");
                    return;
                }

            if (PlayerUtility.checkSlotsAvailable(player.getInventory()) < getContents().size()) {
                MessageManager.sendMessage(player, "&cYou do not have enough inventory space.");
                return;
            }

            for (ItemStack is : getContents()) {
                if (is == null)
                    continue;

                player.getInventory().addItem(is);
            }

            player.updateInventory();

            if (!player.hasPermission("carbyne.bypasskitcooldown"))
                profile.getUsedKits().put(name, System.currentTimeMillis() + ((long) delay * 1000));

            if (!player.hasPermission("carbyne.bypassonetimeuse"))
                if (delay == -1)
                    profile.getUsedKits().put(name, -1L);

            if (cost > 0 && !player.hasPermission("carbyne.bypasskitcost")) {
                profile.setKitPoints(profileManager.getProfile(player.getUniqueId()).getKitPoints() - cost);
                MessageManager.sendMessage(player, "&7You have spent &c" + cost + " Kit Points&7, and received the &b" + name + " &7kit.");
            } else
                MessageManager.sendMessage(player, "&7You have received the &b" + name + " &7kit.");
        } else if (nextUse < 0L)
            MessageManager.sendMessage(player, "&cYou cannot use that kit again.");
        else
            MessageManager.sendMessage(player, "&cYou cannot use kit again for &4" + DateUtil.readableTime(nextUse, true) + "&c.");
    }

    public long getNextUse(Profile profile) {
        long lastUse = profile.getUsedKits().containsKey(name) ? profile.getUsedKits().get(name) : 0;
        if (lastUse == -1)
            return -1;

        long cooldown = lastUse - System.currentTimeMillis();
        if (cooldown < 0)
            return 0;

        return cooldown;
    }
}
