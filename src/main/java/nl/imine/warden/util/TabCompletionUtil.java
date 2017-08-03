package nl.imine.warden.util;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import nl.imine.warden.model.usercache.NameEntry;
import nl.imine.warden.service.UserCacheService;

public class TabCompletionUtil {

	private UserCacheService userCacheService;

	public TabCompletionUtil(UserCacheService userCacheService) {
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
		offlineNameEntries.removeIf(name -> playerNames.stream().anyMatch(onlineName -> name.toLowerCase().equals(onlineName.toLowerCase())));
		List<String> offlineNames = offlineNameEntries.stream().sorted(String.CASE_INSENSITIVE_ORDER).collect(Collectors.toList());

		//Merge the two lists
		playerNames.addAll(offlineNames);
		return playerNames;
	}

}
