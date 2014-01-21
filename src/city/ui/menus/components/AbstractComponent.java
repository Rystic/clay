package city.ui.menus.components;

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
		return _texture;
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

	public void setWidth(int width_)
	{
		_bounds.width = width_;
	}

	public double getHeight()
	{
		return _bounds.getHeight();
	}
	
	public void setHeight(int height_)
	{
		_bounds.height = height_;
	}

	public double getX()
	{
		return _bounds.getX();
	}
	
	public void setX(int x_)
	{
		_bounds.x = x_;
	}

	public double getY()
	{
		return _bounds.getY();
	}
	
	public void setY(int y_)
	{
		_bounds.y = y_;
	}
	
	protected Rectangle _bounds;

	protected Texture _texture;
	protected float _drawRatio;
}
