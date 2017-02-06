package com.medievallords.carbyne.scoreboard;

import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.gear.GearManager;
import com.medievallords.carbyne.heartbeat.Heartbeat;
import com.medievallords.carbyne.heartbeat.HeartbeatTask;
import com.medievallords.carbyne.utils.Cooldowns;
import com.medievallords.carbyne.utils.ScoreboardUtil;
import net.minelink.ctplus.CombatTagPlus;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;

import java.util.ArrayList;

/**
 * Created by Calvin on 2/2/2017
 * for the Carbyne-Gear project.
 */
public class PlayerScoreboard implements HeartbeatTask {

    private Carbyne carbyne = Carbyne.getInstance();
    private GearManager gearManager = carbyne.getGearManager();

    private Player player;
    private Scoreboard scoreboard;
    private Heartbeat heartbeat;

    public PlayerScoreboard(Player player, Scoreboard scoreboard) {
        this.player = player;
        this.scoreboard = scoreboard;
        this.heartbeat = new Heartbeat(this,10L);

        heartbeat.start();
    }

    @Override
    public boolean heartbeat() {
        ArrayList<String> lines = new ArrayList<>();

        lines.add(ChatColor.translateAlternateColorCodes('&', "&b&lMedieval Lords &6[Map 1]"));
        lines.add(ChatColor.translateAlternateColorCodes('&', "&aBalance: &b" + carbyne.getEconomy().getBalance(player.getName())));

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
                lines.add(ChatColor.translateAlternateColorCodes('&', "&cCombat Timer: &b" + combatTagPlus.getTagManager().getTag(player.getUniqueId()).getTagDuration()));
            }
        }

        if (Cooldowns.getCooldown(player.getUniqueId(), "EnderPearlCooldown") > 1) {
            lines.add("   ");
            lines.add(ChatColor.translateAlternateColorCodes('&', "&cEnderpearl CD: &b" + (Cooldowns.getCooldown(player.getUniqueId(), "EnderPearlCooldown") / 1000)));
        }

        if (Cooldowns.getCooldown(player.getUniqueId(), "GodAppleCooldown") > 1) {
            lines.add("    ");
            lines.add(ChatColor.translateAlternateColorCodes('&', "&cGod Apple CD: &b" + (Cooldowns.getCooldown(player.getUniqueId(), "GodAppleCooldown") / 1000)));
        }

        ScoreboardUtil.unrankedSidebarDisplay(player, scoreboard, lines.toArray(new String[lines.size()]));

        return player.isOnline();
    }
}
