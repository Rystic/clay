package city.ui.menus;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import screens.AbstractScreen;
import city.ui.components.AbstractComponent;
import city.ui.events.InterfaceKeyEvent;
import city.ui.events.InterfaceMouseEvent;

public abstract class AbstractMenu
{
	public AbstractMenu(AbstractScreen homeScreen_)
	{
		_homeScreen = homeScreen_;
		_components = new ArrayList<AbstractComponent>();
	}

	public abstract void handleKeyEvent(InterfaceKeyEvent event_);

	public void handleMouseEvent(InterfaceMouseEvent event_)
	{
		Point point = event_.getPoint();
		for (AbstractComponent component : _components)
		{
			if (component.isClicked(point))
				component.clicked();
		}
	}
	
	public List<AbstractComponent> getCopyOfComponents()
	{
		return new ArrayList<AbstractComponent>(_components);
	}

	protected List<AbstractComponent> _components;

	protected AbstractScreen _homeScreen;
}
