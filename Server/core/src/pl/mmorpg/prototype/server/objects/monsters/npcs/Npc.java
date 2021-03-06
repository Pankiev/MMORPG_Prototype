package pl.mmorpg.prototype.server.objects.monsters.npcs;

import com.badlogic.gdx.math.Rectangle;
import pl.mmorpg.prototype.server.objects.monsters.properties.builders.NpcDefaultPropertiesBuilder;
import pl.mmorpg.prototype.server.collision.pixelmap.PixelCollisionMap;
import pl.mmorpg.prototype.server.objects.GameObject;
import pl.mmorpg.prototype.server.objects.monsters.WalkingMonster;
import pl.mmorpg.prototype.server.resources.Assets;
import pl.mmorpg.prototype.server.states.PlayState;

public abstract class Npc extends WalkingMonster
{ 
	private final String name;

	public Npc(String name, long id, Rectangle walkingBounds, PixelCollisionMap<GameObject> collisionMap, PlayState playState)
	{
		super(Assets.get("npc.png"), id, new NpcDefaultPropertiesBuilder().build(), walkingBounds, collisionMap, playState);
		this.name = name;
		setSize(32, 32);
	}
	
	public String getName()
	{
		return name;
	}

}
