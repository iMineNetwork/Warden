package nl.imine.warden.command;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;

import net.md_5.bungee.api.chat.BaseComponent;

public class BanCommandTest extends CommandTest {

	private BanCommand banCommand;

	@Before
	public void setUp() throws Exception {
		banCommand = new BanCommand(mockBanService, mockUserCacheService, mockTabCompletionUtil);
	}

	@Test
 	public void testBanCommandUsingOnlinePlayerName(){
		banCommand.execute(mockCommandSenderPlayer, new String[]{playerOnlineName, "Houd", "eens", "op", "met", "je", "gezeur"});
		verify(mockPlayerOnline).disconnect((BaseComponent[]) any());
	}

	@Test
	public void testBanCommandUsingOnlinePlayerUuid(){
		banCommand.execute(mockCommandSenderPlayer, new String[]{playerOnlineUuid.toString(), "Houd", "eens", "op", "met", "je", "gezeur"});
		verify(mockPlayerOnline).disconnect((BaseComponent[]) any());
	}
}
