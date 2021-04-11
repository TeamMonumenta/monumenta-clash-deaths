package com.playmonumenta.clashdeaths;

import java.lang.reflect.Method;
import java.util.List;

import com.google.gson.JsonObject;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.event.HoverEvent.ShowEntity;
import net.kyori.adventure.text.event.HoverEvent.ShowItem;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class DeathAction {
	private String mName;
	private String mDeathMsgKey;
	private boolean mDeathMsgKeyIsPrefix = false;
	private String mKillerType = null;
	private String mWeaponType = null;
	private String mCommand;

	public DeathAction(JsonObject object) throws Exception {
		if (object.get("name") == null
			|| object.get("name").getAsString() == null
			|| object.get("name").getAsString().isEmpty()) {
			throw new Exception("name must be text you can use to find the file later");
		}
		mName = object.get("name").getAsString();

		if (object.get("death_message_key") == null
			|| object.get("death_message_key").getAsString() == null
			|| object.get("death_message_key").getAsString().isEmpty()) {
			throw new Exception("death_message_key must be a translation key");
		}
		mDeathMsgKey = object.get("death_message_key").getAsString();
		if (mDeathMsgKey.equals("*")) {
			mDeathMsgKeyIsPrefix = true;
			mDeathMsgKey = "";
		} else if (mDeathMsgKey.endsWith("*")) {
			mDeathMsgKeyIsPrefix = true;
			mDeathMsgKey = mDeathMsgKey.substring(0, mDeathMsgKey.length()-1);
		}

		if (object.get("killer_type") != null) {
			if (object.get("killer_type").getAsString() == null
			    || object.get("killer_type").getAsString().isEmpty()) {
				throw new Exception("killer_type must missing or namespaced mob ID");
			}
			mKillerType = object.get("killer_type").getAsString();
		}

		if (object.get("weapon_type") != null) {
			if (object.get("weapon_type").getAsString() == null
			    || object.get("weapon_type").getAsString().isEmpty()) {
				throw new Exception("weapon_type must missing or namespaced item ID");
			}
			mWeaponType = object.get("weapon_type").getAsString();
		}

		if (object.get("command") == null
			|| object.get("command").getAsString() == null) {
			throw new Exception("command must be a command string");
		}
		mCommand = object.get("command").getAsString();
		if (mCommand.startsWith("/")) {
			mCommand = mCommand.substring(1);
		}

		ParseResults<?> pr = null;
		try {
			Object server = Bukkit.getServer().getClass().getDeclaredMethod("getServer").invoke(Bukkit.getServer());
			String packageName = server.getClass().getPackage().getName();

			Class<?> minecraftServerClass = Class.forName(packageName + "." + "MinecraftServer");
			Object minecraftServer = minecraftServerClass.getDeclaredMethod("getServer").invoke(null);
			Object clw = minecraftServerClass.getDeclaredMethod("getServerCommandListener").invoke(minecraftServer);

			Object commandDispatcher = minecraftServerClass.getDeclaredMethod("getCommandDispatcher").invoke(minecraftServer);
			Class<?> commandDispatcherClass = Class.forName(packageName + "." + "CommandDispatcher");
			Object brigadierCmdDispatcher = commandDispatcherClass.getDeclaredMethod("a").invoke(commandDispatcher);

			String testCommandStr = mCommand.replaceAll("@S", "testuser");
			Method parse = CommandDispatcher.class.getDeclaredMethod("parse", String.class, Object.class);
			pr = (ParseResults<?>) parse.invoke(brigadierCmdDispatcher, testCommandStr, clw);
		} catch (Exception e) {
			// Failed to test the command - ignore it and print a log message
			e.printStackTrace();

			pr = null;
		}
		if (pr != null && pr.getReader().canRead()) {
			throw new Exception("Invalid command: '" + mCommand + "'");
		}
	}

	public String name() {
		return mName;
	}

	public void doAction(ClashDeaths plugin, Player player, TranslatableComponent deathMsg) {
		String deathMsgKey = deathMsg.key();
		List<Component> deathMsgArgs = deathMsg.args();

		// Check that translation key matches
		if (mDeathMsgKeyIsPrefix) {
			if (!deathMsgKey.startsWith(mDeathMsgKey)) {
				return;
			}
		} else {
			if (!deathMsgKey.equals(mDeathMsgKey)) {
				return;
			}
		}

		if (mKillerType != null) {
			if (deathMsgArgs.size() < 2) {
				return;
			}
			Component killerComponent = deathMsgArgs.get(1);
			if (killerComponent.hoverEvent() == null || !(killerComponent.hoverEvent().value() instanceof ShowEntity)) {
				return;
			} else {
				ShowEntity showKiller = (ShowEntity) (killerComponent.hoverEvent().value());
				if (!mKillerType.equals(showKiller.type().asString())) {
					return;
				}
			}
		}

		if (mWeaponType != null) {
			if (deathMsgArgs.size() < 3) {
				return;
			}
			Component weaponComponent = deathMsgArgs.get(2);
			if (weaponComponent.hoverEvent() == null || !(weaponComponent.hoverEvent().value() instanceof ShowItem)) {
				return;
			} else {
				ShowItem showItem = (ShowItem) (weaponComponent.hoverEvent().value());
				if (!mWeaponType.equals(showItem.item().asString())) {
					return;
				}
			}
		}

		// Run the command
		String commandStr = mCommand;
		commandStr = commandStr.replaceAll("@S", player.getName());
		plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), commandStr);
	}
}
