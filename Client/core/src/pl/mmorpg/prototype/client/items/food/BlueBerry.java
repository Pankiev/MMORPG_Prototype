package pl.mmorpg.prototype.client.items.food;

import pl.mmorpg.prototype.client.resources.Assets;

public class BlueBerry extends FoodItem
{
	public BlueBerry(long id)
	{
        this(id, 1);
    }
    
    public BlueBerry(long id, int itemCount)
	{
    	super(Assets.get("Items/BlueBerry.png"), id, itemCount);
	}
}
