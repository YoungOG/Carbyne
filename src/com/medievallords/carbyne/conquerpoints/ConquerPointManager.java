package com.medievallords.carbyne.conquerpoints;

import java.util.ArrayList;

import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.conquerpoints.objects.ConquerPoint;
import com.medievallords.carbyne.utils.LocationSerialization;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class ConquerPointManager {

    private Carbyne main = Carbyne.getInstance();
    @Getter
	private ArrayList<ConquerPoint> conquerPoints = new ArrayList<>();

	public ConquerPointManager() {
		loadControlPoints();
    }

	public void saveControlPoints() {
        FileConfiguration conquerPointsFileConfig = main.getConquerPointFileConfiguration();

		if (conquerPoints.size() < 1)
			return;

		for (ConquerPoint conquerPoint : conquerPoints) {
			String path = conquerPoint.getName() + ".";
			conquerPointsFileConfig.set(path + "pos1", LocationSerialization.serializeLocation(conquerPoint.getPos1()));
            conquerPointsFileConfig.set(path + "pos2", LocationSerialization.serializeLocation(conquerPoint.getPos2()));
		}
		try {
			conquerPointsFileConfig.save(main.getConquerPointFile());
		} catch (Exception ignored) { }
	}

	public void loadControlPoints() {
        FileConfiguration conquerPointsFileConfig = main.getConquerPointFileConfiguration();

        for (String id : conquerPointsFileConfig.getKeys(false)) {
            String path = id + ".";

            conquerPoints.add(new ConquerPoint(id, LocationSerialization.deserializeLocation(conquerPointsFileConfig.getString(path + "pos1")),
                    LocationSerialization.deserializeLocation(conquerPointsFileConfig.getString(path + "pos2"))));
        }
    }

	public ConquerPoint getConquerPoint(String name) {
		for (ConquerPoint conquerPoint : conquerPoints)
			if (conquerPoint.getName().equalsIgnoreCase(name))
				return conquerPoint;
		return null;
	}

	public ConquerPoint getConquerPoint(Location location) {
	    for (ConquerPoint conquerPoint : conquerPoints) {
	        if (isInside(location, conquerPoint.getPos1(), conquerPoint.getPos2()))
	            return conquerPoint;
        }

        return null;
    }

	public boolean isCapturing(Player player) {
		for (ConquerPoint conquerPoint : conquerPoints) {
		    if (conquerPoint.getHolder().equals(player.getUniqueId()))
		        return true;
        }
		return false;
	}

//	public ConquerPoint getPlayerConquerPoint(Player p) {
//		for (ConquerPoint cp : getConquerPoints()) {
//            try {
//                if (cp.getHolder().equals(TownyUniverse.getDataSource().getResident(p.getName()).getTown().getNation())) {
//                    return cp;
//                }
//            } catch (NotRegisteredException e) {
//                e.printStackTrace();
//                return null;
//            }
//		}
//		return null;
//	}

	public boolean isInside(Location loc, Location l1, Location l2) {
		int x1 = Math.min(l1.getBlockX(), l2.getBlockX());
		int y1 = Math.min(l1.getBlockY(), l2.getBlockY());
		int z1 = Math.min(l1.getBlockZ(), l2.getBlockZ());
		int x2 = Math.max(l1.getBlockX(), l2.getBlockX());
		int y2 = Math.max(l1.getBlockY(), l2.getBlockY());
		int z2 = Math.max(l1.getBlockZ(), l2.getBlockZ());
		return loc.getX() >= x1 && loc.getX() <= x2 && loc.getY() >= y1 && loc.getY() <= y2 && loc.getZ() >= z1 && loc.getZ() <= z2;
	}
}