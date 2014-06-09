package city.generics;

import java.util.List;

import org.newdawn.slick.opengl.Texture;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import city.generics.util.FieldParser;

public class GenericItem
{
	public GenericItem(Node node_)
	{
		Element eElement = (Element) node_;
		_itemName = eElement.getAttribute("ItemName");
		_itemTag = eElement.getAttribute("ItemTag");
		_itemFamily = eElement.getAttribute("ItemFamily");
		_parent = eElement.getAttribute("Parent");
		_texture = FieldParser
				.parseTexture(eElement.getAttribute("Texture"));
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

	public String getItemFamily()
	{
		return _itemFamily;
	}

	public boolean isFamilyHead()
	{
		return _parent.isEmpty();
	}
	
	public String getParentTag()
	{
		return _parent;
	}

	private final Texture _texture;

	private List<String> _children;
	
	private final String _itemName;
	private final String _itemTag;
	private final String _itemFamily;

	private final String _parent;
}
