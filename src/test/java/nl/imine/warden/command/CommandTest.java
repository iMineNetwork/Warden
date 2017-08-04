package nl.imine.warden.command;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

import java.net.InetAddress;
import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import nl.imine.warden.model.usercache.NameEntry;
import nl.imine.warden.service.BanService;
import nl.imine.warden.service.UserCacheService;
import nl.imine.warden.util.TabCompletionUtil;

@RunWith(PowerMockRunner.class)
@PrepareForTest(ProxyServer.class)
public abstract class CommandTest {

	protected UUID commandSenderUuid;
	protected UUID playerOnlineUuid;
	protected UUID playerOfflineUuid;

	protected String commandSenderName;
	protected String playerOnlineName;
	protected String playerOfflineName;

	protected InetAddress commandSenderInetAddress;
	protected InetAddress playerOnlineInetAddress;
	protected InetAddress playerOfflineInetAddress;

	protected ProxiedPlayer mockCommandSenderPlayer;
	protected ProxiedPlayer mockPlayerOnline;
	protected ProxiedPlayer mockPlayerOffline;

	@Mock
	protected BanService mockBanService;

	@Mock
	protected UserCacheService mockUserCacheService;

	@Mock
	protected TabCompletionUtil mockTabCompletionUtil;

	protected ProxyServer mockProxyServer;

	@Before
	public void setup() throws Exception {
		mockStatic(ProxyServer.class);
		mockProxyServer = mock(ProxyServer.class);
		PowerMockito.when(ProxyServer.getInstance()).thenReturn(mockProxyServer);

		createProxiedPlayerMocks(mockProxyServer);
	}

	private void createProxiedPlayerMocks(ProxyServer proxyServer) throws Exception {
		commandSenderUuid = UUID.fromString("00000000-0000-0000-0000-000000000001");
		playerOnlineUuid = UUID.fromString("00000000-0000-0000-0000-000000000002");
		playerOfflineUuid = UUID.fromString("00000000-0000-0000-0000-000000000003");

		commandSenderName = "cmdSender";
		playerOnlineName = "offlinePlayer";
		playerOfflineName = "onlinePlayer";

		commandSenderInetAddress = InetAddress.getByName("1.1.1.1");
		playerOnlineInetAddress = InetAddress.getByName("10.10.10.10");
		playerOfflineInetAddress = InetAddress.getByName("100.100.100.100");

		mockCommandSenderPlayer = mock(ProxiedPlayer.class);
		when(mockCommandSenderPlayer.getUniqueId()).thenReturn(commandSenderUuid);
		when(mockCommandSenderPlayer.getName()).thenReturn(playerOfflineName);
		when(proxyServer.getPlayer(commandSenderUuid)).thenReturn(mockCommandSenderPlayer);
		when(proxyServer.getPlayer(commandSenderName)).thenReturn(mockCommandSenderPlayer);
		when(mockUserCacheService.getLatestNameByUUID(commandSenderUuid)).thenReturn(new NameEntry(
				commandSenderUuid,
				commandSenderName,
				LocalDateTime.now(),
				LocalDateTime.now(),
				commandSenderInetAddress
				));

		mockPlayerOnline = mock(ProxiedPlayer.class);
		when(mockPlayerOnline.getUniqueId()).thenReturn(playerOnlineUuid);
		when(mockPlayerOnline.getName()).thenReturn(playerOnlineName);
		when(proxyServer.getPlayer(playerOnlineUuid)).thenReturn(mockPlayerOnline);
		when(proxyServer.getPlayer(playerOnlineName)).thenReturn(mockPlayerOnline);
		when(mockUserCacheService.getLatestNameByUUID(playerOnlineUuid)).thenReturn(new NameEntry(
				playerOnlineUuid,
				playerOnlineName,
				LocalDateTime.now(),
				LocalDateTime.now(),
				playerOnlineInetAddress
		));

		mockPlayerOffline = mock(ProxiedPlayer.class);
		when(mockPlayerOffline.getUniqueId()).thenReturn(playerOfflineUuid);
		when(mockPlayerOffline.getName()).thenReturn(playerOfflineName);
		when(mockUserCacheService.getLatestNameByUUID(playerOfflineUuid)).thenReturn(new NameEntry(
				playerOfflineUuid,
				playerOfflineName,
				LocalDateTime.now(),
				LocalDateTime.now(),
				playerOfflineInetAddress
		));
	}
}
