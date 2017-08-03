package nl.imine.warden.dao;

import nl.imine.warden.model.ban.TempBanEntry;

import java.util.List;
import java.util.UUID;

public interface TempBanDao {
    List<TempBanEntry> getAllTempBans();
    TempBanEntry getTempBan(UUID player);
    void updateTempBan(TempBanEntry ban);
    void createTempBan(TempBanEntry ban);
    void deleteTempBan(TempBanEntry ban);
}
