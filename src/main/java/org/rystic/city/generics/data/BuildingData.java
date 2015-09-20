package org.rystic.city.generics.data;

import java.io.File;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.rystic.city.generics.GenericBuilding;
import org.rystic.city.generics.util.FieldParser;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class BuildingData
{
	public static void init()
	{
		File fXmlFile = new File("src/main/data/buildings.xml");
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder;
		int identifier = 0;
		try
		{
			dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);
			doc.getDocumentElement().normalize();
			NodeList nList = doc.getElementsByTagName("Building");
			for (int i = 0; i < nList.getLength(); i++)
			{
				Node nNode = nList.item(i);
				if (nNode.getNodeType() == Node.ELEMENT_NODE)
				{
					GenericBuilding building = new GenericBuilding(nNode,
							identifier);
					_idToBuilding.put(
							building.getBuildingIdentifier(),
							building);
					_tagToBuilding.put(building.getBuildingTag(), building);
					identifier++;

					String menuCategory = building.getMenuCategory();
					if (!menuCategory.isEmpty())
					{
						List<GenericBuilding> CategoryList = _categoryToBuilding
								.get(menuCategory);
						if (CategoryList == null)
						{
							CategoryList = new ArrayList<GenericBuilding>();
							_categoryToBuilding.put(menuCategory, CategoryList);
						}
						CategoryList.add(building);
					}
				}
				for (String key : _categoryToBuilding.keySet())
				{
					Collections.sort(
							_categoryToBuilding.get(key),
							new Comparator<GenericBuilding>()
							{
								@Override
								public int compare(GenericBuilding b1, GenericBuilding b2)
								{
									return b1.getBuildingName().compareTo(
											b2.getBuildingName());
								}
							});
				}
			}
			_buildingCategories.addAll(_categoryToBuilding.keySet());
			Collections.sort(_buildingCategories);
			_constructionTexture = FieldParser
					.parseTexture("constructionBlock32.png");
			_constructionNonSupportTexture = FieldParser
					.parseTexture("constructionNonSupportBlock32.png");
			_clayBlockFoundationTexture = FieldParser
					.parseTexture("clayBlockFoundation32.png");

		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public static GenericBuilding getBuildingById(Integer id_)
	{
		return _idToBuilding.get(id_);
	}

	public static GenericBuilding getBuildingByTag(String tag_)
	{
		return _tagToBuilding.get(tag_);
	}

	public static String getCategory(int index_)
	{
		return _buildingCategories.get(index_);
	}

	public static int getCategoryCount()
	{
		return _buildingCategories.size();
	}

	public static List<GenericBuilding> getBuildingsInCategory(String category_)
	{
		List<GenericBuilding> availableBuildings = new ArrayList<GenericBuilding>();
		for (GenericBuilding building : _categoryToBuilding.get(category_))
		{
			// TODO update this when techs are introduced.
			if (building.isUnlockedFromStart())
				availableBuildings.add(building);
		}
		return availableBuildings;
	}

	public static ByteBuffer _constructionTexture;
	public static ByteBuffer _constructionNonSupportTexture;
	public static ByteBuffer _clayBlockFoundationTexture;

	private static Map<Integer, GenericBuilding> _idToBuilding = new HashMap<Integer, GenericBuilding>();
	private static Map<String, GenericBuilding> _tagToBuilding = new HashMap<String, GenericBuilding>();

	private static Map<String, List<GenericBuilding>> _categoryToBuilding = new HashMap<String, List<GenericBuilding>>();
	private static List<String> _buildingCategories = new ArrayList<String>();
}
