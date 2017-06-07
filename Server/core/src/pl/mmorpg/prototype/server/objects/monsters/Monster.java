package pl.mmorpg.prototype.server.objects.monsters;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import pl.mmorpg.prototype.clientservercommon.packets.monsters.properties.MonsterProperties;
import pl.mmorpg.prototype.server.communication.PacketsMaker;
import pl.mmorpg.prototype.server.communication.PacketsSender;
import pl.mmorpg.prototype.server.exceptions.CannotUseThisItemException;
import pl.mmorpg.prototype.server.exceptions.CharacterDoesntHaveItemException;
import pl.mmorpg.prototype.server.exceptions.NoSuchItemToRemoveException;
import pl.mmorpg.prototype.server.objects.MovableGameObject;
import pl.mmorpg.prototype.server.objects.effects.Effect;
import pl.mmorpg.prototype.server.objects.items.Item;
import pl.mmorpg.prototype.server.objects.items.StackableItem;
import pl.mmorpg.prototype.server.objects.items.Useable;
import pl.mmorpg.prototype.server.objects.monsters.abilities.Ability;
import pl.mmorpg.prototype.server.resources.Assets;
import pl.mmorpg.prototype.server.states.PlayState;

public abstract class Monster extends MovableGameObject implements ItemUser
{
	private static final BitmapFont font = Assets.getFont();
	private Map<Long, Item> items = new ConcurrentHashMap<>();
    private List<Ability> abilities = new LinkedList<>();
    private Map<Class<? extends Effect>, Effect> ongoingEffects = new ConcurrentHashMap<>();

    protected final MonsterProperties properties;
    private Monster targetedMonster = null;
    private float hitTime = 1000.0f;
    protected PlayState linkedState;

    private List<Monster> targetedBy = new LinkedList<>();

    public Monster(Texture lookout, long id, PlayState playState, MonsterProperties properties)
    {
        super(lookout, id, playState);
        linkedState = playState;
        font.setColor(new Color(1, 0, 0, 1));
        this.properties = properties;
    }

    @Override
    public void update(float deltaTime)
    {
        super.update(deltaTime);
        if (isTargetingAnotherMonster())
            attackHandle(deltaTime);
        abilitiesUsageHandle();
        ongoingEffectsHandle(deltaTime);
    }
  

	public boolean isTargetingAnotherMonster()
    {
        return targetedMonster != null;
    }

    private void attackHandle(float deltaTime)
    {
        hitTime += deltaTime;
        if (canAttackTarget())
        {
            hitTime = 0.0f;
            normalAttack(targetedMonster);
        }
    }

    private boolean canAttackTarget()
    {
        return hitTime >= properties.attackSpeed && distance(targetedMonster) <= properties.attackRange;
    }

    private double distance(Monster targetedMonster)
    {
        float selfCenterX = getX() + getWidth() / 2;
        float targetCenterX = targetedMonster.getX() + targetedMonster.getWidth() / 2;
        float deltaX = targetCenterX - selfCenterX;
        float selfCenterY = getY() + getHeight() / 2;
        float targetCenterY = targetedMonster.getY() + targetedMonster.getHeight() / 2;
        float deltaY = targetCenterY - selfCenterY;
        double distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY);
        return distance;
    }

    private void normalAttack(Monster target)
    {
        int damage = DamageCalculator.getDamage(this, target);
        target.properties.hp -= damage;
        linkedState.sendToAll(PacketsMaker.makeNormalDamagePacket(target.getId(), damage));
        if (target.properties.hp <= 0)
        {
            this.killed(target);
            target.die();
        }
    }
    

    private void abilitiesUsageHandle()
	{
		for(Ability ability : abilities)
			if(ability.shouldUse())
			{
				if(ability.shouldBeUsedOnItself())
					ability.use(this, (PacketsSender)linkedState);
				else if(ability.shouldBeUsedOnTargetedMonster() && isTargetingAnotherMonster())
					ability.use(targetedMonster, (PacketsSender)linkedState);
			}
	}
    

	private void ongoingEffectsHandle(float deltaTime)
	{
		for(Entry<Class<? extends Effect>, Effect> effectsElement : ongoingEffects.entrySet())
		{
			Effect effect = effectsElement.getValue();
			effect.update(deltaTime);
			if(effect.shouldDeactivate())
			{
				effect.deactivate();
				ongoingEffects.remove(effectsElement.getKey());
			}
		}
		
	}

    @Override
    public void render(SpriteBatch batch)
    {
        super.render(batch);
        font.draw(batch, String.valueOf(properties.hp), getX() + 3, getY() + 40);
    }

    public void targetMonster(Monster target)
    {
        targetedMonster = target;
        target.isTargetedBy(this);
    }

    public Monster getTargetedMonster()
    {
        return targetedMonster;
    }

    private void isTargetedBy(Monster source)
    {
        targetedBy.add(source);
    }

    public void die()
    {
        linkedState.remove(getId());
        linkedState.sendToAll(PacketsMaker.makeRemovalPacket(getId()));
        for (Monster targetedBY : targetedBy)
            targetedBY.targetedMonster = null;
    }

    public void killed(Monster target)
    {
        targetedMonster = null;
    }

    public MonsterProperties getProperties()
    {
        return properties;
    }
    
    public boolean isInGame()
    {
    	return linkedState.has(getId());
    }
    
    protected void stopTargetingMonster()
    {
    	targetedMonster = null;
    }
    
    @Override
    public void onRemoval()
    {
    	for (Monster targetedBY : targetedBy)
            targetedBY.targetedMonster = null;
    	super.onRemoval();
    }
    
    @Override
	public void addItem(Item newItem)
    {
    	if(newItem instanceof StackableItem)
    	{
    		for(Item item : items.values())
    			if(item.getIdentifier().equals(newItem.getIdentifier()))
    			{
    				((StackableItem)item).stackWith((StackableItem)newItem);
    				return;
    			}
    		
    	}
    	items.put(newItem.getId(), newItem);
    }

    @Override
	public void useItem(long id, PacketsSender packetSender)
    {
        useItem(id, this, packetSender);
    }

    @Override
	public void useItem(long id, Monster target, PacketsSender packetSender)
    {
        Item characterItem = items.get(id);
        if (characterItem == null)
            throw new CharacterDoesntHaveItemException(id);
        if (!(characterItem instanceof Useable))
            throw new CannotUseThisItemException(characterItem);
        ((Useable) characterItem).use(target, packetSender);
    }

    @Override
	public Collection<Item> getItems()
    {
        return items.values();
    }
    
    @Override
    public void removeItem(long id)
    {
    	if(items.remove(id) == null)
    		throw new NoSuchItemToRemoveException(id);
    }
    
    @Override
    public void addItems(Collection<Item> items)
    {
    	items.forEach( item -> this.items.put(item.getId(), item));
    }
    
    protected void addAbility(Ability ability)
    {
    	abilities.add(ability);
    }
    
    public void addEffect(Effect effect)
    {
    	Effect sameTypeEffect = ongoingEffects.get(effect.getClass());
    	if(sameTypeEffect != null)
    		sameTypeEffect.stackWithSameTypeEffect(effect);
    	else
    	{
    		effect.activate();
    		ongoingEffects.put(effect.getClass(), effect);
    	}
    }

}