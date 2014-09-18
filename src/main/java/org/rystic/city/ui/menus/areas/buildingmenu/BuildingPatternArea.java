package org.rystic.city.ui.menus.areas.buildingmenu;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.newdawn.slick.Color;
import org.rystic.city.generics.GenericBuilding;
import org.rystic.city.generics.data.BuildingData;
import org.rystic.city.ui.menus.areas.AbstractArea;
import org.rystic.city.ui.menus.components.AbstractComponent;
import org.rystic.city.ui.menus.components.SelectBuildingButton;
import org.rystic.city.ui.menus.components.TextComponent;
import org.rystic.main.ClayConstants;
import org.rystic.models.CityModel;
import org.rystic.screens.AbstractScreen;
import org.rystic.screens.CityScreen;

public class BuildingPatternArea extends AbstractArea
{
	public BuildingPatternArea(AbstractScreen homeScreen_)
	{
		super(homeScreen_);
		_selectedBuilding = -1;
		_buildingPatternLabel = new TextComponent(5, 5, "Building Pattern");
		_buildingPatternLabel.setColor(ClayConstants.M_AREA_HEADER_COLOR);
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
			List<AbstractComponent> tempComponents = new ArrayList<AbstractComponent>();
			for (String key : placementMap.keySet())
			{
				int xDiff = 0;
				int yDiff = 0;
				boolean base = key
						.equals(ClayConstants.DEFAULT_BUILDING_POSITION);
				if (!base)
				{
					for (int i = 0; i < key.length(); i++)
					{
						if (key.charAt(i) == 'n')
							yDiff -= 4;
						else if (key.charAt(i) == 's')
							yDiff += 4;
						else if (key.charAt(i) == 'e')
							xDiff += 11;
						else if (key.charAt(i) == 'w')
							xDiff -= 11;
					}
				}
				final GenericBuilding building = BuildingData
						.getBuildingByTag(placementMap.get(key));
				if (building != null)
				{
					SelectBuildingButton tc = new SelectBuildingButton(
							15 + xDiff, 25 + yDiff, ClayConstants.TILE_X,
							ClayConstants.TILE_Y, building)
					{
						@Override
						public void clicked()
						{
							CityModel model = ((CityModel)_homeScreen.getModel());
							model.setSelectedBuilding(building.getBuildingIdentifier());
						}
					};
					tc.setDrawRatio(.77f);
					if (base)
						tc.setColor(_unhighlightedColor);
					tempComponents.add(tc);
				}
			}
			if (tempComponents.isEmpty())
				return; // for clay blocks
			AbstractComponent edgeComponent = null;
			AbstractComponent highComponent = null;
			for (AbstractComponent component : tempComponents)
			{
				if (edgeComponent == null
						|| edgeComponent.getUnconvertedX() > component
								.getUnconvertedX())
				{
					edgeComponent = component;
				}
				if (highComponent == null
						|| highComponent.getUnconvertedY() < component
								.getUnconvertedY())
				{
					highComponent = component;
				}
			}
			int xOffset = 10 - edgeComponent.getUnconvertedX();
			int yOffset = 20 - highComponent.getUnconvertedY();
			for (AbstractComponent component : tempComponents)
			{
				component.setX(component.getUnconvertedX() + xOffset);
				component.setY(component.getUnconvertedY() + yOffset);
			}
			_components.addAll(tempComponents);
		}

	}

	private final Color _unhighlightedColor = new Color(1f, .50f, .50f);

	private TextComponent _buildingPatternLabel;

	private int _selectedBuilding;

}