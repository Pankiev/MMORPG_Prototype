package pl.mmorpg.prototype.client.collision.pixelmap;

import com.badlogic.gdx.math.Rectangle;

public class IntegerRectangle
{
    public int x;
    public int y;
    public final int width;
    public final int height;

    public IntegerRectangle(Rectangle rectangle)
    {
        this.x = (int) rectangle.x;
        this.y = (int) rectangle.y;
        this.width = Math.round(rectangle.width);
        this.height = Math.round(rectangle.height);
    }

    public IntegerRectangle(IntegerRectangle rectangle)
	{
		this.x = rectangle.x;
		this.y = rectangle.y;
		this.width = rectangle.width;
		this.height = rectangle.height;
	}

	public int getRightBound()
    {
        return x + width - 1;
    }

    public int getUpperBound()
    {
        return y + height - 1;
    }

    public int getX()
    {
        return x;
    }

    public int getY()
    {
        return y;
    }

    public int getWidth()
    {
        return width;
    }

    public int getHeight()
    {
        return height;
    }

}
