package org.rystic.city.ui.menus.areas;

import java.util.ArrayList;
import java.util.List;

import org.rystic.city.ui.menus.components.AbstractComponent;
import org.rystic.screens.AbstractScreen;

public abstract class AbstractArea
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
	
	public abstract void update();
	
	protected List<AbstractComponent> _components;
	
	protected AbstractScreen _homeScreen;
}
