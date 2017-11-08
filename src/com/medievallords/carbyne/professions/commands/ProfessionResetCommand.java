package com.medievallords.carbyne.professions.commands;

import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.profiles.Profile;
import com.medievallords.carbyne.utils.DateUtil;
import com.medievallords.carbyne.utils.ItemBuilder;
import com.medievallords.carbyne.utils.MessageManager;
import com.medievallords.carbyne.utils.command.BaseCommand;
import com.medievallords.carbyne.utils.command.Command;
import com.medievallords.carbyne.utils.command.CommandArgs;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 * Created by WE on 2017-08-14.
 */
public class ProfessionResetCommand extends BaseCommand implements Listener {

    @Command(name = "profession.reset", aliases = {"prof.reset"}, inGameOnly = true)
    public void onCommand(CommandArgs commandArgs) {
        Player player = commandArgs.getPlayer();
        String[] args = commandArgs.getArgs();

        if (args.length == 1 && player.hasPermission("carbyne.commands.professions.admin")) {
            Player playerTo = Bukkit.getServer().getPlayer(args[0]);
            if (playerTo == null) {
                OfflinePlayer offlinePlayer = Bukkit.getServer().getOfflinePlayer(args[0]);

                if (offlinePlayer == null) {
                    MessageManager.sendMessage(player, "&cCould not find that ");
                    return;
                } else {
                    Profile profile = Carbyne.getInstance().getProfileManager().getProfile(playerTo.getUniqueId());
                    if (profile == null) {
                        MessageManager.sendMessage(player, "&cAn error has occurred");
                        return;
                    }

                    profile.setProfession(null);
                    profile.setProfessionLevel(1);
                    profile.setProfessionProgress(0);
                    MessageManager.sendMessage(player, "&aPlayers profession reset");
                }
            } else {
                Profile profile = Carbyne.getInstance().getProfileManager().getProfile(playerTo.getUniqueId());
                if (profile == null) {
                    MessageManager.sendMessage(player, "&cAn error has occurred");
                    return;
                }

                profile.setProfession(null);
                profile.setProfessionLevel(1);
                profile.setProfessionProgress(0);
                MessageManager.sendMessage(player, "&aPlayers profession reset");
            }

            return;
        }

        Profile profile = Carbyne.getInstance().getProfileManager().getProfile(player.getUniqueId());
        if (profile == null) {
            MessageManager.sendMessage(player, "&cAn error has occurred");
            return;
        }

        if (profile.getProfessionResetCooldown() > System.currentTimeMillis()) {
            String msg = "&cYou have to wait &b" + DateUtil.readableTime(profile.getProfessionResetCooldown() - System.currentTimeMillis()) + "&c before you can reset your profession";
            MessageManager.sendMessage(player, msg);
            return;
        }

        openConfirmGUI(player);
    }

    public void openConfirmGUI(Player player) {
        Inventory confirm = Bukkit.getServer().createInventory(null, 9, "§cAre you SURE you want to reset?");
        confirm.setItem(1, new ItemBuilder(Material.WOOL).durability(5).data(5).name("&aYES").addLore("&eClick this to accept").addLore("&cALL your stats will reset").build());
        confirm.setItem(7, new ItemBuilder(Material.WOOL).durability(14).data(14).name("&4NO").addLore("&eClick this to deny").build());
        player.openInventory(confirm);
    }

    @EventHandler
    public void onAcceptOrDeny(InventoryClickEvent event) {
        if (!event.getInventory().getName().equals("§cAre you SURE you want to reset?")) {
            return;
        }

        event.setCancelled(true);

        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getWhoClicked();

        ItemStack itemStack = event.getCurrentItem();
        if (itemStack == null || itemStack.getType() != Material.WOOL) {
            return;
        }

        Profile profile = Carbyne.getInstance().getProfileManager().getProfile(player.getUniqueId());
        if (profile == null) {
            MessageManager.sendMessage(player, "&cAn error has occurred");
            return;
        }

        if (itemStack.getDurability() == 5) {
            profile.setProfession(null);
            profile.setProfessionLevel(1);
            profile.setProfessionProgress(0);
            player.closeInventory();
            profile.setProfessionResetCooldown(System.currentTimeMillis() + 86400000);
            MessageManager.sendMessage(player, "&cYour profession stats has been reset");
        } else if (itemStack.getDurability() == 14) {
            player.closeInventory();
            MessageManager.sendMessage(player, "&cCancelled");
        }
    }
}
