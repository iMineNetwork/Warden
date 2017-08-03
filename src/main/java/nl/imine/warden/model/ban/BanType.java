package nl.imine.warden.model.ban;

public enum BanType {

	BAN(0), IPBAN(1), TEMPBAN(2);

	int id;

	BanType(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public static BanType getType(int type) {
		for (BanType banType : BanType.values()) {
			if (banType.getId() == type) {
				return banType;
			}
		}
		return BAN;
	}
}
