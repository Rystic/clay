package city.ui.menus;

import org.lwjgl.input.Keyboard;

import screens.AbstractScreen;
import city.ui.events.InterfaceMouseEvent;
import city.ui.menus.areas.BuildingSliderArea;

public class BuildingMenu extends AbstractMenu
{
	public BuildingMenu(AbstractScreen homeScreen_)
	{
		super(homeScreen_);
		_buildingSlider = new BuildingSliderArea(homeScreen_, 800);
		_components.addAll(_buildingSlider.getComponents());
		_hotKeys.add(Keyboard.KEY_Q);
		_hotKeys.add(Keyboard.KEY_E);
	}

	@Override
	public void handleKeyEvent(Integer key_)
	{
		if (key_ == Keyboard.KEY_Q)
			_buildingSlider.moveLeft();
		else if (key_ == Keyboard.KEY_E)
			_buildingSlider.moveRight();
	}

	@Override
	public void handleMouseEvent(InterfaceMouseEvent event_)
	{
		// TODO Auto-generated method stub
	}

	private BuildingSliderArea _buildingSlider;

}
