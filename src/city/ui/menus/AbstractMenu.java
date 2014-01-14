package city.ui.menus;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import screens.AbstractScreen;
import city.ui.components.AbstractButton;
import city.ui.components.AbstractComponent;
import city.ui.events.InterfaceMouseEvent;

public abstract class AbstractMenu
{
	public AbstractMenu(AbstractScreen homeScreen_)
	{
		_homeScreen = homeScreen_;
		_components = new ArrayList<AbstractComponent>();
		_hotKeys = new ArrayList<Integer>();
	}

	public abstract void handleKeyEvent(Integer key_);

	public void handleMouseEvent(InterfaceMouseEvent event_)
	{
		Point point = event_.getPoint();
		for (AbstractComponent component : _components)
		{
			if (component instanceof AbstractButton)
			if (((AbstractButton)component).isClicked(point))
				((AbstractButton)component).clicked();
		}
	}
	
	public List<AbstractComponent> getCopyOfComponents()
	{
		return new ArrayList<AbstractComponent>(_components);
	}
	
	public List<Integer> getHotKeys()
	{
		return _hotKeys;
	}
	
	protected List<Integer> _hotKeys;

	protected List<AbstractComponent> _components;

	protected AbstractScreen _homeScreen;
}
