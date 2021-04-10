package com.playmonumenta.clashdeaths;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.playmonumenta.clashdeaths.utils.QuestUtils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class DeathManager implements Listener {
	private static DeathManager INSTANCE = null;
	private static ClashDeaths mPlugin;
	private static List<DeathAction> mDeathActions = new ArrayList<>();

	private DeathManager(ClashDeaths plugin) {
		INSTANCE = this;
		mPlugin = plugin;
	}

	public static DeathManager getInstance() {
		return INSTANCE;
	}

	public static DeathManager getInstance(ClashDeaths plugin) {
		if (INSTANCE == null) {
			INSTANCE = new DeathManager(plugin);
		}
		return INSTANCE;
	}

	public static void reload(ClashDeaths plugin, CommandSender sender) {
		mDeathActions.clear();

		QuestUtils.loadScriptedQuests(mPlugin, "death_actions", sender, (JsonObject object) -> {
			DeathAction deathAction = new DeathAction(object);
			mDeathActions.add(deathAction);

			return deathAction.name();
		});
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void playerDeathEvent(PlayerDeathEvent event) throws Exception {
		Player player = event.getEntity();

		Component deathMessage = event.deathMessage();
		if (deathMessage == null) {
			return;
		}

		if (mPlugin.debugMessagesEnabled()) {
			JsonElement deathMessageJson = GsonComponentSerializer.gson().serializeToTree(deathMessage);
			String equivalentCommand = "/tellraw @s " + deathMessageJson.toString();
			player.sendMessage(Component.text(equivalentCommand));
		}

		if (deathMessage instanceof TranslatableComponent) {
			TranslatableComponent deathTranslatable = (TranslatableComponent) deathMessage;
			for (DeathAction deathAction : mDeathActions) {
				deathAction.doAction(mPlugin, player, deathTranslatable);
			}
		}
	}
}
