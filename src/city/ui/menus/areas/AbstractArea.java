package city.ui.menus.areas;

import java.util.ArrayList;
import java.util.List;

import screens.AbstractScreen;
import city.ui.components.AbstractComponent;

public class AbstractArea
{
	public AbstractArea(AbstractScreen homeScreen_)
	{
		_components = new ArrayList<AbstractComponent>();
		_homeScreen = homeScreen_;
	}

	public List<AbstractComponent> getComponents()
	{
		return _components;
	}
		
	protected List<AbstractComponent> _components;
	
	protected AbstractScreen _homeScreen;
}
