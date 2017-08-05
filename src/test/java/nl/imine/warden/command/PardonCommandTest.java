package nl.imine.warden.command;

import org.junit.Before;
import org.junit.Test;

public class PardonCommandTest extends CommandTest {

	private PardonCommand pardonCommand;

	@Before
	public void setUp() throws Exception {
		pardonCommand = new PardonCommand(mockBanService, mockUserCacheService, mockTabCompletionUtil);
	}

	@Test
	public void testPardonCommandUsingName() {
		pardonCommand.execute(mockCommandSenderPlayer, new String[]{playerOfflineName});
	}

	@Test
	public void testPardonCommandUsingUuid() {
		pardonCommand.execute(mockCommandSenderPlayer, new String[]{playerOfflineUuid.toString()});
	}

	@Test
	public void testPardonCommandUsingIp() {
		pardonCommand.execute(mockCommandSenderPlayer, new String[]{playerOfflineInetAddress.getHostAddress()});
	}
}
