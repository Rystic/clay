package org.rystic.city.ui.menus.areas.buildingmenu;

import java.util.HashMap;
import java.util.Map;

import org.rystic.city.generics.data.BuildingData;
import org.rystic.city.ui.menus.areas.AbstractArea;
import org.rystic.city.ui.menus.components.HorizontalLineComponent;
import org.rystic.city.ui.menus.components.TextComponent;
import org.rystic.main.ClayConstants;
import org.rystic.models.CityModel;
import org.rystic.screens.AbstractScreen;

public class CategoryDescriptionArea extends AbstractArea
{
	public CategoryDescriptionArea(AbstractScreen homeScreen_,
			Map<Integer, String> categoryHotkeys_)
	{
		super(homeScreen_);
		_model = (CityModel) _homeScreen.getModel();

		_categoryHotkeys = categoryHotkeys_;
		_categoryName = new TextComponent(5, 25, "testing!",
				ClayConstants.M_AREA_HEADER_COLOR);
		_categoryDescription = new TextComponent(5, 28, "testing!");
		_buildingInfo = new TextComponent(5, 34,
				"Press TAB to view building info.",
				ClayConstants.M_MORE_INFORMATION_COLOR);

		_components.add(_categoryName);
		_components.add(_categoryDescription);
		_components.add(_buildingInfo);
		_components.add(new HorizontalLineComponent(38, 2));

		_categoryName.setText("Architecture");
		_categoryDescription.setText(_categoryDescriptions.get("Architecture"));

	}

	public void updateSelection(int key_)
	{
		String newCategory = _categoryHotkeys.get(key_);
		if (newCategory == null)
			return;
		_categoryName.setText(newCategory);
		_categoryDescription.setText(_categoryDescriptions.get(newCategory));
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
				_categoryName.setText(_category);
				_categoryDescription.setText(_categoryDescriptions
						.get(_category));
			}
		}
	}

	private static Map<String, String> _categoryDescriptions = new HashMap<String, String>();

	static
	{
		_categoryDescriptions.put(
				"Architecture",
				"Buildings that create paths and provide storage.");
		_categoryDescriptions.put(
				"Basic Needs",
				"Essentials for golem survival and procreation.");
		_categoryDescriptions.put(
				"Conversions",
				"Industrial buildings used to create new resources.");
		_categoryDescriptions.put(
				"Culture",
				"Buildings crucial to your city's social values.");
		_categoryDescriptions.put(
				"Mana Pool",
				"Buildings that manipulate the mana pool.");
	}

	private CityModel _model;

	private Map<Integer, String> _categoryHotkeys;

	private TextComponent _categoryName;
	private TextComponent _categoryDescription;
	private TextComponent _buildingInfo;

	private String _category;
	private int _buildingIdentifier;
}
