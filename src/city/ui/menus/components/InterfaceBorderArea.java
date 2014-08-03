package city.ui.menus.components;

import main.ClayConstants;
import screens.AbstractScreen;
import city.ui.menus.areas.AbstractArea;

public class InterfaceBorderArea extends AbstractArea
{
	public InterfaceBorderArea(AbstractScreen homeScreen_)
	{
		super(homeScreen_);
		_components.add(new HorizontalLineComponent(0, 2,
				ClayConstants.M_INTERFACE_BORDER_COLOR));
		_components.add(new HorizontalLineComponent(100, -2,
				ClayConstants.M_INTERFACE_BORDER_COLOR));
		_components.add(new VerticalLineComponent(0, 2,
				ClayConstants.M_INTERFACE_BORDER_COLOR));
		_components.add(new VerticalLineComponent(100, -2,
				ClayConstants.M_INTERFACE_BORDER_COLOR));
	}

	@Override
	public void update()
	{

	}
}
