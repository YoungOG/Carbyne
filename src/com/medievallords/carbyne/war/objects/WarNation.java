package com.medievallords.carbyne.war.objects;

import com.palmergames.bukkit.towny.object.Nation;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Williams on 2017-08-21
 * for the Carbyne project.
 */
@Getter
@Setter
public class WarNation {

    private Nation nation;
    private int DTR;

    private ItemStack banner;

    public WarNation(Nation nation, int DTR) {
        this.nation = nation;
        this.DTR = DTR;
    }

    public ItemStack getBanner() {
        return banner;
    }
}
