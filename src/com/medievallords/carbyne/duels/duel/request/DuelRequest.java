package com.medievallords.carbyne.duels.duel.request;

import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.duels.arena.Arena;
import com.medievallords.carbyne.economy.objects.Account;
import com.medievallords.carbyne.squads.Squad;
import com.medievallords.carbyne.utils.JSONMessage;
import com.medievallords.carbyne.utils.MessageManager;
import com.medievallords.carbyne.utils.StaticClasses;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * Created by Calvin on 3/17/2017
 * for the Carbyne project.
 */
@Getter
@Setter
public class DuelRequest  {

    public static List<DuelRequest> requests = new ArrayList<>();

    private HashMap<UUID, Boolean> players = new HashMap<>();
    private HashMap<UUID, Boolean> playersSquadFight = new HashMap<>();
    private boolean squadFight = false;
    private Arena arena;
    private HashMap<UUID, Integer> bets = new HashMap<>();

}
