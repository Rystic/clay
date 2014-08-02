package city.ui.menus.areas;

import main.ClayConstants;
import screens.AbstractScreen;
import city.ui.menus.components.TextComponent;

public class HeaderArea extends AbstractArea
{
	public HeaderArea(AbstractScreen homeScreen_, String menuHeader_)
	{
		super(homeScreen_);
		_components.add(new TextComponent(5, 2, menuHeader_,
				ClayConstants.MENU_HEADER_COLOR));
	}

	@Override
	public void update()
	{
	}

}
