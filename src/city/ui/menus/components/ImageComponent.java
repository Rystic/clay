package city.ui.menus.components;

import org.newdawn.slick.opengl.Texture;

public class ImageComponent extends AbstractComponent
{
	public ImageComponent(int x_, int y_, int width_, int height_)
	{
		super(x_, y_, width_, height_);
	}

	public ImageComponent(int x_, int y_, int width_, int height_, Texture texture_)
	{
		super(x_, y_, width_, height_);
		setTexture(texture_);
		setDrawRatio(.77f);
	}
}
