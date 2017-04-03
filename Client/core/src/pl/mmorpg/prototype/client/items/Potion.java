package pl.mmorpg.prototype.client.items;

import com.badlogic.gdx.graphics.Texture;

public abstract class Potion extends StackableItem implements ItemUseable
{
	public Potion(Texture texture)
	{
		super(texture);
	}
}