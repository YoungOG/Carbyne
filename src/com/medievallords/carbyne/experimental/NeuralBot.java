package com.medievallords.carbyne.experimental;

import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.experimental.neat.Genome;
import com.medievallords.carbyne.utils.Cooldowns;
import net.minecraft.server.v1_8_R3.DamageSource;
import net.minecraft.server.v1_8_R3.EntityVillager;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class NeuralBot extends EntityVillager {

    private final Carbyne main = Carbyne.getInstance();
    private PlayerBot target;

    private Genome genome;
    private BotHandler botHandler;
    private float fitness = 0;

    public NeuralBot(CraftWorld world, Genome genome, BotHandler botHandler) {
        super(world.getHandle());
        this.botHandler = botHandler;
        this.genome = genome;
    }

    public void setFitness() {
        genome.setFitness(fitness);
    }

    public void setTarget(PlayerBot playerBot) {
        this.target = playerBot;
    }

    public PlayerBot getTarget() {
        return target;
    }

    protected String z() {
        return "";
    }

    protected String bo() {
        return "game.player.hurt";
    }

    protected String bp() {
        return "game.player.die";
    }

    public void onDamage() {
        fitness -= 1;
    }

    public void start() {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (getHealth() <= 0 || dead) {
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            botHandler.died(NeuralBot.this);
                        }
                    }.runTask(main);
                    cancel();
                    return;
                }

                float[] input = {
                        getMaxHealth(),
                        getHealth(),
                        (float) getBukkitEntity().getLocation().distance(target.getBukkitEntity().getLocation()),
                        Cooldowns.getCooldown(uniqueID, "potion") > 0 ? 1f : 0f,
                        yaw,
                        pitch,
                        target.getHealth(),
                        target.getMaxHealth(),
                        target.yaw,
                        target.pitch
                };

                float[] output = genome.calculate(input);
                if (output[0] >= 0.5) { //Move left
                    left();
                }

                if (output[1] >= 0.5) { //Move right
                    right();
                }

                if (output[2] >= 0.5) { //Move up
                    up();
                }

                if (output[3] >= 0.5) { //Move down
                    down();
                }

                direction(output[4], output[5]); //Yaw and pitch

                if (output[6] >= 0.5) { //Move up
                    splashHeal();
                }

                if (output[7] >= 0.5) { //Move down
                    hit();
                }
            }
        }.runTaskTimerAsynchronously(main, 0, 1);
    }

    private void left() {
        moveController.a(locX + 1, locY, locZ, 0.57);
    }

    private void right() {
        moveController.a(locX - 1, locY, locZ, 0.57);
    }

    private void up() {
        moveController.a(locX, locY, locZ + 1, 0.57);
    }

    private void down() {
        moveController.a(locX, locY, locZ - 1, 0.57);
    }

    private void direction(float yaw, float pitch) {
        setYawPitch(yaw, pitch);
    }

    private void splashHeal() {

        if (Cooldowns.tryCooldown(uniqueID, "potion", 13000)) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    ThrownPotion potion = getWorld().getWorld().spawn(getBukkitEntity().getLocation(), ThrownPotion.class);
                    potion.setVelocity(getBukkitEntity().getLocation().getDirection());
                    ItemStack itemStack = new ItemStack(org.bukkit.Material.POTION);
                    itemStack.setDurability((short) 16421);
                    potion.setItem(itemStack);
                }
            }.runTask(main);
            fitness += 1;
        }
    }

    private void hit() {
        if (getBukkitEntity().getLocation().distance(target.getBukkitEntity().getLocation()) <= 3.7) {
            if (Cooldowns.tryCooldown(uniqueID, "pvp", 1500)) {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        target.damageEntity(DamageSource.a(NeuralBot.this), 1f);
                    }
                }.runTask(main);

                fitness += 1;
            }
        }
    }
}
