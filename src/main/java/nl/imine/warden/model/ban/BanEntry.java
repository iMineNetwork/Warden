package nl.imine.warden.model.ban;

import java.time.LocalDateTime;
import java.util.UUID;

public class BanEntry {
	private UUID uuid;
	private UUID fromUUID;
	private String reason;
	private LocalDateTime banTimestmap;
	private BanType banType;
	private boolean active;

	public BanEntry() {
	}

	public BanEntry(UUID uuid, UUID fromUUID, String reason, LocalDateTime banTimestmap, BanType banType, boolean active) {
		this.uuid = uuid;
		this.fromUUID = fromUUID;
		this.reason = reason;
		this.banTimestmap = banTimestmap;
		this.banType = banType;
		this.active = active;
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

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public LocalDateTime getBanTimestmap() {
		return banTimestmap;
	}

	public void setBanTimestmap(LocalDateTime banTimestmap) {
		this.banTimestmap = banTimestmap;
	}

	public BanType getBanType() {
		return banType;
	}

	public void setBanType(BanType banType) {
		this.banType = banType;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}
}
