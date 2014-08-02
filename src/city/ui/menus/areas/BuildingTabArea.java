package city.ui.menus.areas;

import main.ClayConstants;
import screens.AbstractScreen;
import city.generics.data.BuildingData;
import city.ui.menus.components.TextComponent;

public class BuildingTabArea extends AbstractArea
{

	public BuildingTabArea(AbstractScreen homeScreen_)
	{
		super(homeScreen_);
		_selectedCategoryIndex = 0;
		_maxCategoryIndex = BuildingData.getCategoryCount() - 1;
		_selectedCategory = BuildingData.getCategory(_selectedCategoryIndex);
		_prevSelectedCategoryIndex = _selectedCategoryIndex;

		_categoryNameLabel = new TextComponent(5, 90, CATAGORY_HEADER
				+ _selectedCategory, ClayConstants.HIGHLIGHTED_TEXT_COLOR);
		_components.add(_categoryNameLabel);
	}

	public void nextTab()
	{
		_selectedCategoryIndex++;
		if (_selectedCategoryIndex == _maxCategoryIndex + 1)
			_selectedCategoryIndex = 0;
		_selectedCategory = BuildingData.getCategory(_selectedCategoryIndex);
	}

	@Override
	public void update()
	{
		if (_prevSelectedCategoryIndex != _selectedCategoryIndex)
		{
			_components.clear();
			_categoryNameLabel.setText(CATAGORY_HEADER + _selectedCategory);
			_prevSelectedCategoryIndex = _selectedCategoryIndex;
			_components.add(_categoryNameLabel);
		}
	}

	private static final String CATAGORY_HEADER = "-- ";

	private TextComponent _categoryNameLabel;

	private String _selectedCategory;

	private int _maxCategoryIndex;
	private int _selectedCategoryIndex;
	private int _prevSelectedCategoryIndex;

}
