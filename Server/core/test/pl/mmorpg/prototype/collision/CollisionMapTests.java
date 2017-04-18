package pl.mmorpg.prototype.collision;

import static org.junit.Assert.*;

import org.junit.Test;

import pl.mmorpg.prototype.server.collision.CollisionMap;

public class CollisionMapTests
{

    @Test
    public void borderColiision()
    {
        CollisionMap<CollisionMapTestObject> collisionMap = new CollisionMap<>(4, 4);
        CollisionMapTestObject object = new CollisionMapTestObject();
        collisionMap.placeObjectOnBorder(object);
        assertTrue(collisionMap.get(0, 0) != null);
        assertTrue(collisionMap.get(0, 1) != null);
        assertTrue(collisionMap.get(0, 2) != null);
        assertTrue(collisionMap.get(0, 3) != null);
        assertTrue(collisionMap.get(3, 0) != null);
        assertTrue(collisionMap.get(3, 1) != null);
        assertTrue(collisionMap.get(3, 2) != null);
        assertTrue(collisionMap.get(3, 3) != null);
        assertTrue(collisionMap.get(1, 0) != null);
        assertTrue(collisionMap.get(2, 0) != null);
        assertTrue(collisionMap.get(1, 3) != null);
        assertTrue(collisionMap.get(2, 3) != null);
        assertEquals(collisionMap.get(1, 1), null);
        assertEquals(collisionMap.get(1, 2), null);
        assertEquals(collisionMap.get(2, 1), null);
        assertEquals(collisionMap.get(2, 2), null);
    }

    @Test
    public void outOfBoundCollision()
    {
        CollisionMap<CollisionMapTestObject> collisionMap = new CollisionMap<>(1000, 1000);
        assertEquals(collisionMap.get(-1, 23), null);
        assertEquals(collisionMap.get(234, -2), null);
        assertEquals(collisionMap.get(-123, -1), null);
        assertEquals(collisionMap.get(1000, 0), null);
        assertEquals(collisionMap.get(0, 1000), null);
        assertEquals(collisionMap.get(1234, 1234), null);
    }

    @Test
    public void successfulInstertionTest()
    {
        CollisionMap<CollisionMapTestObject> collisionMap = new CollisionMap<>(1000, 1000);
        int objectWidth = 20;
        int objectHeight = 20;
        CollisionMapTestObject object = new CollisionMapTestObject(25, 20, objectWidth, objectHeight);
        collisionMap.insert(object);
        assertThereIsPlacedObject(collisionMap, object);     
        assertNullAroundObject(collisionMap, object);
    }

    private void assertThereIsPlacedObject(CollisionMap<CollisionMapTestObject> collisionMap,
            CollisionMapTestObject object)
    {
        for (int i = (int) object.getX(); i <= object.getWidth() + object.getX(); i++)
            for (int j = (int) object.getY(); j <= object.getY() + object.getHeight(); j++)
                assertEquals(collisionMap.get(i, j), object);
    }

    private void assertNullAroundObject(CollisionMap<CollisionMapTestObject> collisionMap,
            CollisionMapTestObject object)
    {
        for (int i = (int) object.getX() - 1; i <= object.getX() + object.getWidth() + 1; i++)
        {
            assertEquals(collisionMap.get(i, (int)object.getY() - 1), null);
            assertEquals(collisionMap.get(i, (int)(object.getY() + object.getHeight() + 1)), null);
        }
        for (int i = (int) object.getY(); i <= object.getY() + object.getHeight(); i++)
        {
            assertEquals(collisionMap.get((int) object.getX() - 1, i), null);
            assertEquals(collisionMap.get((int)(object.getX() + object.getWidth() + 1), i), null);
        }
    }
    
    @Test
    public void successfullMovement()
    {
        CollisionMap<CollisionMapTestObject> collisionMap = new CollisionMap<>(100, 100);
        int objectWidth = 20;
        int objectHeight = 20;
        CollisionMapTestObject object = new CollisionMapTestObject(25, 20, objectWidth, objectHeight);
        collisionMap.insert(object);
        int moveValue = 2;
        collisionMap.tryToRepositionCollisionGoingRight(moveValue, object);
        object.x += 2;
        assertThereIsPlacedObject(collisionMap, object);
        assertNullAroundObject(collisionMap, object);
    }
    
}