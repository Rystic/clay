package city.ui.menus.areas;

import java.util.ArrayList;
import java.util.List;

import screens.AbstractScreen;
import screens.CityScreen;
import city.generics.data.BuildingData;
import city.ui.menus.components.TextComponent;

public class BuildingDescriptionArea extends AbstractArea
{
	public BuildingDescriptionArea(AbstractScreen homeScreen_, int yPos_)
	{
		super(homeScreen_);
		_yPos = yPos_;
		_selectedBuilding = -1;
		_buildingDescriptionLabel = new TextComponent(80, _yPos + 40,
				"Building Description");
	}

	@Override
	public void update()
	{
		int selectedBuilding = ((CityScreen) _homeScreen).getModel()
				.getSelectedBuilding();
		if (selectedBuilding != _selectedBuilding)
		{
			String description = BuildingData.getBuildingById(selectedBuilding)
					.getBuildingDescription();

			String[] words = description.split(" ");

			List<StringBuilder> lines = new ArrayList<StringBuilder>();
			lines.add(new StringBuilder());

			int i = 0;
			for (String word : words)
			{
				if (lines.get(i).length() + word.length()>= _charactersPerLine)
				{
					lines.add(new StringBuilder());
					i++;
				}
				lines.get(i).append(word).append(" ");
			}

			_components.clear();
			_components.add(_buildingDescriptionLabel);
			for (int j = 0; j < lines.size(); j++)
			{
				_components.add(new TextComponent(40, _yPos - (j * _heightPerLine), lines.get(j).toString()));
			}
		}
	}

	private final int _charactersPerLine = 30;
	private final int _heightPerLine = 15;

	private TextComponent _buildingDescriptionLabel;

	private int _selectedBuilding;

	private int _yPos;

}
