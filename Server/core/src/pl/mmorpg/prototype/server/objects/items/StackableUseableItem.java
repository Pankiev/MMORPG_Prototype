package pl.mmorpg.prototype.server.objects.items;


import pl.mmorpg.prototype.server.exceptions.OutOfStockExcpetion;
import pl.mmorpg.prototype.server.objects.monsters.Monster;

public abstract class StackableUseableItem extends StackableItem implements Useable
{
    public StackableUseableItem(long id, int count)
    {
        super(id, count);
    }
    
    public StackableUseableItem(long id)
    {
        super(id);
    }

    @Override
    public void use(Monster target)
    {
        if(count > 0)
        {
            count--;
            useItem(target);
        }
        else
        	throw new OutOfStockExcpetion();
    }
    
    public abstract void useItem(Monster target);

}
