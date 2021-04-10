package com.playmonumenta.clashdeaths;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.CommandPermission;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandReloadDeaths {
	public static void register(ClashDeaths plugin) {
		new CommandAPICommand("reloaddeaths")
			.withPermission(CommandPermission.fromString("clashdeaths"))
			.executes((sender, args) -> {
				plugin.reloadConfig(sender);
			})
			.register();
	}
}
