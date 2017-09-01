package com.medievallords.carbyne.lootchests;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.utils.DateUtil;
import com.medievallords.carbyne.utils.ParticleEffect;
import io.lumine.xikage.mythicmobs.MythicMobs;
import io.lumine.xikage.mythicmobs.mobs.ActiveMob;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.*;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dalton on 6/5/2017.
 */
public class LootChest {

    private Carbyne main = Carbyne.getInstance();
    private LootChestManager lootChestManager;
    @Getter
    private String chestConfigName;
    private String lootTableName;
    @Getter
    private Location location, center;
    private String respawnTimeString;
    @Getter
    private long respawnTime = 0;
    @Getter
    private boolean hidden = true;
    @Getter
    private int maxItems;
    @Setter
    private BlockFace face;
    @Getter
    private Hologram hologram;

    @Getter
    @Setter
    private double health = 0, maxHealth = 0;

    @Getter
    @Setter
    private List<String> mobs = new ArrayList<>();

    @Getter
    private List<ActiveMob> mobsAlive = new ArrayList<>();

    public LootChest(LootChestManager lootChestManager, String chestConfigName, String lootTableName, Location location, String respawnTimeString, int maxItems, BlockFace face, double maxHealth) {
        this.lootChestManager = lootChestManager;
        this.chestConfigName = chestConfigName;
        this.lootTableName = lootTableName;
        this.location = location;
        this.center = this.location.clone();
        this.face = face;
        this.center.add(0.5, 0, 0.5);
        this.respawnTimeString = respawnTimeString;
        this.maxItems = maxItems;
        this.hidden = true;
        this.maxHealth = maxHealth;
        this.hologram = HologramsAPI.createHologram(Carbyne.getInstance(), location.clone().add(0.5, 1.45, 0.5));
        this.showChest();

    }

    public List<ItemStack> getLoot() {
        List<ItemStack> loots = new ArrayList<>();
        lootChestManager.getLootTables().get(lootTableName).forEach(l -> {
            if (l.shouldSpawnItem())
                loots.add(l.getItem());
        });
        return loots;
    }

    public boolean shouldChestSpawn() {
        return respawnTime < System.currentTimeMillis();
    }

    public void hideChest() {
        location.getBlock().setType(Material.AIR);

        try {
            respawnTime = DateUtil.parseDateDiff(respawnTimeString, true);
            hidden = true;
            location.getWorld().playSound(location, Sound.CHEST_OPEN, 10, 1);
            ParticleEffect.SMOKE_NORMAL.display(1f, 1f, 1f, 0.02f, 15, center, 50, true);
        } catch (Exception ignored) {
        }
    }

    public void showChest() {
        new BukkitRunnable() {
            public void run() {
                if (hidden) {
                    Chunk chunk = location.getChunk();
                    if(!chunk.isLoaded()) chunk.load();
                    location.getBlock().setType(Material.CHEST);
                    location.getBlock().setData((byte) face.ordinal());
                    hidden = false;
                    health = maxHealth;
                    if (hologram.size() >= 1) {
                        hologram.removeLine(0);
                    }

                    hologram.appendTextLine(ChatColor.translateAlternateColorCodes('&', "&a" + ((int) maxHealth + " &7/ &a" + (int) maxHealth)));

                    if (!mobs.isEmpty()) {
                        for (String mobName : mobs) {
                            mobsAlive.add(MythicMobs.inst().getMobManager().spawnMob(mobName, location.clone().add(0.5, 1.05, 0.5)));
                        }
                    }
                    //chunk.unload();
                }
            }
        }.runTask(main);
    }

    public void dropLoot() {
        int i = 0;
        for (ItemStack itemStack : getLoot()) {
            if (i > maxItems) {
                return;
            }

            location.getWorld().dropItemNaturally(center, itemStack);
            i++;
        }
    }

    public void setHologramTimeLeft() {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!location.getChunk().isLoaded()) location.getChunk().load();
                hologram.removeLine(0);
                hologram.appendTextLine(ChatColor.AQUA + getTimeLeft());
            }
        }.runTask(Carbyne.getInstance());
    }

    private String getTimeLeft() {
        long diff = respawnTime - System.currentTimeMillis();
        String hoursLeft = String.valueOf((int) (diff / (60 * 60 * 1000)));
        String minutesLeft = String.valueOf((int) ((diff % (60 * 60 * 1000)) / (60 * 1000)));
        String secondsLeft = String.valueOf((int) ((diff % (60 * 1000)) / (1000)));
        if (hoursLeft.length() == 1) hoursLeft = 0 + hoursLeft;
        if (secondsLeft.length() == 1) secondsLeft = 0 + secondsLeft;
        if (minutesLeft.length() == 1) minutesLeft = 0 + minutesLeft;
        return new String(hoursLeft + ":" + minutesLeft + ":" + secondsLeft);
    }

    public boolean allMobsDead() {
        return mobsAlive.isEmpty();
    }
}
