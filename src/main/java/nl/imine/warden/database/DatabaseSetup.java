package nl.imine.warden.database;

import nl.imine.warden.service.mysql.MySQLService;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DatabaseSetup {

    private MySQLService mySQLService;

    public DatabaseSetup(MySQLService mySQLService) {
        this.mySQLService = mySQLService;
    }

    public void setup(){
        try {
            Connection connection = mySQLService.getConnection();
            PreparedStatement createBanTable = connection
                    .prepareStatement("CREATE TABLE IF NOT EXISTS `Ban` (id MEDIUMINT NOT NULL AUTO_INCREMENT,`UUID` VARCHAR(36) NOT NULL, `FromUUID` VARCHAR(36) NOT NULL, `Reason` TEXT NOT NULL, `Timestamp` TIMESTAMP DEFAULT CURRENT_TIMESTAMP, `BanType` TINYINT NOT NULL, `Active` BIT DEFAULT 1, CONSTRAINT PK_Ban PRIMARY KEY(id))");
            createBanTable.execute();
            PreparedStatement createIPBanTable = connection
                    .prepareStatement("CREATE TABLE IF NOT EXISTS `IpBan` (`id` MEDIUMINT NOT NULL, `IP` VARCHAR(16) NOT NULL, FOREIGN KEY(id) REFERENCES Ban(id), CONSTRAINT PK_IPBan PRIMARY KEY(id));");
            createIPBanTable.execute();
            PreparedStatement createTempBanTable = connection
                    .prepareStatement("CREATE TABLE IF NOT EXISTS `TempBan` (`id` MEDIUMINT NOT NULL, `UnbanTimestamp` TIMESTAMP NOT NULL, `Duration` INT NOT NULL, FOREIGN KEY(id) REFERENCES Ban(id), CONSTRAINT PK_TempBan PRIMARY KEY(id));");
            createTempBanTable.execute();
            PreparedStatement createNameCacheTable = connection
                    .prepareStatement("CREATE TABLE IF NOT EXISTS `UserCache` (`UUID` VARCHAR(36), `Name` VARCHAR(16), `FirstSeen` TIMESTAMP, `LastSeen` TIMESTAMP, `IP` VARCHAR(32) NOT NULL, CONSTRAINT PK_NameCache PRIMARY KEY(UUID, FirstSeen))");
            createNameCacheTable.execute();
            PreparedStatement createPardonTable = connection
                    .prepareStatement("CREATE TABLE IF NOT EXISTS `Pardon` (`UUID` VARCHAR(36) NOT NULL, `FromUUID` VARCHAR(36), `Timestamp` TIMESTAMP NOT NULL, `BanTime` TIMESTAMP NOT NULL, CONSTRAINT PK_Pardon PRIMARY KEY(UUID, BanTime))");
            createPardonTable.execute();
        } catch (SQLException e) {
            System.err.println("Could not create tables | " + e.getMessage());
        }
    }
}
