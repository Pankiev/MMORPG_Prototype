package pl.mmorpg.prototype.server.collision.pixelmap;

import com.badlogic.gdx.math.Rectangle;

public class IntegerRectangle
{
    public int x;
    public int y;
    public int width;
    public int height;

    public IntegerRectangle(Rectangle rectangle)
    {
        this(rectangle, 1);
    }

    public IntegerRectangle(Rectangle rectangle, int scale)
    {
        this.x = (int) rectangle.x;
        this.y = (int) rectangle.y;
        this.width = (int) rectangle.width;
        this.height = (int) rectangle.height;
        scale(scale);
    }

    public IntegerRectangle(int x, int y, int width, int height)
    {
        this(x, y, width, height, 1);
    }

    public IntegerRectangle(int x, int y, int width, int height, int scale)
    {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        scale(scale);
    }

    public int getRightBound()
    {
        return x + width;
    }

    public int getUpperBound()
    {
        return y + height;
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

    public void setX(int x)
    {
        this.x = x;
    }

    public void setY(int y)
    {
        this.y = y;
    }

    public void setWidth(int width)
    {
        this.width = width;
    }

    public void setHeight(int height)
    {
        this.height = height;
    }
    
    public void scale(int scale)
    {
    	this.x /= scale;
        this.y /= scale;
        this.width /= scale;
        this.height /= scale;
    }

}
