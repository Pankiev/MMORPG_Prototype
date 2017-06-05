package pl.mmorpg.prototype.client.objects.monsters;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import pl.mmorpg.prototype.client.objects.CustomAnimation;
import pl.mmorpg.prototype.client.objects.graphic.HealthBar;
import pl.mmorpg.prototype.clientservercommon.packets.monsters.properties.MonsterProperties;

public abstract class HealthBarMonster extends Monster
{
	private HealthBar healthBar;

	public HealthBarMonster(Texture textureSheet, int sheetStartX, int sheetStartY, long id, MonsterProperties monster)
	{
		super(textureSheet, sheetStartX, sheetStartY, id, monster);
		healthBar = new HealthBar(monster.maxHp, this);
	}

	public HealthBarMonster(long id, CustomAnimation<TextureRegion> moveLeftAnimation,
			CustomAnimation<TextureRegion> moveRightAnimation, CustomAnimation<TextureRegion> moveDownAnimation,
			CustomAnimation<TextureRegion> moveUpAnimation, MonsterProperties monster)
	{
		super(id, moveLeftAnimation, moveRightAnimation, moveDownAnimation, moveUpAnimation, monster);
		healthBar = new HealthBar(monster.maxHp, this);
	
	}
	@Override
	public void update(float deltaTime)
	{
		healthBar.updatePosition();
		healthBar.updateBar(properties.hp);
		super.update(deltaTime);
	}
	
	@Override
	public void render(SpriteBatch batch)
	{
		healthBar.render(batch);
		super.render(batch);
	}
	
	protected void recreateHealthBar()
	{
		healthBar = new HealthBar(getProperties().maxHp, this);
	}

}
