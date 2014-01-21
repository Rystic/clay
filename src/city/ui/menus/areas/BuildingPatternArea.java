package city.ui.menus.areas;

import java.util.Map;

import main.ClayConstants;
import screens.AbstractScreen;
import screens.CityScreen;
import city.generics.GenericBuilding;
import city.generics.data.BuildingData;
import city.ui.menus.components.ImageComponent;
import city.ui.menus.components.TextComponent;

public class BuildingPatternArea extends AbstractArea
{

	public BuildingPatternArea(AbstractScreen homeScreen_, int yPos_)
	{
		super(homeScreen_);
		_yPos = yPos_;
		_selectedBuilding = -1;
		_buildingPatternLabel = new TextComponent(95, _yPos, "Building Pattern");
		_components.add(_buildingPatternLabel);
	}

	@Override
	public void update()
	{
		int selectedBuilding = ((CityScreen) _homeScreen).getModel()
				.getSelectedBuilding();
		if (selectedBuilding != _selectedBuilding)
		{
			_components.clear();
			_components.add(_buildingPatternLabel);
			_selectedBuilding = selectedBuilding;
			Map<String, String> placementMap = BuildingData.getBuildingById(
					_selectedBuilding).getValidPlacementMap();
			for (String key : placementMap.keySet())
			{
				int xDiff = 0;
				int yDiff = 0;
				if (!key.equals(ClayConstants.DEFAULT_BUILDING_POSITION))
				{
					for (int i = 0; i < key.length(); i++)
					{
						if (key.charAt(i) == 'n')
							yDiff++;
						else if (key.charAt(i) == 's')
							yDiff--;
						else if (key.charAt(i) == 'e')
							xDiff++;
						else if (key.charAt(i) == 'w')
							xDiff--;
					}
				}
				GenericBuilding building = BuildingData
						.getBuildingByTag(placementMap.get(key));
				if (building != null)
				{
					ImageComponent tc = new ImageComponent(
							125 + (ClayConstants.TILE_X * xDiff), _yPos - 80
									+ (ClayConstants.TILE_Y * yDiff),
							ClayConstants.TILE_X, ClayConstants.TILE_Y,
							building.getTexture(
									ClayConstants.T_STATE_DEFAULT,
									ClayConstants.DEFAULT_BUILDING_POSITION));
					_components.add(tc);
				}
			}
		}

	}

	private TextComponent _buildingPatternLabel;

	private int _selectedBuilding;

	private int _yPos;
}
