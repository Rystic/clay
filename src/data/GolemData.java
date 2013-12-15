package data;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import city.generics.GenericGolem;

public class GolemData
{
	public static void init()
	{
		File fXmlFile = new File("src/golems.xml");
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder;
		try
		{
			dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);
			doc.getDocumentElement().normalize();
			NodeList nList = doc.getElementsByTagName("Golem");
			for (int i = 0; i < nList.getLength(); i++)
			{
				Node nNode = nList.item(i);
				if (nNode.getNodeType() == Node.ELEMENT_NODE)
				{
					GenericGolem golem = new GenericGolem(nNode);
					_tagToGolem.put(golem.getGolemTag(), golem);
				}
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public static GenericGolem getGolem(String tag_)
	{
		return _tagToGolem.get(tag_);
	}
	
	public static Map<String, GenericGolem> _tagToGolem = new HashMap<String, GenericGolem>();
}
