package pl.mmorpg.prototype.client.objects.icons.items;

import com.badlogic.gdx.graphics.Texture;

import pl.mmorpg.prototype.client.items.ItemIdentifier;
import pl.mmorpg.prototype.client.objects.icons.DraggableIcon;

public abstract class Item extends DraggableIcon
{
    private long id;

    public Item(Texture texture, long id)
    {
    	super(texture);
        this.id = id;
    }
    
    public long getId()
    {
        return id;
    }

    public String getIdentifier()
    {
        return ItemIdentifier.getIdentifier(getClass());
    }
}
