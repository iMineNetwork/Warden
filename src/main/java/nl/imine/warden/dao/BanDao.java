package nl.imine.warden.dao;

import nl.imine.warden.model.ban.BanEntry;

import java.util.List;
import java.util.UUID;

public interface BanDao {
    List<BanEntry> getAllBans();
    BanEntry getBan(UUID player);
    void updateBan(BanEntry ban);
    void createBan(BanEntry ban);
    void deleteBan(BanEntry ban);
}
