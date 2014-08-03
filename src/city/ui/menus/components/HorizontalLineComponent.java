package city.ui.menus.components;

import main.ClayConstants;

import org.newdawn.slick.Color;

public class HorizontalLineComponent extends AbstractComponent
{
	public HorizontalLineComponent(int y_, int thickness_)
	{
		this(y_, thickness_,ClayConstants.M_AREA_HEADER_COLOR);
	}
	
	public HorizontalLineComponent(int y_, int thickness_, Color color_)
	{
		super(0, y_, ClayConstants.DEFAULT_INTERFACE_WIDTH, thickness_);
		setColor(color_);
	}
}
