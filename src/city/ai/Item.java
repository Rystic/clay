package city.ai;

import city.generics.GenericItem;

public class Item
{
	public Item(GenericItem item_)
	{
		_item = item_;
	}
	
	public String getTag()
	{
		return _item.getItemTag();
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if (!(obj instanceof Item)) return false;
		return getTag().equals(((Item)obj).getTag());
	}
	
	private GenericItem _item;
}
