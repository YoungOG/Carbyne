package com.medievallords.carbyne.donator;

import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.utils.ItemBuilder;
import com.medievallords.carbyne.utils.ParticleEffect;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

/**
 * Created by Dalton on 6/26/2017.
 */
public class TrailManager
{

    public static String guiInvName = ChatColor.translateAlternateColorCodes('&', "&2&lTrails"),
                        advancedGuiName = ChatColor.translateAlternateColorCodes('&', "&2&lAdvanced Particle Effects"),
                        selectionGuiName = ChatColor.translateAlternateColorCodes('&', "&2&lParticles Hub");

    @Getter
    private Map<UUID, ParticleEffect> activePlayerEffects;
    @Getter
    private Map<UUID, AdvancedEffect> advancedEffects;

    public TrailManager()
    {
        activePlayerEffects = Collections.synchronizedMap(new HashMap<>());
        advancedEffects = Collections.synchronizedMap(new HashMap<>());

        new BukkitRunnable()
        {
            public void run()
            {
                tickTrailEffects();
            }
        }.runTaskTimerAsynchronously(Carbyne.getInstance(), 0L, 1L);
    }

    public void tickTrailEffects()
    {
        synchronized (activePlayerEffects) // tick basic effects
        {
            Iterator itr = activePlayerEffects.entrySet().iterator();
            while(itr.hasNext())
            {
                Map.Entry pair = (Map.Entry) itr.next();
                Player player = Bukkit.getPlayer((UUID) pair.getKey());
                if (player == null || !player.isOnline()) continue;
                ((ParticleEffect) pair.getValue()).display(0.2f, 0f, 0.2f, 0.02f, 5, player.getLocation().add(0,0.08,0), 50, true);
            }
        }
        synchronized (advancedEffects) // tick advanced effects
        {
            Iterator itr = advancedEffects.entrySet().iterator();
            while(itr.hasNext())
            {
                Map.Entry pair = (Map.Entry) itr.next();
                Player player = Bukkit.getPlayer((UUID) pair.getKey());
                if (player == null || !player.isOnline()) continue;
                ((AdvancedEffect)pair.getValue()).tick();
            }
        }
    }

    /**
     * PRECONDITION: Player has donator perms to open tihs inventory!
     * @param player Player to show the inventory to
     */
    public void showPlayerInvenotry(Player player)
    {
        Inventory gui = Bukkit.createInventory(null, 27, guiInvName);
        gui.setContents(new ItemStack[] {
                player.hasPermission(AdvancedEffect.trailOrigamiStar) ? new ItemBuilder(Material.EMERALD).name("&aOrigami Star").build() : new ItemBuilder(Material.EMERALD).name("&aOrigami Star").addLore("&cYou have not unlocked this effect").build(),
                player.hasPermission(AdvancedEffect.trailWarning) ? new ItemBuilder(Material.COAL).name("&8Coal").build() : new ItemBuilder(Material.BARRIER).name("&8Coal").addLore("&cYou have not unlocked this effect").build(),
                player.hasPermission(AdvancedEffect.trailFlame) ? new ItemBuilder(Material.FLINT_AND_STEEL).name("&6Flame").build() : new ItemBuilder(Material.FLINT_AND_STEEL).name("&6Flame").addLore("&cYou have not unlocked this effect").build(),
                player.hasPermission(AdvancedEffect.trailSpell) ? new ItemBuilder(Material.WEB).name("Spell").build() : new ItemBuilder(Material.WEB).name("Spell").addLore("&cYou have not unlocked this effect").build(),
                player.hasPermission(AdvancedEffect.trailCloud) ? new ItemBuilder(Material.WOOL).name("&7Cloud").build() : new ItemBuilder(Material.WOOL).name("&7Cloud").addLore("&cYou have not unlocked this effect").build(),
                player.hasPermission(AdvancedEffect.trailCrit) ? new ItemBuilder(Material.DIAMOND_SWORD).name("&3Crit").build() : new ItemBuilder(Material.DIAMOND_SWORD).name("&3Crit").addLore("&cYou have not unlocked this effect").build(),
                player.hasPermission(AdvancedEffect.trailMagicCrit) ? new ItemBuilder(Material.STICK).name("&dMagic Crit").build() : new ItemBuilder(Material.STICK).name("&dMagic Crit").addLore("&cYou have not unlocked this effect").build(),
                player.hasPermission(AdvancedEffect.trailWater) ?  new ItemBuilder(Material.WATER_BUCKET).name("&9Water").build() : new ItemBuilder(Material.WATER_BUCKET).name("&9Water").addLore("&cYou have not unlocked this effect").build(),
                player.hasPermission(AdvancedEffect.trailWaterDrop) ?  new ItemBuilder(Material.WATER_LILY).name("&9Water Drip").build() : new ItemBuilder(Material.WATER_LILY).name("&9Water Drip").addLore("&cYou have not unlocked this effect").build(),
                player.hasPermission(AdvancedEffect.trailSnow) ? new ItemBuilder(Material.SNOW_BALL).name("Snow").build() : new ItemBuilder(Material.SNOW_BALL).name("Snow").addLore("&cYou have not unlocked this effect").build(),
                player.hasPermission(AdvancedEffect.trailLavaDrip) ? new ItemBuilder(Material.LAVA_BUCKET).name("&6Lava Drip").build() : new ItemBuilder(Material.LAVA_BUCKET).name("&6Lava Drip").addLore("&cYou have not unlocked this effect").build(),
                player.hasPermission(AdvancedEffect.trailMagicLetters) ? new ItemBuilder(Material.BOOK).name("&8Magic Letters").build() : new ItemBuilder(Material.BOOK).name("&8Magic Letters").addLore("&cYou have not unlocked this effect").build(),
                player.hasPermission(AdvancedEffect.trailExplosion) ? new ItemBuilder(Material.TNT).name("&eExplosion").build() : new ItemBuilder(Material.TNT).name("&eExplosion").addLore("&cYou have not unlocked this effect").build(),
                player.hasPermission(AdvancedEffect.trailSpark) ? new ItemBuilder(Material.FIREWORK).name("&4Spark").build() : new ItemBuilder(Material.FIREWORK).name("&4Spark").addLore("&cYou have not unlocked this effect").build(),
                player.hasPermission(AdvancedEffect.trailHearts) ? new ItemBuilder(Material.SHEARS).name("&4Hearts").build() : new ItemBuilder(Material.SHEARS).name("&4Hearts").addLore("&cYou have not unlocked this effect").build(),
                player.hasPermission(AdvancedEffect.trailMagicDust) ? new ItemBuilder(Material.REDSTONE).name("&cMagic Dust").build() : new ItemBuilder(Material.REDSTONE).name("&cMagic Dust").addLore("&cYou have not unlocked this effect").build(),
                player.hasPermission(AdvancedEffect.trailMusic) ? new ItemBuilder(Material.JUKEBOX).name("&aMusic").build() : new ItemBuilder(Material.JUKEBOX).name("&aMusic").addLore("&cYou have not unlocked this effect").build(),
                player.hasPermission(AdvancedEffect.trailWitchSpell) ? new ItemBuilder(Material.BROWN_MUSHROOM).name("&dWitch Spell").build() : new ItemBuilder(Material.BROWN_MUSHROOM).name("&dWitch Spell").addLore("&cYou have not unlocked this effect").build(),
                new ItemBuilder(Material.COMMAND).name("&2&lDisable trail").build()
        });
        gui.setItem(gui.getSize() - 1, new ItemBuilder(Material.BARRIER).name("&c&lBACK").build());
        player.openInventory(gui);
    }

    public void showAdvancedEffectsInventory(Player player)
    {
        Inventory gui = Bukkit.createInventory(null, 18, advancedGuiName);
        gui.setContents(new ItemStack[] {
                player.hasPermission(AdvancedEffect.yinYangPermission) ? new ItemBuilder(Material.FLINT_AND_STEEL).name("&fYin &0Yang").build() : new ItemBuilder(Material.FLINT_AND_STEEL).name("&fYin &0Yang").addLore("&cYou have not unlocked this effect").build(),
                player.hasPermission(AdvancedEffect.loveShieldPermission) ? new ItemBuilder(Material.RED_ROSE).name("&2Love Shield").build() : new ItemBuilder(Material.RED_ROSE).name("&2Love Shield").addLore("&cYou have not unlocked this effect").build(),
                player.hasPermission(AdvancedEffect.waterSpiralPermission) ? new ItemBuilder(Material.WATER_BUCKET).name("&9Water Spiral").build() : new ItemBuilder(Material.WATER_BUCKET).name("&9Water Spiral").addLore("&cYou have not unlocked this effect").build(),
                player.hasPermission(AdvancedEffect.scannerPermission) ? new ItemBuilder(Material.STAINED_GLASS).name("&5Scanner").build() : new ItemBuilder(Material.STAINED_GLASS).name("&5Scanner").addLore("&cYou have not unlocked this effect").build(),
                player.hasPermission(AdvancedEffect.emeraldTwirlPemission) ? new ItemBuilder(Material.EMERALD).name("&aEmerald Twirl").build() : new ItemBuilder(Material.EMERALD).name("&aEmerald Twirl").addLore("&cYou have not unlocked this effect").build(),
                player.hasPermission(AdvancedEffect.boxPermission) ? new ItemBuilder(Material.IRON_BLOCK).name("&dBox of &4&lDOOM").build() : new ItemBuilder(Material.IRON_BLOCK).name("&dBox of &4DOOM").addLore("&cYou have not unlocked this effect").build(),
                null, null,
                new ItemBuilder(Material.COMMAND).name("&2&lTurn off effect").build()
        });
        gui.setItem(gui.getSize() - 1, new ItemBuilder(Material.BARRIER).name("&c&lBACK").build());
        player.openInventory(gui);
    }

    public void showAllEffectsGui(Player player)
    {
        Inventory gui = Bukkit.createInventory(null, InventoryType.HOPPER, selectionGuiName);
        gui.setContents(new ItemStack[] {
                null,
                new ItemBuilder(Material.EMERALD).name("&2&lTrail Effects").build(),
                null,
                new ItemBuilder(Material.DIAMOND).name("&5&lAdvanced Effects").build(),
                null
        });

        player.openInventory(gui);
    }

}
