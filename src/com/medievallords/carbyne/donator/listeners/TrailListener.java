package com.medievallords.carbyne.donator.listeners;

import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.customevents.CombatTaggedEvent;
import com.medievallords.carbyne.donator.TrailManager;
import com.medievallords.carbyne.donator.advancedeffects.*;
import com.medievallords.carbyne.utils.MessageManager;
import com.medievallords.carbyne.utils.ParticleEffect;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Dalton on 6/26/2017.
 */
public class TrailListener implements Listener
{

    private TrailManager trailManager;

    public TrailListener()
    {
        trailManager = Carbyne.getInstance().getTrailManager();
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e)
    {
        if(e.getInventory().getName().equalsIgnoreCase(TrailManager.guiInvName)) {
            e.setCancelled(true);
            if (e.getCurrentItem() != null) {
                ItemStack is = e.getCurrentItem();

                if (is.hasItemMeta() && is.getItemMeta().hasLore() && ChatColor.stripColor(is.getItemMeta().getLore().get(0)).equalsIgnoreCase("You have not unlocked this effect")) {
                    MessageManager.sendMessage((Player) e.getWhoClicked(), "&cYou cannot use this effect");
                    return;
                }

                switch (is.getType()) {
                    case EMERALD:
                        trailManager.getActivePlayerEffects().put(e.getWhoClicked().getUniqueId(), ParticleEffect.VILLAGER_HAPPY);
                        break;
                    case BARRIER:
                        if (is.hasItemMeta() && is.getItemMeta().hasDisplayName() && ChatColor.stripColor(is.getItemMeta().getDisplayName()).equalsIgnoreCase("BACK")) {
                            e.getWhoClicked().closeInventory();
                            trailManager.showAllEffectsGui((Player) e.getWhoClicked());
                            return;
                        }
                    case COAL:
                        trailManager.getActivePlayerEffects().put(e.getWhoClicked().getUniqueId(), ParticleEffect.TOWN_AURA);
                        break;
                    case FLINT_AND_STEEL:
                        trailManager.getActivePlayerEffects().put(e.getWhoClicked().getUniqueId(), ParticleEffect.FLAME);
                        break;
                    case WEB:
                        trailManager.getActivePlayerEffects().put(e.getWhoClicked().getUniqueId(), ParticleEffect.SPELL);
                        break;
                    case WOOL:
                        trailManager.getActivePlayerEffects().put(e.getWhoClicked().getUniqueId(), ParticleEffect.CLOUD);
                        break;
                    case DIAMOND_SWORD:
                        trailManager.getActivePlayerEffects().put(e.getWhoClicked().getUniqueId(), ParticleEffect.CRIT);
                        break;
                    case STICK:
                        trailManager.getActivePlayerEffects().put(e.getWhoClicked().getUniqueId(), ParticleEffect.CRIT_MAGIC);
                        break;
                    case WATER_BUCKET:
                        trailManager.getActivePlayerEffects().put(e.getWhoClicked().getUniqueId(), ParticleEffect.WATER_SPLASH);
                        break;
                    case WATER_LILY:
                        trailManager.getActivePlayerEffects().put(e.getWhoClicked().getUniqueId(), ParticleEffect.WATER_WAKE);
                        break;
                    case SNOW_BALL:
                        trailManager.getActivePlayerEffects().put(e.getWhoClicked().getUniqueId(), ParticleEffect.SNOWBALL);
                        break;
                    case LAVA_BUCKET:
                        trailManager.getActivePlayerEffects().put(e.getWhoClicked().getUniqueId(), ParticleEffect.DRIP_LAVA);
                        break;
                    case BOOK:
                        trailManager.getActivePlayerEffects().put(e.getWhoClicked().getUniqueId(), ParticleEffect.ENCHANTMENT_TABLE);
                        break;
                    case TNT:
                        trailManager.getActivePlayerEffects().put(e.getWhoClicked().getUniqueId(), ParticleEffect.EXPLOSION_NORMAL);
                        break;
                    case FIREWORK:
                        trailManager.getActivePlayerEffects().put(e.getWhoClicked().getUniqueId(), ParticleEffect.FIREWORKS_SPARK);
                        break;
                    case SHEARS:
                        trailManager.getActivePlayerEffects().put(e.getWhoClicked().getUniqueId(), ParticleEffect.HEART);
                        break;
                    case REDSTONE:
                        trailManager.getActivePlayerEffects().put(e.getWhoClicked().getUniqueId(), ParticleEffect.REDSTONE);
                        break;
                    case JUKEBOX:
                        trailManager.getActivePlayerEffects().put(e.getWhoClicked().getUniqueId(), ParticleEffect.NOTE);
                        break;
                    case BROWN_MUSHROOM:
                        trailManager.getActivePlayerEffects().put(e.getWhoClicked().getUniqueId(), ParticleEffect.SPELL_WITCH);
                        break;
                    case COMMAND:
                        trailManager.getActivePlayerEffects().remove(e.getWhoClicked().getUniqueId());
                    default:
                        return;
                }

            }

        }
        else if (e.getInventory().getName().equalsIgnoreCase(TrailManager.advancedGuiName))
        {
            e.setCancelled(true);
            if(e.getCurrentItem() != null)
            {
                ItemStack is = e.getCurrentItem();

                if (is.hasItemMeta() && is.getItemMeta().hasLore() && ChatColor.stripColor(is.getItemMeta().getLore().get(0)).equalsIgnoreCase("You have not unlocked this effect")) {
                    MessageManager.sendMessage((Player) e.getWhoClicked(), "&cYou cannot use this effect");
                    return;
                }

                switch (is.getType())
                {
                    case FLINT_AND_STEEL: {
                        trailManager.getAdvancedEffects().put(e.getWhoClicked().getUniqueId(), new YinYang((Player) e.getWhoClicked()));
                        break;
                    }
                    case RED_ROSE: {
                        trailManager.getAdvancedEffects().put(e.getWhoClicked().getUniqueId(), new LoveShieldEffect((Player) e.getWhoClicked()));
                        break;
                    }
                    case COMMAND: {
                        trailManager.getAdvancedEffects().remove(e.getWhoClicked().getUniqueId());
                        break;
                    }
                    case BARRIER:
                    {
                        e.getWhoClicked().closeInventory();
                        trailManager.showAllEffectsGui((Player) e.getWhoClicked());
                        break;
                    }
                    case WATER_BUCKET: {
                        trailManager.getAdvancedEffects().put(e.getWhoClicked().getUniqueId(), new WaterSpiralEffect((Player) e.getWhoClicked()));
                        break;
                    }
                    case STAINED_GLASS: {
                        trailManager.getAdvancedEffects().put(e.getWhoClicked().getUniqueId(), new ScannerEffect((Player) e.getWhoClicked()));
                        break;
                    }
                    case EMERALD: {
                        trailManager.getAdvancedEffects().put(e.getWhoClicked().getUniqueId(), new EmeraldTwirlEffect((Player) e.getWhoClicked()));
                        break;
                    }
                    case IRON_BLOCK: {
                        trailManager.getAdvancedEffects().put(e.getWhoClicked().getUniqueId(), new BoxEffect((Player) e.getWhoClicked()));
                        break;
                    }

                    default: break;
                }
            }
        }
        else if (e.getInventory().getName().equalsIgnoreCase(TrailManager.selectionGuiName))
        {
            e.setCancelled(true);
            if(e.getCurrentItem() != null)
            {
                ItemStack is = e.getCurrentItem();
                switch (is.getType())
                {
                    case EMERALD: {
                        trailManager.showPlayerInvenotry((Player) e.getWhoClicked());
                        break;
                    }
                    case DIAMOND: {
                        trailManager.showAdvancedEffectsInventory((Player) e.getWhoClicked());
                        break;
                    }
                }
            }
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e)
    {
        if(trailManager.getAdvancedEffects().containsKey(e.getPlayer().getUniqueId()))
            trailManager.getAdvancedEffects().remove(e.getPlayer().getUniqueId());
        if(trailManager.getActivePlayerEffects().containsKey(e.getPlayer().getUniqueId()))
            trailManager.getActivePlayerEffects().containsKey(e.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onCombat(CombatTaggedEvent event) {
        Player player = event.getPlayer();

        if (trailManager.getAdvancedEffects().containsKey(player.getUniqueId())) {
            trailManager.getAdvancedEffects().remove(player.getUniqueId());
            MessageManager.sendMessage(player, "&cYou have been hit, your effect was removed");
        }

        if (trailManager.getActivePlayerEffects().containsKey(player.getUniqueId())) {
            trailManager.getActivePlayerEffects().remove(player.getUniqueId());
            MessageManager.sendMessage(player, "&cYou have been hit, your effect was removed");
        }
    }

}
