package city.generics;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class GenericItem
{
	public GenericItem(Node node_)
	{
		Element eElement = (Element) node_;
		_itemName = eElement.getAttribute("ItemName");
		_itemTag = eElement.getAttribute("ItemTag");
	}

	public String getItemName()
	{
		return _itemName;
	}
	
	public String getItemTag()
	{
		return _itemTag;
	}
	
	private final String _itemName;
	private final String _itemTag;
	
}
