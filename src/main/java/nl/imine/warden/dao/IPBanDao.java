package nl.imine.warden.dao;

import nl.imine.warden.model.ban.IPBanEntry;

import java.net.InetAddress;
import java.util.List;

public interface IPBanDao {
    List<IPBanEntry> getAllIPBans();
    IPBanEntry getIPBan(InetAddress player);
    void updateIPBan(IPBanEntry ban);
    void createIPBan(IPBanEntry ban);
    void deleteIPBan(IPBanEntry ban);
}
