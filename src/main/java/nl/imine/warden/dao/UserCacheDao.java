package nl.imine.warden.dao;

import java.net.InetAddress;
import java.util.List;
import java.util.UUID;

import nl.imine.warden.model.usercache.NameEntry;

public interface UserCacheDao {
	List<NameEntry> findAllByUUID(UUID uuid);
	NameEntry findLatestByUUID(UUID uuid);
	List<NameEntry> findAllByName(String name);
	NameEntry findLatestByName(String name);
	NameEntry findLatestByInetAddress(InetAddress address);
	List<String> findAllUniqueNamesStartingWith(String prefix);
	void save(NameEntry nameEntry);
	void update(NameEntry nameEntry);
}
