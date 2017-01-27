package com.medievallords.carbyne.skill;

import org.bukkit.entity.Player;

public abstract class Special {
	
	public Special() {
		
	}
	
	public abstract boolean run(Player caster);
	
	public abstract String getName();
}
