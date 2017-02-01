package com.medievallords.carbyne.skill;

import com.medievallords.carbyne.Carbyne;
import de.slikey.effectlib.EffectManager;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class FireStorm {

    private EffectManager effectManager = Carbyne.getInstance().getEffectManager();
    private final FireworkEffect fe = FireworkEffect.builder().flicker(true).with(Type.BURST).withColor(Color.RED).withColor(Color.ORANGE).trail(false).build();

//    public boolean run(Player caster) {
//        Location center = caster.getEyeLocation();
//
//        WarpEffect warpEffect = new WarpEffect(effectManager);
//        warpEffect.setEntity(caster);
//        warpEffect.iterations = 10 * 20;
//        warpEffect.start();
//
//        CircleEffect circleEffect = new CircleEffect(effectManager);
//        circleEffect.setLocation(center);
//        circleEffect.iterations = 34;
//        circleEffect.particle = ParticleEffect.FLAME;
//        circleEffect.radius = 7;
//        circleEffect.start();
//
//        new BukkitRunnable() {
//            @Override
//            public void run() {
//                strikeLigtning(center, 15, 7, caster);
//            }
//        }.runTaskLater(Carbyne.getInstance(), 34L);
//
//        return true;
//    }

    public void strikeLigtning(Location center, int height, int radius, Player caster) {
        int radiusX = radius;
        int radiusZ = radius;

        radiusX += 0.5;
        radiusZ += 0.5;

        double invRadiusX = 1 / radiusX;
        double invRadiusZ = 1 / radiusZ;

        //int ceilRadiusX = (int) Math.ceil(radiusX);
        //int ceilRadiusZ = (int) Math.ceil(radiusZ);

        for (Entity en : center.getWorld().getEntities()) {
            if (en == null) {
                continue;
            }

            if (!(en instanceof LivingEntity)) {
                continue;
            }

            if (en instanceof Player) {
                Player le = (Player) en;

                if (le.equals(caster)) {
                    continue;
                }
            }

            LivingEntity le = (LivingEntity) en;
            Bukkit.broadcastMessage("1");
            Location loc = le.getLocation();
            Bukkit.broadcastMessage(center.getBlockX() + "");
//            if (center.getBlockX() + radius - loc.getBlockX() >= 0 && center.getBlockX() + radius - loc.getBlockX() < radius * 2) {
//                Bukkit.broadcastMessage("2");
//                if (center.getBlockZ() + radius - loc.getBlockZ() >= 0 && center.getBlockZ() + radius - loc.getBlockZ() < radius * 2) {
//                    Bukkit.broadcastMessage("3");
//                    if (center.getBlockY() + height - loc.getBlockY() < center.getBlockY() + height) {
//                        Bukkit.broadcastMessage("4");
//                        if (lengthSq((center.getBlockX() + radius - loc.getBlockX() + 1) * invRadiusX, (center.getBlockZ() + radius - loc.getBlockZ() + 1) * invRadiusZ) <= 1) {
//                            Bukkit.broadcastMessage("5");
//                            center.getWorld().strikeLightningEffect(le.getLocation());
//
//                            le.setHealth(le.getHealth() / 2);
//                            le.setFireTicks(20 * 6);
//                            le.damage(0f);
//
//                            FlameEffect flameEffect = new FlameEffect(effectManager);
//                            flameEffect.setEntity(le);
//                            flameEffect.iterations = 20 * 6;
//                            flameEffect.start();
//
//                            new BukkitRunnable() {
//                                @Override
//                                public void run() {
//                                    SmokeEffect smokeEffect = new SmokeEffect(effectManager);
//                                    smokeEffect.setEntity(le);
//                                    smokeEffect.iterations = 5 * 20;
//                                    smokeEffect.start();
//                                }
//                            }.runTaskLater(Carbyne.getInstance(), 20 * 6);
//                        }
//                    }
//                }
//            }
        }
    }

    public String getName() {
        return "Fire Storm";
    }

    private double lengthSq(double x, double z) {
        return (x * x) + (z * z);
    }
}
