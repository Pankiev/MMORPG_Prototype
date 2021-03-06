package pl.mmorpg.prototype.server.objects.monsters.properties.builders;

import pl.mmorpg.prototype.clientservercommon.packets.monsters.properties.MonsterProperties;

public class GreenDragonPropertiesBuilder extends MonsterProperties.Builder
{
	@Override
	public MonsterProperties build()
	{
		experienceGain(100)
				.hp(100)
				.strength(8)
				.level(1);
		return super.build();
	}
}
