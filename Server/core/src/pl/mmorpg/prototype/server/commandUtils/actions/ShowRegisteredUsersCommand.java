package pl.mmorpg.prototype.server.commandUtils.actions;

import pl.mmorpg.prototype.SpringApplicationContext;
import pl.mmorpg.prototype.server.database.repositories.UserRepository;

public class ShowRegisteredUsersCommand implements CommandAction
{

	@Override
	public void run(String args)
	{
		UserRepository characterRepo = SpringApplicationContext.getBean(UserRepository.class);
		characterRepo.findAll().forEach(user -> System.out.println(user));
	}

	@Override
	public String getDescription()
	{
		return "Show all registered users";
	}

	@Override
	public String getName()
	{
		return "users-show";
	}

}
