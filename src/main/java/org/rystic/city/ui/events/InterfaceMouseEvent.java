package org.rystic.city.ui.events;

import java.awt.Point;

public class InterfaceMouseEvent
{
	public InterfaceMouseEvent(int x_, int y_)
	{
		_point = new Point(x_, y_);
	}
	
	public Point getPoint()
	{
		return _point;
	}

	private Point _point;
}
