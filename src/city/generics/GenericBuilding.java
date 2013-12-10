package city.generics;

import java.awt.Point;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import main.ClayConstants;
import models.CityModel;

import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import screens.AbstractScreen;
import xml.BehaviorData;
import xml.BuildingData;
import xml.ItemData;
import city.ai.GolemBehaviorProcess;
import city.ai.objects.Behavior;
import city.ai.objects.Item;
import city.entities.BuildingEntity;
import city.generics.util.GenericUtil;

public final class GenericBuilding
{
	public GenericBuilding(Node node_, int identifier_)
	{
		Element eElement = (Element) node_;
		_buildingName = eElement.getAttribute("BuildingName");
		_buildingTag = eElement.getAttribute("BuildingTag");
		_buildingIdentifier = identifier_;
		_buildingDescription = eElement.getAttribute("BuildingDescription");

		_buildTime = GenericUtil.parseInteger(eElement.getAttribute("BuildTime"));
		_tickStart = GenericUtil.parseInteger(eElement.getAttribute("tickStart"));
		_storageCapacity = GenericUtil.parseInteger(eElement
				.getAttribute("StorageCapacity"));

		_lesserManaCost = GenericUtil.parseDouble(eElement.getAttribute("LesserManaCost"));
		_lesserManaCumulativeCost = GenericUtil.parseDouble(eElement
				.getAttribute("LesserManaCumulativeCost"));
		_greaterManaCost = GenericUtil.parseDouble(eElement.getAttribute("GreaterManaCost"));
		_greaterManaCumulativeCost = GenericUtil.parseDouble(eElement
				.getAttribute("GreaterManaCumulativeCost"));

		_isClaimable = GenericUtil.parseBoolean(eElement.getAttribute("isClaimable"));
		_isHouse = GenericUtil.parseBoolean(eElement.getAttribute("isHouse"));
		_isNatural = GenericUtil.parseBoolean(eElement.getAttribute("isNatural"));
		_isPassable = GenericUtil.parseBoolean(eElement.getAttribute("isPassable"));
		_isScalable = GenericUtil.parseBoolean(eElement.getAttribute("isScalable"));
		_isStorage = GenericUtil.parseBoolean(eElement.getAttribute("isStorage"));
		_isSupport = GenericUtil.parseBoolean(eElement.getAttribute("isSupport"));

		parseBuildingRequirements(eElement.getAttribute("BuildingRequirements"));

		_stateOrder = new ArrayList<String>();
		NodeList children = node_.getChildNodes();
		for (int i = 0; i < children.getLength(); i++)
		{
			Node child = children.item(i);
			if (child.getNodeName().equals("State"))
			{
				Element subChild = (Element) children.item(i);
				String state = subChild.getAttribute("StateName");
				HashMap<String, Texture> textureMap = new HashMap<String, Texture>();
				_stateMap.put(state, textureMap);

				String[] textures = subChild.getAttribute("Graphic").split(",");
				for (int h = 0; h < textures.length; h += 2)
				{
					textureMap.put(textures[h], parseTexture(textures[h + 1]));
				}
				_stateOrder.add(state);
			}
			if (child.getNodeName().equals("TickComplete"))
			{
				Element subChild = (Element) children.item(i);
				_tickCompleteCode = subChild.getAttribute("code");
				_tickResetCode = subChild.getAttribute("reset");
			}
		}
	}

	private static Texture parseTexture(String value_)
	{
		try
		{
			return TextureLoader.getTexture("PNG", new FileInputStream(
					new File("art/" + value_)));
		} catch (Exception e_)
		{
			e_.printStackTrace();
		}

		return null;
	}

	private void parseBuildingRequirements(String requirementList_)
	{
		String[] require = requirementList_.split(",");
		for (int i = 0; i < require.length; i += 2)
		{
			_validPlacementMap
					.put(
							require[i],
							require[i + 1].equals(ClayConstants.EMPTY_TILE) ? null : require[i + 1]);
		}
	}

	public boolean isValidLocation(Point p_, BuildingEntity[][] tiles_)
	{
		if (tiles_[p_.x][p_.y - 1] == null
				|| !tiles_[p_.x][p_.y - 1].isSupportBlock())
			return false;

		boolean valid = true;
		for (String key : _validPlacementMap.keySet())
		{
			int xDiff = 0;
			int yDiff = 0;
			if (!key.equals(ClayConstants.DEFAULT_BUILDING_POSITION))
			{
				for (int j = 0; j < key.length(); j++)
				{
					if (key.charAt(j) == 'n')
						yDiff++;
					else if (key.charAt(j) == 's')
						yDiff--;
					else if (key.charAt(j) == 'e')
						xDiff++;
					else if (key.charAt(j) == 'w')
						xDiff--;
				}
			}
			if (yDiff == 0)
			{
				BuildingEntity ground = tiles_[p_.x + xDiff][p_.y - 1];
				if (ground == null || !ground.isSupportBlock())
				{
					valid = false;
					break;
				}
			}
			GenericBuilding building = BuildingData
					.getBuildingByTag(_validPlacementMap.get(key));
			if (building == null
					|| (tiles_[p_.x + xDiff][p_.y + yDiff] != null && tiles_[p_.x
							+ xDiff][p_.y + yDiff].getIdentifier() == building
							.getBuildingIdentifier()))
				continue;
			valid = false;
			break;
		}
		return valid;
	}

	public void placeBuilding(Point p_, BuildingEntity[][] tiles_, AbstractScreen homeScreen_) throws Exception
	{
		List<BuildingEntity> newBuildings = new ArrayList<BuildingEntity>();
		for (String key : _validPlacementMap.keySet())
		{
			int xDiff = 0;
			int yDiff = 0;
			if (!key.equals(ClayConstants.DEFAULT_BUILDING_POSITION))
			{
				if ((key.contains("n") && key.contains("s")) || (key.contains("e") && key.contains("w")))
					throw new Exception("Invalid build instructions (cannot instuct N and S together, or E and W together).");
				System.out.println(key);
				for (int j = 0; j < key.length(); j++)
				{
					if (key.charAt(j) == 'n')
						yDiff++;
					else if (key.charAt(j) == 's')
						yDiff--;
					else if (key.charAt(j) == 'e')
						xDiff++;
					else if (key.charAt(j) == 'w')
						xDiff--;
				}
			}
			tiles_[p_.x + xDiff][p_.y + yDiff] = new BuildingEntity(this,
					new Point((p_.x + xDiff) * ClayConstants.TILE_X, (p_.y + yDiff)
							* ClayConstants.TILE_Y), homeScreen_, key);
			newBuildings.add(tiles_[p_.x + xDiff][p_.y + yDiff]);
		}
		if (_buildTime > 0)
		{
			GolemBehaviorProcess behaviorProcess = ((GolemBehaviorProcess) homeScreen_
					.getProcess(GolemBehaviorProcess.class));
			for (BuildingEntity entity : newBuildings)
			{
				behaviorProcess.queueBehavior(new Behavior(BehaviorData
						.getTask("construct-building"), tiles_[p_.x][p_.y], entity));
			}
		}
	}

	int text = 0;

	public String calculateTexture(BuildingEntity building_)
	{
		CityModel model = (CityModel) building_.getModel();
		Point point = new Point(building_.getGridX(), building_.getGridY());
		String returnState = ClayConstants.T_STATE_DEFAULT;
		for (String state : _stateOrder)
		{
			String[] stateConditions = state.split(",");
			int metConditions = 0;
			for (String stateCondition : stateConditions)
			{
				String[] stateValues = stateCondition.split(":");
				String key = stateValues[0];
				if (key.equals(ClayConstants.T_STATE_NONE_ABOVE))
				{
					if (model.getTileValue(point.x, point.y + 1) == null)
						metConditions++;
					continue;
				}
				if (key.equals(ClayConstants.T_STATE_UNNATURAL_ABOVE))
				{
					if (model.getTileValue(point.x, point.y + 1) != null
							&& !model.getTileValue(point.x, point.y + 1)
									.isNatural())
						metConditions++;
					continue;
				}
				if (key.equals(ClayConstants.T_STATE_UNNATURAL_BELOW))
				{
					if (model.getTileValue(point.x, point.y - 1) != null
							&& !model.getTileValue(point.x, point.y - 1)
									.isNatural())
						metConditions++;
					continue;
				}
				if (key.equals(ClayConstants.T_STATE_UNNATURAL_LEFT))
				{
					if (model.getTileValue(point.x - 1, point.y) != null
							&& !model.getTileValue(point.x - 1, point.y)
									.isNatural())
						metConditions++;
					continue;
				}
				if (key.equals(ClayConstants.T_STATE_UNNATURAL_RIGHT))
				{
					if (model.getTileValue(point.x + 1, point.y) != null
							&& !model.getTileValue(point.x + 1, point.y)
									.isNatural())
						metConditions++;
					continue;
				}
				if (key.equals(ClayConstants.T_STATE_ADJACENT))
				{
					Point above = new Point(point.x, point.y + 1);
					Point below = new Point(point.x, point.y - 1);
					Point left = new Point(point.x - 1, point.y);
					Point right = new Point(point.x + 1, point.y);
					boolean isAdjacent = false;
					if (model.getTileValue(above.x, above.y) != null
							&& model.getTileValue(above.x, above.y)
									.getBuildingTag().equals(stateValues[1]))
						isAdjacent = true;
					else if (model.getTileValue(below.x, below.y) != null
							&& model.getTileValue(below.x, below.y)
									.getBuildingTag().equals(stateValues[1]))
						isAdjacent = true;
					else if (model.getTileValue(left.x, left.y) != null
							&& model.getTileValue(left.x, left.y)
									.getBuildingTag().equals(stateValues[1]))
						isAdjacent = true;
					else if (model.getTileValue(right.x, right.y) != null
							&& model.getTileValue(right.x, right.y)
									.getBuildingTag().equals(stateValues[1]))
						isAdjacent = true;
					if (isAdjacent)
						metConditions++;
					continue;
				}
				if (key.equals(ClayConstants.T_STATE_TICK_FINISHED))
				{
					if (building_.isTickFinished())
						metConditions++;
					continue;
				}
				if (key.equals(ClayConstants.T_STATE_STORAGE_FULL))
				{
					if (building_.getCopyOfHeldItems().size() >= _storageCapacity)
						metConditions++;
					continue;
				}
				if (key.equals(ClayConstants.T_STATE_BURIED))
				{
					int neededDepth = Integer.parseInt(stateValues[1]);
					int depth = 0;
					while (depth < neededDepth)
					{
						if (model.getTileValue(
								building_.getGridX(),
								building_.getGridY() + depth) == null)
							break;
						depth++;
					}
					if (depth == neededDepth)
						metConditions++;
					continue;
				}
			}
			if (metConditions == stateConditions.length)
				returnState = state;
		}
		return returnState;
	}

	public void tickFinished(BuildingEntity building_)
	{
		String[] code = _tickCompleteCode.split(",");
		for (String command : code)
		{
			String[] commandAndParams = command.split(":");
			if (commandAndParams[0]
					.equals(ClayConstants.TICK_CODE_PRODUCE_ITEM))
			{
				building_.generate(new Item(ItemData
						.getItem(commandAndParams[1])));
				GolemBehaviorProcess behaviorProcess = ((GolemBehaviorProcess) building_
						.getHomeScreen().getProcess(GolemBehaviorProcess.class));
				behaviorProcess.queueBehavior(new Behavior(BehaviorData
						.getTask(commandAndParams[2]), building_));
			}
			if (commandAndParams[0].equals("behavior"))
			{
				GolemBehaviorProcess behaviorProcess = ((GolemBehaviorProcess) building_
						.getHomeScreen().getProcess(GolemBehaviorProcess.class));
				behaviorProcess.queueBehavior(new Behavior(BehaviorData
						.getTask(commandAndParams[1]), building_));
			}
		}
	}

	public boolean tickReset(BuildingEntity building_)
	{
		if (_tickResetCode.isEmpty())
			return true;
		String[] code = _tickResetCode.split(",");
		for (String command : code)
		{
			String[] commandAndParams = command.split(":");
			if (commandAndParams[0]
					.equals(ClayConstants.TICK_RESET_NO_HELD_ITEMS))
			{
				if (building_.getCopyOfHeldItems().size() == 0)
					return true;
			}
		}
		return false;
	}

	public Texture getTexture(String textureKey_, String position_)
	{
		return _stateMap.get(textureKey_).get(position_);
	}

	public String getBuildingName()
	{
		return _buildingName;
	}

	public String getBuildingDescription()
	{
		return _buildingDescription;
	}

	public String getBuildingTag()
	{
		return _buildingTag;
	}

	public double getLesserManaCost()
	{
		return _lesserManaCost;
	}

	public double getLesserManaCumulativeCost()
	{
		return _lesserManaCumulativeCost;
	}

	public double getGreaterManaCost()
	{
		return _greaterManaCost;
	}

	public double getGreaterManaCumulativeCost()
	{
		return _greaterManaCumulativeCost;
	}

	public int getBuildingIdentifier()
	{
		return _buildingIdentifier;
	}

	public int getTickStart()
	{
		return _tickStart;
	}

	public int getBuildTime()
	{
		return _buildTime;
	}

	public boolean isClaimable()
	{
		return _isClaimable;
	}

	public boolean isHouse()
	{
		return _isHouse;
	}

	public boolean isNatural()
	{
		return _isNatural;
	}

	public boolean isPassable()
	{
		return _isPassable;
	}

	public boolean isScalable()
	{
		return _isScalable;
	}

	public boolean isStorage()
	{
		return _isStorage;
	}

	public boolean isSupport()
	{
		return _isSupport;
	}

	public int getStorageCapacity()
	{
		return _storageCapacity;
	}

	public boolean isValid(BuildingEntity building_)
	{
		return !building_.isInUse();
	}

	@Override
	public boolean equals(Object obj)
	{
		if (!(obj instanceof GenericBuilding))
			return false;
		return _buildingIdentifier == ((GenericBuilding) obj)
				.getBuildingIdentifier();
	}

	@Override
	public int hashCode()
	{
		return _buildingIdentifier;
	}

	private final Map<String, Map<String, Texture>> _stateMap = new HashMap<String, Map<String, Texture>>();

	private final Map<String, String> _validPlacementMap = new HashMap<String, String>();
	private final Map<String, Boolean> _scalabilityMap = new HashMap<String, Boolean>();

	private final List<String> _stateOrder;

	private final String _buildingName;
	private final String _buildingTag;
	private final int _buildingIdentifier;
	private final String _buildingDescription;
	private String _tickCompleteCode;
	private String _tickResetCode;

	private final int _buildTime;
	private final int _tickStart;
	private final int _storageCapacity;

	private final double _lesserManaCost;
	private final double _lesserManaCumulativeCost;
	private final double _greaterManaCost;
	private final double _greaterManaCumulativeCost;

	private final boolean _isClaimable;
	private final boolean _isHouse;
	private final boolean _isNatural;
	private final boolean _isPassable;
	private final boolean _isScalable;
	private final boolean _isStorage;
	private final boolean _isSupport;

}
