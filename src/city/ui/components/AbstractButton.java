package city.ui.components;

import java.awt.Rectangle;

import org.lwjgl.input.Mouse;
import org.newdawn.slick.opengl.Texture;

@SuppressWarnings("serial")
public abstract class AbstractButton extends Rectangle
{
	public AbstractButton(int x_, int y_, int width_, int height_)
	{
		super(x_, y_, width_, height_);
	}
	
	public boolean isPressed()
	{
		return getBounds().contains(Mouse.getX(), Mouse.getY());
	}
	
	public void execute()
	{
		
	}
	
	public Texture getTexture()
	{
		return null;
	}
	
}
