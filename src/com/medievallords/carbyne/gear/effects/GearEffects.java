package com.medievallords.carbyne.gear.effects;

import com.medievallords.carbyne.heartbeat.Heartbeat;
import com.medievallords.carbyne.heartbeat.HeartbeatTask;
import com.medievallords.carbyne.utils.ParticleEffect;
import com.medievallords.carbyne.utils.PlayerUtility;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class GearEffects implements HeartbeatTask {

    private Heartbeat heartbeat;
    private Random random = new Random();

    public GearEffects(){

        if (this.heartbeat == null) {
            this.heartbeat = new Heartbeat(this, 250L);
            heartbeat.start();
        }

        random.doubles(-1.5D, 1.5D);
    }

    public void effectsTick() {
        for (Player all : PlayerUtility.getOnlinePlayers()) {
            if (all.getFireTicks() > 1) {
                ParticleEffect.FLAME.display(0.35f, 0.35f, 0.35f, (float) 0.02, 10, all.getLocation().add(0, 1, 0), 50, false);
            }

            if (all.getItemInHand().containsEnchantment(Enchantment.DAMAGE_ALL) || all.getItemInHand().containsEnchantment(Enchantment.ARROW_DAMAGE)) {
                effectSharpnessPlayers(all);
            }

            if (all.isSprinting()){
                ParticleEffect.FOOTSTEP.display(0.2f, 0f, 0.2f, (float) 0.15, 1, all.getLocation().add(0, 0.02, 0), 50, false);
            }

            if (all.getHealth() < 9){
                bleed(all);
            }

            for (PotionEffect effects : all.getActivePotionEffects()) {
                switch (effects.getType().getName()) {
                    case "WITHER":
                        ParticleEffect.TOWN_AURA.display(0.5f, 0.5f, 0.5f, (float) 0.02, 40, all.getLocation().add(0, 1, 0), 50, false);
                        break;
                    case "POISON":
                        ParticleEffect.BLOCK_CRACK.display(new ParticleEffect.BlockData(Material.LONG_GRASS, (byte) 0), 0.2f, 0.2f, 0.2f, (float) 0.02, 60, all.getLocation().add(0, 0.2, 0), 50, false);
                        break;
                    case "BLINDNESS":
                        ParticleEffect.TOWN_AURA.display(0.1f, 0.1f, 0.1f, (float) 0.01, 60, all.getLocation().add(0, 2, 0), 50, false);
                        break;
                    case "SPEED":
                        ParticleEffect.SMOKE_NORMAL.display(0.2f, 0.1f, 0.2f, (float) 0.03, 30, all.getLocation(), 50, false);
                        break;
                    case "SLOW":
                        ParticleEffect.CLOUD.display(0.2f, -0.2f, 0.2f, (float) 0.0001, 35, all.getLocation().subtract(0, 0.1, 0), 50, false);
                        break;
                    case "REGENERATION":
                        ParticleEffect.HEART.display((float) random.nextDouble(), (float) random.nextDouble(), (float)random.nextDouble(), 0.3F, 3, all.getLocation(), 50, false);
                        break;
                }
            }
        }
    }

    public void effectSharpnessPlayers(Player player) {
        float enchantsO = 0;
        enchantsO += (float) player.getItemInHand().getEnchantmentLevel(Enchantment.DAMAGE_ALL);
        enchantsO += (float) player.getItemInHand().getEnchantmentLevel(Enchantment.KNOCKBACK) * 2;
        enchantsO += (float) player.getItemInHand().getEnchantmentLevel(Enchantment.FIRE_ASPECT) * 2;
        enchantsO += (float) player.getItemInHand().getEnchantmentLevel(Enchantment.ARROW_INFINITE) * 3;
        enchantsO += (float) player.getItemInHand().getEnchantmentLevel(Enchantment.ARROW_FIRE) * 2;
        enchantsO += (float) player.getItemInHand().getEnchantmentLevel(Enchantment.ARROW_DAMAGE);
        float amount = enchantsO;
        ParticleEffect.PORTAL.display(0.2f, 0.2f, 0.2f, (float) 0.15, (int) amount * 5, player.getLocation().add(0, 0.2, 0), 50, false);
    }

    public void effectTeleport(Player player, Location location) {
        List<Player> playerList = new ArrayList<>();
        playerList.add(player);
        for (Entity entity : location.getWorld().getNearbyEntities(location, 30, 30, 30)) {
            if (entity instanceof Player) {
                Player all = (Player) entity;

                if (all.canSee(player)) {
                    playerList.add(all);
                }
            }
        }
        ParticleEffect.VILLAGER_HAPPY.display(getRandomNegPosOne( -.5f), 2.0f, getRandomNegPosOne(-.5f), 1.15f, 5, location, playerList, false);
        ParticleEffect.VILLAGER_HAPPY.display(getRandomNegPosOne(-.4f), 1.5f, getRandomNegPosOne(-.4f), 2.15f, 5, location, playerList, false);
        ParticleEffect.VILLAGER_HAPPY.display(getRandomNegPosOne(-.3f), 1f, getRandomNegPosOne(-.3f), 3.15f, 5, location, playerList, false);
        ParticleEffect.VILLAGER_HAPPY.display(getRandomNegPosOne(-.4f), 0.5f, getRandomNegPosOne(-.4f), 4.15f, 5, location, playerList, false);
        ParticleEffect.VILLAGER_HAPPY.display(getRandomNegPosOne(-.5f), 0.0f, getRandomNegPosOne(-.5f), 5.15f, 5, location, playerList, false);
        ParticleEffect.VILLAGER_HAPPY.display(getRandomNegPosOne( -.5f), 1.9f, getRandomNegPosOne(-.5f), 1.15f, 5, location, playerList, false);
        ParticleEffect.VILLAGER_HAPPY.display(getRandomNegPosOne(-.4f), 1.4f, getRandomNegPosOne(-.4f), 2.15f, 5, location, playerList, false);
        ParticleEffect.VILLAGER_HAPPY.display(getRandomNegPosOne(-.3f), 1f, getRandomNegPosOne(-.3f), 3.15f, 5, location, playerList, false);
        ParticleEffect.VILLAGER_HAPPY.display(getRandomNegPosOne(-.4f), 0.6f, getRandomNegPosOne(-.4f), 4.15f, 5, location, playerList, false);
        ParticleEffect.VILLAGER_HAPPY.display(getRandomNegPosOne(-.5f), 0.0f, getRandomNegPosOne(-.5f), 5.15f, 5, location, playerList, false);
        ParticleEffect.VILLAGER_HAPPY.display(getRandomNegPosOne( -.5f), 1.8f, getRandomNegPosOne(-.5f), 1.15f, 5, location, playerList, false);
        ParticleEffect.VILLAGER_HAPPY.display(getRandomNegPosOne(-.4f), 1.3f, getRandomNegPosOne(-.4f), 2.15f, 5, location, playerList, false);
        ParticleEffect.VILLAGER_HAPPY.display(getRandomNegPosOne(-.3f), 1f, getRandomNegPosOne(-.3f), 3.15f, 5, location, playerList, false);
        ParticleEffect.VILLAGER_HAPPY.display(getRandomNegPosOne(-.4f), 0.7f, getRandomNegPosOne(-.4f), 4.15f, 5, location, playerList, false);
        ParticleEffect.VILLAGER_HAPPY.display(getRandomNegPosOne(-.5f), 0.0f, getRandomNegPosOne(-.5f), 5.15f, 5, location, playerList, false);
        ParticleEffect.VILLAGER_HAPPY.display(getRandomNegPosOne( -.5f), 1.7f, getRandomNegPosOne(-.5f), 1.15f, 5, location, playerList, false);
        ParticleEffect.VILLAGER_HAPPY.display(getRandomNegPosOne(-.4f), 1.2f, getRandomNegPosOne(-.4f), 2.15f, 5, location, playerList, false);
        ParticleEffect.VILLAGER_HAPPY.display(getRandomNegPosOne(-.3f), 1f, getRandomNegPosOne(-.3f), 3.15f, 5, location, playerList, false);
        ParticleEffect.VILLAGER_HAPPY.display(getRandomNegPosOne(-.4f), 0.8f, getRandomNegPosOne(-.4f), 4.15f, 5, location, playerList, false);
        ParticleEffect.VILLAGER_HAPPY.display(getRandomNegPosOne(-.5f), 0.0f, getRandomNegPosOne(-.5f), 5.15f, 5, location, playerList, false);
    }

    public void bleed(Player player) {
        switch((int)(player.getHealth() * 1)){
            case 9:
                ParticleEffect.DRIP_LAVA.display(0.35f, 0.35f, 0.35f, (float) 0.02, 20, player.getLocation(), 50, false);
                break;
            case 8:
                ParticleEffect.DRIP_LAVA.display(0.35f, 0.35f, 0.35f, (float) 0.02, 25, player.getLocation(), 50, false);
                break;
            case 7:
                ParticleEffect.DRIP_LAVA.display(0.35f, 0.35f, 0.35f, (float) 0.02, 30, player.getLocation(), 50, false);
                break;
            case 6:
                ParticleEffect.DRIP_LAVA.display(0.35f, 0.35f, 0.35f, (float) 0.02, 35, player.getLocation(), 50, false);
                break;
            case 5:
                ParticleEffect.DRIP_LAVA.display(0.35f, 0.35f, 0.35f, (float) 0.02, 40, player.getLocation(), 50, false);
                break;
            case 4:
                ParticleEffect.DRIP_LAVA.display(0.35f, 0.35f, 0.35f, (float) 0.02, 45, player.getLocation(), 50, false);
                break;
            case 3:
                ParticleEffect.DRIP_LAVA.display(0.35f, 0.35f, 0.35f, (float) 0.02, 25, player.getLocation(), 50, false);
                ParticleEffect.BLOCK_CRACK.display(new ParticleEffect.BlockData(Material.REDSTONE_BLOCK, (byte) 0), 0.35f, 0.35f, 0.35f, (float) 0.02, 25, player.getLocation(), 50, false);
                break;
            case 2:
                ParticleEffect.DRIP_LAVA.display(0.35f, 0.35f, 0.35f, (float) 0.02, 27, player.getLocation(), 50, false);
                ParticleEffect.BLOCK_CRACK.display(new ParticleEffect.BlockData(Material.REDSTONE_BLOCK, (byte) 0), 0.35f, 0.35f, 0.35f, (float) 0.02, 27, player.getLocation(), 50, false);
                break;
            case 1:
                ParticleEffect.DRIP_LAVA.display(0.35f, 0.35f, 0.35f, (float) 0.02, 30, player.getLocation(), 50, false);
                ParticleEffect.BLOCK_CRACK.display(new ParticleEffect.BlockData(Material.REDSTONE_BLOCK, (byte) 0), 0.35f, 0.35f, 0.35f, (float) 0.02, 30, player.getLocation(), 50, false);
                break;
            default:
                ParticleEffect.DRIP_LAVA.display(0.35f, 0.35f, 0.35f, (float) 0.02, 10, player.getLocation(), 50, false);
                ParticleEffect.BLOCK_CRACK.display(new ParticleEffect.BlockData(Material.REDSTONE_BLOCK, (byte) 0), 0.35f, 0.35f, 0.35f, (float) 0.02, 10, player.getLocation(), 50, false);
                break;
        }
    }
    public float getRandomNegPosOne(float modifier){
        float rand1 = (float)random.nextDouble();
        float rand2 = (float)random.nextDouble() + modifier;
        if(rand1 > 0.5f) rand2 = rand2 * -1;
        return rand2;
    }
    @Override
    public boolean heartbeat() {
        this.effectsTick();
        return true;
    }

//    public static void superSaien(Player player) {
//        ParticleEffect.FLAME.display(0.3f, 0.3f, 0.3f, (float) 0.025, 70, player.getLocation());
//    }
//    public static void administrator(Player player) {
//        ParticleEffect.SPELL_WITCH.display(0.35f, 0.35f, 0.35f, (float) 0.02, 100, player.getLocation());
//    }
}