package com.medievallords.carbyne.war;

import com.medievallords.carbyne.utils.ItemBuilder;
import com.medievallords.carbyne.war.objects.WarNation;
import com.medievallords.carbyne.war.objects.WarPlot;
import com.medievallords.carbyne.war.objects.request.WarRequest;
import com.palmergames.bukkit.towny.object.TownBlock;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * Created by Williams on 2017-08-21
 * for the Carbyne project.
 */
@Getter
@Setter
public class WarManager {

    private List<WarPlot> warPlots = new ArrayList<>();
    private List<WarRequest> requests = new ArrayList<>();

    private HashMap<UUID, WarPlot> enteringPlayers = new HashMap<>();

    private List<WarNation> warNations = new ArrayList<>();

    public WarManager() {

    }

    public void load() {

    }

    public void save() {

    }

    public void requestWar(WarRequest warRequest) {

    }

    public ItemStack getWarBanner() {
        return new ItemBuilder(Material.BANNER).build();
    }

    public WarPlot getWarPlot(TownBlock plot) {
        for (int i = 0; i < warPlots.size(); i++) {
            WarPlot warPlot = warPlots.get(i);
            if (warPlot != null && warPlot.getPlot().equals(plot)) {
                return warPlot;
            }
        }

        return null;
    }

    public WarNation getWarNation(String name) {
        for (int i = 0; i < warNations.size(); i++) {
            WarNation warNation = warNations.get(i);
            if (warNation != null && warNation.getNation().getName() == name) {
                return warNation;
            }
        }

        return null;
    }
}
