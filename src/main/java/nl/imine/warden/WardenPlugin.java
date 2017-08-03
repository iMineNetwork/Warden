package nl.imine.warden;

import java.util.UUID;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import nl.imine.warden.command.BanCommand;
import nl.imine.warden.command.IPBanCommand;
import nl.imine.warden.command.KickCommand;
import nl.imine.warden.command.TempBanCommand;
import nl.imine.warden.service.UserCacheService;
import nl.imine.warden.service.mysql.MySQLConfig;
import nl.imine.warden.database.DatabaseSetup;
import nl.imine.warden.service.BanService;
import nl.imine.warden.service.mysql.MySQLService;
import nl.imine.warden.util.TabCompletionUtil;

public class WardenPlugin extends Plugin {

	public static final UUID CONSOLE = UUID.fromString("00000000-0000-0000-0000-000000000000");

	private static Plugin instance;

	@Override
	public void onEnable() {
		WardenPlugin.setInstance(this);
		MySQLService mySQLService = initDatabase();
		BanService banService = new BanService(mySQLService);
		UserCacheService userCacheService = new UserCacheService(mySQLService);
		TabCompletionUtil tabCompletionUtil = new TabCompletionUtil(userCacheService);
		//TODO Retrieve all bans from Bukkit's ban list
		//TODO Retrieve all IP Bans from Bukkit's ipban list

		//TODO Pardon all players on Bukkit's Ban list (We handle banned players ourselves)
		//TODO Pardon all players on Bukkit's IP Ban list (We handle banned players ourselves)
		//TODO Remove ban list
		//TODO Remove ipban list
		ProxyServer.getInstance().getPluginManager().registerListener(this, new EventListener(banService, userCacheService));

		//Register commands
		ProxyServer.getInstance().getPluginManager().registerCommand(this, new BanCommand(banService, userCacheService, tabCompletionUtil));
		ProxyServer.getInstance().getPluginManager().registerCommand(this, new IPBanCommand(banService, userCacheService, tabCompletionUtil));
		ProxyServer.getInstance().getPluginManager().registerCommand(this, new TempBanCommand(banService, userCacheService, tabCompletionUtil));
		ProxyServer.getInstance().getPluginManager().registerCommand(this, new KickCommand(tabCompletionUtil));
	}

//	@Override
//	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
//		return CommandHandler.onCommand(sender, command.getName(), args);
//	}
//
//	@Override
//	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
//		//Tabcomplete for Pardon:
//			//List all players on the pardon list.
//			//List all players on the IPBan list.
//
//		//Tabcomplete for other commands
//			//List all online players
//				//Sort online players
//			//List offline players
//				//Sort offline players
//
//		return null;
//	}

	private MySQLService initDatabase(){
		MySQLConfig mySQLConfig = new MySQLConfig();
		mySQLConfig.loadConfigFile();

		MySQLService mySQLService = new MySQLService(mySQLConfig.getUser(), mySQLConfig.getPassword(), mySQLConfig.getJdbcUrl());
		mySQLService.connect();

		new DatabaseSetup(mySQLService).setup();
		return mySQLService;
	}

	public static Plugin getInstance() {
		return instance;
	}

	public static void setInstance(Plugin instance) {
		WardenPlugin.instance = instance;
	}
}
