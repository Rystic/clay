package city.generics;

import org.newdawn.slick.opengl.Texture;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import city.generics.util.GenericUtil;

public class GenericItem
{
	public GenericItem(Node node_)
	{
		Element eElement = (Element) node_;
		_itemName = eElement.getAttribute("ItemName");
		_itemTag = eElement.getAttribute("ItemTag");
		_texture = GenericUtil.parseTexture(eElement.getAttribute("Texture"));
	}

	public String getItemName()
	{
		return _itemName;
	}
	
	public String getItemTag()
	{
		return _itemTag;
	}
	
	public Texture getTexture()
	{
		return _texture;
	}
	
	private final Texture _texture;
	
	private final String _itemName;
	private final String _itemTag;
	
}
