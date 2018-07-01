package com.medievallords.carbyne.experimental;

import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.utils.Cooldowns;
import com.medievallords.carbyne.utils.Maths;
import net.minecraft.server.v1_8_R3.DamageSource;
import net.minecraft.server.v1_8_R3.EntityVillager;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class PlayerBot extends EntityVillager {

    private final Carbyne main = Carbyne.getInstance();
    //private BotPathfinder botPathfinder;
    private NeuralBot target;
    private double reach;
    private boolean notFighting = false, hit = true, setDir = false, emergencyPotted = false, continueRunning = true;
    private int randomHealth = 4, hitsPerSecond;
    private int timesSinceLastHit = 0;
    private Location moveTo;
    private BotHandler botHandler;

    public PlayerBot(CraftWorld world, NeuralBot target, BotHandler botHandler) {
        super(world.getHandle());
        this.target = target;
        this.botHandler = botHandler;
        moveTo = target.getBukkitEntity().getLocation().clone();
        //this.botPathfinder = new BotPathfinder(this, target.getLocation(), 0.6);
        //this.goalSelector.a(0, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 8.0F));
        setSprinting(true);
        reach = 3.7;
        hitsPerSecond = 15;//20 = 1 second, 5 = every 5 ticks
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
        timesSinceLastHit++;
        Location entityLocation = getBukkitEntity().getLocation();

        if (!notFighting) {
            randomHealth = Maths.randomNumberBetween(7, 4);
        }

        //checkPotions();

        if (!continueRunning) {
            return;
        }

        if (getHealth() <= 4 && Cooldowns.tryCooldown(uniqueID, "potion", 13000)) {
            ItemStack itemStack = new ItemStack(org.bukkit.Material.POTION);
            itemStack.setDurability((short) 16421);
            setEquipment(0, CraftItemStack.asNMSCopy(itemStack));
            setDir = true;

            new BukkitRunnable() {
                @Override
                public void run() {
                    splashHeal(entityLocation, false);
                    emergencyPotted = true;
                }
            }.runTaskLater(main, 5);
        }


        if (getHealth() <= randomHealth && Cooldowns.tryCooldown(uniqueID, "potion", 13000)) {
            hit = false;

            /// /botPathfinder.loc = randomLocation(entityLocation.getX(), entityLocation.getY(), entityLocation.getZ());
            moveTo = randomLocation(entityLocation.getX(), entityLocation.getY(), entityLocation.getZ());

            setDir = true;
            notFighting = true;

            ItemStack itemStack = new ItemStack(org.bukkit.Material.POTION);
            itemStack.setDurability((short) 16421);
            setEquipment(0, CraftItemStack.asNMSCopy(itemStack));

            new BukkitRunnable() {
                @Override
                public void run() {
                    splashHeal(entityLocation, true);
                }
            }.runTaskLater(main, 48);
        }
    }

    public void start() {
        getBukkitEntity().getLocation().getChunk().load();

        //setCustomName("PvP_B0t");
        //setCustomNameVisible(true);


        new BukkitRunnable() {
            private int last = 1;
            private double strafe = 0;
            private Location add = new Location(target.getBukkitEntity().getWorld(), 0, 0, 0);
            private CraftEntity entity = getBukkitEntity();

            @Override
            public void run() {
                Location targetLocation = target.getBukkitEntity().getLocation().clone();
                if (continueRunning) {
                    Location entityLocation = entity.getLocation();

                    if (setDir) {
                        setYawPitch(yaw, 40);
                    }

                    if (getHealth() <= 0 || dead) {
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                botHandler.died(PlayerBot.this);
                            }
                        }.runTask(main);
                        cancel();
                        return;
                    }

                    double distance = entityLocation.distance(targetLocation);
                    if (!notFighting) {

                        getControllerLook().a(targetLocation.getX(), target.getBukkitEntity().getLocation().clone().add(0, 2, 0).getY(), targetLocation.getZ(), 10.0F, (float) bQ()); // 10
                        moveTo = targetLocation.clone().add(add);
                        //botPathfinder.loc = targetLocation.clone().add(add);
                    }

                    moveController.a(moveTo.getX(), Math.floor(moveTo.getY()), moveTo.getZ(), 0.57);


                    if (hit && hasLineOfSight(target.getBukkitEntity().getHandle())) {
                        if (distance <= reach && last >= 20 && Math.random() <= 0.9) {
                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    if (Cooldowns.tryCooldown(uniqueID, "pvp", 1500)) {
                                        target.damageEntity(DamageSource.a(PlayerBot.this), 1f);
                                    }
                                }
                            }.runTask(main);
                            timesSinceLastHit = 0;
                            last = 1;
                        }

                    }

                    if (strafe >= 3) {
                        add = new Location(target.getBukkitEntity().getWorld(), Maths.randomNumberBetween(5, -5), 0, Maths.randomNumberBetween(5, -5));
                        strafe = 0;
                    }

                    last += hitsPerSecond;
                    strafe += 0.2;
                }
            }
        }.runTaskTimerAsynchronously(main, 0, 1);
    }

    public NeuralBot getTarget() {
        return target;
    }

    private void splashHeal(Location entityLocation, boolean resplash) {
        if (getHealth() >= 12 || emergencyPotted) {
            emergencyPotted = false;
            return;
        }

        notFighting = false;
        hit = true;
        setDir = false;
        setYawPitch(yaw, 40);

        moveTo = target.getBukkitEntity().getLocation().clone();
        //botPathfinder.loc = target.getLocation().clone();

        if (Cooldowns.tryCooldown(uniqueID, "potion", 13000)) {
            ThrownPotion potion = getWorld().getWorld().spawn(entityLocation, ThrownPotion.class);
            potion.setVelocity(entityLocation.getDirection());
            ItemStack itemStack = new ItemStack(org.bukkit.Material.POTION);
            itemStack.setDurability((short) 16421);
            potion.setItem(itemStack);
        }
    }

    private Location randomLocation(double x, double y, double z) {
        return new Location(world.getWorld(), (Maths.randomNumberBetween(10, 0) - 5) + x, 0 + y, (Maths.randomNumberBetween(10, 0) - 5) + z);
    }
}
