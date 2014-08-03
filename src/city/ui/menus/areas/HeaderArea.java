package city.ui.menus.areas;

import main.ClayConstants;
import screens.AbstractScreen;
import city.ui.menus.components.HorizontalLineComponent;
import city.ui.menus.components.TextComponent;

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
