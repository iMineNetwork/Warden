package nl.imine.warden.util;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;

public class ComponentHelper {

	public static ComponentBuilder getPrefixedComponentBuilder() {
		return new ComponentBuilder("[")
					.color(ChatColor.GOLD)
				.append("Warden")
					.color(ChatColor.RED)
				.append("]")
					.color(ChatColor.GOLD)
				.append(" ")
					.color(ChatColor.RESET);
	}
}
