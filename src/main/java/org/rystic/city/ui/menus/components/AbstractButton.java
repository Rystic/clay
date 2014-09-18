package org.rystic.city.ui.menus.components;

import java.awt.Point;

public abstract class AbstractButton extends AbstractComponent
{
	public AbstractButton(int x_, int y_, int width_, int height_)
	{
		super(x_, y_, width_, height_);
	}

	public boolean isClicked(Point point_)
	{
		return _bounds.contains(point_);
	}

	public abstract void clicked();

}