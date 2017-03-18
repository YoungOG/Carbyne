package com.medievallords.carbyne.controlpoints.listeners;

import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.controlpoints.ControlPoint;
import com.medievallords.carbyne.utils.MessageManager;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.HashMap;

/**
 * Created by Williams on 2017-03-16.
 * for the Carbyne project.
 */
@Getter
@Setter
public class TimerListener implements Listener{

    private Carbyne main = Carbyne.getInstance();

    private HashMap<ControlPoint, Integer> taskID = new HashMap<ControlPoint, Integer>();
    private HashMap<ControlPoint, Integer> capTimer = new HashMap<ControlPoint, Integer>();
    private HashMap<ControlPoint, Integer> capCooldown = new HashMap<ControlPoint, Integer>();
    private HashMap<ControlPoint, Integer> taskID2 = new HashMap<ControlPoint, Integer>();
    private HashMap<ControlPoint, Player> isCapCool = new HashMap<>();

    @EventHandler
    public void onInteract(PlayerInteractEvent e){
        if(e.getAction() == Action.RIGHT_CLICK_BLOCK){
            final Player player = e.getPlayer();
            for(final ControlPoint cp : main.getControlManager().getControlPoints()){
                if(e.getClickedBlock().getLocation().equals(cp.getLocation())){
                    e.setCancelled(true);
                    if(isCapCool.get(cp) != player && main.getControlManager().isCapturing(player)){
                        endTask2(cp);
                        MessageManager.sendMessage(player, "&aCapture continued");
                        return;
                    }
                    if(main.getControlManager().isCapturing(player) && main.getControlPointConfiguration().getBoolean("Multiple-Capturing")){
                        MessageManager.sendMessage(player, "&cYou are already capturing a point");
                        return;
                    }
                    if(isCapCool.get(cp) == player){
                        return;
                    }
                    scheduleRepeatingTask2(player, cp, 10);
                    isCapCool.put(cp, player);
                }
            }


        }
    }

    public void scheduleRepeatingTask(final ControlPoint cp, int timer){
        endTask(cp);
        cp.getLocation().getWorld().playEffect(cp.getLocation(), Effect.ENDER_SIGNAL, 10);
        MessageManager.sendMessage(cp.getCapper(), "&aYou captured the point!");
        capTimer.put(cp, timer);
        final int tid = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(main, new Runnable(){
            public void run(){
                if(capTimer.containsKey(cp)){
                    if(capTimer.get(cp) <= 0){
                        giveReward(cp);
                        capTimer.remove(cp);
                        cp.setCapper(null);
                        endTask(cp);
                        return;
                    }
                    capTimer.put(cp, capTimer.get(cp) - 1);
                }
            }
        },0, 20);

        taskID.put(cp, tid);
    }

    public void endTask(ControlPoint cp){
        if(taskID.containsKey(cp)){
            int tid = taskID.get(cp);
            Bukkit.getServer().getScheduler().cancelTask(tid);
            taskID.remove(cp);
        }
    }
    public void scheduleRepeatingTask2(final Player player, final ControlPoint cp, int timer){
        endTask2(cp);
        MessageManager.sendMessage(player, "&aStarted capturing the point");
        capCooldown.put(cp, timer);
        final int tid = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(main, new Runnable(){
            public void run(){
                if(capCooldown.containsKey(cp)){
                    if(capCooldown.get(cp) <= 0){
                        if(cp.getCapper() != null){
                            MessageManager.sendMessage(cp.getCapper(), "&9" + player.getName() + "&chas captured the point from you!");
                            cp.getCapper().playEffect(cp.getCapper().getEyeLocation(), Effect.ENDER_SIGNAL, 1);
                        }
                        main.getControlManager().setPlayer(player, cp.getName());
                        isCapCool.remove(player);
                        capCooldown.remove(cp);
                        endTask2(cp);
                        return;
                    }
                    capCooldown.put(cp, capCooldown.get(cp) - 1);
                    if(cp.getCapper() != null){
                        MessageManager.sendMessage(cp.getCapper(), "&9" + player.getName() + "&a is trying to capture your point!");
                        if(Bukkit.getServer().getBukkitVersion().contains("1.10")){
                            cp.getCapper().playEffect(cp.getCapper().getEyeLocation(), Effect.NOTE, 2);
                        }
                    }
                    cp.getLocation().getWorld().spawn(cp.getLocation(), Firework.class);
                }
            }
        },0, 20);

        taskID2.put(cp, tid);
    }

    public void endTask2(ControlPoint cp){
        if(taskID2.containsKey(cp)){
            int tid = taskID2.get(cp);
            Bukkit.getServer().getScheduler().cancelTask(tid);
            taskID2.remove(cp);
            isCapCool.remove(cp);
        }
    }
    public void giveReward(ControlPoint cp){
        for (String command : cp.getCommandRewards()) {
            Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), command.replace("%player%", cp.getCapper().getName()));
        }
			/*for (String items : plugin.getConfig().getConfigurationSection("ControlPoints." + cp.getName() + ".Rewards").getKeys(true)) {
				ItemStack itemtoadd = new ItemStack(Material.valueOf(plugin.getConfig().getString("ControlPoints." + cp.getName() + ".Rewards." + items + ".Type")));
				ItemMeta itemtoaddmeta = itemtoadd.getItemMeta();
				if(plugin.getConfig().getString(this.plugin.getConfig().getString("ControlPoints." + cp.getName() + ".Rewards." + items + ".Display")).length() >= 1){
					itemtoaddmeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', this.plugin.getConfig().getString("ControlPoints." + cp.getName() + ".Rewards." + items + ".Display")));
				}
				List<String> lores = new ArrayList();
				for (String lore : this.plugin.getConfig().getStringList("ControlPoints." + cp.getName() + ".Rewards." + items + ".Lore")) {
					lores.add(ChatColor.translateAlternateColorCodes('&', lore));
				}
				itemtoaddmeta.setLore(lores);
				for (String enchantments : this.plugin.getConfig().getStringList("ControlPoints." + cp.getName() + ".Rewards." + items + ".Enchantments")) {
					String[] enchants = enchantments.split(",");
					if (enchants.length == 2) {
						itemtoaddmeta.addEnchant(org.bukkit.enchantments.Enchantment.getByName(enchants[0]), Integer.parseInt(enchants[1]), true);

					}
				}
				itemtoadd.setDurability((short) this.plugin.getConfig().getInt("ControlPoints." + cp.getName() + ".Rewards." + items + ".Data"));
				itemtoadd.setItemMeta(itemtoaddmeta);
				cp.getCapper().getInventory().addItem(itemtoadd);

			}*/

    }
}
