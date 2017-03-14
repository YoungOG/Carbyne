package com.medievallords.carbyne.utils;

import com.bizarrealex.aether.scoreboard.Board;
import com.bizarrealex.aether.scoreboard.BoardAdapter;
import com.bizarrealex.aether.scoreboard.cooldown.BoardCooldown;
import com.bizarrealex.aether.scoreboard.cooldown.BoardFormat;
import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.gear.GearManager;
import net.minelink.ctplus.CombatTagPlus;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Created by Calvin on 3/13/2017
 * for the Carbyne project.
 */
public class CarbyneBoardAdapter implements BoardAdapter {

    private Carbyne main;

    public CarbyneBoardAdapter(Carbyne main) {
        this.main = main;
    }

    @Override
    public String getTitle(Player player) {
        return "&b&lMedieval Lords";
    }

    @Override
    public List<String> getScoreboard(Player player, Board board, Set<BoardCooldown> set) {
        GearManager gearManager = main.getGearManager();
        ArrayList<String> lines = new ArrayList<>();
        Iterator itr = set.iterator();

        lines.add(ChatColor.translateAlternateColorCodes('&', "&aBalance: &b" + main.getEconomy().getBalance(player.getName())));

        if ((player.getItemInHand() != null && gearManager.getDurability(player.getItemInHand()) > -1.0) || (player.getInventory().getHelmet() != null && gearManager.getDurability(player.getInventory().getHelmet()) > -1.0) || (player.getInventory().getChestplate() != null && gearManager.getDurability(player.getInventory().getChestplate()) > -1.0) || (player.getInventory().getLeggings() != null && gearManager.getDurability(player.getInventory().getLeggings()) > -1.0) || (player.getInventory().getBoots() != null && gearManager.getDurability(player.getInventory().getBoots()) > -1.0)) {
            lines.add(" ");
            lines.add(ChatColor.translateAlternateColorCodes('&', "&6Durability:"));

            if (player.getInventory().getHelmet() != null && gearManager.getDurability(player.getInventory().getHelmet()) > -1) {
                lines.add(ChatColor.translateAlternateColorCodes('&', " &eHelmet: &b" + gearManager.getDurability(player.getInventory().getHelmet())));
            }

            if (player.getInventory().getChestplate() != null && gearManager.getDurability(player.getInventory().getChestplate()) > -1) {
                lines.add(ChatColor.translateAlternateColorCodes('&', " &eChestplayerlate: &b" + gearManager.getDurability(player.getInventory().getChestplate())));
            }

            if (player.getInventory().getLeggings() != null && gearManager.getDurability(player.getInventory().getLeggings()) > -1) {
                lines.add(ChatColor.translateAlternateColorCodes('&', " &eLeggings: &b" + gearManager.getDurability(player.getInventory().getLeggings())));
            }

            if (player.getInventory().getBoots() != null && gearManager.getDurability(player.getInventory().getBoots()) > -1) {
                lines.add(ChatColor.translateAlternateColorCodes('&', " &eBoots: &b" + gearManager.getDurability(player.getInventory().getBoots())));
            }
        }

        if (player.getItemInHand() != null && gearManager.getDurability(player.getItemInHand()) > -1) {
            lines.add(ChatColor.translateAlternateColorCodes('&', " &eHand: &b" + gearManager.getDurability(player.getItemInHand())));
        }

        if (Carbyne.getInstance().isCombatTagPlusEnabled()) {
            CombatTagPlus combatTagPlus = Carbyne.getInstance().getCombatTagPlus();

            if (combatTagPlus.getTagManager().isTagged(player.getUniqueId())) {
                lines.add("  ");

                while (itr.hasNext()) {
                    BoardCooldown cooldown = (BoardCooldown) itr.next();

                    if (cooldown.getId().equals("combattag")) {
                        lines.add("&cCombat Timer: &b" + cooldown.getFormattedString(BoardFormat.SECONDS));
                    }
                }
            }
        }

        while (itr.hasNext()) {
            BoardCooldown cooldown = (BoardCooldown) itr.next();

            if (cooldown.getId().equals("combattag")) {
                if (Carbyne.getInstance().isCombatTagPlusEnabled()) {
                    CombatTagPlus combatTagPlus = Carbyne.getInstance().getCombatTagPlus();

                    if (combatTagPlus.getTagManager().isTagged(player.getUniqueId())) {
                        lines.add("  ");
                        lines.add("&cCombat Timer: &b" + cooldown.getFormattedString(BoardFormat.SECONDS));
                    }
                }
            }

            if (cooldown.getId().equals("enderpearl")) {
                lines.add("   ");
                lines.add(ChatColor.translateAlternateColorCodes('&', "&cEnderpearl CD: &b" + cooldown.getFormattedString(BoardFormat.SECONDS)));
            }

            if (cooldown.getId().equals("godapple")) {
                lines.add("   ");
                lines.add(ChatColor.translateAlternateColorCodes('&', "&cGod Apple CD: &b" + cooldown.getFormattedString(BoardFormat.MINUTES)));
            }
        }

        return lines;
    }
}
