package org.rystic.city.generics.data;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.newdawn.slick.opengl.Texture;
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
			_unbuiltTexture = FieldParser.parseTexture("constructionBlock.png");
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

	public static List<GenericBuilding> getBuildingsInCategory(String Category_)
	{
		return _categoryToBuilding.get(Category_);
	}

	public static void main(String[] args)
	{
		// for testing only
		try
		{
			Display.setDisplayMode(new DisplayMode(200, 200));
			Display.create();
		} catch (LWJGLException e)
		{
			e.printStackTrace();
			System.exit(0);
		}
		BuildingData.init();
	}

	public static Texture _unbuiltTexture;

	private static Map<Integer, GenericBuilding> _idToBuilding = new HashMap<Integer, GenericBuilding>();
	private static Map<String, GenericBuilding> _tagToBuilding = new HashMap<String, GenericBuilding>();

	private static Map<String, List<GenericBuilding>> _categoryToBuilding = new HashMap<String, List<GenericBuilding>>();
	private static List<String> _buildingCategories = new ArrayList<String>();
}
