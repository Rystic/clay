package org.rystic.city.ui.menus.areas;

import org.rystic.city.generics.util.FieldParser;
import org.rystic.city.ui.menus.components.AbstractButton;
import org.rystic.city.ui.menus.components.HorizontalLineComponent;
import org.rystic.city.ui.menus.components.SelectMenuButton;
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
		_leftArrow = new SelectMenuButton(homeScreen_, 80, 4, 35, 35, FieldParser.parseTexture("arrowLeft.png"), true);
		_rightArrow = new SelectMenuButton(homeScreen_, 90, 4, 35, 35, FieldParser.parseTexture("arrowRight.png"), false);
		_components.add(_leftArrow);
		_components.add(_rightArrow);
	}

	@Override
	public void update()
	{
	}
	
	private AbstractButton _leftArrow;
	private AbstractButton _rightArrow; 
}
