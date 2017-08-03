package nl.imine.warden.dao.impl;

import nl.imine.warden.dao.BanDao;
import nl.imine.warden.model.ban.BanEntry;
import nl.imine.warden.model.ban.BanType;
import nl.imine.warden.service.mysql.MySQLService;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

public class BanDaoImpl implements BanDao {

    private MySQLService mySQLService;

    public BanDaoImpl(MySQLService mySQLService) {
        this.mySQLService = mySQLService;
    }

    @Override
    public List<BanEntry> getAllBans() {
        List<BanEntry> ret = new ArrayList<>();
        try {
            PreparedStatement statement = mySQLService.getConnection().prepareStatement("SELECT * FROM `Ban` WHERE BanType = ?;");
            statement.setInt(1, BanType.BAN.getId());
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                BanEntry ban = new BanEntry();
                ban.setUuid(UUID.fromString(resultSet.getString("UUID")));
                ban.setFromUUID(UUID.fromString(resultSet.getString("FromUUID")));
                ban.setReason(resultSet.getString("Reason"));
                ban.setBanTimestmap(LocalDateTime.ofInstant(resultSet.getTimestamp("Timestamp").toInstant(), ZoneId.systemDefault()));
                ban.setBanType(BanType.BAN);
                ban.setActive(resultSet.getBoolean("Active"));
                ret.add(ban);
            }
        } catch (SQLException e) {
            System.err.println("Could not load Bans from database | " + e.getMessage());
        }
        return ret;
    }

    @Override
    public BanEntry getBan(UUID uuid) {
        BanEntry ret = null;
        try {
            PreparedStatement statement = mySQLService.getConnection().prepareStatement("SELECT * FROM `Ban` WHERE `UUID` = ? AND BanType = ? AND Active = 1 ORDER BY `Timestamp` DESC LIMIT 1;");
            statement.setString(1, uuid.toString());
            statement.setInt(2, BanType.BAN.getId());
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                ret = new BanEntry();
                ret.setUuid(UUID.fromString(resultSet.getString("UUID")));
                ret.setFromUUID(UUID.fromString(resultSet.getString("FromUUID")));
                ret.setReason(resultSet.getString("Reason"));
                ret.setBanTimestmap(LocalDateTime.ofInstant(resultSet.getTimestamp("Timestamp").toInstant(), ZoneId.systemDefault()));
                ret.setBanType(BanType.BAN);
                ret.setActive(resultSet.getBoolean("Active"));
            }
        } catch (SQLException e) {
            System.err.println("Could not load Ban from database | " + e.getMessage());
        }
        return ret;
    }

    @Override
    public void createBan(BanEntry ban) {
        try {
            PreparedStatement statement = mySQLService.getConnection().prepareStatement("INSERT INTO `Ban` (UUID, FromUUID, Reason, BanType, Active) VALUES (?, ?, ?, ?, ?);");
            statement.setString(1, ban.getUuid().toString());
            statement.setString(2, ban.getFromUUID().toString());
            statement.setString(3, ban.getReason());
            statement.setInt(4, ban.getBanType().getId());
            statement.setBoolean(5, ban.isActive());
            statement.execute();
        } catch (SQLException e) {
            System.err.println("Could not load Ban from database | " + e.getMessage());
        }
    }

    @Override
    public void updateBan(BanEntry ban) {
        try {
            PreparedStatement statement = mySQLService.getConnection().prepareStatement("UPDATE `Ban` SET `FromUUID`=?, `Reason`=?, `Timestamp`=?, Active=? WHERE `UUID` = ?;");
            statement.setString(1, ban.getFromUUID().toString());
            statement.setString(2, ban.getReason());
            statement.setDate(3, new Date(Calendar.getInstance().getTimeInMillis()));
            statement.setBoolean(4, ban.isActive());
            statement.setString(5, ban.getUuid().toString());
            statement.execute();
        } catch (SQLException e) {
            System.err.println("Could not load Ban from database | " + e.getMessage());
        }
    }

    @Override
    public void deleteBan(BanEntry ban) {
        try {
            PreparedStatement statement = mySQLService.getConnection().prepareStatement("UPDATE `Ban` SET Active='0' WHERE `UUID` = ?;");
            statement.setString(1, ban.getUuid().toString());
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
