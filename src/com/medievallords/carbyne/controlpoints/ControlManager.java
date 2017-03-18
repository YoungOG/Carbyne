package com.medievallords.carbyne.controlpoints;

import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.utils.MessageManager;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Williams on 2017-03-16.
 * for the Carbyne project.
 */
@Getter
@Setter
public class ControlManager {

    private Carbyne main = Carbyne.getInstance();
    private List<ControlPoint> controlPoints = new ArrayList<>();

    public void createControlPoint(Player player, Location location, String name, int timer){
        if (getControlPoint(name) != null) {
            MessageManager.sendMessage(player, "&cThere is already a controlpoint named that");
            return;
        }
        ControlPoint cp = new ControlPoint(location, name, timer);

        ConfigurationSection cps = main.getControlPointConfiguration().getConfigurationSection("ControlPoints");
        main.getControlPointConfiguration().createSection("ControlPoints." + cp.getName());
        main.getControlPointConfiguration().set("ControlPoints." + name + ".Location", location);
        main.getControlPointConfiguration().set("ControlPoints." + name + ".Timer", timer);
        List<String> cmds = new ArrayList<String>();
        cmds.add("say %player% has been rewarded for capturing a point");
        main.getControlPointConfiguration().set("ControlPoints." + name + ".CommandRewards", cmds);
        YamlConfiguration.loadConfiguration(main.getControlPointFile());

        controlPoints.add(cp);
        MessageManager.sendMessage(player, "&9" + cp.getName() + "  &ahas been created");
    }

    public void removeControlPoint(String name, Player player){
        ControlPoint cp = getControlPoint(name);
        if (cp == null) {
            MessageManager.sendMessage(player, "&cCould not find controlpoint &9" + name);
            return;
        }
        main.getControlPointConfiguration().set("ControlPoints." + name, null);
        YamlConfiguration.loadConfiguration(main.getControlPointFile());
        controlPoints.remove(cp);
        loadControlPoints();
    }

    public ControlPoint getControlPoint(String name){
        for(ControlPoint cp : controlPoints){
            if(cp.getName().equalsIgnoreCase(name)){
                return cp;
            }
        }
        return null;
    }

    public void loadControlPoints(){
        Bukkit.getServer().getScheduler().cancelAllTasks();

        controlPoints.clear();
        for(String name : main.getControlPointConfiguration().getConfigurationSection("ControlPoints").getKeys(false)){
            //Bukkit.getLogger().log(Level.SEVERE, plugin.getConfig().get("ControlPoints" + name + ".Location").toString() + "");
            ControlPoint cp = new ControlPoint((Location) main.getControlPointConfiguration().get("ControlPoints." + name + ".Location"), name, main.getControlPointConfiguration().getInt("ControlPoints." + name + ".Timer"));
            controlPoints.add(cp);
        }
    }

    public void setPlayer(Player player, String name){
        if(isCapturing(player) && main.getControlPointConfiguration().getBoolean("Multiple-Capturing")){
            MessageManager.sendMessage(player, "&cYou are already capturing a point");
            return;
        }
        ControlPoint cp = getControlPoint(name);
        if(cp == null){
            return;
        }
        cp.setCapper(player);
        main.getTimerListener().scheduleRepeatingTask(cp, cp.getTimer());

    }

    public void removePlayer(Player player, String name){
        ControlPoint cp = null;
        for(ControlPoint cppoint : controlPoints){
            if(cppoint.getCapper().equals(player)){
                cp = cppoint;
                cp.setCapper(null);
                return;
            }
        }

    }

    public boolean isCapturing(Player player){
        for(ControlPoint cp : controlPoints){
            if(cp.getCapper() == player){
                return true;
            }
        }
        return false;
    }

    public ControlPoint getControlPoint(Player player) {
        for (ControlPoint controlPoint : controlPoints) {
            if (controlPoint.getCapper().equals(player)) {
                return controlPoint;
            }
        }
        return null;
    }
}
