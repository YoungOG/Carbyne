package com.medievallords.carbyne.scoreboard;

import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.gear.GearManager;
import com.medievallords.carbyne.utils.Cooldowns;
import com.medievallords.carbyne.utils.PlayerUtility;
import com.medievallords.carbyne.utils.ScoreboardUtil;
import net.minelink.ctplus.CombatTagPlus;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class ScoreboardHandler implements Listener {

    private Carbyne carbyne = Carbyne.getInstance();
    private GearManager gearManager = carbyne.getGearManager();

    public HashMap<UUID, BukkitTask> playerTasks = new HashMap<>();

    public ScoreboardHandler() {
        Bukkit.getPluginManager().registerEvents(this, carbyne);

        for (Player all : PlayerUtility.getOnlinePlayers()) {
            addTask(all);
        }
    }

    public void addTask(Player player) {
        playerTasks.put(player.getUniqueId(), new BukkitRunnable() {
            @Override
            public void run() {
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

                ScoreboardUtil.unrankedSidebarDisplay(player, lines.toArray(new String[lines.size()]));
            }
        }.runTaskTimerAsynchronously(carbyne, 0L, 10L));
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();

        if (!playerTasks.containsKey(player.getUniqueId())) {
            addTask(player);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Player player = e.getPlayer();

        if (playerTasks.containsKey(player.getUniqueId())) {
            playerTasks.get(player.getUniqueId()).cancel();
            playerTasks.remove(player.getUniqueId());
        }
    }

    public HashMap<UUID, BukkitTask> getPlayerTasks() {
        return playerTasks;
    }
}