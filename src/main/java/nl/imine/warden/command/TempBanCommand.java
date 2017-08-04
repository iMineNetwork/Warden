package nl.imine.warden.command;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
import nl.imine.warden.util.DateUtil;
import nl.imine.warden.util.TabCompletionUtil;

public class TempBanCommand extends Command implements TabExecutor {

	private BanService banService;
	private UserCacheService userCacheService;
	private TabCompletionUtil tabCompletionUtil;

	public TempBanCommand(BanService banService, UserCacheService userCacheService, TabCompletionUtil tabCompletionUtil) {
		super("tempban", "warden.tempban");
		this.banService = banService;
		this.userCacheService = userCacheService;
		this.tabCompletionUtil = tabCompletionUtil;
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		if (args.length > 2) {
			UUID uuidSender = sender instanceof ProxiedPlayer ? ((ProxiedPlayer) sender).getUniqueId() : WardenPlugin.CONSOLE;
			UUID uuid = null;
			String currentName = null;
			ProxiedPlayer player = null;

			try {
				uuid = UUID.fromString(args[0]);
				currentName = userCacheService.getLatestNameByUUID(uuid).getName();
				player = ProxyServer.getInstance().getPlayer(uuid);
			} catch (IllegalArgumentException e) {
				player = ProxyServer.getInstance().getPlayer(args[0]);
				if (player != null) {
					uuid = player.getUniqueId();
					currentName = player.getName();
				} else {
					NameEntry nameEntry = userCacheService.getLatestByName(args[0]);
					if (nameEntry != null) {
						uuid = nameEntry.getUuid();
						currentName = nameEntry.getName();
					}
				}
			}
			if (uuid != null) {
				if (!args[1].isEmpty()) {
					Duration duration = DateUtil.fromString(args[1]);
					if (!args[2].isEmpty()) {
						String reason = String.join(" ", String.join(" ", Arrays.copyOfRange(args, 2, args.length)));
						banService.tempBanPlayer(uuid, uuidSender, reason, duration);

						if (player != null) {
							ComponentBuilder kickMessageBuilder = ComponentHelper.getPrefixedComponentBuilder();
							kickMessageBuilder
									.append("You have been Tempbanned for '").color(ChatColor.RED)
									.append(reason).color(ChatColor.WHITE)
									.append("'").color(ChatColor.RED)
									.append(" until ").color(ChatColor.WHITE)
									.append(LocalDateTime.now().plus(duration).format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"))).color(ChatColor.GOLD)
									.append(" (").color(ChatColor.WHITE)
									.append(DateUtil.durationToString(duration)).color(ChatColor.GOLD)
									.append(").").color(ChatColor.WHITE);

							//Kick the player
							player.disconnect(kickMessageBuilder.create());
						}
						//Notify all players online with the permission
						ComponentBuilder messageBuilder = ComponentHelper.getPrefixedComponentBuilder();
						messageBuilder
								.append("Player ").color(ChatColor.GRAY)
								.append(currentName).color(ChatColor.RED)
								.append(" has been temp banned by ").color(ChatColor.GRAY)
								.append(sender.getName()).color(ChatColor.DARK_GRAY)
								.append(" for '").color(ChatColor.GRAY)
								.append(reason).color(ChatColor.YELLOW)
								.append("' until ").color(ChatColor.GRAY)
								.append(LocalDateTime.now().plus(duration).format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"))).color(ChatColor.GOLD)
								.append(" (").color(ChatColor.GRAY)
								.append(DateUtil.durationToString(duration)).color(ChatColor.GOLD)
								.append(").").color(ChatColor.GRAY);
						for (ProxiedPlayer proxiedPlayer : ProxyServer.getInstance().getPlayers()) {
							if (proxiedPlayer.hasPermission("warden.ban.notify")) {
								proxiedPlayer.sendMessage(messageBuilder.create());
							}
						}

					} else {
						ComponentBuilder messageBuilder = ComponentHelper.getPrefixedComponentBuilder();
						messageBuilder
								.append("Please provide a tempban reason.")
								.color(ChatColor.RED);
						sender.sendMessage(messageBuilder.create());
					}
				} else {
					ComponentBuilder messageBuilder = ComponentHelper.getPrefixedComponentBuilder();
					messageBuilder
							.append("Please provide a duration.")
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
					.append("/tempban [playername] [duration] [reason]")
					.color(ChatColor.RED);
			sender.sendMessage(messageBuilder.create());
		}
	}

	@Override
	public Iterable<String> onTabComplete(CommandSender commandSender, String[] args) {
		return tabCompletionUtil.getAllPlayerNamesStartingWith(args[0]);
	}
}
