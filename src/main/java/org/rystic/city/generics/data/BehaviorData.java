package org.rystic.city.generics.data;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.rystic.city.generics.GenericBehavior;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class BehaviorData
{
	public static void init()
	{
		File fXmlFile = new File("src/behaviors.xml");
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder;
		try
		{
			dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);
			doc.getDocumentElement().normalize();
			NodeList nList = doc.getElementsByTagName("Behavior");
			for (int i = 0; i < nList.getLength(); i++)
			{
				Node nNode = nList.item(i);
				if (nNode.getNodeType() == Node.ELEMENT_NODE)
				{
					GenericBehavior behavior = new GenericBehavior(nNode);
					if (behavior.isPersonalBehavior())
						_personalBehaviors.add(behavior);
					_allBehaviors.add(behavior);
					_tagToBehavior.put(behavior.getBehaviorTag(), behavior);
				}
			}
			_wantedBehaviors.addAll(_personalBehaviors);
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public static GenericBehavior getBehavior(String tag_)
	{
		return _tagToBehavior.get(tag_);
	}

	public static List<GenericBehavior> getAllBeahviors()
	{
		return _allBehaviors;
	}

	public static List<GenericBehavior> getWantedBehaviors()
	{
		return _wantedBehaviors;
	}
	
	public static List<GenericBehavior> getPersonalBehaviors()
	{
		return _personalBehaviors;
	}
	

	public static final Map<String, GenericBehavior> _tagToBehavior = new HashMap<String, GenericBehavior>();

	private static final List<GenericBehavior> _allBehaviors = new ArrayList<GenericBehavior>();
	private static final List<GenericBehavior> _wantedBehaviors = new ArrayList<GenericBehavior>();
	private static final List<GenericBehavior> _personalBehaviors = new ArrayList<GenericBehavior>();

}
