package com.medievallords.carbyne.gear.effects;

import com.medievallords.carbyne.heartbeat.Heartbeat;
import com.medievallords.carbyne.heartbeat.HeartbeatTask;
import com.medievallords.carbyne.listeners.CombatTagListeners;
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

    public GearEffects() {
        if (this.heartbeat == null) {
            this.heartbeat = new Heartbeat(this, 250L);
            heartbeat.start();
        }

        random.doubles(-1.5D, 1.5D);
    }

    public void effectsTick() {
        for (Player all : PlayerUtility.getOnlinePlayers()) {
            if (all.getFireTicks() > 1)
                ParticleEffect.FLAME.display(0.35f, 0.35f, 0.35f, (float) 0.02, 5, all.getLocation().add(0, 1, 0), 50, false);

            if (all.getInventory().getItemInMainHand().containsEnchantment(Enchantment.DAMAGE_ALL) || all.getInventory().getItemInMainHand().containsEnchantment(Enchantment.ARROW_DAMAGE))
                if (!CombatTagListeners.isInCombat(all.getUniqueId()))
                    effectSharpnessPlayers(all);

            if (all.isSprinting() && all.isOnGround())
                ParticleEffect.FOOTSTEP.display(0.2f, 0f, 0.2f, (float) 0.15, 1, all.getLocation().add(0, 0.02, 0), 50, false);

            if (all.getHealth() < 5)
                bleed(all);

            for (PotionEffect effects : all.getActivePotionEffects()) {
                switch (effects.getType().getName()) {
                    case "WITHER":
                        ParticleEffect.VILLAGER_ANGRY.display(0.2f, -0.2f, 0.2f, (float) 0.06, 2, all.getLocation(), 50, false);
                        break;
                    case "POISON":
                        ParticleEffect.BLOCK_CRACK.display(new ParticleEffect.BlockData(Material.LONG_GRASS, (byte) 0), 0.2f, 0.2f, 0.2f, (float) 0.02, 20, all.getLocation().clone().add(0, 0.2, 0), 50, false);
                        break;
                    case "BLINDNESS":
                        ParticleEffect.TOWN_AURA.display(0.1f, 0.1f, 0.1f, (float) 0.01, 20, all.getLocation().add(0, 2, 0), 50, false);
                        break;
                    case "SPEED":
                        ParticleEffect.SMOKE_NORMAL.display(0.2f, 0.1f, 0.2f, (float) 0.03, 12, all.getLocation(), 50, false);
                        break;
                    case "SLOW":
                        ParticleEffect.CLOUD.display(0.2f, -0.2f, 0.2f, (float) 0.0001, 15, all.getLocation().subtract(0, 0.1, 0), 50, false);
                        break;
                    case "REGENERATION":
                        ParticleEffect.HEART.display((float) random.nextDouble(), (float) random.nextDouble(), (float) random.nextDouble(), 0.3F, 2, all.getLocation(), 50, false);
                        break;
                    case "INCREASE_DAMAGE":
                        ParticleEffect.LAVA.display(0.2F, -0.2F, 0.2F, (float) 00.5, 2, all.getLocation().subtract(0.0, 0.3, 0.0), 50, false);
                        break;
                }
            }
        }
    }

    public void effectSharpnessPlayers(Player player) {
        float enchantsO = 0;
        enchantsO += (float) player.getInventory().getItemInMainHand().getEnchantmentLevel(Enchantment.DAMAGE_ALL);
        enchantsO += (float) player.getInventory().getItemInMainHand().getEnchantmentLevel(Enchantment.KNOCKBACK) * 2;
        enchantsO += (float) player.getInventory().getItemInMainHand().getEnchantmentLevel(Enchantment.FIRE_ASPECT) * 2;
        enchantsO += (float) player.getInventory().getItemInMainHand().getEnchantmentLevel(Enchantment.ARROW_INFINITE) * 3;
        enchantsO += (float) player.getInventory().getItemInMainHand().getEnchantmentLevel(Enchantment.ARROW_FIRE) * 2;
        enchantsO += (float) player.getInventory().getItemInMainHand().getEnchantmentLevel(Enchantment.ARROW_DAMAGE);
        float amount = enchantsO;
        ParticleEffect.PORTAL.display(0.2f, 0.2f, 0.2f, (float) 0.15, (int) amount * 5, player.getLocation().add(0, 0.2, 0), 50, false);
    }

    public void effectTeleport(Player player, Location location) {
        List<Player> playerList = new ArrayList<>();
        playerList.add(player);

        for (Entity entity : location.getWorld().getNearbyEntities(location, 30, 30, 30))
            if (entity instanceof Player) {
                Player all = (Player) entity;

                if (all.canSee(player))
                    playerList.add(all);
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
        ParticleEffect.DRIP_LAVA.display(0.35f, 0.35f, 0.35f, (float) 0.02, 40, player.getLocation(), 50, false);
    }
    public float getRandomNegPosOne(float modifier){
        float rand1 = (float)random.nextDouble();
        float rand2 = (float)random.nextDouble() + modifier;
        if (rand1 > 0.5f) rand2 = rand2 * -1;
        return rand2;
    }
    @Override
    public boolean heartbeat() {
        this.effectsTick();
        return true;
    }
}