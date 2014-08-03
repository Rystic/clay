package city.ui.menus.components;

import java.awt.Rectangle;

import main.ClayConstants;

import org.newdawn.slick.Color;
import org.newdawn.slick.opengl.Texture;

public abstract class AbstractComponent
{
	public AbstractComponent(int x_, int y_, int width_, int height_)
	{
		_bounds = new Rectangle(getConvertedX(x_), getConvertedY(y_), width_, height_);
	}
	
	public void setTexture(Texture texture_)
	{
		_texture = texture_;
	}

	public Texture getTexture()
	{
		return _texture;
	}
	
	public void setColor(Color color_)
	{
		_color = color_;
	}
	
	public Color getColor()
	{
		return _color;
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
		_bounds.x = getConvertedY(x_);
	}

	public double getY()
	{
		return _bounds.getY();
	}
	
	public void setY(int y_)
	{
		_bounds.y = getConvertedY(y_);
	}
	
	private int getConvertedX(double x_)
	{
		return (int) ((x_ / 100) * ClayConstants.DEFAULT_INTERFACE_WIDTH);
	}
	
	private int getConvertedY(double y_)
	{
		return (int) (((100 - y_) / 100) * ClayConstants.DEFAULT_MAP_HEIGHT);
	}
	
	protected Rectangle _bounds;

	protected Texture _texture;
	protected Color _color;
	protected float _drawRatio;
}
