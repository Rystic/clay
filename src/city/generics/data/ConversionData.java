package city.generics.data;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import city.generics.GenericConversion;

public class ConversionData
{
	public static void init()
	{
		File fXmlFile = new File("src/conversions.xml");
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder;
		try
		{
			dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);
			doc.getDocumentElement().normalize();
			NodeList nList = doc.getElementsByTagName("Conversion");
			for (int i = 0; i < nList.getLength(); i++)
			{
				Node nNode = nList.item(i);
				if (nNode.getNodeType() == Node.ELEMENT_NODE)
				{
					GenericConversion conversion = new GenericConversion(nNode);
					_tagToConversion.put(conversion.getConversionTag(), conversion);
				}
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public static GenericConversion getItem(String tag_)
	{
		return _tagToConversion.get(tag_);
	}
	
	private static Map<String, GenericConversion> _tagToConversion = new HashMap<String, GenericConversion>();
}
