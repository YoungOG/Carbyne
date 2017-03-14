package com.medievallords.carbyne.leaderboards;

import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.leaderboards.board.Board;
import com.medievallords.carbyne.leaderboards.profile.StatProfile;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * Created by Calvin on 1/23/2017
 * for the Carbyne-Gear project.
 */
public class LeaderboardManager {

    private Carbyne carbyne = Carbyne.getInstance();

    private ArrayList<Board> boards = new ArrayList<>();
    private HashSet<StatProfile> statProfiles = new HashSet<>();


}