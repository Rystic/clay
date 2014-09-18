package org.rystic.city.ui.menus.components;

import org.newdawn.slick.Color;
import org.rystic.main.ClayConstants;

public class VerticalLineComponent extends AbstractComponent
{
	public VerticalLineComponent(int x_, int thickness_)
	{
		this(x_, thickness_, ClayConstants.M_AREA_HEADER_COLOR);
	}

	public VerticalLineComponent(int x_, int thickness_, Color color_)
	{
		super(x_, 0, thickness_, ClayConstants.DEFAULT_MAP_HEIGHT);
		setColor(color_);
	}
}
