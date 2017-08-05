package nl.imine.warden.util;

import java.util.*;
import java.util.stream.Collectors;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import nl.imine.warden.model.usercache.NameEntry;
import nl.imine.warden.service.BanService;
import nl.imine.warden.service.UserCacheService;

public class TabCompletionUtil {

	private BanService banService;

	private UserCacheService userCacheService;

	public TabCompletionUtil(BanService banService, UserCacheService userCacheService) {
		this.banService = banService;
		this.userCacheService = userCacheService;
	}

	public Iterable<String> getOnlinePlayerNames() {
		return ProxyServer.getInstance().getPlayers().stream()
				.map(ProxiedPlayer::getDisplayName)
				.sorted(String.CASE_INSENSITIVE_ORDER)
				.collect(Collectors.toSet());
	}

	public Iterable<String> getAllPlayerNamesStartingWith(String prefix) {
		List<String> playerNames = new ArrayList<>();
		//Get all online players and put their names in a list
		playerNames.addAll(
				ProxyServer.getInstance().getPlayers().stream()
						.filter(p -> p.getName().toLowerCase().startsWith(prefix.toLowerCase()))
						.map(ProxiedPlayer::getName)
						.sorted(String.CASE_INSENSITIVE_ORDER)
						.collect(Collectors.toList())
		);

		//Find all unique PlayerNames
		List<String> offlineNameEntries = userCacheService.getAllUniqueNamesStartingWith(prefix);

		//Loop through the online player names (as this list will usually be smaller than the offline list)
		//and remove the online names from all the names to create a list of offline player names
		offlineNameEntries.removeIf(name -> playerNames.stream().anyMatch(name::equalsIgnoreCase));
		List<String> offlineNames = offlineNameEntries.stream().sorted(String.CASE_INSENSITIVE_ORDER).collect(Collectors.toList());

		//Merge the two lists
		playerNames.addAll(offlineNames);
		return playerNames;
	}

	public Iterable<String> getAllBannedPlayersStartingWith(String prefix) {
		List<String> playerNames = new ArrayList<>();
		Set<UUID> bannedPlayers = new HashSet<>();
		bannedPlayers.addAll(banService.getBannedPlayerIds());
		bannedPlayers.addAll(banService.getTempBannedPlayerIds());
		bannedPlayers.addAll(banService.getIPBannedPlayerIds());

		for (UUID id : bannedPlayers) {
			if (id != null) {
				NameEntry entry = userCacheService.getLatestNameByUUID(id);
				if (entry != null && entry.getName().toLowerCase().startsWith(prefix.toLowerCase())) {
					playerNames.add(entry.getName());
				}
			}
		}

		return playerNames;
	}

}
