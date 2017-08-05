package nl.imine.warden.command;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.UUID;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;
import nl.imine.warden.WardenPlugin;
import nl.imine.warden.model.usercache.NameEntry;
import nl.imine.warden.service.BanService;
import nl.imine.warden.service.UserCacheService;
import nl.imine.warden.util.ComponentHelper;
import nl.imine.warden.util.TabCompletionUtil;

public class PardonCommand extends Command implements TabExecutor {

	private BanService banService;
	private UserCacheService userCacheService;
	private TabCompletionUtil tabCompletionUtil;

	public PardonCommand(BanService banService, UserCacheService userCacheService, TabCompletionUtil tabCompletionUtil) {
		super("pardon", "warden.pardon", "unban");
		this.banService = banService;
		this.userCacheService = userCacheService;
		this.tabCompletionUtil = tabCompletionUtil;
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		if (args.length > 1) {
			UUID uuidSender = sender instanceof ProxiedPlayer ? ((ProxiedPlayer) sender).getUniqueId() : WardenPlugin.CONSOLE;
			UUID uuid = null;
			InetAddress address = null;
			String currentName = null;

			//Try to parse the argument as a IPAddress
			try {
				address = InetAddress.getByName(args[0]);
				NameEntry nameEntry = userCacheService.getLatestByInetAddress(address);
				if (nameEntry != null) {
					currentName = nameEntry.getName();
					uuid = nameEntry.getUuid();
				}
			} catch (UnknownHostException e) {
				//Try to parse the argument as a UUID
				try {
					uuid = UUID.fromString(args[0]);
					NameEntry nameEntry = userCacheService.getLatestNameByUUID(uuid);
					if (nameEntry != null) {
						currentName = nameEntry.getName();
						address = nameEntry.getInetAddress();
					}
				} catch (IllegalArgumentException iae) {
					//Look if a player with this name is online
					//As a last resort, try to find it by name in the cache
					NameEntry nameEntry = userCacheService.getLatestByName(args[0]);
					if (nameEntry != null) {
						address = nameEntry.getInetAddress();
						uuid = nameEntry.getUuid();
						currentName = nameEntry.getName();
					}
				}
			}
			if (uuid != null) {
				if (banService.pardonPlayer(uuid, uuidSender)) {
					//Notify all players online with the permission
					ComponentBuilder messageBuilder = ComponentHelper.getPrefixedComponentBuilder();
					messageBuilder
							.append("Player ").color(ChatColor.GRAY)
							.append(currentName).color(ChatColor.RED)
							.append(" has been unbanned by ").color(ChatColor.GRAY)
							.append(sender.getName()).color(ChatColor.DARK_GRAY);

					for (ProxiedPlayer proxiedPlayer : ProxyServer.getInstance().getPlayers()) {
						if (proxiedPlayer.hasPermission("warden.pardon.notify")) {
							proxiedPlayer.sendMessage(messageBuilder.create());
						}
					}
				} else {
					ComponentBuilder messageBuilder = ComponentHelper.getPrefixedComponentBuilder();
					messageBuilder
							.append("Player '")
							.color(ChatColor.RED)
							.append(args[0])
							.color(ChatColor.GOLD)
							.append("' was not banned.")
							.color(ChatColor.RED);
					sender.sendMessage(messageBuilder.create());
				}
			} else if (address != null) {
				if (banService.pardonIp(address, uuidSender)) {
					//Notify all players online with the permission
					ComponentBuilder messageBuilder = ComponentHelper.getPrefixedComponentBuilder();
					messageBuilder
							.append("IP ").color(ChatColor.GRAY)
							.append(address.getHostAddress()).color(ChatColor.RED)
							.append(" has been unbanned by ").color(ChatColor.GRAY)
							.append(sender.getName()).color(ChatColor.DARK_GRAY);

					for (ProxiedPlayer proxiedPlayer : ProxyServer.getInstance().getPlayers()) {
						if (proxiedPlayer.hasPermission("warden.pardon.notify")) {
							proxiedPlayer.sendMessage(messageBuilder.create());
						}
					}
				} else {
					ComponentBuilder messageBuilder = ComponentHelper.getPrefixedComponentBuilder();
					messageBuilder
							.append("IP '")
							.color(ChatColor.RED)
							.append(args[0])
							.color(ChatColor.GOLD)
							.append("' was not banned.")
							.color(ChatColor.RED);
					sender.sendMessage(messageBuilder.create());
				}
			} else {
				ComponentBuilder messageBuilder = ComponentHelper.getPrefixedComponentBuilder();
				messageBuilder
						.append("Player ")
						.color(ChatColor.RED)
						.append(args[0])
						.color(ChatColor.GOLD)
						.append(" not Found.")
						.color(ChatColor.RED);
				sender.sendMessage(messageBuilder.create());
			}
		} else {
			ComponentBuilder messageBuilder = ComponentHelper.getPrefixedComponentBuilder();
			messageBuilder
					.append("/pardon [playername]")
					.color(ChatColor.RED);
			sender.sendMessage(messageBuilder.create());
		}
	}

	@Override
	public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
		return tabCompletionUtil.getAllBannedPlayersStartingWith(args[0]);
	}
}
