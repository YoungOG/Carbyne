package com.medievallords.carbyne.utils;

import com.bizarrealex.aether.scoreboard.Board;
import com.bizarrealex.aether.scoreboard.BoardAdapter;
import com.bizarrealex.aether.scoreboard.cooldown.BoardCooldown;
import com.bizarrealex.aether.scoreboard.cooldown.BoardFormat;
import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.gear.GearManager;
import com.medievallords.carbyne.squads.Squad;
import com.medievallords.carbyne.squads.SquadManager;
import net.minelink.ctplus.CombatTagPlus;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;
import java.util.*;

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
        SquadManager squadManager = main.getSquadManager();
        ArrayList<String> lines = new ArrayList<>();
        Iterator itr = set.iterator();

        lines.add("&aBalance: &b" + main.getEconomy().getBalance(Bukkit.getOfflinePlayer(player.getUniqueId())));

        if ((player.getItemInHand() != null && gearManager.getDurability(player.getItemInHand()) > -1.0) || (player.getInventory().getHelmet() != null && gearManager.getDurability(player.getInventory().getHelmet()) > -1.0) || (player.getInventory().getChestplate() != null && gearManager.getDurability(player.getInventory().getChestplate()) > -1.0) || (player.getInventory().getLeggings() != null && gearManager.getDurability(player.getInventory().getLeggings()) > -1.0) || (player.getInventory().getBoots() != null && gearManager.getDurability(player.getInventory().getBoots()) > -1.0)) {
            lines.add(" ");
            lines.add("&6Durability:");

            if (player.getInventory().getHelmet() != null && gearManager.getDurability(player.getInventory().getHelmet()) > -1) {
                lines.add(" &eHelmet: &b" + gearManager.getDurability(player.getInventory().getHelmet()));
            }

            if (player.getInventory().getChestplate() != null && gearManager.getDurability(player.getInventory().getChestplate()) > -1) {
                lines.add(" &eChest&eplate: &b" + gearManager.getDurability(player.getInventory().getChestplate()));
            }

            if (player.getInventory().getLeggings() != null && gearManager.getDurability(player.getInventory().getLeggings()) > -1) {
                lines.add(" &eLeggings: &b" + gearManager.getDurability(player.getInventory().getLeggings()));
            }

            if (player.getInventory().getBoots() != null && gearManager.getDurability(player.getInventory().getBoots()) > -1) {
                lines.add(" &eBoots: &b" + gearManager.getDurability(player.getInventory().getBoots()));
            }
        }

        if (player.getItemInHand() != null && gearManager.getDurability(player.getItemInHand()) > -1) {
            lines.add(" &eHand: &b" + gearManager.getDurability(player.getItemInHand()));
        }

        if (squadManager.getSquad(player.getUniqueId()) != null) {
            Squad squad = squadManager.getSquad(player.getUniqueId());

            if (squad.getMembers().size() > 0) {
                lines.add(" ");
                lines.add("&dSquad:");

                for (UUID member : squad.getAllPlayers()) {
                    if (!member.equals(player.getUniqueId())) {
                        if (squad.getLeader().equals(member)) {
                            lines.add(" &b&l" + Bukkit.getPlayer(member).getName() + " " + formatHealth(Bukkit.getPlayer(member).getHealth()));
                        } else {
                            lines.add(" &b" + Bukkit.getPlayer(member).getName() + " " + formatHealth(Bukkit.getPlayer(member).getHealth()));
                        }
                    }
                }
            }
        }

        while (itr.hasNext()) {
            BoardCooldown cooldown = (BoardCooldown) itr.next();

            if (cooldown.getId().equals("combattag")) {
                if (Carbyne.getInstance().isCombatTagPlusEnabled()) {
                    CombatTagPlus combatTagPlus = main.getCombatTagPlus();

                    if (combatTagPlus.getTagManager().isTagged(player.getUniqueId())) {
                        lines.add("  ");
                        lines.add("&cCombat Timer: &b" + cooldown.getFormattedString(BoardFormat.SECONDS));
                    }
                }
            }

            if (cooldown.getId().equals("special")) {
                lines.add("   ");
                lines.add("&cSpecial CD: &b" + cooldown.getFormattedString(BoardFormat.SECONDS));
            }

            if (cooldown.getId().equals("enderpearl")) {
                lines.add("   ");
                lines.add("&cEnderpearl CD: &b" + cooldown.getFormattedString(BoardFormat.SECONDS));
            }

            if (cooldown.getId().equals("godapple")) {
                lines.add("   ");
                lines.add("&cGod Apple CD: &b" + cooldown.getFormattedString(BoardFormat.MINUTES));
            }
        }

        return lines;
    }

    String formatHealth(double health) {
        double hearts = health / 2;
        DecimalFormat format = new DecimalFormat("#");

        if (hearts <= 10 && hearts >= 7.5) {
            return String.format(" &a%s \u2764", format.format(hearts));
        } else if (hearts <= 7.5 && hearts >= 5) {
            return String.format(" &e%s \u2764", format.format(hearts));
        } else if (hearts <= 5 && hearts >= 2.5) {
            return String.format(" &6%s \u2764", format.format(hearts));
        } else {
            return String.format(" &c%s \u2764", format.format(hearts));
        }
    }
}
