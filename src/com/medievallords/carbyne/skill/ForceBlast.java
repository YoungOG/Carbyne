package com.medievallords.carbyne.skill;

import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class ForceBlast {

	private final FireworkEffect.Builder builder = FireworkEffect.builder();
	private final FireworkEffect fe = builder.flicker(true).with(Type.BURST).withColor(Color.BLUE).withColor(Color.ORANGE).trail(false).build();
	
	public boolean run(Player caster) {
		throwEntities(caster);
		return true;
	}

	public void throwEntities(Player caster) {
		Vector direction = caster.getLocation().getDirection().multiply(2);
		for (Entity e : caster.getNearbyEntities(7, 7, 7)) {
			if (e == null) continue;
			if (!(e instanceof LivingEntity)) continue;
			if (e.equals(caster)) continue;
//			CustomEntityFirework.spawn(e.getLocation(), fe);
			e.setVelocity(direction);
		}
	}
	
	public String getName() {
		return "ForceBlast";
	}
}
