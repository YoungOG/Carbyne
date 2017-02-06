package com.medievallords.carbyne.utils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Scoreboard;

import java.util.Arrays;

public class ScoreboardUtil {

    public static String[] cutUnranked(String[] content) {
        String[] elements = Arrays.copyOf(content, 16);

        if (elements[0] == null)
            elements[0] = "Unamed board";

        if (elements[0].length() > 32)
            elements[0] = elements[0].substring(0, 32);

        for (int i = 1; i < elements.length; i++)
            if (elements[i] != null)
                if (elements[i].length() > 40)
                    elements[i] = elements[i].substring(0, 40);

        return elements;
    }

    public static boolean unrankedSidebarDisplay(Player p, Scoreboard scoreboard, String[] elements) {
        elements = cutUnranked(elements);
        if(p.getName().contains("Young")){
            Bukkit.broadcastMessage(scoreboard.toString() + ":" + scoreboard.getClass().toString());
        }
        try {

            //            if (p.getScoreboard() == null || p.getScoreboard() == Bukkit.getScoreboardManager().getMainScoreboard() || p.getScoreboard().getObjectives().size() != 1) {
//
//            }

            p.setScoreboard(scoreboard);

            if (scoreboard.getObjective(p.getUniqueId().toString().substring(0, 16)) == null) {
                scoreboard.registerNewObjective(p.getUniqueId().toString().substring(0, 16), "dummy");
                scoreboard.getObjective(p.getUniqueId().toString().substring(0, 16)).setDisplaySlot(DisplaySlot.SIDEBAR);
            }

            scoreboard.getObjective(DisplaySlot.SIDEBAR).setDisplayName(elements[0]);

            for (int i = 1; i < elements.length; i++)
                if (elements[i] != null)
                    if (scoreboard.getObjective(DisplaySlot.SIDEBAR).getScore(elements[i]).getScore() != 16 - i) {
                        scoreboard.getObjective(DisplaySlot.SIDEBAR).getScore(elements[i]).setScore(16 - i);
                        for (String string : scoreboard.getEntries())
                            if (scoreboard.getObjective(p.getUniqueId().toString().substring(0, 16)).getScore(string).getScore() == 16 - i)
                                if (!string.equals(elements[i]))
                                    scoreboard.resetScores(string);

                    }

            for (String entry : scoreboard.getEntries()) {
                boolean toErase = true;
                for (String element : elements) {
                    if (element != null && element.equals(entry) && scoreboard.getObjective(p.getUniqueId().toString().substring(0, 16)).getScore(entry).getScore() == 16 - Arrays.asList(elements).indexOf(element)) {
                        toErase = false;
                        break;
                    }
                }

                if (toErase)
                    scoreboard.resetScores(entry);

            }

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
//
//    public static boolean unrankedSidebarDisplay(Collection<Player> players, Scoreboard scoreboard, String[] elements) {
//        for (Player player : players)
//            if (!unrankedSidebarDisplay(player, scoreboard, elements))
//                return false;
//
//        return true;
//    }
//
//    public static boolean unrankedSidebarDisplay(Collection<Player> players, String[] elements, Scoreboard board) {
//        try {
//            String objName = "COLLAB-SB-WINTER";
//
//            if (board == null)
//                board = Bukkit.getScoreboardManager().getNewScoreboard();
//
//            elements = cutUnranked(elements);
//
//            for (Player player : players)
//                if (player.getScoreboard() != board)
//                    player.setScoreboard(board);
//
//            if (board.getObjective(objName) == null) {
//                board.registerNewObjective(objName, "dummy");
//                board.getObjective(objName).setDisplaySlot(DisplaySlot.SIDEBAR);
//            }
//
//
//            board.getObjective(DisplaySlot.SIDEBAR).setDisplayName(elements[0]);
//
//            for (int i = 1; i < elements.length; i++)
//                if (elements[i] != null && board.getObjective(DisplaySlot.SIDEBAR).getScore(elements[i]).getScore() != 16 - i) {
//                    board.getObjective(DisplaySlot.SIDEBAR).getScore(elements[i]).setScore(16 - i);
//                    for (String string : board.getEntries())
//                        if (board.getObjective(objName).getScore(string).getScore() == 16 - i)
//                            if (!string.equals(elements[i]))
//                                board.resetScores(string);
//
//                }
//
//            for (String entry : board.getEntries()) {
//                boolean toErase = true;
//                for (String element : elements) {
//                    if (element != null && element.equals(entry) && board.getObjective(objName).getScore(entry).getScore() == 16 - Arrays.asList(elements).indexOf(element)) {
//                        toErase = false;
//                        break;
//                    }
//                }
//
//                if (toErase)
//                    board.resetScores(entry);
//
//            }
//
//            return true;
//        } catch (Exception e) {
//            e.printStackTrace();
//            return false;
//        }
//    }
}
