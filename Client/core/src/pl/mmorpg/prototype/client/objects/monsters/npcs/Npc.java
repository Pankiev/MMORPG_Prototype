package pl.mmorpg.prototype.client.objects.monsters.npcs;

import pl.mmorpg.prototype.client.collision.interfaces.CollisionMap;
import pl.mmorpg.prototype.client.objects.GameObject;
import pl.mmorpg.prototype.client.objects.monsters.Monster;
import pl.mmorpg.prototype.client.objects.monsters.TextureSheetAnimationInfo;
import pl.mmorpg.prototype.client.packethandlers.PacketHandlerRegisterer;
import pl.mmorpg.prototype.clientservercommon.packets.monsters.properties.MonsterProperties;

public abstract class Npc extends Monster
{
	protected Npc(String name, TextureSheetAnimationInfo sheetInfo, long id,
			MonsterProperties properties, CollisionMap<GameObject> collisionMap,
			PacketHandlerRegisterer registerer)
	{
		super(sheetInfo, id, properties, collisionMap, registerer);
		super.setName(name);
	}

}
