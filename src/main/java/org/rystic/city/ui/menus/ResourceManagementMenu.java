package org.rystic.city.ui.menus;

import org.rystic.city.ui.menus.areas.ResourceFamilyArea;
import org.rystic.screens.AbstractScreen;

public class ResourceManagementMenu extends AbstractMenu
{
	public ResourceManagementMenu(AbstractScreen homeScreen_)
	{
		super(homeScreen_, "Resource Management Menu");
		_areas.add(new ResourceFamilyArea(homeScreen_));
	}

	@Override
	public void handleKeyEvent(Integer key_)
	{
	}

	@Override
	public void handleMouseWheel(boolean upwardScroll_)
	{
	}
}
