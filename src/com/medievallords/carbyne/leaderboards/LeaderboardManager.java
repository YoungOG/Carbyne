package com.medievallords.carbyne.leaderboards;

import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.leaderboards.board.Board;
import com.medievallords.carbyne.leaderboards.profile.StatProfile;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

/**
 * Created by Calvin on 1/23/2017
 * for the Carbyne-Gear project.
 */
public class LeaderboardManager {

    private Carbyne carbyne = Carbyne.getInstance();

    private ArrayList<Board> boards = new ArrayList<>();
    private HashSet<StatProfile> statProfiles = new HashSet<>();

    public LeaderboardManager() {

    }

    public void load() {

    }

    public Board getBoard(String boardId) {
        for (Board board : boards) {
            if (board.getBoardId().equalsIgnoreCase(boardId)) {
                return board;
            }
        }

        return null;
    }

    public Board getBoard(Location location) {
        for (Board board : boards) {
            if (board.getBoardLocationSet() != null) {
                ArrayList<Location> locations = new ArrayList<>();

                locations.add(board.getBoardLocationSet().getPrimarySignLocation());

                Collections.addAll(locations, board.getBoardLocationSet().getSignLocations());
                Collections.addAll(locations, board.getBoardLocationSet().getHeadLocations());

                for (Location loc : locations) {
                    if (location == loc) {
                        return board;
                    }
                }
            }
        }

        return null;
    }
}