package xml;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import city.generics.GenericBuilding;

public class BuildingData
{
	public static void init()
	{
		File fXmlFile = new File("src/buildings.xml");
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
				}
			}
			_unbuiltTexture = TextureLoader.getTexture(
					"PNG",
					new FileInputStream(new File("art/constructionBlock.png")));
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
}
