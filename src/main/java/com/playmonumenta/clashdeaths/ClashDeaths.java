package com.playmonumenta.clashdeaths;

import org.bukkit.plugin.java.JavaPlugin;

public class ClashDeaths extends JavaPlugin {
	@Override
	public void onEnable() {
		getServer().getPluginManager().registerEvents(new DeathListener(), this);
	}
}
