package nl.imine.warden.command;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;

import net.md_5.bungee.api.chat.BaseComponent;

public class IPBanCommandTest extends CommandTest {

	private IPBanCommand ipBanCommand;

	@Before
	public void setUp() throws Exception {
		ipBanCommand = new IPBanCommand(mockBanService, mockUserCacheService, mockTabCompletionUtil);
	}

	@Test
 	public void testBanCommandUsingOnlinePlayerName(){
		ipBanCommand.execute(mockCommandSenderPlayer, new String[]{playerOnlineName, "That's", "enough", "with", "your", "alt,", "accounts"});
		verify(mockPlayerOnline).disconnect((BaseComponent[]) any());
	}

	@Test
	public void testBanCommandUsingOnlinePlayerUuid(){
		ipBanCommand.execute(mockCommandSenderPlayer, new String[]{playerOnlineUuid.toString(), "That's", "enough", "with", "your", "alt,", "accounts"});
		verify(mockPlayerOnline).disconnect((BaseComponent[]) any());
	}

	@Test
	public void testBanCommandUsingOnlinePlayerIP(){
		ipBanCommand.execute(mockCommandSenderPlayer, new String[]{playerOnlineInetAddress.getHostAddress(), "That's", "enough", "with", "your", "alt,", "accounts"});
		verify(mockPlayerOnline).disconnect((BaseComponent[]) any());
	}
}
