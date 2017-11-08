package com.medievallords.carbyne.gear.effects;

import com.medievallords.carbyne.utils.ParticleEffect;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

/**
 * Created by Calvin on 1/11/2017
 * for the Carbyne-Gear project.
 */
public class PotionEffects {

    public static void runEffect(Player player) {
        for (PotionEffect effects : player.getActivePotionEffects()) {
            switch (effects.getType().getName().toLowerCase()) {
                case "increase_damage":
                    switch (effects.getAmplifier()) {
                        case 0:
                            ParticleEffect.FLAME.display(0.2F, 0.0F, 0.2F, 0.05F, 15, player.getLocation().add(0.0, 0.5, 0.0), 20);
                            break;
                        case 1:
                            ParticleEffect.LAVA.display(0.2F, 0.0F, 0.2F, 0.05F, 2, player.getLocation().add(0.0, -0.5, 0.0), 20);
                            break;
                    }
                    break;
                case "speed":
                    switch (effects.getAmplifier()) {
                        case 0:
                            ParticleEffect.SMOKE_NORMAL.display(0.2F, 0.0F, 0.2F, 0.1F, 15, player.getLocation().add(0.0, 0.3, 0.0), 20);
                            break;
                        case 1:
                            ParticleEffect.SMOKE_LARGE.display(0.2F, 0.0F, 0.2F, 0.1F, 15, player.getLocation().add(0.0, 0.3, 0.0), 20);
                            break;
                    }
                    break;
                case "regeneration":
                    ParticleEffect.HEART.display(0.2F, 0.0F, 0.2F, 0.1F, 1, player.getLocation().add(0.0, 0.3, 0.0), 20);
            }
        }
    }
}
