package nl.imine.warden.service;

import java.net.InetAddress;
import java.util.List;
import java.util.UUID;

import nl.imine.warden.dao.UserCacheDao;
import nl.imine.warden.dao.impl.UserCacheDaoImpl;
import nl.imine.warden.model.usercache.NameEntry;
import nl.imine.warden.service.mysql.MySQLService;

public class UserCacheService {

	private UserCacheDao userCacheDao;

	public UserCacheService(MySQLService mySQLService) {
		this.userCacheDao = new UserCacheDaoImpl(mySQLService);
	}

	public NameEntry getLatestByName(String name){
		return userCacheDao.findLatestByName(name);
	}

	public List<String> getAllUniqueNamesStartingWith(String prefix) {
		return userCacheDao.findAllUniqueNamesStartingWith(prefix);
	}

	public NameEntry getLatestNameByUUID(UUID uuid) {
		return userCacheDao.findLatestByUUID(uuid);
	}

	public NameEntry getLatestByInetAddress(InetAddress inetAddress) { return userCacheDao.findLatestByInetAddress(inetAddress); }

	public void create(NameEntry nameEntry) {
		userCacheDao.save(nameEntry);
	}

	public void update(NameEntry nameEntry) {
		userCacheDao.update(nameEntry);
	}
}
