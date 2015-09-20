package org.rystic.city.generics;

import java.nio.ByteBuffer;

import org.rystic.city.generics.util.FieldParser;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class GenericItem
{
	public GenericItem(Node node_)
	{
		Element eElement = (Element) node_;
		_itemName = eElement.getAttribute("ItemName");
		_itemTag = eElement.getAttribute("ItemTag");
		_itemFamily = eElement.getAttribute("ItemFamily");
		_parent = eElement.getAttribute("Parent");
		_texture = FieldParser.parseTexture(eElement.getAttribute("Texture"));
	}

	public String getItemName()
	{
		return _itemName;
	}

	public String getItemTag()
	{
		return _itemTag;
	}

	public ByteBuffer getTexture()
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

	private final ByteBuffer _texture;

	private final String _itemName;
	private final String _itemTag;
	private final String _itemFamily;

	private final String _parent;

}
