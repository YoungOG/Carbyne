package com.medievallords.carbyne.lootchests;

import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.utils.DateUtil;
import com.medievallords.carbyne.utils.ParticleEffect;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class LootChest {


    private Carbyne main = Carbyne.getInstance();
    private LootChestManager lootChestManager;
    @Getter
    private String chestConfigName;
    private String lootTableName;
    @Getter
    private Location location, center;
    private String respawnTimeString;
    private long respawnTime;
    @Getter
    private boolean hidden;
    @Getter
    private int maxItems;
    @Setter
    private BlockFace face;

    public LootChest(LootChestManager lootChestManager, String chestConfigName, String lootTableName, Location location, String respawnTimeString, int maxItems, BlockFace face) {
        this.lootChestManager = lootChestManager;
        this.chestConfigName = chestConfigName;
        this.lootTableName = lootTableName;
        this.location = location;
        this.center = this.location.clone();
        this.face = face;
        this.center.add(center.getX() > 0 ? -0.5D : 0.5D, 0D, center.getZ() < 0 ? -0.5D : 0.5D);
        this.respawnTimeString = respawnTimeString;
        this.maxItems = maxItems;
        this.hidden = true;
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

                    boolean wasChunkLoaded = true;
                    if (!chunk.isLoaded()) {
                        wasChunkLoaded = false;
                        chunk.load();
                    }

                    location.getBlock().setType(Material.CHEST);
                    location.getBlock().setData((byte) face.ordinal());
                    hidden = false;

                    if (!wasChunkLoaded)
                        chunk.unload();
                }
            }
        }.runTask(main);
    }
}
