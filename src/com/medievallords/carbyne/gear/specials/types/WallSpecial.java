package com.medievallords.carbyne.gear.specials.types;

import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.gear.specials.Special;
import com.medievallords.carbyne.utils.ParticleEffect;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Williams on 2017-03-19.
 * for the Carbyne project.
 */
public class WallSpecial implements Special {


    private int radius = 7;

    @Override
    public int getRequiredCharge() {
        return 50;
    }

    @Override
    public String getSpecialName() {
        return "Wall_Special";
    }

    @Override
    public void callSpecial(Player caster) {
        Location loc = caster.getLocation();
        List<Block> blocks = new ArrayList<>();
        for (int i = 0; i < 70; i++) {
            double x = Math.sin(i) * radius;
            double z = Math.cos(i) * radius;
            for (int y = -3; y < 5; y++) {
                loc.add(x, y, z);
                for (Entity entity : loc.getWorld().getNearbyEntities(loc, 50, 50, 50)) {
                    if (entity instanceof Player) {
                        Player player = (Player) entity;
                        blocks.add(loc.getBlock());
                        player.sendBlockChange(loc, Material.COBBLESTONE, (byte) 0);
                    }
                }
                ParticleEffect.SPELL_MOB.display(0f, 0f, 0f, 0f, 1, loc, 20, false);
                loc.subtract(x, y, z);
            }
        }
        for (int i = 0; i < 70; i++) {
            double x = Math.sin(i) * 8;
            double z = Math.cos(i) * 8;
            for (int y = -3; y < Math.random() * 7; y++) {
                loc.add(x, y, z);
                for (Entity entity : loc.getWorld().getNearbyEntities(loc, 50, 50, 50)) {
                    if (entity instanceof Player) {
                        Player player = (Player) entity;
                        blocks.add(loc.getBlock());
                        player.sendBlockChange(loc, Material.COBBLESTONE, (byte) 0);
                    }
                }
                loc.subtract(x, y, z);
            }
        }
        for (int i = 0; i < 36; i+=2) {
            double x = Math.sin(i) * 6;
            double z = Math.cos(i) * 6;
            for (int y = -3; y < Math.random() * 7; y++) {
                loc.add(x, y, z);
                for (Entity entity : loc.getWorld().getNearbyEntities(loc, 50, 50, 50)) {
                    if (entity instanceof Player) {
                        Player player = (Player) entity;
                        blocks.add(loc.getBlock());
                        player.sendBlockChange(loc, Material.COBBLESTONE, (byte) 0);
                    }
                }
                loc.subtract(x, y, z);
            }
        }
        new BukkitRunnable() {
            public void run() {

                new BukkitRunnable() {
                    public void run() {
                        if (blocks.isEmpty()) {
                            this.cancel();
                            return;
                        }
                        if (blocks.size() <= 10) {
                            for (Block block : blocks) {
                                for (Player player : Bukkit.getServer().getOnlinePlayers()) {
                                    player.sendBlockChange(block.getLocation(), block.getType(), block.getData());
                                }
                            }
                            this.cancel();
                            return;
                        }
                        for (int i = 0; i<4; i++) {
                            Block block = blocks.get(0);
                            for (Player player : Bukkit.getServer().getOnlinePlayers()) {
                                player.sendBlockChange(block.getLocation(), block.getType(), block.getData());
                            }
                            blocks.remove(block);
                        }
                    }
                }.runTaskTimer(Carbyne.getInstance(), 0, 1);
            }
        }.runTaskLater(Carbyne.getInstance(), 150);

        broadcastMessage("&7[&aCarbyne&7]: &5" + caster.getName() + " &ahas casted the &c" + getSpecialName().replace("_", " ") + " &aspecial!", caster.getLocation(), 50);
    }

}
