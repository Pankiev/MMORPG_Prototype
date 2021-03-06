package pl.mmorpg.prototype.server.database.seeders;

import pl.mmorpg.prototype.SpringContext;
import pl.mmorpg.prototype.data.entities.User;
import pl.mmorpg.prototype.data.entities.UserRole;
import pl.mmorpg.prototype.data.entities.repositories.UserRepository;

public class UsersTableSeeder implements TableSeeder
{
	private final UserRepository userRepo = SpringContext.getBean(UserRepository.class);
	
	@Override
	public void seed()
	{
		userRepo.save(createUser("Pankiev", "password123", UserRole.USER));
		userRepo.save(createUser("Smyk", "bro", UserRole.USER));
		userRepo.save(createUser("admin", "admin", UserRole.ADMIN));
	}

	private User createUser(String username, String password, UserRole role)
	{
		User user = new User();
		user.setUsername(username);
		user.setPassword(password);
		user.setRole(role);
		return user;
	}

}
