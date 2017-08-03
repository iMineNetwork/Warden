package nl.imine.warden.service.mysql;

import nl.imine.warden.WardenPlugin;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

public class MySQLConfig {

	private String jdbcUrl = "UNDEFINED";
	private String user = "UNDEFINED";
	private String password = "UNDEFINED";

	public void loadConfigFile() {
		try {
			Path dir = WardenPlugin.getInstance().getDataFolder().toPath();
			if (!Files.exists(dir) || !Files.isDirectory(dir)) {
				Files.createDirectory(dir);
			}

			Properties properties = null;
			Path config = dir.resolve("mysql.properties");
			if (!Files.exists(config)) {
				properties = createConfigFile(config);
			}

			if (properties == null) {
				properties = new Properties();
				properties.load(Files.newInputStream(config));
			}

			jdbcUrl = properties.getProperty("JDBC-URL");
			user = properties.getProperty("Username");
			password = properties.getProperty("Password");
		} catch (IOException e) {
			System.err.println("Could not load MySQL config | " + e.getMessage());
		}
	}

	private Properties createConfigFile(Path config) {
		Properties properties = new Properties();
		properties.setProperty("JDBC-URL", "jdbc:mysql://HOST:PORT/DATABASE");
		properties.setProperty("Username", "user");
		properties.setProperty("Password", "pass");
		try {
			properties.store(Files.newOutputStream(config), "This config contains the credentials and url to the Mysql server to connect to. \nhis is reloaded every time the plugin reloads.");
		} catch (IOException e) {
			System.err.println("Could not create MySQL config | " + e.getMessage());
		}
		return properties;
	}

	public String getJdbcUrl() {
		return jdbcUrl;
	}

	public String getUser() {
		return user;
	}

	public String getPassword() {
		return password;
	}
}
