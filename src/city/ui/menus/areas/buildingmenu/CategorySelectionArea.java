package city.ui.menus.areas.buildingmenu;

import java.util.Map;

import main.ClayConstants;
import models.CityModel;
import screens.AbstractScreen;
import city.generics.data.BuildingData;
import city.ui.menus.areas.AbstractArea;
import city.ui.menus.components.AbstractComponent;
import city.ui.menus.components.HorizontalLineComponent;
import city.ui.menus.components.TextComponent;

public class CategorySelectionArea extends AbstractArea
{
	public CategorySelectionArea(AbstractScreen homeScreen_,
			Map<String, String> categoryLabels_,
			Map<Integer, String> categoryHotkeys_)
	{
		super(homeScreen_);
		_model = (CityModel) _homeScreen.getModel();

		_categoryLabels = categoryLabels_;
		_categoryHotkeys = categoryHotkeys_;
		_components.add(new TextComponent(5, 5, HEADER_STRING,
				ClayConstants.M_AREA_HEADER_COLOR));
		int increment = 8;
		for (String label : _categoryLabels.keySet())
		{
			_components.add(new TextComponent(7, increment, label,
					ClayConstants.M_UNHIGHLIGHTED_TEXT_COLOR));
			increment += 3;
		}
		_components.add(new HorizontalLineComponent(increment + 1, 2));
		_category = "Architecure";
		_buildingIdentifier = -1;
	}

	public void updateSelection(int key_)
	{
		String newCategory = _categoryHotkeys.get(key_);
		if (newCategory == null)
			return;
		updateHighlight(newCategory);
	}
	
	private void updateHighlight(String category_)
	{
		for (AbstractComponent component : _components)
		{
			if (component instanceof TextComponent)
			{
				TextComponent textComponent = (TextComponent) component;
				if (textComponent.getText().equals(HEADER_STRING))
					continue;
				textComponent
						.setColor(_categoryLabels.get(textComponent.getText())
								.equals(category_) ? ClayConstants.M_HIGHLIGHTED_TEXT_COLOR : ClayConstants.M_UNHIGHLIGHTED_TEXT_COLOR);
			}
		}
	}

	@Override
	public void update()
	{
		if (_model.getSelectedBuilding() != _buildingIdentifier)
		{
			_buildingIdentifier = _model.getSelectedBuilding();
			String category = BuildingData.getBuildingById(_buildingIdentifier)
					.getMenuCategory();
			if (!category.equals(_category))
			{
				_category = BuildingData.getBuildingById(_buildingIdentifier)
						.getMenuCategory();
				updateHighlight(_category);
			}
		}
	}

	private CityModel _model;

	private Map<String, String> _categoryLabels;
	private Map<Integer, String> _categoryHotkeys;

	private String _category;
	private int _buildingIdentifier;

	private static final String HEADER_STRING = "Building Categories";
}
