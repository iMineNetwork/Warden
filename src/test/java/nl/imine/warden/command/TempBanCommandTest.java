package nl.imine.warden.command;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;

import net.md_5.bungee.api.chat.BaseComponent;

public class TempBanCommandTest extends CommandTest {

	private TempBanCommand tempBanCommand;

	@Before
	public void setUp() throws Exception {
		tempBanCommand = new TempBanCommand(mockBanService, mockUserCacheService, mockTabCompletionUtil);
	}

	@Test
 	public void testBanCommandUsingOnlinePlayerName(){
		tempBanCommand.execute(mockCommandSenderPlayer, new String[]{playerOnlineName, "2h", "Lets", "put", "you", "in", "Time-out,", "mister"});
		verify(mockPlayerOnline).disconnect((BaseComponent[]) any());
	}

	@Test
	public void testBanCommandUsingOnlinePlayerUuid(){
		tempBanCommand.execute(mockCommandSenderPlayer, new String[]{playerOnlineUuid.toString(), "2h", "Lets", "put", "you", "in", "Time-out,", "mister"});
		verify(mockPlayerOnline).disconnect((BaseComponent[]) any());
	}
}
