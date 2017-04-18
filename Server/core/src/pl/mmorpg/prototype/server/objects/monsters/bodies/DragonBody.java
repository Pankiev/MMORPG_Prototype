package pl.mmorpg.prototype.server.objects.monsters.bodies;

import java.util.Collection;

import pl.mmorpg.prototype.server.objects.items.Item;
import pl.mmorpg.prototype.server.resources.Assets;

public class DragonBody extends MonsterBody
{
	public DragonBody(long id, int gold, Collection<Item> loot)
	{
		super(Assets.get("dead-dragon.png"), id, gold, loot);
	}

}
