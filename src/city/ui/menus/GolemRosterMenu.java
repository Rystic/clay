package city.ui.menus;

import screens.AbstractScreen;
import city.ui.menus.areas.GolemRosterArea;

public class GolemRosterMenu extends AbstractMenu
{
	public GolemRosterMenu(AbstractScreen homeScreen_)
	{
		super(homeScreen_,  "Golem Roster Menu");
		_areas.add(new GolemRosterArea(homeScreen_));
	}

	@Override
	public void handleKeyEvent(Integer key_)
	{

	}

	@Override
	public void handleMouseWheel(boolean upwardScroll_)
	{
		// TODO Auto-generated method stub
		
	}
}
