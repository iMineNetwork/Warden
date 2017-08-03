package nl.imine.warden.model.ban;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

public class TempBanEntry extends BanEntry {

	private LocalDateTime unbanTimestamp;
	private Duration banDuration;

	public TempBanEntry(UUID uuid, UUID fromUUID, String reason, LocalDateTime banTimestmap, BanType banType, boolean active, LocalDateTime unbanTimestamp, Duration banDuration) {
		super(uuid, fromUUID, reason, banTimestmap, banType, active);
		this.unbanTimestamp = unbanTimestamp;
		this.banDuration = banDuration;
	}

	public LocalDateTime getUnbanTimestamp() {
		return unbanTimestamp;
	}

	public void setUnbanTimestamp(LocalDateTime unbanTimestamp) {
		this.unbanTimestamp = unbanTimestamp;
	}

	public Duration getBanDuration() {
		return banDuration;
	}

	public void setBanDuration(Duration banDuration) {
		this.banDuration = banDuration;
	}
}
