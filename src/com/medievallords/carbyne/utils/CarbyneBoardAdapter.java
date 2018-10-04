package com.medievallords.carbyne.utils;

import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.gear.types.carbyne.CarbyneWeapon;
import com.medievallords.carbyne.listeners.CombatTagListeners;
import com.medievallords.carbyne.profiles.Profile;
import com.medievallords.carbyne.squads.Squad;
import com.medievallords.carbyne.squads.SquadType;
import com.medievallords.carbyne.staff.StaffManager;
import com.medievallords.carbyne.utils.scoreboard.Board;
import com.medievallords.carbyne.utils.scoreboard.BoardCooldown;
import com.medievallords.carbyne.utils.scoreboard.BoardFormat;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

/**
 * Created by Calvin on 3/13/2017
 * <p>
 * for the Carbyne project.
 */
public class CarbyneBoardAdapter {

    //private Carbyne main;
    private String title = "&b&lMedieval Lords";
    //private ProfileManager profileManager;
    //private SquadManager squadManager;
    //private ColorScrollPlus colorScrollPlus;

    public CarbyneBoardAdapter() {
//        this.colorScrollPlus = new ColorScrollPlus(ChatColor.AQUA, "Medieval Lords", "&f", "&b", "&f", false, false, ColorScrollPlus.ScrollType.FORWARD);
//
//        new BukkitRunnable() {
//            @Override
//            public void run() {
//                if (colorScrollPlus.getScrollType() == ColorScrollPlus.ScrollType.FORWARD) {
//                    if (colorScrollPlus.getPosition() >= colorScrollPlus.getString().length())
//                        colorScrollPlus.setScrollType(ColorScrollPlus.ScrollType.BACKWARD);
//                } else if (colorScrollPlus.getPosition() <= -1)
//                    colorScrollPlus.setScrollType(ColorScrollPlus.ScrollType.FORWARD);
//
//                setTitle(colorScrollPlus.next());
//            }
//        }.runTaskTimerAsynchronously(Carbyne.getInstance(), 0L, 3);
    }

    public String getTitle(Player player) {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<String> getScoreboard(Profile profile, Player player, Board board, Set<BoardCooldown> set) {
        ArrayList<String> lines = new ArrayList<>();
        Iterator itr = set.iterator();

        if (StaticClasses.staffManager.getStaffModePlayers().contains(player.getUniqueId()))
            return staffScoreboard(player);

        if (player.hasPermission("carbyne.staff.staffmode"))
            lines.add("&7Vanished&c: " + StaticClasses.staffManager.isVanished(player));

        if (profile.getRemainingPvPTime() > 1) {
            lines.add("         ");
            lines.add("&dProtection&7: " + formatTime(profile.getRemainingPvPTime()));
        }

        PlayerHealth playerHealth = PlayerHealth.getPlayerHealth(player.getUniqueId());

        if (player.hasPermission("carbyne.staff.staffmode"))
            lines.add(" ");

        lines.add("&aHealth&7: " + (int) playerHealth.getHealth());
        lines.add(" ");
        lines.add("&aStamina&7: " + playerHealth.getStamina());

        if (StaticClasses.squadManager.getSquad(player.getUniqueId()) != null) {
            Squad squad = StaticClasses.squadManager.getSquad(player.getUniqueId());

            if (squad.getMembers().size() > 0) {
                lines.add(" ");
                lines.add("&dSquad [&7" + (squad.getType() == SquadType.PUBLIC ? "&7" : "&c") + squad.getType().toString().toLowerCase().substring(0, 1).toUpperCase() + squad.getType().toString().toLowerCase().substring(1) + "&d]:");

                for (UUID member : squad.getAllPlayers())
                    if (!member.equals(player.getUniqueId()))
                        lines.add(" &7" + (squad.getLeader() == member ? "&l" : "") + (Bukkit.getPlayer(member).getName().length() > 7 ? Bukkit.getPlayer(member).getName().substring(0, 8) : Bukkit.getPlayer(member).getName()) + "    " + formatHealth(Bukkit.getPlayer(member).getHealth()));
            }
        }

        if (board.getCooldown("target") == null) {
            if (StaticClasses.squadManager.getSquad(player.getUniqueId()) != null) {
                Squad squad = StaticClasses.squadManager.getSquad(player.getUniqueId());

                if (squad.getTargetUUID() != null)
                    squad.setTargetUUID(null);

                if (squad.getTargetSquad() != null)
                    squad.setTargetSquad(null);
            }
        }

        ItemStack hand = player.getInventory().getItemInMainHand();
        if (hand != null && hand.getType() != Material.DRAGON_EGG) {
            CarbyneWeapon carbyneWeapon = StaticClasses.gearManager.getCarbyneWeapon(hand);

            if (carbyneWeapon != null && carbyneWeapon.getSpecial() != null) {
                lines.add("    ");
                lines.add("&dCharge&7: " + formatCharge(carbyneWeapon.getSpecialCharge(hand), carbyneWeapon.getSpecial().getRequiredCharge()));
            }
        }

        try {
            while (itr.hasNext()) {
                BoardCooldown cooldown = (BoardCooldown) itr.next();

                if (cooldown.getId().equals("logout")) {
                    lines.add("       ");
                    lines.add("&dLogout&7: " + cooldown.getFormattedString(BoardFormat.SECONDS));
                }

                if (cooldown.getId().equals("target"))
                    if (StaticClasses.squadManager.getSquad(player.getUniqueId()) != null) {
                        Squad squad = StaticClasses.squadManager.getSquad(player.getUniqueId());

                        if (squad.getTargetUUID() != null || squad.getTargetSquad() != null) {
                            lines.add("     ");
                            lines.add("&dTarget&7: " + (squad.getTargetSquad() != null ? Bukkit.getPlayer(squad.getTargetSquad().getLeader()).getName() + "'s Squad" : Bukkit.getPlayer(squad.getTargetUUID()).getName()));
                        }
                    }

                if (cooldown.getId().equals("combattag"))
                    if (CombatTagListeners.isInCombat(player.getUniqueId())) {
                        lines.add("  ");
                        lines.add("&dCombat Timer&7: " + cooldown.getFormattedString(BoardFormat.SECONDS));
                    }

                if (cooldown.getId().equals("potion")) {
                    lines.add("   ");
                    lines.add("&dPotion&7: " + cooldown.getFormattedString(BoardFormat.SECONDS));
                }

                if (cooldown.getId().equals("enderpearl")) {
                    lines.add("   ");
                    lines.add("&dEnderpearl&7: " + cooldown.getFormattedString(BoardFormat.SECONDS));
                }

                if (cooldown.getId().equals("skill")) {
                    lines.add("   ");
                    lines.add("&dSkill&7: " + cooldown.getFormattedString(BoardFormat.SECONDS));
                }

                if (cooldown.getId().equals("special")) {
                    lines.add("   ");
                    lines.add("&dSpecial&7: " + cooldown.getFormattedString(BoardFormat.SECONDS));
                }
            }
        } catch (Exception e) {
            Carbyne.getInstance().getLogger().log(Level.WARNING, e.getMessage());
        }

        if (lines.size() >= 1) {
            lines.add(0, "&7&m-------------------");
            lines.add(" ");
            lines.add("&7&owww.playminecraft.org");
        }

        return lines;
    }

    private List<String> staffScoreboard(Player player) {
        StaffManager staffManager = StaticClasses.staffManager;
        ArrayList<String> lines = new ArrayList<>();
        lines.add("&7Vanished: &c" + staffManager.isVanished(player));
        lines.add("    ");

        lines.add("&7Chat Muted: &a" + (staffManager.isChatMuted() ? "&a" + staffManager.isChatMuted() : "&c" + staffManager.isChatMuted()));
        lines.add("&7Chat Speed: &a" + staffManager.getSlowChatTime() + "s");
        lines.add("    ");

        lines.add("&7Flying: " + (player.isFlying() ? "&atrue" : "&cfalse"));

        if (lines.size() >= 1) {
            lines.add(0, "&7&m-------------------");
            lines.add(" ");
            lines.add("&7&owww.playminecraft.org");
        }

        return lines;
    }

    String formatCharge(int charge, int required) {
        double part = required / 10;
        double at = part;
        StringBuilder s = new StringBuilder();

        while (at <= charge) {
            s.append("\u2758");
            at += part;
        }

        int length = 10 - s.length();

        if (length <= 0) {
            s.insert(0, "&a");
            return s.toString();
        } else if (length <= 4)
            s.insert(0, "&a");
        else if (length <= 7)
            s.insert(0, "&e");
        else
            s.insert(0, "&c");

        s.append("&7");

        for (int i = 0; i < length; i++)
            s.append("\u2758");

        return s.toString();
    }

    String formatHealth(double health) {
        double hearts = (health / 2);
        DecimalFormat format = new DecimalFormat("#");

        if (hearts <= 10 && hearts >= 7.5)
            return String.format(" &a%s \u2764", format.format(hearts));
        else if (hearts <= 7.5 && hearts >= 5)
            return String.format(" &e%s \u2764", format.format(hearts));
        else if (hearts <= 5 && hearts >= 2.5)
            return String.format(" &6%s \u2764", format.format(hearts));
        else
            return String.format(" &c%s \u2764", format.format(hearts));
    }

    String formatTime(long millis) {
        return String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(millis) % TimeUnit.HOURS.toMinutes(1),
                TimeUnit.MILLISECONDS.toSeconds(millis) % TimeUnit.MINUTES.toSeconds(1));
    }
}
