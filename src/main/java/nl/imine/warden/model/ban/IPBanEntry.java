package nl.imine.warden.model.ban;

import java.net.InetAddress;
import java.time.LocalDateTime;
import java.util.UUID;

public class IPBanEntry extends BanEntry {

	private InetAddress ip;

	public IPBanEntry() {

	}

	public IPBanEntry(UUID uuid, UUID fromUUID, String reason, LocalDateTime banTimestmap, BanType banType, boolean active, InetAddress ip) {
		super(uuid, fromUUID, reason, banTimestmap, banType, active);
		this.ip = ip;
	}

	public InetAddress getIp() {
		return ip;
	}

	public void setIp(InetAddress ip) {
		this.ip = ip;
	}
}
