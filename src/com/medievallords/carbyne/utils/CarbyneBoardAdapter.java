package com.medievallords.carbyne.utils;

import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.gear.types.carbyne.CarbyneWeapon;
import com.medievallords.carbyne.listeners.CombatTagListeners;
import com.medievallords.carbyne.profiles.Profile;
import com.medievallords.carbyne.profiles.ProfileManager;
import com.medievallords.carbyne.squads.Squad;
import com.medievallords.carbyne.squads.SquadManager;
import com.medievallords.carbyne.squads.SquadType;
import com.medievallords.carbyne.staff.StaffManager;
import com.medievallords.carbyne.utils.scoreboard.Board;
import com.medievallords.carbyne.utils.scoreboard.BoardCooldown;
import com.medievallords.carbyne.utils.scoreboard.BoardFormat;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

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

    private Carbyne main;
    private ProfileManager profileManager;
    private SquadManager squadManager;

    public CarbyneBoardAdapter(Carbyne main) {
        this.main = main;
        this.profileManager = main.getProfileManager();
        this.squadManager = main.getSquadManager();
    }

    public String getTitle(Player player) {
        return "&b&lMedieval Lords";
    }

    public List<String> getScoreboard(Player player, Board board, Set<BoardCooldown> set) {
        ArrayList<String> lines = new ArrayList<>();
        Iterator itr = set.iterator();

        if (main.getStaffManager().getStaffModePlayers().contains(player.getUniqueId())) {
            return staffScoreboard(player);
        }

        if (player.hasPermission("carbyne.staff.staffmode")) {
            lines.add("&7Vanished&c: " + main.getStaffManager().isVanished(player));
        }

        Profile profile = profileManager.getProfile(player.getUniqueId());
        if (profile.getRemainingPvPTime() > 1) {
            lines.add("         ");
            lines.add("&dProtection&7: " + formatTime(profile.getRemainingPvPTime()));
        }

//        if (!CombatTagListeners.isInCombat(player.getUniqueId())) {
//            if (Account.getAccount(player.getName()) != null) {
//                lines.add("&aBalance&7: " + MessageManager.format(Account.getAccount(player.getName()).getBalance()));
//            }
//
//            if (main.getMissionsManager().getUuidMissions().containsKey(player.getUniqueId())) {
//                ArrayList<Mission> activeMissions = new ArrayList<>();
//                Mission[] missions = main.getMissionsManager().getUuidMissions().get(player.getUniqueId()).getCurrentMissions();
//
//                for (Mission mission : missions) {
//                    if (mission != null && !mission.isActive())
//                        continue;
//
//                    activeMissions.add(mission);
//                }
//
//                if (activeMissions.size() > 0) {
//                    lines.add(" ");
//                    lines.add("&aActive Missions&7: " + activeMissions.size());
//                }
//            }
//
//            if (profileManager.getProfile(player.getUniqueId()).getProfession() != null) {
//                Profile profile = profileManager.getProfile(player.getUniqueId());
//                lines.add(" ");
//                lines.add("&aProfession&7: " + profile.getProfession().getName());
//                lines.add("  &aLevel&7: " + profile.getProfessionLevel());
//                lines.add("  &aProgress&7: " + profile.getProfessionProgress());
//            }
//        }
//
//        if (gearManager.getDamageReduction(player) > 0.0 || gearManager.getProtectionReduction(player) > 0.0) {
//            lines.add(" ");
//            lines.add("&aDamage Reduction&7: " + doubleFormat((gearManager.getDamageReduction(player) + gearManager.getProtectionReduction(player)) * 100.0) + "%");
//        }

        if (squadManager.getSquad(player.getUniqueId()) != null) {
            Squad squad = squadManager.getSquad(player.getUniqueId());

            if (squad.getMembers().size() > 0) {
                lines.add(" ");
                lines.add("&dSquad [&7" + (squad.getType() == SquadType.PUBLIC ? "&7" : "&c") + squad.getType().toString().toLowerCase().substring(0, 1).toUpperCase() + squad.getType().toString().toLowerCase().substring(1) + "&d]:");

                for (UUID member : squad.getAllPlayers())
                    if (!member.equals(player.getUniqueId()))
                        lines.add(" &7" + (squad.getLeader() == member ? "&l" : "") + (Bukkit.getPlayer(member).getName().length() > 7 ? Bukkit.getPlayer(member).getName().substring(0, 8) : Bukkit.getPlayer(member).getName()) + "    " + formatHealth(Bukkit.getPlayer(member).getHealth()));
            }
        }

        /*Collection<DropPoint> conquerPoints = Collections2.filter(main.getConquerPointManager().getConquerPoints(),
                conquerPoint -> (conquerPoint != null && conquerPoint.getState() == DropPointState.CAPTURING));

        if (conquerPoints.size() > 0) {
            lines.add(" ");
            lines.add("&4Conquer Points:");

            for (DropPoint conquerPoint : conquerPoints)
                lines.add("&b- &5" + conquerPoint.getId().replace("_", " ").substring(0, Math.min(conquerPoint.getId().replace("_", "").length(), 5)) + "   &c[" + "&4" + MessageManager.convertSecondsToMinutes(conquerPoint.getCaptureTime()) + "&c]");
        }*/

        if (board.getCooldown("target") == null) {
            if (squadManager.getSquad(player.getUniqueId()) != null) {
                Squad squad = squadManager.getSquad(player.getUniqueId());

                if (squad.getTargetUUID() != null)
                    squad.setTargetUUID(null);

                if (squad.getTargetSquad() != null)
                    squad.setTargetSquad(null);
            }
        }

        ItemStack hand = player.getInventory().getItemInHand();
        if (hand != null) {
            CarbyneWeapon carbyneWeapon = main.getGearManager().getCarbyneWeapon(hand);

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

                if (cooldown.getId().equals("target")) {
                    if (squadManager.getSquad(player.getUniqueId()) != null) {
                        Squad squad = squadManager.getSquad(player.getUniqueId());

                        if (squad.getTargetUUID() != null || squad.getTargetSquad() != null) {
                            lines.add("     ");
                            lines.add("&dTarget&7: " + (squad.getTargetSquad() != null ? Bukkit.getPlayer(squad.getTargetSquad().getLeader()).getName() + "'s Squad" : Bukkit.getPlayer(squad.getTargetUUID()).getName()));
                        }
                    }
                }

                if (cooldown.getId().equals("combattag")) {
                    if (CombatTagListeners.isInCombat(player.getUniqueId())) {
                        lines.add("  ");
                        lines.add("&dCombat Timer&7: " + cooldown.getFormattedString(BoardFormat.SECONDS));
                    }
                }

                if (cooldown.getId().equals("special")) {
                    lines.add("   ");
                    lines.add("&dSpecial&7: " + cooldown.getFormattedString(BoardFormat.SECONDS));
                }

                if (cooldown.getId().equals("enderpearl")) {
                    lines.add("   ");
                    lines.add("&dEnderpearl&7: " + cooldown.getFormattedString(BoardFormat.SECONDS));
                }

                if (cooldown.getId().equals("godapple")) {
                    lines.add("   ");
                    lines.add("&dGod Apple&7: " + cooldown.getFormattedString(BoardFormat.MINUTES));
                }
            }
        } catch (Exception e) {
            main.getLogger().log(Level.WARNING, e.getMessage());
        }

        if (lines.size() >= 1) {
            lines.add(0, "&7&m-------------------");
            lines.add("&7&m-------------------");
        }

        return lines;
    }

    private List<String> staffScoreboard(Player player) {
        StaffManager staffManager = main.getStaffManager();
        ArrayList<String> lines = new ArrayList<>();
        lines.add("&7Vanished: &c" + staffManager.isVanished(player));
        lines.add("    ");

        lines.add("&7Chat Muted: &a" + (staffManager.isChatMuted() ? "&a" + staffManager.isChatMuted() : "&c" + staffManager.isChatMuted()));
        lines.add("&7Chat Speed: &a" + staffManager.getSlowChatTime() + "s");
        lines.add("    ");

        lines.add("&7Flying: " + (player.isFlying() ? "&atrue" : "&cfalse"));

        if (lines.size() >= 1) {
            lines.add(0, "&7&m-------------------");
            lines.add("&7&m-------------------");
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
        double hearts = (health / 2) / 5;
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
