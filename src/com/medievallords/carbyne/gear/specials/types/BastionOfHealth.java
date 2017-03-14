package com.medievallords.carbyne.gear.specials.types;

import com.medievallords.carbyne.gear.specials.Special;
import com.medievallords.carbyne.gear.types.carbyne.CarbyneWeapon;
import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * Created by xwiena22 on 2017-03-13.
 */
public class BastionOfHealth implements Special{
    @Override
    public String getSpecialName() {
        return null;
    }

    @Override
    public int getRequiredCharge() {
        return 0;
    }

    @Override
    public void callSpecial(Player caster, Location centerLocation, CarbyneWeapon carbyneWeapon) {

    }

    public void healPlayer(Player player) {

    }
}
