package com.medievallords.carbyne.gear.listeners;


import com.codingforcookies.armorequip.ArmorEquipEvent;
import com.google.common.collect.ImmutableList;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.customevents.CarbyneRepairedEvent;
import com.medievallords.carbyne.duels.duel.Duel;
import com.medievallords.carbyne.gear.GearManager;
import com.medievallords.carbyne.gear.effects.WeakenedEffect;
import com.medievallords.carbyne.gear.types.CarbyneGear;
import com.medievallords.carbyne.gear.types.carbyne.CarbyneArmor;
import com.medievallords.carbyne.gear.types.carbyne.CarbyneWeapon;
import com.medievallords.carbyne.gear.types.minecraft.MinecraftArmor;
import com.medievallords.carbyne.gear.types.minecraft.MinecraftWeapon;
import com.medievallords.carbyne.region.Region;
import com.medievallords.carbyne.region.RegionUser;
import com.medievallords.carbyne.region.Selection;
import com.medievallords.carbyne.utils.*;
import com.medievallords.carbyne.utils.scoreboard.Board;
import com.medievallords.carbyne.utils.scoreboard.BoardCooldown;
import com.medievallords.carbyne.utils.scoreboard.BoardFormat;
import com.palmergames.bukkit.towny.object.TownyUniverse;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class GearListeners implements Listener {

    private static final List<BlockFace> ALL_DIRECTIONS = ImmutableList.of(BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST);
    private final Map<UUID, Set<Location>> previousUpdates = new HashMap<>();
    private final ExecutorService executorService = Executors.newSingleThreadExecutor(new ThreadFactoryBuilder().setNameFormat("ForceField Thread").build());
    private Carbyne carbyne = Carbyne.getInstance();
    private GearManager gearManager = carbyne.getGearManager();
    private HashMap<UUID, WeakenedEffect> weakEffects = new HashMap<>();

    public void removeFromExhaust(Player player) {
        weakEffects.remove(player.getUniqueId());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();

            if (event.getCause() == EntityDamageEvent.DamageCause.FALL) {
                event.setCancelled(true);
                return;
            }

            if (player.getHealth() <= 32 && !weakEffects.containsKey(player.getUniqueId())) {
                WeakenedEffect weakenedEffect = new WeakenedEffect(player);
                weakenedEffect.runTaskTimerAsynchronously(carbyne, 0, 1);
                weakEffects.put(player.getUniqueId(), weakenedEffect);
            }

            if (carbyne.getDuelManager().getDuelFromUUID(player.getUniqueId()) != null)
                return;

            if (player.isDead()) {
                event.setCancelled(true);
                return;
            }

            float damage = gearManager.calculateDamage(player, event.getCause(), event.getDamage());

            event.setDamage(damage);
        }
    }

    @EventHandler
    public void onHealthRegain(EntityRegainHealthEvent event) {
        if (event.getEntityType() == EntityType.PLAYER)
            event.setAmount(event.getAmount() * 5);
    }

    @EventHandler
    public void onRodThrow(PlayerFishEvent event) {
        if (event.getCaught() != null && event.getCaught() instanceof Player) {
            Player caught = (Player) event.getCaught();
            Vector direction = caught.getLocation().toVector().subtract(event.getPlayer().getLocation().toVector()).normalize().multiply(1.3);
            direction.setX(direction.getX() * -1);
            direction.setY(direction.getY() * -1);
            direction.setZ(direction.getZ() * -1);
            caught.setVelocity(direction);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityDamagebyEntity(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player) {
            Player damager = (Player) event.getDamager();

            if (damager.hasPotionEffect(PotionEffectType.INCREASE_DAMAGE))
                for (PotionEffect effect : damager.getActivePotionEffects())
                    if (effect.getType().equals(PotionEffectType.INCREASE_DAMAGE)) {
                        event.setDamage(event.getDamage() * (1 + (0.1 * effect.getAmplifier())));
                        break;
                    }
        }

        if (event.getEntity() instanceof Player) {
            Player damaged = (Player) event.getEntity();

            if (TownyUniverse.getTownBlock(damaged.getLocation()) != null && !TownyUniverse.getTownBlock(damaged.getLocation()).getPermissions().pvp) {
                if (carbyne.getDuelManager().getDuelFromUUID(damaged.getUniqueId()) != null)
                    return;

                return;
            }

            for (ItemStack itemStack : damaged.getInventory().getArmorContents()) {
                if (gearManager.isCarbyneArmor(itemStack) || gearManager.isCarbyneWeapon(itemStack)) {
                    CarbyneGear carbyneGear = gearManager.getCarbyneGear(itemStack);

                    if (carbyneGear != null) {
                        if (carbyneGear instanceof CarbyneArmor) {
                            CarbyneArmor carbyneArmor = (CarbyneArmor) carbyneGear;

                            //if (itemStack.getType().equals(Material.LEATHER_CHESTPLATE) || itemStack.getType().equals(Material.LEATHER_LEGGINGS)) {
                            if (carbyneArmor.getDefensivePotionEffects().size() > 0)
                                carbyneArmor.applyDefensiveEffect(damaged);

                            if (carbyneArmor.getOffensivePotionEffects().size() > 0)
                                if (event.getDamager() != null && event.getDamager() instanceof Player)
                                    carbyneArmor.applyOffensiveEffect((Player) event.getDamager());

                            carbyneArmor.damageItem(damaged, itemStack);
                        }

                        if (carbyneGear instanceof CarbyneWeapon) {
                            CarbyneWeapon carbyneWeapon = (CarbyneWeapon) carbyneGear;

                            carbyneWeapon.damageItem(damaged, itemStack);
                        }
                    }
                }
            }
        }

        if (event.getDamager() instanceof Player) {
            Player damager = (Player) event.getDamager();
            ItemStack itemStack = damager.getItemInHand();

            if (gearManager.isCarbyneWeapon(itemStack)) {
                CarbyneWeapon carbyneWeapon = gearManager.getCarbyneWeapon(itemStack);

                if (carbyneWeapon != null) {
                    if (event.getEntity() != null && event.getEntity() instanceof Player)
                        if (carbyneWeapon.getOffensivePotionEffects().size() > 0)
                            carbyneWeapon.applyOffensiveEffect((Player) event.getEntity());

                    if (carbyneWeapon.getDefensivePotionEffects().size() > 0)
                        carbyneWeapon.applyDefensiveEffect(damager);

                    carbyneWeapon.damageItem(damager, itemStack);
                }
            }
        }
    }

    @EventHandler
    public void onDurabilityLoss(PlayerItemDamageEvent event) {
        ItemStack item = event.getItem();

        if (gearManager.isCarbyneWeapon(item) || gearManager.isCarbyneArmor(item))
            event.setCancelled(true);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        ItemStack itemStack = player.getInventory().getItemInHand();

        if (itemStack == null || itemStack.getType() == Material.AIR)
            return;

        CarbyneGear gear = gearManager.getCarbyneGear(itemStack);

        if (gear == null)
            return;

        if (gear instanceof CarbyneWeapon) {
            CarbyneWeapon carbyneWeapon = (CarbyneWeapon) gear;

            carbyneWeapon.damageItem(player, itemStack);
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        PlayerUtility.checkForIllegalItems(event.getPlayer(), event.getPlayer().getInventory());

        if (!event.getAction().equals(Action.RIGHT_CLICK_AIR) && !event.getAction().equals(Action.RIGHT_CLICK_BLOCK))
            return;

        if (!event.getPlayer().isSneaking())
            return;

        ItemStack itemStack = event.getPlayer().getInventory().getItemInHand();
        if (itemStack == null)
            return;

        CarbyneWeapon carbyneWeapon = gearManager.getCarbyneWeapon(itemStack);
        if (carbyneWeapon == null)
            return;

        Duel duel = Carbyne.getInstance().getDuelManager().getDuelFromUUID(event.getPlayer().getUniqueId());
        if (duel != null)
            return;

        if (TownyUniverse.getTownBlock(event.getPlayer().getLocation()) != null && !TownyUniverse.getTownBlock(event.getPlayer().getLocation()).getPermissions().pvp)
            return;

        if (carbyneWeapon.getSpecial() != null) {
            if (carbyneWeapon.getSpecialCharge(itemStack) >= carbyneWeapon.getSpecial().getRequiredCharge() || event.getPlayer().hasPermission("carbyne.specials.override")) {

                Board board = Board.getByPlayer(event.getPlayer());
                if (board != null) {
                    BoardCooldown boardCooldown = board.getCooldown("special");

                    if (boardCooldown == null) {
                        carbyneWeapon.setSpecialCharge(itemStack, 0);
                        carbyneWeapon.getSpecial().callSpecial(event.getPlayer());

                        if (!event.getPlayer().hasPermission("carbyne.specials.override"))
                            new BoardCooldown(board, "special", 60.0D);
                    } else
                        MessageManager.sendMessage(event.getPlayer(), "&eYou cannot use another weapon special for another &6" + boardCooldown.getFormattedString(BoardFormat.SECONDS) + " &eseconds.");
                }
            } else
                MessageManager.sendMessage(event.getPlayer(), "&cYour weapon must be fully charged.");
        }
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        LivingEntity livingEntity = event.getEntity();

        if (livingEntity.getKiller() != null) {
            ItemStack itemStack = livingEntity.getKiller().getItemInHand();

            if (gearManager.isCarbyneWeapon(itemStack)) {
                CarbyneWeapon carbyneWeapon = gearManager.getCarbyneWeapon(itemStack);

                if (carbyneWeapon == null)
                    return;

                if (carbyneWeapon.getSpecial() == null)
                    return;

                int specialCharge = carbyneWeapon.getSpecialCharge(itemStack);

                if (specialCharge >= carbyneWeapon.getSpecial().getRequiredCharge()) {
//                    MessageManager.sendMessage(livingEntity.getKiller(), "&7[&aCarbyne&7]: &aYour &b" + carbyneWeapon.getSpecial().getSpecialName() + " &aweapon special is fully charged!");
                    carbyneWeapon.setSpecialCharge(itemStack, carbyneWeapon.getSpecial().getRequiredCharge());
                    return;
                }

                if (livingEntity instanceof Player) {
                    if (Cooldowns.tryCooldown(livingEntity.getKiller().getUniqueId(), livingEntity.getUniqueId().toString() + ":charge", 300000))
                        specialCharge += 10;
                } else if (livingEntity instanceof Monster)
                    specialCharge += 5;

                carbyneWeapon.setSpecialCharge(itemStack, specialCharge);
            }
        }
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (event.getCurrentItem() != null && event.getCurrentItem().getType() != Material.AIR) {
            if (gearManager.isDefaultArmor(event.getCurrentItem())) {
                if (event.getInventory() != null)
                    if (gearManager.getGearGuiManager().isCustomInventory(event.getInventory()))
                        return;

                MinecraftArmor minecraftArmor = gearManager.getDefaultArmor(event.getCurrentItem());

                if (minecraftArmor != null)
                    event.setCurrentItem(gearManager.convertDefaultItem(event.getCurrentItem()));
            } else if (gearManager.isDefaultWeapon(event.getCurrentItem())) {
                if (event.getInventory() != null)
                    if (gearManager.getGearGuiManager().isCustomInventory(event.getInventory()))
                        return;

                MinecraftWeapon minecraftWeapon = gearManager.getDefaultWeapon(event.getCurrentItem());

                if (minecraftWeapon != null)
                    event.setCurrentItem(gearManager.convertDefaultItem(event.getCurrentItem()));

            } else if (event.getCurrentItem().getType() == Material.NETHER_STAR && !event.getInventory().getTitle().contains("Spell"))
                event.setCurrentItem(new ItemBuilder(gearManager.getTokenItem()).amount(event.getCurrentItem().getAmount()).build());
        }
    }

    @EventHandler
    public void onCraft(CraftItemEvent event) {
        ItemStack itemStack = event.getCurrentItem();
        Player player = (Player) event.getWhoClicked();

        if (gearManager.isCarbyneArmor(itemStack) || gearManager.isCarbyneWeapon(itemStack)) {
            event.setCancelled(true);
            MessageManager.sendMessage(player, "&cYou cannot craft with Carbyne gear.");
        }
    }

    @EventHandler
    public void onEnchant(EnchantItemEvent event) {
        ItemStack itemStack = event.getItem();
        Player player = event.getEnchanter();

        if (gearManager.isCarbyneArmor(itemStack) || gearManager.isCarbyneWeapon(itemStack)) {
            event.setCancelled(true);
            MessageManager.sendMessage(player, "&cYou cannot enchant Carbyne gear.");
        }
    }

    @EventHandler
    public void onAnvilUse(InventoryClickEvent event) {
        Inventory inv = event.getInventory();

        if (inv instanceof AnvilInventory)
            if (inv.getItem(0) != null)
                if (gearManager.isCarbyneArmor(inv.getItem(0)) || gearManager.isCarbyneWeapon(inv.getItem(0))) {
                    event.setCancelled(true);
                    MessageManager.sendMessage(event.getWhoClicked(), "&cYou cannot enchant or re-name Carbyne gear.");
                }
    }

    @EventHandler
    public void onRepairCarbyne(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Block block = event.getClickedBlock();

            if (block.getType() != Material.ANVIL)
                return;


            Player player = event.getPlayer();

            ItemStack itemStack = player.getItemInHand();

            if (itemStack == null)
                return;

            CarbyneGear gear = gearManager.getCarbyneGear(itemStack);

            if (gear == null)
                return;


            if (gear.getDurability(itemStack) >= gear.getMaxDurability()) {
                player.closeInventory();
                MessageManager.sendMessage(player, "&aThis item is already fully repaired.");
                event.setCancelled(true);
                return;
            }
            if (!player.getInventory().containsAtLeast(gearManager.getTokenItem(), 1)) {
                MessageManager.sendMessage(player, "&cYou do not have enough carbyne ingots.");
                event.setCancelled(true);
                return;
            }

            int repairCost = gear.getRepairCost(itemStack);

            if (!player.getInventory().containsAtLeast(gearManager.getTokenItem(), repairCost)) {
                int amountOfIngots = getAmountOfIngots(player.getInventory(), gearManager.getTokenMaterial(), gearManager.getTokenData());

                removeItems(player.getInventory(), gearManager.getTokenMaterial(), gearManager.getTokenData(), repairCost);

                event.setCancelled(true);
                Item item = player.getWorld().dropItem(block.getLocation().add(0.5, 1.15, 0.5), player.getItemInHand());
                item.setVelocity(new Vector(0, 0, 0));
                item.teleport(block.getLocation().add(0.5, 1.1, 0.5));
                ParticleEffect.LAVA.display(0, 0, 0, 0, 2, block.getLocation().add(0.5, 0.15, 0.5), 40, false);
                player.getWorld().playSound(block.getLocation(), Sound.FIREWORK_BLAST2, 10f, 1f);
                item.setPickupDelay(1000000000);
                player.setItemInHand(null);
                player.updateInventory();
                int breakTime = 0;

                if (Math.random() < 0.05)
                    if ((repairCost * repairCost) - 3 > 0) {
                        Random random = new Random();
                        breakTime = random.nextInt((repairCost * repairCost) - 3) + 3;
                    }

                repairItem(player, item, amountOfIngots, gear, block.getLocation().add(0.5, 1.12, 0.5), breakTime);
            } else {
                removeItems(player.getInventory(), gearManager.getTokenMaterial(), gearManager.getTokenData(), repairCost);

                event.setCancelled(true);
                Item item = player.getWorld().dropItem(block.getLocation().add(0.5, 1.15, 0.5), player.getItemInHand());
                item.setVelocity(new Vector(0, 0, 0));
                item.teleport(block.getLocation().add(0.5, 1.1, 0.5));
                ParticleEffect.LAVA.display(0, 0, 0, 0, 2, block.getLocation().add(0.5, 1.12, 0.5), 40, false);
                player.getWorld().playSound(block.getLocation(), Sound.FIREWORK_BLAST2, 10f, 1f);
                item.setPickupDelay(1000000000);
                player.setItemInHand(null);
                player.updateInventory();
                int breakTime = 0;

                if (Math.random() < 0.05)
                    if ((repairCost * repairCost) - 3 > 0) {
                        Random random = new Random();
                        breakTime = random.nextInt((repairCost * repairCost) - 3) + 3;
                    }

                repairItem(player, item, repairCost, gear, block.getLocation().add(0.5, 1.12, 0.5), breakTime);
            }
        }
    }

    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent event) {
        Player player = (Player) event.getPlayer();
        Inventory inventory = event.getInventory();

        PlayerUtility.checkForIllegalItems(player, inventory);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        Inventory inventory = event.getInventory();

        PlayerUtility.checkForIllegalItems(player, inventory);
    }

    @EventHandler
    public void itemDrop(ItemSpawnEvent event) {
        gearManager.convertToMoneyItem(event.getEntity().getItemStack());
    }

    @EventHandler
    public void onPick(PlayerPickupItemEvent event) {
        new BukkitRunnable() {
            public void run() {
                PlayerUtility.checkForIllegalItems(event.getPlayer(), event.getPlayer().getInventory());
            }
        }.runTaskLater(Carbyne.getInstance(), 5L);
    }

    @EventHandler
    public void onConsume(PlayerItemConsumeEvent event) {
        Player player = event.getPlayer();
        ItemStack itemStack = event.getItem();

        if (itemStack.getType() == Material.POTION) {
            event.setCancelled(true);

            player.setItemInHand(new ItemStack(Material.GLASS_BOTTLE));

            Potion potion = Potion.fromItemStack(itemStack);

            for (PotionEffect effect : potion.getEffects())
                player.addPotionEffect(new PotionEffect(effect.getType(), effect.getDuration(), effect.getAmplifier(), false, false), true);
        }
    }

    @EventHandler
    public void onPotionSplash(PotionSplashEvent event) {
        event.setCancelled(true);

        for (Entity affectedEntity : event.getAffectedEntities()) {
            if (affectedEntity instanceof Player) {
                Player affectedPlayer = (Player) affectedEntity;

                for (PotionEffect effect : event.getPotion().getEffects())
                    affectedPlayer.addPotionEffect(new PotionEffect(effect.getType(), effect.getDuration(), effect.getAmplifier(), false, false), true);
            }
        }
    }

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        if (event.getCause() == PlayerTeleportEvent.TeleportCause.COMMAND || event.getCause() == PlayerTeleportEvent.TeleportCause.NETHER_PORTAL || event.getCause() == PlayerTeleportEvent.TeleportCause.END_PORTAL) {
            gearManager.getGearEffects().effectTeleport(event.getPlayer(), event.getFrom());
            gearManager.getGearEffects().effectTeleport(event.getPlayer(), event.getTo());
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        player.setMaxHealth(100);
        player.setHealthScale(20);

        if (gearManager.getPlayerGearFadeSchedulers().containsKey(player.getUniqueId())) {
            gearManager.getPlayerGearFadeSchedulers().get(player.getUniqueId()).cancel();
            gearManager.getPlayerGearFadeSchedulers().remove(player.getUniqueId());
        }

        gearManager.getPlayerGearFadeSchedulers().put(player.getUniqueId(), new BukkitRunnable() {
            boolean shouldFadeUp = false;
            int prizedHue = 0;

            @Override
            public void run() {
                if (!player.isOnline()) {
                    gearManager.getPlayerGearFadeSchedulers().remove(player.getUniqueId());
                    cancel();
                }

                if (player.getInventory().getArmorContents().length > 0)
                    for (ItemStack itemStack : player.getInventory().getArmorContents()) {
                        boolean isPrized;

                        if (itemStack == null || itemStack.getType() == Material.AIR)
                            continue;

                        if (gearManager.isCarbyneArmor(itemStack)) {
                            CarbyneArmor armor = gearManager.getCarbyneArmor(itemStack);

                            if (armor == null)
                                continue;

                            if (armor.isPolished(itemStack)) {
                                isPrized = armor.getGearCode().equalsIgnoreCase("PH") || armor.getGearCode().equalsIgnoreCase("PC") || armor.getGearCode().equalsIgnoreCase("PL") || armor.getGearCode().equalsIgnoreCase("PB");

                                LeatherArmorMeta laMeta = (LeatherArmorMeta) itemStack.getItemMeta();

                                int r = laMeta.getColor().getRed(),
                                        g = laMeta.getColor().getGreen(),
                                        b = laMeta.getColor().getBlue();

                                if (r >= armor.getMaxFadeColor().getRed() && g >= armor.getMaxFadeColor().getGreen() && b >= armor.getMaxFadeColor().getBlue())
                                    shouldFadeUp = false;
                                else if (r <= armor.getMinFadeColor().getRed() && g <= armor.getMinFadeColor().getGreen() && b <= armor.getMinFadeColor().getBlue())
                                    shouldFadeUp = true;

                                if (isPrized) {
                                    if (shouldFadeUp)
                                        prizedHue++;
                                    else
                                        prizedHue--;

                                    r = HSLColor.toRGB(prizedHue, 100, 50).getRed();
                                    g = HSLColor.toRGB(prizedHue, 100, 50).getGreen();
                                    b = HSLColor.toRGB(prizedHue, 100, 50).getBlue();
                                } else if (shouldFadeUp) {
                                    r += armor.getTickFadeColor()[0];
                                    g += armor.getTickFadeColor()[1];
                                    b += armor.getTickFadeColor()[2];
                                } else {
                                    r -= armor.getTickFadeColor()[0];
                                    g -= armor.getTickFadeColor()[1];
                                    b -= armor.getTickFadeColor()[2];
                                }

                                if (r < 0)
                                    r = 0;
                                else if (r > 255)
                                    r = 255;

                                if (g < 0)
                                    g = 0;
                                else if (g > 255)
                                    g = 255;

                                if (b < 0)
                                    b = 0;
                                else if (b > 255)
                                    b = 255;

                                Color color = Color.fromRGB(r, g, b);

                                laMeta.setColor(color);
                                itemStack.setItemMeta(laMeta);
                            }
                        }
                    }
            }
        }.runTaskTimerAsynchronously(Carbyne.getInstance(), 0L, 5L));
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        if (gearManager.getPlayerGearFadeSchedulers().containsKey(player.getUniqueId())) {
            gearManager.getPlayerGearFadeSchedulers().get(player.getUniqueId()).cancel();
            gearManager.getPlayerGearFadeSchedulers().remove(player.getUniqueId());
        }

        if (player.getInventory().getArmorContents().length > 0)
            for (ItemStack itemStack : player.getInventory().getArmorContents()) {
                if (itemStack == null)
                    continue;

                if (gearManager.isCarbyneArmor(itemStack)) {
                    CarbyneArmor armor = gearManager.getCarbyneArmor(itemStack);

                    if (!armor.isPolished(itemStack)) {
                        LeatherArmorMeta laMeta = (LeatherArmorMeta) itemStack.getItemMeta();
                        laMeta.setColor(armor.getBaseColor());
                        itemStack.setItemMeta(laMeta);
                    }
                }
            }
    }

    @EventHandler
    public void onEquip(ArmorEquipEvent event) {
        Player player = event.getPlayer();
        player.playSound(player.getLocation(), Sound.ENDERDRAGON_WINGS, 1, (float) Math.random() * 3);
        if (event.getOldArmorPiece() != null && event.getOldArmorPiece().getType() != Material.AIR) {
            ItemStack item = event.getOldArmorPiece();

            if (gearManager.isCarbyneArmor(item)) {
                CarbyneArmor armor = gearManager.getCarbyneArmor(item);

                if (armor.isPolished(item)) {
                    ItemMeta meta = item.getItemMeta();
                    LeatherArmorMeta lameta = (LeatherArmorMeta) meta;
                    lameta.setColor(armor.getMinFadeColor());
                    item.setItemMeta(lameta);
                } else {
                    ItemMeta meta = item.getItemMeta();
                    LeatherArmorMeta lameta = (LeatherArmorMeta) meta;
                    lameta.setColor(armor.getBaseColor());
                    item.setItemMeta(lameta);
                }
            }
        }
    }

    public void removeItems(Inventory inventory, Material type, int data, int amount) {
        if (amount <= 0)
            return;

        int size = inventory.getSize();

        for (int slot = 0; slot < size; slot++) {
            ItemStack is = inventory.getItem(slot);

            if (is == null)
                continue;

            if (type == is.getType() && is.getDurability() == data) {
                int newAmount = is.getAmount() - amount;

                if (newAmount > 0) {
                    is.setAmount(newAmount);
                    break;
                } else {
                    inventory.clear(slot);
                    amount = -newAmount;

                    if (amount == 0)
                        break;
                }
            }
        }
    }

    public int getAmountOfIngots(Inventory inventory, Material type, int data) {
        int amount = 0;
        int size = inventory.getSize();

        for (int slot = 0; slot < size; slot++) {
            ItemStack is = inventory.getItem(slot);

            if (is == null)
                continue;

            if (type == is.getType() && is.getDurability() == data)
                amount += is.getAmount();
        }
        return amount;
    }

    public void repairItem(Player player, Item item, int repairCost, CarbyneGear gear, Location location, int breakTime) {
        gearManager.getRepairItems().add(item);

        new BukkitRunnable() {
            int i = -1;
            boolean far = false;

            @Override
            public void run() {
                i++;

                if (breakTime > 0 && i >= breakTime) {
                    cancel();
                    MessageManager.sendMessage(player, "&cYour item broke!");

                    ParticleEffect.SMOKE_LARGE.display(0f, 0f, 0f, 0.002f, 4, location, 40, true);
                    player.getWorld().playSound(location, Sound.ANVIL_BREAK, 10f, (float) Math.random() * 2.5f);

                    if (item != null) {
                        gearManager.getRepairItems().remove(item);
                        item.remove();
                    }

                    return;
                }

                if ((!player.getWorld().getName().equals(location.getWorld().getName()) && !far) || ((player.getWorld().getName().equals(location.getWorld().getName()) && (player.getLocation().distance(location) >= 9 && !far)))) {
                    far = true;
                    MessageManager.sendMessage(player, "&cYou are too far from the anvil, your item will be dropped on the ground");
                }

                if (player.getWorld().getName().equals(location.getWorld().getName()) && (far && player.getLocation().distance(location) < 9)) {
                    far = false;
                    MessageManager.sendMessage(player, "&aYou are no longer too far away");
                }

                if (i >= repairCost * 4) {
                    cancel();

                    ItemStack itemStack = gear.getItem(false).clone();

                    if (!player.isOnline() || far) {
                        location.getWorld().dropItem(location, itemStack);

                        if (item != null) {
                            gearManager.getRepairItems().remove(item);
                            item.remove();
                        }

                        if (player.isOnline()) {
                            MessageManager.sendMessage(player, "&aYour item has been repaired.");
                            Bukkit.getServer().getPluginManager().callEvent(new CarbyneRepairedEvent(player, gear));
                        }
                    } else {
                        if (player.getItemInHand() != null) {
                            if (player.getInventory().firstEmpty() == -1) {
                                player.getWorld().dropItem(player.getLocation(), itemStack);
                                return;
                            }

                            player.getInventory().addItem(itemStack);
                        } else
                            player.setItemInHand(itemStack);

                        MessageManager.sendMessage(player, "&aYour item has been repaired.");

                        if (item != null) {
                            gearManager.getRepairItems().remove(item);
                            item.remove();
                        }

                        Bukkit.getServer().getPluginManager().callEvent(new CarbyneRepairedEvent(player, gear));
                    }
                }

                ParticleEffect.FLAME.display(0f, 0f, 0f, 0.075f, 20, location, 40, true);
                player.getWorld().playSound(location, Sound.ANVIL_USE, 10f, (float) Math.random() * 2.5f);
            }
        }.runTaskTimer(Carbyne.getInstance(), 0, 20);
    }

    @EventHandler
    public void onDraw(EntityShootBowEvent event) {
        if (event.getEntity() instanceof Player)
            if (event.getForce() < 0.5) {
                event.setCancelled(true);
                MessageManager.sendMessage(event.getEntity(), "&cYou must draw your bow back harder!");
            }
    }

    @EventHandler
    public void onWandInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (event.getItem() != null && event.getItem().getType() == Material.BONE && event.getClickedBlock() != null && player.hasPermission("carbyne.wand") && player.getGameMode() == GameMode.CREATIVE) {
            RegionUser user = RegionUser.getRegionUser(player);

            if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
                event.setCancelled(true);
                Selection selection = (user.getSelection() != null) ? user.getSelection() : new Selection(null, null);
                Location location = event.getClickedBlock().getLocation();
                location.setY(0.0);
                selection.setLocation1(location);
                user.setSelection(selection);
                MessageManager.sendMessage(player, "&aSelection 1: &b" + location.getWorld().getName() + "&a, &b" + location.getBlockX() + "&a, &b" + location.getBlockY() + "&a, &b" + location.getBlockZ() + "&a.");
            } else if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                event.setCancelled(true);
                Selection selection = (user.getSelection() != null) ? user.getSelection() : new Selection(null, null);
                Location location = event.getClickedBlock().getLocation();
                location.setY((double) location.getWorld().getMaxHeight());
                selection.setLocation2(location);
                user.setSelection(selection);
                MessageManager.sendMessage(player, "&aSelection 2: &b" + location.getWorld().getName() + "&a, &b" + location.getBlockX() + "&a, &b" + location.getBlockY() + "&a, &b" + location.getBlockZ() + "&a.");
            }
        }
    }

    @EventHandler
    public void shutdown(PluginDisableEvent event) {
        if (event.getPlugin() != carbyne)
            return;

        // Shutdown executor service and clean up threads
        executorService.shutdown();
        try {
            executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException ignore) {
        }

        // Go through all previous updates and revert spoofed blocks
        for (UUID uuid : previousUpdates.keySet()) {
            Player player = Bukkit.getPlayer(uuid);

            if (player == null)
                continue;

            for (Location location : previousUpdates.get(uuid)) {
                Block block = location.getBlock();
                player.sendBlockChange(location, block.getType(), block.getData());
            }
        }
    }

    @EventHandler
    public void onTeleport(PlayerTeleportEvent event) {
        Player player = event.getPlayer();

        if ((Region.get(event.getTo()) != null || Region.get(event.getFrom()) != null) && Region.get(event.getTo()) != null) {
            if (gearManager.isWearingCarbyne(player, true)) {
                RegionUser.getRegionUser(player).setExceptTeleportation(true);
                event.setCancelled(true);
                MessageManager.sendMessage(player, "&cYou cannot enter this area while using Carbyne Gear.");
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void updateViewedBlocks(PlayerMoveEvent event) {
        // Do nothing if player hasn't moved over a whole block
        Location t = event.getTo();
        Location f = event.getFrom();

        if (t.getBlockX() == f.getBlockX() && t.getBlockY() == f.getBlockY() && t.getBlockZ() == f.getBlockZ())
            return;

        Player player = event.getPlayer();

        if (gearManager.isWearingCarbyne(player, true))
            if ((Region.get(event.getTo()) == null || Region.get(event.getFrom()) == null) && Region.get(event.getTo()) != null) {
                RegionUser.getRegionUser(player).setExceptTeleportation(true);
                Location from = new Location(event.getFrom().getWorld(), (double) event.getFrom().getBlockX(), (double) event.getFrom().getBlockY(), (double) event.getFrom().getBlockZ(), event.getFrom().getYaw(), event.getFrom().getPitch());
                from.add(0.5, 0.0, 0.5);
                player.teleport(from);
                MessageManager.sendMessage(player, "&cYou cannot enter this area while using Carbyne Gear.");
            }

        // Asynchronously send block changes around player
        executorService.submit(() -> {
            // Stop processing if player has logged off
            UUID uuid = player.getUniqueId();

            if (Bukkit.getPlayer(uuid) == null)
                return;

            // Update the players force field perspective and find all blocks to stop spoofing
            Set<Location> changedBlocks = getChangedBlocks(player);

            Set<Location> removeBlocks;
            if (previousUpdates.containsKey(uuid))
                removeBlocks = previousUpdates.get(uuid);
            else
                removeBlocks = new HashSet<>();


            for (Location location : changedBlocks) {
                player.sendBlockChange(location, Material.STAINED_GLASS, (byte) 9);
                removeBlocks.remove(location);
            }

            // Remove no longer used spoofed blocks
            for (Location location : removeBlocks) {
                Block block = location.getBlock();
                player.sendBlockChange(location, block.getType(), block.getData());
            }

            previousUpdates.put(uuid, changedBlocks);
        });
    }

    private boolean isRegionSurrounding(Location loc) {
        for (BlockFace direction : ALL_DIRECTIONS) {
            Location location = loc.getBlock().getRelative(direction).getLocation();

            for (Region region : gearManager.getNerfedRegions())
                if (region.get(location) != null)
                    return true;
        }

        return false;
    }

    private Set<Location> getChangedBlocks(Player player) {
        Set<Location> locations = new HashSet<>();

        // Do nothing if player is not tagged
        if (!gearManager.isWearingCarbyne(player, true))
            return locations;

        // Find the radius around the player
        int r = 7;
        Location l = player.getLocation();
        Location loc1 = l.clone().add(r, 0, r);
        Location loc2 = l.clone().subtract(r, 0, r);
        int topBlockX = loc1.getBlockX() < loc2.getBlockX() ? loc2.getBlockX() : loc1.getBlockX();
        int bottomBlockX = loc1.getBlockX() > loc2.getBlockX() ? loc2.getBlockX() : loc1.getBlockX();
        int topBlockZ = loc1.getBlockZ() < loc2.getBlockZ() ? loc2.getBlockZ() : loc1.getBlockZ();
        int bottomBlockZ = loc1.getBlockZ() > loc2.getBlockZ() ? loc2.getBlockZ() : loc1.getBlockZ();

        // Iterate through all blocks surrounding the player
        for (int x = bottomBlockX; x <= topBlockX; x++) {
            for (int z = bottomBlockZ; z <= topBlockZ; z++) {
                // Location corresponding to current loop
                Location location = new Location(l.getWorld(), (double) x, l.getY(), (double) z);

                if (Region.get(location) == null) continue;

                if (!isRegionSurrounding(location)) continue;

                for (int i = -r; i < r; i++) {
                    Location loc4 = new Location(location.getWorld(), location.getX(), location.getY(), location.getZ());

                    loc4.setY(loc4.getY() + i);

                    // Do nothing if the block at the location is not air
                    if (!loc4.getBlock().getType().equals(Material.AIR)) continue;

                    // Add this location to locations
                    locations.add(new Location(loc4.getWorld(), loc4.getBlockX(), loc4.getBlockY(), loc4.getBlockZ()));
                }
            }
        }

        return locations;
    }

    public static class ForceFieldTask extends BukkitRunnable {

        private final Carbyne plugin;

        private final Map<UUID, Location> validLocations = new HashMap<>();

        private ForceFieldTask(Carbyne plugin) {
            this.plugin = plugin;
        }

        public static void run(Carbyne plugin) {
            new GearListeners.ForceFieldTask(plugin).runTaskTimer(plugin, 1, 1);
        }

        @Override
        public void run() {
            for (Player player : PlayerUtility.getOnlinePlayers()) {
                UUID playerId = player.getUniqueId();

                // Do nothing if player isn't even tagged.
                if (!plugin.getGearManager().isWearingCarbyne(player, true))
                    continue;

                Location loc = player.getLocation();

                if (Region.get(loc) != null)
                    // Track the last PVP-enabled location that the player was in.
                    validLocations.put(playerId, loc);
                else if (validLocations.containsKey(playerId))
                    // Teleport the player to the last valid PVP-enabled location.
                    player.teleport(validLocations.get(playerId));
            }
        }
    }
}