package nl.imine.warden;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;
import nl.imine.warden.model.ban.BanEntry;
import nl.imine.warden.model.ban.IPBanEntry;
import nl.imine.warden.model.ban.TempBanEntry;
import nl.imine.warden.model.usercache.NameEntry;
import nl.imine.warden.service.BanService;
import nl.imine.warden.service.UserCacheService;
import nl.imine.warden.util.ComponentHelper;

public class EventListener implements Listener {

	private BanService banService;
	private UserCacheService userCacheService;

	public EventListener(BanService banService, UserCacheService userCacheService) {
		this.banService = banService;
		this.userCacheService = userCacheService;
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onJoinEvent(PostLoginEvent evt) {

		//On Join
		NameEntry nameEntry = userCacheService.getLatestNameByUUID(evt.getPlayer().getUniqueId());
		if (nameEntry != null) {
			if (!nameEntry.getName().equals(evt.getPlayer().getName())) {
				userCacheService.create(new NameEntry(evt.getPlayer().getUniqueId(), evt.getPlayer().getName(), LocalDateTime.now(), LocalDateTime.now(), evt.getPlayer().getAddress().getAddress()));
			} else {
				nameEntry.setLastSeen(LocalDateTime.now());
				nameEntry.setInetAddress(evt.getPlayer().getAddress().getAddress());
				userCacheService.update(nameEntry);
			}
		} else {
			userCacheService.create(new NameEntry(evt.getPlayer().getUniqueId(), evt.getPlayer().getName(), LocalDateTime.now(), LocalDateTime.now(), evt.getPlayer().getAddress().getAddress()));
		}

		BanEntry banEntry = banService.getBan(evt.getPlayer().getUniqueId());
		if (banEntry != null) {
			evt.getPlayer().disconnect(ComponentHelper.getPrefixedComponentBuilder()
					.append("You have been banned from this server for '").color(ChatColor.RED)
					.append(banEntry.getReason()).color(ChatColor.WHITE)
					.append("'").color(ChatColor.RED).create());
		}

		IPBanEntry ipBanEntry = banService.getIPBan(evt.getPlayer().getAddress().getAddress());
		if (ipBanEntry != null) {
			evt.getPlayer().disconnect(ComponentHelper.getPrefixedComponentBuilder()
					.append("You have been banned from this server for '").color(ChatColor.RED)
					.append(ipBanEntry.getReason()).color(ChatColor.WHITE)
					.append("'").color(ChatColor.RED).create());
		}

		TempBanEntry tempBanEntry = banService.getTempBan(evt.getPlayer().getUniqueId());
		if (tempBanEntry != null) {
			evt.getPlayer().disconnect(ComponentHelper.getPrefixedComponentBuilder()
					.append("You have been banned from this server for '").color(ChatColor.RED)
					.append(tempBanEntry.getReason()).color(ChatColor.WHITE)
					.append("'").color(ChatColor.RED)
					.append(" until ").color(ChatColor.RED)
					.append(tempBanEntry.getUnbanTimestamp().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))).color(ChatColor.GOLD)
					.create());
		}

		//TODO ShadowBan
	}
}
