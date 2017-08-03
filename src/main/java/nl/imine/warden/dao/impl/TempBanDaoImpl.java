package nl.imine.warden.dao.impl;

import java.sql.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import nl.imine.warden.dao.TempBanDao;
import nl.imine.warden.database.DatabaseSetup;
import nl.imine.warden.model.ban.BanType;
import nl.imine.warden.model.ban.TempBanEntry;
import nl.imine.warden.service.mysql.MySQLConfig;
import nl.imine.warden.service.mysql.MySQLService;

public class TempBanDaoImpl implements TempBanDao {

	private MySQLService mySQLService;

	public TempBanDaoImpl(MySQLService mySQLService) {
		this.mySQLService = mySQLService;
	}

	@Override
	public List<TempBanEntry> getAllTempBans() {
		List<TempBanEntry> ret = new ArrayList<>();
		try {
			PreparedStatement statement = mySQLService.getConnection().prepareStatement("SELECT * FROM `Ban` INNER JOIN `TempBan` ON `Ban.id` = `TempBan.id` WHERE `ban.BanType` = ?;");
			statement.setInt(1, BanType.TEMPBAN.getId());
			ResultSet resultSet = statement.executeQuery();
			if (resultSet.next()) {
				TempBanEntry tempBan = new TempBanEntry(
						UUID.fromString(resultSet.getString("UUID")),
						UUID.fromString(resultSet.getString("FromUUID")),
						resultSet.getString("Reason"),
						LocalDateTime.ofInstant(resultSet.getTimestamp("Timestamp").toInstant(), ZoneId.systemDefault()),
						BanType.TEMPBAN,
						resultSet.getBoolean("Active"),
						LocalDateTime.ofInstant(resultSet.getTimestamp("UnbanTimestamp").toInstant(), ZoneId.systemDefault()),
						Duration.ofSeconds(resultSet.getInt("Duration"))
				);
				ret.add(tempBan);
			}
		} catch (SQLException e) {
			System.err.println("Could not load TempBans from database | " + e.getMessage());
		}
		return ret;
	}

	@Override
	public TempBanEntry getTempBan(UUID uuid) {
		TempBanEntry ret = null;
		try {
			PreparedStatement statement = mySQLService.getConnection().prepareStatement("SELECT * FROM `Ban` INNER JOIN `TempBan`ON Ban.id = TempBan.id WHERE `UUID` = ? AND `Active` = '1' AND `UnbanTimestamp` > ? LIMIT 1;");
			statement.setString(1, uuid.toString());
			statement.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
			ResultSet resultSet = statement.executeQuery();
			if (resultSet.next()) {
				ret = new TempBanEntry(
						UUID.fromString(resultSet.getString("UUID")),
						UUID.fromString(resultSet.getString("FromUUID")),
						resultSet.getString("Reason"),
						LocalDateTime.ofInstant(resultSet.getTimestamp("Timestamp").toInstant(), ZoneId.systemDefault()),
						BanType.TEMPBAN,
						resultSet.getBoolean("Active"),
						LocalDateTime.ofInstant(resultSet.getTimestamp("UnbanTimestamp").toInstant(), ZoneId.systemDefault()),
						Duration.ofSeconds(resultSet.getInt("Duration"))
				);
			}
		} catch (SQLException e) {
			System.err.println("Could not load Tempban from database | " + e.getMessage());
		}
		return ret;
	}

	public static void main(String[] args) {
		MySQLConfig mySQLConfig = new MySQLConfig();
		mySQLConfig.loadConfigFile();

		MySQLService mySQLService = new MySQLService(mySQLConfig.getUser(), mySQLConfig.getPassword(), mySQLConfig.getJdbcUrl());
		mySQLService.connect();

		new DatabaseSetup(mySQLService).setup();

		TempBanDao tempBanDao = new TempBanDaoImpl(mySQLService);
		tempBanDao.createTempBan(new TempBanEntry(UUID.randomUUID(), UUID.randomUUID(), "reason", LocalDateTime.now(), BanType.TEMPBAN, true, LocalDateTime.now().plusDays(1), Duration.ofSeconds(60 * 60 * 24)));
	}

	@Override
	public void createTempBan(TempBanEntry tempBan) {
		Connection conn = mySQLService.getNewConnection();
		try {
			conn.setAutoCommit(false);
			PreparedStatement createBanStatement = conn.prepareStatement("INSERT INTO `Ban` (UUID, FromUUID, Reason, BanType, Active) VALUES (?, ?, ?, ?, ?);", Statement.RETURN_GENERATED_KEYS);
			createBanStatement.setString(1, tempBan.getUuid().toString());
			createBanStatement.setString(2, tempBan.getFromUUID().toString());
			createBanStatement.setString(3, tempBan.getReason());
			createBanStatement.setInt(4, tempBan.getBanType().getId());
			createBanStatement.setBoolean(5, tempBan.isActive());
			createBanStatement.execute();

			PreparedStatement createTempBanStatement = conn.prepareStatement("INSERT  INTO `TempBan` (id, UnbanTimestamp, Duration) VALUES (?, ?, ?);");
			ResultSet resultSet = createBanStatement.getGeneratedKeys();
			resultSet.next();
			createTempBanStatement.setInt(1, resultSet.getInt(1));
			createTempBanStatement.setTimestamp(2, Timestamp.valueOf(tempBan.getUnbanTimestamp()));
			createTempBanStatement.setLong(3, tempBan.getBanDuration().getSeconds());
			createTempBanStatement.execute();

			conn.commit();
		} catch (SQLException e) {
			System.err.println("Could not insert TempBan into database | " + e.getMessage());
			try {
				conn.rollback();
			} catch (SQLException e1) {
				System.err.println("Could not rollback TempBan query | " + e.getMessage());
			}
		} finally {
			try {
				conn.setAutoCommit(true);
			} catch (SQLException e) {
				System.err.println("Could not re-enable TempBan Autocommit | " + e.getMessage());
			}
		}
	}

	@Override
	public void updateTempBan(TempBanEntry tempBan) {
		try {
			PreparedStatement statement = mySQLService.getNewConnection().prepareStatement("UPDATE `Ban` ban INNER JOIN `TempBan` tempBan ON Ban.id = tempBan.id SET `FromUUID`=?, `Reason`=?, `Timestamp`=?, `UnbanTimestamp`=?, `Duration`=? WHERE `UUID` = ? AND `Timestamp` = ?;");
			statement.setString(1, tempBan.getFromUUID().toString());
			statement.setString(2, tempBan.getReason());
			statement.setDate(3, new Date(Calendar.getInstance().getTimeInMillis()));
			statement.setTimestamp(4, Timestamp.valueOf(tempBan.getUnbanTimestamp()));
			statement.setLong(5, tempBan.getBanDuration().getSeconds());
			statement.setString(6, tempBan.getUuid().toString());
			statement.setTimestamp(7, Timestamp.valueOf(tempBan.getBanTimestmap()));
			statement.execute();
		} catch (SQLException e) {
			System.err.println("Could not update TempBan to database | " + e.getMessage());
		}
	}

	@Override
	public void deleteTempBan(TempBanEntry tempBan) {
		try {
			PreparedStatement statement = mySQLService.getConnection().prepareStatement("DELETE FROM `TempBan` WHERE `UUID` = ?;");
			statement.setString(1, tempBan.getUuid().toString());
			statement.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
