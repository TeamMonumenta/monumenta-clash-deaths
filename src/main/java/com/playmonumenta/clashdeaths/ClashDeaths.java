package com.playmonumenta.clashdeaths;

import java.io.File;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class ClashDeaths extends JavaPlugin {
	private FileConfiguration mConfig;
	private File mConfigFile = new File(getDataFolder(), "config.yml");;
	public DeathManager mDeathManager = null;
	private int mDebugLevel;

	@Override
	public void onLoad() {
		/*
		 * CommandAPI commands which register directly and are usable in functions
		 *
		 * These need to register immediately on load to prevent function loading errors
		 */
		CommandReloadDeaths.register(this);
	}

	@Override
	public void onEnable() {
		mDeathManager = DeathManager.getInstance(this);

		getServer().getPluginManager().registerEvents(mDeathManager, this);

		reloadConfig(null);
	}

	public void reloadConfig(CommandSender sender) {
		reloadConfigYaml(sender);
		mDeathManager.reload(this, sender);
	}

	private void reloadConfigYaml(CommandSender sender) {
		mConfig = YamlConfiguration.loadConfiguration(mConfigFile);

		if (mConfig.isInt("debug_messages")) {
			mDebugLevel = mConfig.getInt("debug_messages", 0);
		} else {
			mDebugLevel = 0;
		}
	}

	public int debugMessagLevel() {
		return mDebugLevel;
	}
}
