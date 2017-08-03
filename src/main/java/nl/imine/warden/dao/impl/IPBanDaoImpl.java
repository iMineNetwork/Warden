package nl.imine.warden.dao.impl;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import nl.imine.warden.WardenPlugin;
import nl.imine.warden.dao.IPBanDao;
import nl.imine.warden.model.ban.BanType;
import nl.imine.warden.model.ban.IPBanEntry;
import nl.imine.warden.service.mysql.MySQLService;

public class IPBanDaoImpl implements IPBanDao {

	private MySQLService mySQLService;

	public IPBanDaoImpl(MySQLService mySQLService) {
		this.mySQLService = mySQLService;
	}

	@Override
	public List<IPBanEntry> getAllIPBans() {
		List<IPBanEntry> ret = new ArrayList<>();
		try {
			PreparedStatement statement = mySQLService.getConnection().prepareStatement("SELECT * FROM `Ban` ban INNER JOIN `IpBan` ipBan ON ban.id = ipBan.id WHERE ban.BanType = ?;");
			statement.setInt(1, BanType.IPBAN.getId());
			ResultSet resultSet = statement.executeQuery();
			if (resultSet.next()) {
				IPBanEntry ban = new IPBanEntry();
				ban.setUuid(UUID.fromString(resultSet.getString("UUID")));
				ban.setFromUUID(UUID.fromString(resultSet.getString("FromUUID")));
				ban.setReason(resultSet.getString("Reason"));
				ban.setBanTimestmap(LocalDateTime.ofInstant(resultSet.getTimestamp("Timestamp").toInstant(), ZoneId.systemDefault()));
				ban.setActive(resultSet.getBoolean("Active"));
				ban.setIp(InetAddress.getByName(resultSet.getString("IP")));
				ret.add(ban);
			}
		} catch (SQLException | UnknownHostException e) {
			System.err.println("Could not load Bans from database | " + e.getMessage());
		}
		return ret;
	}

	@Override
	public IPBanEntry getIPBan(InetAddress inetAddress) {
		IPBanEntry ret = null;
		try {
			PreparedStatement statement = mySQLService.getConnection().prepareStatement("SELECT * FROM `Ban` ban INNER JOIN `IpBan` ipBan ON ban.id = ipBan.id WHERE `IP` = ? AND `Active` = '1' LIMIT 1;");
			statement.setString(1, inetAddress.getHostAddress().replaceFirst("/", ""));
			ResultSet resultSet = statement.executeQuery();
			if (resultSet.next()) {
				ret = new IPBanEntry();
				ret.setUuid(UUID.fromString(resultSet.getString("UUID")));
				ret.setFromUUID(UUID.fromString(resultSet.getString("FromUUID")));
				ret.setReason(resultSet.getString("Reason"));
				ret.setBanTimestmap(LocalDateTime.ofInstant(resultSet.getTimestamp("Timestamp").toInstant(), ZoneId.systemDefault()));
				ret.setBanType(BanType.BAN);
				ret.setActive(resultSet.getBoolean("Active"));
				ret.setIp(InetAddress.getByName(resultSet.getString("IP")));
			}
		} catch (SQLException | UnknownHostException e) {
			System.err.println("Could not load IPBan from database | " + e.getMessage());
		}
		return ret;
	}

	@Override
	public void createIPBan(IPBanEntry ipBan) {
		try (Connection connection = mySQLService.getNewConnection()) {
			connection.setAutoCommit(false);

			String sql = "INSERT INTO `Ban` (UUID, FromUUID, Reason, BanType) VALUES (?, ?, ?, ?);";
			PreparedStatement banStatement = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
			banStatement.setString(1, ipBan.getUuid() != null ? ipBan.getUuid().toString() : WardenPlugin.CONSOLE.toString());
			banStatement.setString(2, ipBan.getFromUUID().toString());
			banStatement.setString(3, ipBan.getReason());
			banStatement.setInt(4, ipBan.getBanType().getId());
			banStatement.execute();

			PreparedStatement ipBanStatement = connection.prepareStatement("INSERT INTO `IpBan` (id, IP) VALUES (?, ?);");
			ResultSet resultSet = banStatement.getGeneratedKeys();
			resultSet.next();
			ipBanStatement.setInt(1, resultSet.getInt(1));
			ipBanStatement.setString(2, ipBan.getIp().getHostAddress());
			ipBanStatement.execute();

			connection.commit();
			connection.setAutoCommit(true);

			connection.close();
		} catch (SQLException e) {
			System.err.println("Could not load IPBan from database | " + e.getMessage());
		}
	}

	@Override
	public void updateIPBan(IPBanEntry ipBan) {
		try {
			PreparedStatement statement = mySQLService.getNewConnection().prepareStatement("UPDATE `Ban` ban INNER JOIN `IpBan` ipBan ON ban.id = ipBan.id SET UUID=?, `FromUUID`=?, `Reason`=?, `Timestamp`=?, `IP`=? WHERE `IP` = ?;");
			statement.setString(1, ipBan.getUuid().toString());
			statement.setString(2, ipBan.getFromUUID().toString());
			statement.setString(3, ipBan.getReason());
			statement.setDate(4, new Date(Calendar.getInstance().getTimeInMillis()));
			statement.setString(5, ipBan.getIp().getHostAddress().replaceFirst("/", ""));
			statement.execute();
		} catch (SQLException e) {
			System.err.println("Could not load IPBan from database | " + e.getMessage());
		}
	}

	@Override
	public void deleteIPBan(IPBanEntry ipBan) {
		try {
			PreparedStatement statement = mySQLService.getConnection().prepareStatement("DELETE FROM `IPBan` WHERE `IP` = ?;");
			statement.setString(1, ipBan.getIp().getHostAddress().replaceFirst("/", ""));
			statement.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
