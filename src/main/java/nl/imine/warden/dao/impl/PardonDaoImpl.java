package nl.imine.warden.dao.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

import nl.imine.warden.dao.PardonDao;
import nl.imine.warden.model.ban.PardonEntry;
import nl.imine.warden.service.mysql.MySQLService;

public class PardonDaoImpl implements PardonDao {

	private MySQLService mySQLService;

	public PardonDaoImpl(MySQLService mySQLService) {
		this.mySQLService = mySQLService;
	}

	@Override
	public void createPardon(PardonEntry pardonEntry) {
		try {
			PreparedStatement statement = mySQLService.getConnection().prepareStatement("INSERT INTO `Pardon` (UUID, FromUUID, Timestamp, BanTime) VALUES (?, ?, ?, ?)");
			statement.setString(1, pardonEntry.getUuid().toString());
			statement.setString(2, pardonEntry.getFromUUID().toString());
			statement.setTimestamp(3, Timestamp.valueOf(pardonEntry.getTimestamp()));
			statement.setTimestamp(4, Timestamp.valueOf(pardonEntry.getBanTime()));
			statement.execute();
		} catch (SQLException e) {
			System.err.println("Could create Pardon Entry in database | " + e.getMessage());
		}
	}
}