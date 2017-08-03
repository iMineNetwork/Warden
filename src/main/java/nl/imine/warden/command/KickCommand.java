package nl.imine.warden.command;

import java.util.Arrays;
import java.util.Optional;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;
import nl.imine.warden.util.ComponentHelper;
import nl.imine.warden.util.TabCompletionUtil;

public class KickCommand extends Command implements TabExecutor {

	private TabCompletionUtil tabCompletionUtil;

	public KickCommand(TabCompletionUtil tabCompletionUtil) {
		super("kick", "warden.kick");
		this.tabCompletionUtil = tabCompletionUtil;
	}

	@Override
	public void execute(CommandSender commandSender, String[] args) {
		//Kick should contain two arguments, one player name and a reason for kicking
		if (args.length > 1) {
			Optional<ProxiedPlayer> oPlayer = ProxyServer.getInstance().getPlayers().stream().filter(p -> p.getName().equals(args[0])).findAny();
			if (oPlayer.isPresent()) {
				if (!args[1].isEmpty()) {

					//Build up the kick reason from the arguments
					String msg = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
					ComponentBuilder kickMessageBuilder = ComponentHelper.getPrefixedComponentBuilder();
					kickMessageBuilder
							.append(msg)
							.color(ChatColor.WHITE);
					ProxiedPlayer proxiedPlayer = oPlayer.get();

					//Kick the player
					proxiedPlayer.disconnect(kickMessageBuilder.create());

					//Notify all players online with the permission
					ComponentBuilder messageBuilder = ComponentHelper.getPrefixedComponentBuilder();
					messageBuilder
							.append("Player ").color(ChatColor.GRAY)
							.append(proxiedPlayer.getDisplayName()).color(ChatColor.RED)
							.append(" has been kicked by ").color(ChatColor.GRAY)
							.append(proxiedPlayer.getDisplayName()).color(ChatColor.DARK_GRAY)
							.append(" for ").color(ChatColor.GRAY)
							.append(msg).color(ChatColor.YELLOW)
							.append(".").color(ChatColor.GRAY);
					for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
						if (player.hasPermission("warden.kick.notify")) {
							player.sendMessage(messageBuilder.create());
						}
					}
				} else {
					//Kicks should always have a reason
					ComponentBuilder messageBuilder = ComponentHelper.getPrefixedComponentBuilder();
					messageBuilder
							.append("Please provide a kick reason.")
							.color(ChatColor.RED);
					commandSender.sendMessage(messageBuilder.create());
				}
			} else {
				//Player was not Online
				ComponentBuilder messageBuilder = ComponentHelper.getPrefixedComponentBuilder();
				messageBuilder
						.append("Player")
						.color(ChatColor.RED)
						.append(args[0])
						.color(ChatColor.GOLD)
						.append("not Found.")
						.color(ChatColor.RED);
				commandSender.sendMessage(messageBuilder.create());
			}
		} else {
			//General command usage information
			ComponentBuilder messageBuilder = ComponentHelper.getPrefixedComponentBuilder();
			messageBuilder
					.append("/kick [playername] [reason]")
					.color(ChatColor.RED);
			commandSender.sendMessage(messageBuilder.create());
		}
	}

	@Override
	public Iterable<String> onTabComplete(CommandSender commandSender, String[] strings) {
		return tabCompletionUtil.getOnlinePlayerNames();
	}
}
