package city.ui.menus.areas;

import city.generics.data.BuildingData;
import city.ui.menus.components.TextComponent;
import screens.AbstractScreen;

public class BuildingTabArea extends AbstractArea
{

	public BuildingTabArea(AbstractScreen homeScreen_)
	{
		super(homeScreen_);
		_selectedCategoryIndex = 0;
		_maxCategoryIndex = BuildingData.getCategoryCount() - 1;
		_selectedCategory = BuildingData.getCategory(_selectedCategoryIndex);
		
		_CategoryName = new TextComponent(50, 100, _selectedCategory);
		_components.add(_CategoryName);
	}
	
	public void moveDown()
	{
		
	}

	@Override
	public void update()
	{

	}
	
	private TextComponent _CategoryName;

	private String _selectedCategory;
	
	private int _maxCategoryIndex;
	private int _selectedCategoryIndex;
}
