package com.medievallords.carbyne.quests.listeners;

import com.boydti.fawe.util.TaskManager;
import com.medievallords.carbyne.customevents.*;
import com.medievallords.carbyne.lootchests.Loot;
import com.medievallords.carbyne.profiles.Profile;
import com.medievallords.carbyne.quests.Quest;
import com.medievallords.carbyne.quests.QuestHandler;
import com.medievallords.carbyne.quests.Task;
import com.medievallords.carbyne.quests.types.*;
import com.medievallords.carbyne.utils.MessageManager;
import com.medievallords.carbyne.utils.StaticClasses;
import com.nisovin.magicspells.events.SpellCastEvent;
import com.nisovin.magicspells.events.SpellCastedEvent;
import com.nisovin.magicspells.events.SpellLearnEvent;
import com.palmergames.bukkit.towny.event.NewNationEvent;
import com.palmergames.bukkit.towny.event.NewTownEvent;
import com.palmergames.bukkit.towny.event.TownAddResidentEvent;
import io.lumine.xikage.mythicmobs.api.bukkit.events.MythicMobDeathEvent;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import okhttp3.internal.Internal;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.UUID;


public class QuestListener implements Listener {

    private QuestHandler questHandler = StaticClasses.questHandler;

    @EventHandler(ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        List<Task> tasks = questHandler.getTasks(uuid);
        Block block = event.getBlock();
        for (Task task : tasks) {
            if (task instanceof GatherResourceTask) {
                GatherResourceTask gatherResourceQuest = (GatherResourceTask) task;
                if (gatherResourceQuest.isMatching(block))
                    gatherResourceQuest.incrementProgress(uuid, 1);
            }
        }
    }

    @EventHandler
    public void onEntityDeath(MythicMobDeathEvent event) {
        if (!(event.getKiller() instanceof Player))
            return;

        Player player = (Player) event.getKiller();
        if (player == null)
            return;

        List<Task> tasks = questHandler.getTasks(player.getUniqueId());
        for (Task task : tasks)
            if (task instanceof KillEntityTask) {
                KillEntityTask killEntityQuest = (KillEntityTask) task;
                if (killEntityQuest.getMobsToKill().contains(event.getMobType().getInternalName()))
                    killEntityQuest.incrementProgress(player.getUniqueId(), 1);
            }
    }

    @EventHandler
    public void onCraft(CraftItemEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;

        UUID uuid = event.getWhoClicked().getUniqueId();
        List<Task> tasks = questHandler.getTasks(uuid);

        for (Task task : tasks) {
            if (task instanceof CraftTask) {
                CraftTask craftTask = (CraftTask) task;
                if (craftTask.isMatching(event.getRecipe().getResult())) {
                    craftTask.incrementProgress(uuid, event.getRecipe().getResult().getAmount());
                }
            }
        }
    }

    @EventHandler
    public void onEnchant(EnchantItemEvent event) {
        UUID uuid = event.getEnchanter().getUniqueId();
        List<Task> tasks = questHandler.getTasks(uuid);

        for (Task task : tasks) {
            if (task instanceof EnchantTask) {
                EnchantTask enchantTask = (EnchantTask) task;
                if (enchantTask.isMatching(event.getItem(), event.getEnchantsToAdd())) {
                    enchantTask.incrementProgress(uuid, 1);
                }
            }
        }
    }

    @EventHandler
    public void onLoot(LootChestLootEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        List<Task> tasks = questHandler.getTasks(uuid);

        for (Task task : tasks) {
            if (task instanceof LootChestTask) {
                LootChestTask lootChestTask = (LootChestTask) task;
                for (Loot loot : event.getLoot()) {
                    if (lootChestTask.isMatching(loot.getItem())) {
                        lootChestTask.incrementProgress(uuid, loot.getAmount());
                    }
                }
            }
        }
    }

    @EventHandler
    public void onZone(ZoneEnterEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        List<Task> tasks = questHandler.getTasks(uuid);

        for (Task task : tasks) {
            if (task instanceof DiscoverZoneTask) {
                DiscoverZoneTask discoverZoneTask = (DiscoverZoneTask) task;
                if (discoverZoneTask.getZone().equalsIgnoreCase(event.getZone().getName())) {
                    discoverZoneTask.incrementProgress(uuid, 1);
                }
            }
        }
    }

    @EventHandler
    public void onPortal(PortalEnterEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        List<Task> tasks = questHandler.getTasks(uuid);

        for (Task task : tasks) {
            if (task instanceof EnterPortalTask) {
                EnterPortalTask enterPortalTask = (EnterPortalTask) task;
                if (enterPortalTask.getPortal().equalsIgnoreCase(event.getPortal().getName())) {
                    enterPortalTask.incrementProgress(uuid, 1);
                }
            }
        }
    }

    @EventHandler
    public void onSpellLearn(SpellCastedEvent event) {
        UUID uuid = event.getCaster().getUniqueId();
        List<Task> tasks = questHandler.getTasks(uuid);

        for (Task task : tasks) {
            if (task instanceof CastSpellTask) {
                CastSpellTask castSpellTask = (CastSpellTask) task;
                if (castSpellTask.getSpell().equalsIgnoreCase("any")) {
                    castSpellTask.incrementProgress(uuid, 1);
                } else if (castSpellTask.getSpell().equalsIgnoreCase(event.getSpell().getName())) {
                    castSpellTask.incrementProgress(uuid, 1);
                }
            }
        }
    }

    @EventHandler
    public void onSpellLearn(SpellLearnEvent event) {
        UUID uuid = event.getLearner().getUniqueId();
        List<Task> tasks = questHandler.getTasks(uuid);

        for (Task task : tasks) {
            if (task instanceof LearnSpellTask) {
                LearnSpellTask learnSpellTask = (LearnSpellTask) task;
                if (learnSpellTask.getSpell().equalsIgnoreCase("any")) {
                    learnSpellTask.incrementProgress(uuid, 1);
                } else if (learnSpellTask.getSpell().equalsIgnoreCase(event.getSpell().getName())) {
                    learnSpellTask.incrementProgress(uuid, 1);
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onInteract(PlayerInteractEntityEvent event) {
        if (event.getRightClicked() != null) {
            NPC npc = CitizensAPI.getNPCRegistry().getNPC(event.getRightClicked());
            if (npc == null) {
                return;
            }

            UUID uuid = event.getPlayer().getUniqueId();
            List<Task> tasks = questHandler.getTasks(uuid);

            for (Task task : tasks) {
                if (task instanceof InteractWithNPCTask) {
                    InteractWithNPCTask interactWithNPCTask = (InteractWithNPCTask) task;
                    if (interactWithNPCTask.getNPCs().contains(npc.getName())) {
                        interactWithNPCTask.incrementProgress(uuid, 1);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onRepair(CarbyneRepairedEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        List<Task> tasks = questHandler.getTasks(uuid);

        for (Task task : tasks) {
            if (task instanceof RepairCarbyneTask) {
                RepairCarbyneTask repairCarbyneTask = (RepairCarbyneTask) task;
                if (repairCarbyneTask.getGear().equalsIgnoreCase("any")) {
                    repairCarbyneTask.incrementProgress(uuid, 1);
                } else if (repairCarbyneTask.getGear().equalsIgnoreCase(event.getGear().getGearCode())) {
                    repairCarbyneTask.incrementProgress(uuid, 1);
                }
            }
        }
    }

    @EventHandler
    public void onJoinTown(NewTownEvent event) {
        Player player = Bukkit.getPlayer(event.getTown().getMayor().getName());
        if (player == null) {
            return;
        }

        List<Task> tasks = questHandler.getTasks(player.getUniqueId());

        for (Task task : tasks) {
            if (task instanceof JoinTownTask) {
                task.incrementProgress(player.getUniqueId(), 1);
            }
        }
    }

    @EventHandler
    public void onNation(NewNationEvent event) {
        Player player = Bukkit.getPlayer(event.getNation().getCapital().getMayor().getName());
        if (player == null) {
            return;
        }

        List<Task> tasks = questHandler.getTasks(player.getUniqueId());

        for (Task task : tasks) {
            if (task instanceof CreateNationTask) {
                task.incrementProgress(player.getUniqueId(), 1);
            }
        }
    }

    @EventHandler
    public void onJoinTown(TownAddResidentEvent event) {
        Player player = Bukkit.getPlayer(event.getResident().getName());
        if (player == null) {
            return;
        }

        List<Task> tasks = questHandler.getTasks(player.getUniqueId());

        for (Task task : tasks) {
            if (task instanceof JoinTownTask) {
                task.incrementProgress(player.getUniqueId(), 1);
            }
        }
    }

    //----------------------------------------------------------------------------------------------

    @EventHandler
    public void onNewProfile(ProfileCreatedEvent event) {
        Quest quest = StaticClasses.questHandler.findForcedQuest(event.getPlayerProfile());
        if (quest != null) {
            quest.takeQuest(event.getPlayer());
        }
    }

    @EventHandler
    public void onQuestDelete(InventoryClickEvent event) {
        Inventory clickedInventory = event.getInventory();
        if (clickedInventory == null) {
            return;
        }

        if (!clickedInventory.getTitle().equals("§c§lSkip Quest")) {
            return;
        }

        Player player = (Player) event.getWhoClicked();

        event.setCancelled(true);

        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem == null) {
            return;
        }

        Quest quest = StaticClasses.questHandler.getQuest(clickedItem);
        if (quest == null) {
            return;
        }

        if (clickedItem.getType() == Material.EMERALD_BLOCK) {
            quest.skipQuest(player);
            player.openInventory(StaticClasses.questHandler.getQuestInventory(player));
        } else if (clickedItem.getType() == Material.REDSTONE_BLOCK) {
            MessageManager.sendMessage(player, "&cCancelled.");
            player.openInventory(StaticClasses.questHandler.getQuestInventory(player));
        }
    }

    @EventHandler
    public void onQuestClick(InventoryClickEvent event) {
        Inventory clickedInventory = event.getInventory();
        if (clickedInventory == null) {
            return;
        }

        if (!clickedInventory.getTitle().equals("§d§lQuests")) {
            return;
        }

        Player player = (Player) event.getWhoClicked();

        event.setCancelled(true);

        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem == null) {
            return;
        }

        Quest quest = StaticClasses.questHandler.getQuest(clickedItem);
        if (quest == null) {
            return;
        }

        if (event.getAction() == InventoryAction.PICKUP_HALF) {
            Profile profile = StaticClasses.profileManager.getProfile(player.getUniqueId());
            profile.checkQuestSkip();
            if (profile.getQuestSkipsLeft() <= 0) {
                MessageManager.sendMessage(player, "&cYou can skip more quests in &7" + profile.getSkipTimeString());
                return;
            }

            if (quest.isForcedQuest()) {
                MessageManager.sendMessage(player, "&cYou may not skip this quest.");
                return;
            }

            StaticClasses.questHandler.handleDelete(quest, player);
        } else if (event.getAction() == InventoryAction.PICKUP_ALL) {
            List<Quest> quests = StaticClasses.questHandler.getQuests(player.getUniqueId());

            if (quest.getPlayers().containsKey(player.getUniqueId())) {
                if (quest.completeQuest(player)) {
                    StaticClasses.questHandler.handleComplete(quest, event.getSlot(), player, clickedInventory);
                }
            } else {
                if (player.hasPermission("carbyne.donator")) {
                    if (quests.size() >= 4) {
                        MessageManager.sendMessage(player, "&cYou cannot have more than 4 quests at a time.");
                        return;
                    }
                } else {
                    if (quests.size() >= 2) {
                        MessageManager.sendMessage(player, "&cYou cannot have more than 2 quests at a time.");
                        return;
                    }
                }

                quest.takeQuest(player);
                clickedInventory.setItem(event.getSlot(), quest.getDisplayItem(player, true));
            }
        }
    }
}
