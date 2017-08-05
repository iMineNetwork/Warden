package nl.imine.warden.service;

import java.net.InetAddress;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import nl.imine.warden.dao.BanDao;
import nl.imine.warden.dao.IPBanDao;
import nl.imine.warden.dao.PardonDao;
import nl.imine.warden.dao.TempBanDao;
import nl.imine.warden.dao.impl.BanDaoImpl;
import nl.imine.warden.dao.impl.IPBanDaoImpl;
import nl.imine.warden.dao.impl.PardonDaoImpl;
import nl.imine.warden.dao.impl.TempBanDaoImpl;
import nl.imine.warden.model.ban.*;
import nl.imine.warden.service.mysql.MySQLService;

public class BanService {

	private BanDao banDao;
	private TempBanDao tempBanDao;
	private IPBanDao ipBanDao;
	private PardonDao pardonDao;

	public BanService(MySQLService mySQLService) {
		banDao = new BanDaoImpl(mySQLService);
		tempBanDao = new TempBanDaoImpl(mySQLService);
		ipBanDao = new IPBanDaoImpl(mySQLService);
		pardonDao = new PardonDaoImpl(mySQLService);
	}

	public BanEntry getBan(UUID player) {
		return banDao.getBan(player);
	}

	public IPBanEntry getIPBan(InetAddress address) {
		return ipBanDao.getIPBan(address);
	}

	public TempBanEntry getTempBan(UUID uuid) {
		return tempBanDao.getTempBan(uuid);
	}

	public List<UUID> getBannedPlayerIds() {
		return banDao.getAllBans().stream().filter(BanEntry::isActive).map(BanEntry::getUuid).collect(Collectors.toList());
	}

	public List<UUID> getIPBannedPlayerIds() {
		return ipBanDao.getAllIPBans().stream().filter(IPBanEntry::isActive).map(BanEntry::getUuid).collect(Collectors.toList());
	}

	public List<UUID> getTempBannedPlayerIds() {
		return tempBanDao.getAllTempBans().stream().filter(tempBan -> !LocalDateTime.now().isAfter(tempBan.getUnbanTimestamp())).filter(BanEntry::isActive).map(BanEntry::getUuid).collect(Collectors.toList());
	}

	public void banPlayer(UUID player, UUID source, String reason) {
		banDao.createBan(new BanEntry(player, source, reason, LocalDateTime.now(), BanType.BAN, true));
	}

	public void ipBanPlayer(InetAddress playerAddress, UUID player, UUID source, String reason) {
		ipBanDao.createIPBan(new IPBanEntry(player, source, reason, LocalDateTime.now(), BanType.IPBAN, true, playerAddress));
	}

	public void ipBanAddress(InetAddress address, UUID source, String reason) {
		ipBanDao.createIPBan(new IPBanEntry(null, source, reason, LocalDateTime.now(), BanType.IPBAN, true, address));
	}

	public void tempBanPlayer(UUID player, UUID source, String reason, Duration duration) {
		tempBanDao.createTempBan(new TempBanEntry(player, source, reason, LocalDateTime.now(), BanType.TEMPBAN, true, LocalDateTime.now().plus(duration), duration));
	}

	public boolean pardonPlayer(UUID playerId, UUID fromId) {
		BanEntry ban = banDao.getBan(playerId);
		if (ban != null) {
			banDao.deleteBan(ban);
			pardonDao.createPardon(new PardonEntry(playerId, fromId, LocalDateTime.now(), ban.getBanTimestmap()));
			return true;
		}
		return false;
	}

	public boolean pardonIp(InetAddress address, UUID fromUUID){
		IPBanEntry ipBanEntry = ipBanDao.getIPBan(address);
		if (ipBanEntry != null) {
			banDao.deleteBan(ipBanEntry);
			pardonDao.createPardon(new PardonEntry(ipBanEntry.getUuid(), fromUUID, LocalDateTime.now(), ipBanEntry.getBanTimestmap()));
			return true;
		}
		return false;
	}
}
