package city.ui.menus.components;

import main.ClayConstants;

import org.newdawn.slick.Color;

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
