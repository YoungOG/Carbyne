package com.medievallords.carbyne.duels.duel;

import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.duels.arena.Arena;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * Created by xwiena22 on 2017-03-something
 * for the Carbyne project.
 */
@Getter
@Setter
public abstract class Duel {

    private static Carbyne main = Carbyne.getInstance();

    private static final int COUNTDOWN_DURATION = 5;

    private Arena arena;
    private DuelStage duelStage;
    private List<Item> drops;
    private long startTimeMillis;
    private List<UUID> playersAlive = new ArrayList<>();
    private int bets;
    private HashMap<UUID, Integer> playerBets = new HashMap<>();
    public int taskId;
    private boolean ended = false;

    public Duel(Arena arena) {
        this.arena = arena;
        this.duelStage = DuelStage.COUNTING_DOWN;
        this.drops = new ArrayList<>();
    }

    public abstract void start();

    public abstract void countdown();

    public abstract void end(UUID winnerId);

    public abstract void check();

    public abstract void task();

    public abstract void stopTask();

}
