package city.generics;

import org.newdawn.slick.opengl.Texture;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import city.generics.util.XmlFieldParser;

public class GenericItem
{
	public GenericItem(Node node_)
	{
		Element eElement = (Element) node_;
		_itemName = eElement.getAttribute("ItemName");
		_itemTag = eElement.getAttribute("ItemTag");
		_itemFamily = eElement.getAttribute("ItemFamily");
		_familyHead = XmlFieldParser.parseBoolean(eElement
				.getAttribute("FamilyHead"));
		_texture = XmlFieldParser
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
		return _familyHead;
	}

	private final Texture _texture;

	private final String _itemName;
	private final String _itemTag;
	private final String _itemFamily;

	private boolean _familyHead;
}
