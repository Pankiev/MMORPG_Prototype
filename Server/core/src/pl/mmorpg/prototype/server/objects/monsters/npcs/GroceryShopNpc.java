package pl.mmorpg.prototype.server.objects.monsters.npcs;

import java.util.Collection;
import java.util.LinkedList;

import com.badlogic.gdx.graphics.Texture;

import pl.mmorpg.prototype.clientservercommon.packets.monsters.properties.MonsterProperties;
import pl.mmorpg.prototype.server.collision.pixelmap.PixelCollisionMap;
import pl.mmorpg.prototype.server.communication.IdSupplier;
import pl.mmorpg.prototype.server.objects.GameObject;
import pl.mmorpg.prototype.server.objects.items.potions.SmallHpPotion;
import pl.mmorpg.prototype.server.objects.items.potions.SmallMpPotion;
import pl.mmorpg.prototype.server.states.PlayState;

public class GroceryShopNpc extends ShopNpc
{
	public GroceryShopNpc(Texture lookout, long id, MonsterProperties properties,
			PixelCollisionMap<GameObject> collisionMap, PlayState playState)
	{
		super(lookout, id, properties, collisionMap, playState, getShopItems());
	}
	
	private static Collection<ShopItemWrapper> getShopItems()
	{
		Collection<ShopItemWrapper> availableItems = new LinkedList<>();
		availableItems.add(new ShopItemWrapper(new SmallHpPotion(IdSupplier.getId()), 100));
		availableItems.add(new ShopItemWrapper(new SmallMpPotion(IdSupplier.getId()), 100));
		return availableItems; 
	}
	
}
