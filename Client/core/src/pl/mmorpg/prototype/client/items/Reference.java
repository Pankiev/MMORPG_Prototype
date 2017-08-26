package pl.mmorpg.prototype.client.items;

import com.badlogic.gdx.graphics.g2d.Batch;

public class Reference<T extends InventoryIcon> extends InventoryIcon
{
	private T referencedObject;

	public Reference(T referencedObject)
	{
		super(referencedObject.getTexture());
        putObject(referencedObject);
	}

	public void removeObject()
	{
	    referencedObject = null;
	}

	public void putObject(T object)
	{
	    setTexture(object.getTexture());
	    this.referencedObject = object;
	    setWidth(object.getWidth());
	    setHeight(object.getHeight());
	}

	public T getObject()
	{
	    return referencedObject;
	}

	@Override
	public void draw(Batch batch, float parentAlpha)
	{
	    referencedObject.draw(batch, getX(), getY());
	}

}