package pl.mmorpg.prototype.client.resources;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScalingViewport;

public class CustomStage extends Stage
{
	private boolean isUsed = true;

	public CustomStage(ScalingViewport scalingViewport, SpriteBatch batch)
	{
		super(scalingViewport, batch);
	}

	public boolean isUsed()
	{
		return isUsed;
	}

	public void setUsed()
	{
		isUsed = true;
	}

	@Override
	public void dispose()
	{
		clear();
		isUsed = false;
	}
}