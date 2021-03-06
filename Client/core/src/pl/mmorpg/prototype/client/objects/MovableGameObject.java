package pl.mmorpg.prototype.client.objects;

import com.badlogic.gdx.graphics.Texture;

import pl.mmorpg.prototype.client.collision.interfaces.CollisionMap;
import pl.mmorpg.prototype.client.packethandlers.GameObjectTargetPacketHandler;
import pl.mmorpg.prototype.client.packethandlers.PacketHandlerBase;
import pl.mmorpg.prototype.client.packethandlers.PacketHandlerRegisterer;
import pl.mmorpg.prototype.clientservercommon.packets.movement.Directions;
import pl.mmorpg.prototype.clientservercommon.packets.movement.ObjectRepositionPacket;

public abstract class MovableGameObject extends GameObject
{
	private final static float stopMovingTimeBound = 0.2f;
	private float stopMovingTime = 0.0f;
	private float stepSpeed = 150.0f;
	private float targetX;
	private float targetY;
	private boolean slidingActive = true;
	private final MoveInfo currentMoveInfo = new MoveInfo();
	private CollisionMap<GameObject> linkedCollisionMap;

	public MovableGameObject(Texture lookout, long id, CollisionMap<GameObject> linkedCollisionMap,
			PacketHandlerRegisterer registerer)
	{
		super(lookout, id, registerer);
		this.linkedCollisionMap = linkedCollisionMap;
		targetX = getX();
		targetY = getY();
		registerPacketHandler(new ObjectRepositionPacketHandler());
	}

	protected void initPosition(float x, float y)
	{
		targetX = x;
		targetY = y;
		super.setX(x);
		super.setY(y);
	}

	@Override
	public void update(float deltaTime)
	{
		repositionX(deltaTime);
		repositionY(deltaTime);
		handleMovementStop(deltaTime);
	}

	private void handleMovementStop(float deltaTime)
	{
		stopMovingTime += deltaTime;
		if (stopMovingTime > stopMovingTimeBound)
		{
			stopMovingTime = 0.0f;
			currentMoveInfo.setMoveDirection(Directions.NONE);
		}
	}

	private void repositionX(float deltaTime)
	{
		float deltaX = targetX - getX();
		float stepValue = getStepValue(deltaTime, deltaX);
		if (Math.abs(deltaX) > Math.abs(stepValue))
			directPositionChangeX(stepValue);
		else
			directPositionChangeX(deltaX);
	}

	private float getStepValue(float deltaTime, float deltaDistance)
	{
		float stepValue = stepSpeed * deltaTime;
		if (slidingActive)
			stepValue *= (Math.abs(deltaDistance) / 30);
		if (deltaDistance < 0)
			return -stepValue;
		return stepValue;
	}

	private void directPositionChangeX(float moveValue)
	{
		float newPosition = getX() + moveValue;
		int integerMovement = (int) newPosition - (int) getX();
		if (integerMovement != 0)
			if (moveValue < 0)
				linkedCollisionMap.repositionGoingLeft(-integerMovement, this);
			else
				linkedCollisionMap.repositionGoingRight(integerMovement, this);

		super.setX(newPosition);
	}

	private void repositionY(float deltaTime)
	{
		float deltaY = targetY - getY();
		float stepValue = getStepValue(deltaTime, deltaY);
		if (Math.abs(deltaY) > Math.abs(stepValue))
			directPositionChangeY(stepValue);
		else
			directPositionChangeY(deltaY);
	}

	private void directPositionChangeY(float moveValue)
	{
		float newPosition = getY() + moveValue;
		int integerMovement = (int) newPosition - (int) getY();
		if (integerMovement != 0)
			if (moveValue < 0)
				linkedCollisionMap.repositionGoingDown(-integerMovement, this);
			else
				linkedCollisionMap.repositionGoingUp(integerMovement, this);

		super.setY(newPosition);
	}

	@Override
	public void setX(float x)
	{
		if (x > getX())
			currentMoveInfo.setMoveDirection(Directions.RIGHT);
		else if (x < getX())
			currentMoveInfo.setMoveDirection(Directions.LEFT);
		targetX = x;
	}

	@Override
	public void setY(float y)
	{
		if (y > getY())
			currentMoveInfo.setMoveDirection(Directions.UP);
		else if (y < getY())
			currentMoveInfo.setMoveDirection(Directions.DOWN);
		targetY = y;
	}

	@Override
	public void setPosition(float x, float y)
	{
		setX(x);
		setY(y);
	}

	public MoveInfo getMoveInfo()
	{
		if (!slidingActive)
			return currentMoveInfo.withoutSliding();

		float deltaX = targetX - getX();
		float deltaY = targetY - getY();

		if (deltaX == 0 && deltaY == 0)
			return currentMoveInfo.reset();

		if (Math.abs(deltaX) > Math.abs(deltaY))
			updateCurrentMovementWithXAxis(deltaX);
		else
			updateCurrentMovementWithYAxis(deltaY);
		return currentMoveInfo;
	}

	private void updateCurrentMovementWithYAxis(float deltaY)
	{
		currentMoveInfo.setCurrentMovementSpeed(Math.abs(deltaY) / 3.5f);
		int moveDirection = deltaY > 0 ? Directions.UP : Directions.DOWN;
		currentMoveInfo.setMoveDirection(moveDirection);
	}

	private void updateCurrentMovementWithXAxis(float deltaX)
	{
		currentMoveInfo.setCurrentMovementSpeed(Math.abs(deltaX) / 3.5f);
		int moveDirection = deltaX > 0 ? Directions.RIGHT : Directions.LEFT;
		currentMoveInfo.setMoveDirection(moveDirection);
	}

	public void disableSliding()
	{
		slidingActive = false;
	}

	public void setStepSpeed(float stepSpeed)
	{
		this.stepSpeed = stepSpeed;
	}

	private class ObjectRepositionPacketHandler extends PacketHandlerBase<ObjectRepositionPacket>
			implements GameObjectTargetPacketHandler<ObjectRepositionPacket>
	{
		@Override
		protected void doHandle(ObjectRepositionPacket packet)
		{
			setX(packet.x);
			setY(packet.y);
		}

		@Override
		public long getObjectId()
		{
			return getId();
		}
	}
}
