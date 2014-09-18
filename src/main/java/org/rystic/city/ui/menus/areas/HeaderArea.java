package org.rystic.city.ui.menus.areas;

import org.rystic.city.ui.menus.components.HorizontalLineComponent;
import org.rystic.city.ui.menus.components.TextComponent;
import org.rystic.main.ClayConstants;
import org.rystic.screens.AbstractScreen;

public class HeaderArea extends AbstractArea
{
	public HeaderArea(AbstractScreen homeScreen_, String menuHeader_)
	{
		super(homeScreen_);
		_components.add(new TextComponent(5, 1, menuHeader_,
				ClayConstants.M_MENU_HEADER_COLOR));
		_components.add(new HorizontalLineComponent(4, 2));
	}

	@Override
	public void update()
	{
	}

}
