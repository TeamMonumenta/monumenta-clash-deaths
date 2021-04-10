package com.playmonumenta.clashdeaths;

import java.lang.reflect.Method;

import com.google.gson.JsonObject;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;

import org.bukkit.entity.Player;

public class DeathAction {
	private String mName;
	private String mDeathMsgKey;
	private boolean mDeathMsgKeyIsPrefix = false;
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
			|| object.get("death_message_key").getAsString().isEmpty()
			|| object.get("death_message_key").getAsString().equals("*")) {
			throw new Exception("death_message_key must be a translation key");
		}
		mDeathMsgKey = object.get("death_message_key").getAsString();
		if (mDeathMsgKey.endsWith("*")) {
			mDeathMsgKeyIsPrefix = true;
			mDeathMsgKey = mDeathMsgKey.substring(0, mDeathMsgKey.length()-1);
		}

		if (object.get("command") == null
			|| object.get("command").getAsString() == null) {
			throw new Exception("command must be a command string");
		}
		mCommand = object.get("command").getAsString();
		if (mCommand.startsWith("/")) {
			mCommand = mCommand.substring(1);
		}
		// TODO Verify command similar to ScriptedQuests
	}

	public String name() {
		return mName;
	}

	public void doAction(ClashDeaths plugin, Player player, TranslatableComponent deathMsg) {
		String deathMsgKey = deathMsg.key();

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

		// TODO Check that translation args match

		// Run the command
		String commandStr = mCommand;
		commandStr = commandStr.replaceAll("@S", player.getName());
		plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), commandStr);
	}
}
