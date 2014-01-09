package city.ui.components;

import java.awt.Point;
import java.awt.Rectangle;

import org.newdawn.slick.opengl.Texture;

public abstract class AbstractComponent
{
	public AbstractComponent(int x_, int y_, int width_, int height_)
	{
		_bounds = new Rectangle(x_, y_, width_, height_);
	}
	
	public void setTexture(Texture texture_)
	{
		_texture = texture_;
	}
	
	public Texture getTexture()
	{
		return null;
	}

	public void setDrawRatio(float drawRatio_)
	{
		_drawRatio = drawRatio_;
	}
	
	public float getDrawRatio()
	{
		return _drawRatio;
	}
	
	public double getWidth()
	{
		return _bounds.getWidth();
	}
	
	public double getHeight()
	{
		return _bounds.getHeight();
	}
	
	public double getX()
	{
		return _bounds.getX();
	}
	
	public double getY()
	{
		return _bounds.getY();
	}
	
	public boolean isClicked(Point point_)
	{
		return _bounds.contains(point_);
	}
	
	public abstract void clicked();
	
	private Rectangle _bounds;
	
	private Texture _texture;
	private float _drawRatio;
}
