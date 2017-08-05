package nl.imine.warden.dao;

import nl.imine.warden.model.ban.PardonEntry;

public interface PardonDao {

	void createPardon(PardonEntry pardonEntry);
}
