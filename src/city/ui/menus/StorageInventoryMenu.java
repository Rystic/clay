package city.ui.menus;

import screens.AbstractScreen;
import city.ui.menus.areas.StorageInventoryArea;

public class StorageInventoryMenu extends AbstractMenu
{
	public StorageInventoryMenu(AbstractScreen homeScreen_)
	{
		super(homeScreen_);
		_storageInventoryArea = new StorageInventoryArea(homeScreen_, 825);
		_areas.add(_storageInventoryArea);
	}

	@Override
	public void handleKeyEvent(Integer key_)
	{
		
	}

	@Override
	public void handleMouseWheel(boolean upwardScroll_)
	{
		
	}
	
	private StorageInventoryArea _storageInventoryArea;
}
