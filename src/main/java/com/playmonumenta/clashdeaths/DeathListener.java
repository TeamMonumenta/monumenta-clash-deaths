package com.playmonumenta.clashdeaths;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import com.google.gson.JsonElement;

public class DeathListener implements Listener {
	@EventHandler(priority = EventPriority.HIGHEST)
	public void playerDeathEvent(PlayerDeathEvent event) throws Exception {
		Player player = event.getEntity();

		Component deathMessage = event.deathMessage();
		if (deathMessage == null) {
			return;
		}

		JsonElement deathMessageJson = GsonComponentSerializer.gson().serializeToTree(deathMessage);
		String equivalentCommand = "/tellraw @s " + deathMessageJson.toString();
		player.sendMessage(Component.text(equivalentCommand));
	}
}
