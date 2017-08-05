package nl.imine.warden.model.ban;

import java.time.LocalDateTime;
import java.util.UUID;

public class PardonEntry {

	private UUID uuid;
	private UUID fromUUID;
	private LocalDateTime timestamp;
	private LocalDateTime banTime;

	public PardonEntry(UUID uuid, UUID fromUUID, LocalDateTime timestamp, LocalDateTime banTime) {
		this.uuid = uuid;
		this.fromUUID = fromUUID;
		this.timestamp = timestamp;
		this.banTime = banTime;
	}

	public UUID getUuid() {
		return uuid;
	}

	public void setUuid(UUID uuid) {
		this.uuid = uuid;
	}

	public UUID getFromUUID() {
		return fromUUID;
	}

	public void setFromUUID(UUID fromUUID) {
		this.fromUUID = fromUUID;
	}

	public LocalDateTime getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(LocalDateTime timestamp) {
		this.timestamp = timestamp;
	}

	public LocalDateTime getBanTime() {
		return banTime;
	}

	public void setBanTime(LocalDateTime banTime) {
		this.banTime = banTime;
	}
}
