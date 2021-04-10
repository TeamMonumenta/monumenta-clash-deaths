package com.playmonumenta.clashdeaths.utils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashSet;
import java.util.Set;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import org.bukkit.command.CommandSender;

public class MessagingUtils {
	public static void sendStackTrace(CommandSender sender, Exception e) {
		Set<CommandSender> senders = new HashSet<CommandSender>();
		senders.add(sender);
		sendStackTrace(senders, e);
	}

	public static void sendStackTrace(Set<CommandSender> senders, Exception e) {
		Component formattedMessage;
		String errorMessage = e.getLocalizedMessage();
		if (errorMessage != null) {
			formattedMessage = Component.text(errorMessage, NamedTextColor.RED);
		} else {
			formattedMessage = Component.text("An error occured without a set message. Hover for stack trace.", NamedTextColor.RED);
		}

		// Get the first 300 characters of the stacktrace and send them to the player
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		e.printStackTrace(pw);
		String sStackTrace = sw.toString();
		sStackTrace = sStackTrace.substring(0, Math.min(sStackTrace.length(), 300));

		Component textStackTrace = Component.text(sStackTrace.replace("\t", "  "), NamedTextColor.RED);
		formattedMessage = formattedMessage.hoverEvent(textStackTrace);

		if (senders != null) {
			for (CommandSender sender : senders) {
				if (sender != null) {
					sender.sendMessage(formattedMessage);
				}
			}
		}
	}
}
