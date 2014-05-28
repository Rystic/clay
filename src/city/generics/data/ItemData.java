package city.generics.data;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import city.generics.GenericItem;

public class ItemData
{
	public static void init()
	{
		File fXmlFile = new File("src/items.xml");
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder;
		try
		{
			dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);
			doc.getDocumentElement().normalize();
			NodeList nList = doc.getElementsByTagName("Item");
			for (int i = 0; i < nList.getLength(); i++)
			{
				Node nNode = nList.item(i);
				if (nNode.getNodeType() == Node.ELEMENT_NODE)
				{
					GenericItem item = new GenericItem(nNode);
					_tagToItem.put(item.getItemTag(), item);
				}
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public static GenericItem getItem(String tag_)
	{
		return _tagToItem.get(tag_);
	}
	
	public static Set<String> getAllItemTags()
	{
		return _tagToItem.keySet();
	}
	
	private static Map<String, GenericItem> _tagToItem = new HashMap<String, GenericItem>();
}
