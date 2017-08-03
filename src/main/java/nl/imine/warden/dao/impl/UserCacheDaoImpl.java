package nl.imine.warden.dao.impl;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import nl.imine.warden.dao.UserCacheDao;
import nl.imine.warden.model.usercache.NameEntry;
import nl.imine.warden.service.mysql.MySQLService;

public class UserCacheDaoImpl implements UserCacheDao {

	private MySQLService mySQLService;

	public UserCacheDaoImpl(MySQLService mySQLService) {
		this.mySQLService = mySQLService;
	}

	@Override
	public List<NameEntry> findAllByUUID(UUID uuid) {
		List<NameEntry> ret = new ArrayList<>();
		try {
			PreparedStatement statement = mySQLService.getConnection().prepareStatement("SELECT * FROM `UserCache` WHERE `UUID` = ?;");
			statement.setString(1, uuid.toString());
			ResultSet resultSet = statement.executeQuery();
			if (resultSet.next()) {
				ret.add(new NameEntry(UUID.fromString(resultSet.getString("UUID")),
						resultSet.getString("Name"),
						LocalDateTime.ofInstant(resultSet.getTimestamp("FirstSeen").toInstant(), ZoneId.systemDefault()),
						LocalDateTime.ofInstant(resultSet.getTimestamp("LastSeen").toInstant(), ZoneId.systemDefault()),
						InetAddress.getByName(resultSet.getString("IP")))
				);
			}
		} catch (SQLException | UnknownHostException e) {
			System.err.println("Could not load Name History from database | " + e.getMessage());
		}
		return ret;
	}

	@Override
	public NameEntry findLatestByUUID(UUID uuid) {
		NameEntry ret = null;
		try {
			PreparedStatement statement = mySQLService.getConnection().prepareStatement("SELECT * FROM `UserCache` WHERE `UUID` = ? ORDER BY `FirstSeen` DESC LIMIT 1;");
			statement.setString(1, uuid.toString());
			ResultSet resultSet = statement.executeQuery();
			if (resultSet.next()) {
				ret = new NameEntry(UUID.fromString(resultSet.getString("UUID")),
						resultSet.getString("Name"),
						LocalDateTime.ofInstant(resultSet.getTimestamp("FirstSeen").toInstant(), ZoneId.systemDefault()),
						LocalDateTime.ofInstant(resultSet.getTimestamp("LastSeen").toInstant(), ZoneId.systemDefault()),
						InetAddress.getByName(resultSet.getString("IP"))
				);
			}
		} catch (SQLException | UnknownHostException e) {
			System.err.println("Could not load Name History from database | " + e.getMessage());
		}
		return ret;
	}

	@Override
	public List<NameEntry> findAllByName(String name) {
		List<NameEntry> ret = new ArrayList<>();
		try {
			PreparedStatement statement = mySQLService.getConnection().prepareStatement("SELECT * FROM `UserCache` WHERE `Name` = ?;");
			statement.setString(1, name);
			ResultSet resultSet = statement.executeQuery();
			if (resultSet.next()) {
				ret.add(new NameEntry(UUID.fromString(resultSet.getString("UUID")),
						resultSet.getString("Name"),
						LocalDateTime.ofInstant(resultSet.getTimestamp("FirstSeen").toInstant(), ZoneId.systemDefault()),
						LocalDateTime.ofInstant(resultSet.getTimestamp("LastSeen").toInstant(), ZoneId.systemDefault()),
						InetAddress.getByName(resultSet.getString("IP")))
				);
			}
		} catch (SQLException | UnknownHostException e) {
			System.err.println("Could not load Name History from database | " + e.getMessage());
		}
		return ret;
	}

	@Override
	public NameEntry findLatestByName(String name) {
		NameEntry ret = null;
		try {
			PreparedStatement statement = mySQLService.getConnection().prepareStatement("SELECT * FROM `UserCache` WHERE `Name` = ? ORDER BY `FirstSeen` DESC LIMIT 1;");
			statement.setString(1, name);
			ResultSet resultSet = statement.executeQuery();
			if (resultSet.next()) {
				ret = new NameEntry(UUID.fromString(resultSet.getString("UUID")),
						resultSet.getString("Name"),
						LocalDateTime.ofInstant(resultSet.getTimestamp("FirstSeen").toInstant(), ZoneId.systemDefault()),
						LocalDateTime.ofInstant(resultSet.getTimestamp("LastSeen").toInstant(), ZoneId.systemDefault()),
						InetAddress.getByName(resultSet.getString("IP"))
				);
			}
		} catch (SQLException | UnknownHostException e) {
			System.err.println("Could not load Name History from database | " + e.getMessage());
		}
		return ret;
	}

	@Override
	public List<String> findAllUniqueNamesStartingWith(String prefix) {
		List<String> ret = new ArrayList<>();
		try {
			PreparedStatement statement = mySQLService.getConnection().prepareStatement("SELECT DISTINCT `Name` FROM `UserCache` WHERE `Name` LIKE ?");
			statement.setString(1, prefix + "%");
			ResultSet resultSet = statement.executeQuery();
			if (resultSet.next()) {
				ret.add(resultSet.getString("Name"));
			}
		} catch (SQLException e) {
			System.err.println("Could not load Name History from database | " + e.getMessage());
		}
		return ret;
	}

	@Override
	public NameEntry findLatestByInetAddress(InetAddress address) {
		NameEntry ret = null;
		try {
			PreparedStatement statement = mySQLService.getConnection().prepareStatement("SELECT * FROM `UserCache` WHERE `IP` LIKE ? ORDER BY `LastSeen` DESC LIMIT 1");
			statement.setString(1, address.getHostName());
			ResultSet resultSet = statement.executeQuery();
			if (resultSet.next()) {
				ret = new NameEntry(UUID.fromString(resultSet.getString("UUID")),
						resultSet.getString("Name"),
						LocalDateTime.ofInstant(resultSet.getTimestamp("FirstSeen").toInstant(), ZoneId.systemDefault()),
						LocalDateTime.ofInstant(resultSet.getTimestamp("LastSeen").toInstant(), ZoneId.systemDefault()),
						InetAddress.getByName(resultSet.getString("IP")));
			}
		} catch (SQLException | UnknownHostException e) {
			System.err.println("Could not load Name History from database | " + e.getMessage());
		}
		return ret;
	}

	@Override
	public void save(NameEntry nameEntry) {
		try {
			PreparedStatement statement = mySQLService.getConnection().prepareStatement("INSERT INTO `UserCache` (UUID, Name, FirstSeen, LastSeen, IP) VALUES (?, ?, ?, ?, ?)");
			statement.setString(1, nameEntry.getUuid().toString());
			statement.setString(2, nameEntry.getName());
			statement.setTimestamp(3, Timestamp.valueOf(nameEntry.getFirstSeen()));
			statement.setTimestamp(4, Timestamp.valueOf(nameEntry.getLastSeen()));
			statement.setString(5, nameEntry.getInetAddress().getHostAddress());
			statement.execute();
		} catch (SQLException e) {
			System.err.println("Could create Name History in database | " + e.getMessage());
		}
	}

	@Override
	public void update(NameEntry nameEntry) {
		try {
			PreparedStatement statement = mySQLService.getConnection().prepareStatement("UPDATE `UserCache` SET `LastSeen`=?, `IP` = ? WHERE `UUID`=? AND `FirstSeen`=? ");
			statement.setTimestamp(1, Timestamp.valueOf(nameEntry.getLastSeen()));
			statement.setString(2, nameEntry.getInetAddress().getHostAddress());
			statement.setString(3, nameEntry.getUuid().toString());
			statement.setTimestamp(4, Timestamp.valueOf(nameEntry.getFirstSeen()));
			statement.executeUpdate();
		} catch (SQLException e) {
			System.err.println("Could update Name History in database | " + e.getMessage());
		}
	}

}
