package city.ui.menus;

import org.lwjgl.input.Keyboard;

import screens.AbstractScreen;
import city.ui.menus.areas.BuildingInformationArea;
import city.ui.menus.areas.BuildingPatternArea;
import city.ui.menus.areas.BuildingSliderArea;

public class BuildingMenu extends AbstractMenu
{
	public BuildingMenu(AbstractScreen homeScreen_)
	{
		super(homeScreen_);
		_buildingSlider = new BuildingSliderArea(homeScreen_, 800);
		_buildingPattern = new BuildingPatternArea(homeScreen_, 650);
		_buildingInformation = new BuildingInformationArea(homeScreen_, 450);

		_areas.add(_buildingSlider);
		_areas.add(_buildingPattern);
		_areas.add(_buildingInformation);
		
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
	public void handleMouseWheel(boolean upwardScroll_)
	{
		if (upwardScroll_)
			_buildingSlider.moveRight();
		else
			_buildingSlider.moveLeft();
	}

	private BuildingSliderArea _buildingSlider;
	private BuildingPatternArea _buildingPattern;
	private BuildingInformationArea _buildingInformation;
}
