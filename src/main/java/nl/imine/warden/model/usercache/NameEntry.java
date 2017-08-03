package nl.imine.warden.model.usercache;

import java.net.InetAddress;
import java.time.LocalDateTime;
import java.util.UUID;

public class NameEntry {

	private UUID uuid;
	private String name;
	private LocalDateTime firstSeen;
	private LocalDateTime lastSeen;
	private InetAddress inetAddress;

	public NameEntry(UUID uuid, String name, LocalDateTime firstSeen, LocalDateTime lastSeen, InetAddress inetAddress) {
		this.uuid = uuid;
		this.name = name;
		this.firstSeen = firstSeen;
		this.lastSeen = lastSeen;
		this.inetAddress = inetAddress;
	}

	public UUID getUuid() {
		return uuid;
	}

	public void setUuid(UUID uuid) {
		this.uuid = uuid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public LocalDateTime getFirstSeen() {
		return firstSeen;
	}

	public void setFirstSeen(LocalDateTime firstSeen) {
		this.firstSeen = firstSeen;
	}

	public LocalDateTime getLastSeen() {
		return lastSeen;
	}

	public void setLastSeen(LocalDateTime lastSeen) {
		this.lastSeen = lastSeen;
	}

	public InetAddress getInetAddress() {
		return inetAddress;
	}

	public void setInetAddress(InetAddress inetAddress) {
		this.inetAddress = inetAddress;
	}
}
