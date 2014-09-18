package org.rystic.city.ui.menus;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import org.rystic.city.ui.events.InterfaceMouseEvent;
import org.rystic.city.ui.menus.areas.AbstractArea;
import org.rystic.city.ui.menus.areas.HeaderArea;
import org.rystic.city.ui.menus.components.AbstractButton;
import org.rystic.city.ui.menus.components.AbstractComponent;
import org.rystic.city.ui.menus.components.InterfaceBorderArea;
import org.rystic.screens.AbstractScreen;

public abstract class AbstractMenu
{
	public AbstractMenu(AbstractScreen homeScreen_, String menuTitle_)
	{
		_homeScreen = homeScreen_;
		_components = new ArrayList<AbstractComponent>();
		_hotKeys = new ArrayList<Integer>();
		_areas = new ArrayList<AbstractArea>();
		_areas.add(new HeaderArea(_homeScreen, menuTitle_));
		_areas.add(new InterfaceBorderArea(_homeScreen));
	}

	public void update()
	{
		_components.clear();
		for (AbstractArea area : _areas)
		{
			area.update();
			_components.addAll(area.getComponents());
		}
	}
	
	public abstract void handleKeyEvent(Integer key_);

	public abstract void handleMouseWheel(boolean upwardScroll_);
	
	public final void handleMouseEvent(InterfaceMouseEvent event_)
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
	
	protected List<AbstractArea> _areas;

	protected AbstractScreen _homeScreen;
}
