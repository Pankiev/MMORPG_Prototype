package pl.mmorpg.prototype.server.packetshandling.characteractions;

import com.badlogic.gdx.Gdx;

import pl.mmorpg.prototype.server.collision.pixelmap.PixelCollisionMap;
import pl.mmorpg.prototype.server.objects.GameObject;
import pl.mmorpg.prototype.server.objects.MovableGameObject;
import pl.mmorpg.prototype.server.states.PlayState;

public class MoveLeftPacketHandler extends MovePacketHandler
{

	public MoveLeftPacketHandler(PlayState playState)
	{
		super(playState);
	}

	@Override
	public void perform(MovableGameObject operationTarget, PixelCollisionMap<GameObject> collisionMap)
	{
		operationTarget.moveLeft(collisionMap, Gdx.graphics.getDeltaTime());
	}

}
