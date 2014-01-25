package city.ui.menus;

import org.lwjgl.input.Keyboard;

import screens.AbstractScreen;
import city.ui.menus.areas.BuildingDescriptionArea;
import city.ui.menus.areas.BuildingInformationArea;
import city.ui.menus.areas.BuildingPatternArea;
import city.ui.menus.areas.BuildingSliderArea;

public class BuildingMenu extends AbstractMenu
{
	public BuildingMenu(AbstractScreen homeScreen_)
	{
		super(homeScreen_);
		_buildingSlider = new BuildingSliderArea(homeScreen_, 825);
		_buildingPattern = new BuildingPatternArea(homeScreen_, 625);
		_buildingInformation = new BuildingInformationArea(homeScreen_, 425);
		_buildingDescription = new BuildingDescriptionArea(homeScreen_, 200);

		_areas.add(_buildingSlider);
		_areas.add(_buildingPattern);
		_areas.add(_buildingInformation);
		_areas.add(_buildingDescription);
		
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
			_buildingSlider.moveLeft();
		else
			_buildingSlider.moveRight();
	}

	private BuildingSliderArea _buildingSlider;
	private BuildingPatternArea _buildingPattern;
	private BuildingInformationArea _buildingInformation;
	private BuildingDescriptionArea _buildingDescription;
}
