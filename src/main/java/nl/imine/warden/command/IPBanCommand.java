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

public class IPBanCommand extends Command implements TabExecutor {

	private BanService banService;
	private UserCacheService userCacheService;
	private TabCompletionUtil tabCompletionUtil;

	public IPBanCommand(BanService banService, UserCacheService userCacheService, TabCompletionUtil tabCompletionUtil) {
		super("ipban", "warden.ipban");
		this.banService = banService;
		this.userCacheService = userCacheService;
		this.tabCompletionUtil = tabCompletionUtil;
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		if (args.length > 1) {
			UUID uuidSender = sender instanceof ProxiedPlayer ? ((ProxiedPlayer) sender).getUniqueId() : WardenPlugin.CONSOLE;
			UUID uuid = null;
			String customName = null;
			InetAddress address = null;

			//Try to parse the argument as a IPAddress
			try {
				address = InetAddress.getByName(args[0]);
				NameEntry nameEntry = userCacheService.getLatestByInetAddress(address);
				if (nameEntry != null) {
					customName = nameEntry.getName();
					uuid = nameEntry.getUuid();
				}
			} catch (UnknownHostException e) {
				//Try to parse the argument as a UUID
				try {
					uuid = UUID.fromString(args[0]);
					NameEntry nameEntry = userCacheService.getLatestNameByUUID(uuid);
					if (nameEntry != null) {
						customName = nameEntry.getName();
						address = nameEntry.getInetAddress();
					}
				} catch (IllegalArgumentException iae) {
					//Look if a player with this name is online
					ProxiedPlayer player = ProxyServer.getInstance().getPlayer(args[0]);
					if (player != null) {
						uuid = player.getUniqueId();
						address = player.getAddress().getAddress();
					} else {
						//As a last resort, try to find it by name in the cache
						NameEntry nameEntry = userCacheService.getLatestByName(args[0]);
						if (nameEntry != null) {
							address = nameEntry.getInetAddress();
							uuid = nameEntry.getUuid();
							customName = nameEntry.getName();
						}
					}
				}
			}
			if (address != null) {
				if (!args[1].isEmpty()) {
					String reason = String.join(" ", String.join(" ", Arrays.copyOfRange(args, 1, args.length)));
					banService.ipBanPlayer(address, uuid, uuidSender, reason);

					for(ProxiedPlayer proxiedPlayer : ProxyServer.getInstance().getPlayers()) {
						if(proxiedPlayer.getAddress().getAddress().equals(address)) {
							ComponentBuilder kickMessageBuilder = ComponentHelper.getPrefixedComponentBuilder();
							kickMessageBuilder
									.append(reason)
									.color(ChatColor.WHITE);

							//Kick the player
							proxiedPlayer.disconnect(kickMessageBuilder.create());
						}
					}
					if (uuid != null) {
						//Notify all players online with the permission
						ComponentBuilder messageBuilder = ComponentHelper.getPrefixedComponentBuilder();
						messageBuilder
								.append("Player ").color(ChatColor.GRAY)
								.append(customName).color(ChatColor.RED)
								.append(" has been ip banned by ").color(ChatColor.GRAY)
								.append(sender.getName()).color(ChatColor.DARK_GRAY)
								.append(" for '").color(ChatColor.GRAY)
								.append(reason).color(ChatColor.YELLOW)
								.append("'.").color(ChatColor.GRAY);
						for (ProxiedPlayer proxiedPlayer : ProxyServer.getInstance().getPlayers()) {
							if (proxiedPlayer.hasPermission("warden.ban.notify")) {
								proxiedPlayer.sendMessage(messageBuilder.create());
							}
						}
					} else {
						ComponentBuilder messageBuilder = ComponentHelper.getPrefixedComponentBuilder();
						messageBuilder
								.append("IP ").color(ChatColor.GRAY)
								.append(address.getHostName().replace("/", "")).color(ChatColor.RED)
								.append(" has been ip banned by ").color(ChatColor.GRAY)
								.append(sender.getName()).color(ChatColor.DARK_GRAY)
								.append(" for '").color(ChatColor.GRAY)
								.append(reason).color(ChatColor.YELLOW)
								.append("'.").color(ChatColor.GRAY);
						for (ProxiedPlayer proxiedPlayer : ProxyServer.getInstance().getPlayers()) {
							if (proxiedPlayer.hasPermission("warden.ban.notify")) {
								proxiedPlayer.sendMessage(messageBuilder.create());
							}
						}
					}

				} else {
					ComponentBuilder messageBuilder = ComponentHelper.getPrefixedComponentBuilder();
					messageBuilder
							.append("Please provide a ban reason.")
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
					.append("/ipban [playername] [reason]")
					.color(ChatColor.RED);
			sender.sendMessage(messageBuilder.create());
		}
	}

	@Override
	public Iterable<String> onTabComplete(CommandSender commandSender, String[] args) {
		return tabCompletionUtil.getAllPlayerNamesStartingWith(args[0]);
	}
}
