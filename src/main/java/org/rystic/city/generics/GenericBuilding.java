package org.rystic.city.generics;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bushe.swing.event.EventBus;
import org.newdawn.slick.opengl.Texture;
import org.rystic.city.generics.data.BehaviorData;
import org.rystic.city.generics.data.BuildingData;
import org.rystic.city.generics.data.ItemData;
import org.rystic.city.generics.entities.BuildingEntity;
import org.rystic.city.generics.objects.Behavior;
import org.rystic.city.generics.objects.Item;
import org.rystic.city.generics.util.FieldParser;
import org.rystic.city.processes.GolemBehaviorProcess;
import org.rystic.city.util.MapUpdateEvent;
import org.rystic.main.ClayConstants;
import org.rystic.models.CityModel;
import org.rystic.screens.AbstractScreen;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public final class GenericBuilding implements Comparator<GenericBuilding>
{
	public GenericBuilding(Node node_, int identifier_)
	{
		Element eElement = (Element) node_;
		_buildingName = eElement.getAttribute("BuildingName");
		_buildingTag = eElement.getAttribute("BuildingTag");
		_buildingIdentifier = identifier_;
		_buildingDescription = eElement.getAttribute("BuildingDescription");
		_menuCategory = eElement.getAttribute("MenuCategory");

		_extraWeightConditions = eElement.getAttribute("ExtraWeight");

		_buildTime = FieldParser.parseInt(eElement.getAttribute("BuildTime"));
		_tickStart = FieldParser.parseInt(eElement.getAttribute("tickStart"));
		_storageCapacity = FieldParser.parseInt(eElement
				.getAttribute("StorageCapacity"));

		_transformCode = eElement.getAttribute("Transform");
		_constructionItems = eElement.getAttribute("ConstructionItems");

		_coolingRate = FieldParser.parseInt(eElement
				.getAttribute("CoolingRate"));
		_heatAbsorb = FieldParser.parseInt(eElement.getAttribute("HeatAbsorb"));
		_insulation = FieldParser.parseInt(eElement.getAttribute("Insulation"));
		_heatResistance = FieldParser.parseInt(eElement
				.getAttribute("HeatResistance"));

		String[] heatThresholds = eElement.getAttribute("HeatThresholds")
				.split(",");
		_firstHeatThreshold = Integer.parseInt(heatThresholds[0]);
		_secondHeatThreshold = Integer.parseInt(heatThresholds[1]);
		_thirdHeadThreshold = Integer.parseInt(heatThresholds[2]);

		_lesserManaCost = FieldParser.parseDouble(eElement
				.getAttribute("LesserManaCost"));
		_lesserManaCumulativeCost = FieldParser.parseDouble(eElement
				.getAttribute("LesserManaCumulativeCost"));
		_greaterManaCost = FieldParser.parseDouble(eElement
				.getAttribute("GreaterManaCost"));
		_greaterManaCumulativeCost = FieldParser.parseDouble(eElement
				.getAttribute("GreaterManaCumulativeCost"));

		_isHouse = FieldParser.parseBoolean(eElement.getAttribute("isHouse"));
		_isNatural = FieldParser.parseBoolean(eElement
				.getAttribute("isNatural"));
		_isPassable = FieldParser.parseBoolean(eElement
				.getAttribute("isPassable"));
		_isStorage = FieldParser.parseBoolean(eElement
				.getAttribute("isStorage"));
		_isSupport = FieldParser.parseBoolean(eElement
				.getAttribute("isSupport"));
		_unlockedFromStart = FieldParser.parseBoolean(eElement
				.getAttribute("UnlockedFromStart"));
		_isBridge = FieldParser.parseBoolean(eElement.getAttribute("isBridge"));

		_psychologyType = FieldParser.parseByte(eElement
				.getAttribute("PsychologyType"));
		_psychologyInfluence = FieldParser.parseByte(eElement
				.getAttribute("PsychologyInfluence"));

		parseBuildingRequirements(eElement.getAttribute("BuildingRequirements"));
		parseScalability(eElement.getAttribute("Scalability"));

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
					textureMap.put(
							textures[h],
							FieldParser.parseTexture(textures[h + 1]));
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

	private void parseScalability(String requirementList_)
	{
		String[] require = requirementList_.split(",");
		for (int i = 0; i < require.length; i += 2)
		{
			_scalabilityMap.put(require[i], require[i + 1]);
		}
	}

	public boolean isValidLocation(Point p_, BuildingEntity[][] tiles_, boolean isSupport_)
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
			if (!isSupport_)
			{
				if (p_.y < tiles_[0].length - 1)
				{
					if (key.equals(ClayConstants.DEFAULT_BUILDING_POSITION))
					{
						if (tiles_[p_.x][p_.y + 1] != null
								&& !tiles_[p_.x][p_.y + 1].isBridge())
						{
							valid = _validPlacementMap.keySet().contains("n");
						}
					}
					else
					{
						if (tiles_[p_.x + xDiff][p_.y + yDiff + 1] != null
								&& !tiles_[p_.x + xDiff][p_.y + yDiff + 1]
										.isBridge())
						{
							StringBuilder nBuilder = new StringBuilder("n");
							StringBuilder ewBuilder = new StringBuilder();
							for (char c : key.toCharArray())
							{
								if (c == 'n')
									nBuilder.append("n");
								else
									ewBuilder.append(c);
							}
							StringBuilder newBuilder = new StringBuilder(
									nBuilder).append(ewBuilder);
							StringBuilder ewBuidler = new StringBuilder(
									ewBuilder).append(nBuilder);
							valid = _validPlacementMap.keySet().contains(
									newBuilder.toString())
									|| _validPlacementMap.keySet().contains(
											ewBuidler.toString());
							if (!valid)
								return valid;
						}
					}
				}
			}
			if (!_transformCode.isEmpty())
			{
				GenericBuilding transformCheck = transform(p_, tiles_);
				if (!transformCheck.equals(this))
					return false;
			}
			GenericBuilding building = BuildingData
					.getBuildingByTag(_validPlacementMap.get(key));
			if (yDiff == 0 && building != null)
			{
				BuildingEntity ground = tiles_[p_.x + xDiff][p_.y - 1];
				if (ground == null || !ground.isSupportBlock())
				{
					valid = false;
					break;
				}
			}

			if (p_.y + yDiff < 0
					|| p_.y + yDiff >= (ClayConstants.DEFAULT_MAP_HEIGHT / ClayConstants.TILE_Y))
			{
				valid = false;
				break;
			}
			if (p_.x + xDiff < 0
					|| p_.x + xDiff >= (ClayConstants.DEFAULT_MAP_WIDTH / ClayConstants.TILE_X))
			{
				valid = false;
				break;
			}
			if (tiles_[p_.x + xDiff][p_.y + yDiff] != null
					&& tiles_[p_.x + xDiff][p_.y + yDiff]
							.getBuildingIdentifier() == _buildingIdentifier)
			{
				valid = false;
				break;
			}
			BuildingEntity tileBuilding = tiles_[p_.x + xDiff][p_.y + yDiff];
			if (building == null)
			{
				if (tileBuilding == null)
					continue;
			}
			else if (tileBuilding != null
					&& tileBuilding.getBuildingIdentifier() == building
							.getBuildingIdentifier() && tileBuilding.isBuilt())
				continue;
			valid = false;
			break;
		}
		return valid;
	}

	public void placeBuilding(Point p_, BuildingEntity[][] tiles_, AbstractScreen homeScreen_)
			throws Exception
	{
		List<BuildingEntity> newBuildings = new ArrayList<BuildingEntity>();
		for (String key : _validPlacementMap.keySet())
		{
			int xDiff = 0;
			int yDiff = 0;
			if (!key.equals(ClayConstants.DEFAULT_BUILDING_POSITION))
			{
				if ((key.contains("n") && key.contains("s"))
						|| (key.contains("e") && key.contains("w")))
					throw new Exception(
							"Invalid build instructions (cannot instruct N and S together, or E and W together).");
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
			if (tiles_[p_.x + xDiff][p_.y + yDiff] != null)
				tiles_[p_.x + xDiff][p_.y + yDiff].deleteBuilding();
			tiles_[p_.x + xDiff][p_.y + yDiff] = new BuildingEntity(this,
					new Point((p_.x + xDiff) * ClayConstants.TILE_X,
							(p_.y + yDiff) * ClayConstants.TILE_Y),
					homeScreen_, key);
			newBuildings.add(tiles_[p_.x + xDiff][p_.y + yDiff]);
		}
		if (_buildTime > 0)
		{
			GolemBehaviorProcess behaviorProcess = ((GolemBehaviorProcess) homeScreen_
					.getProcess(GolemBehaviorProcess.class));
			for (BuildingEntity entity : newBuildings)
			{
				Behavior constructionBehavior = new Behavior(
						BehaviorData.getBehavior("construct-building"),
						tiles_[p_.x][p_.y], entity);
				constructionBehavior.setAssigningBuilding(entity);
				entity.addActiveBehavior(constructionBehavior);
				behaviorProcess.queueBehavior(constructionBehavior);
			}
		}
		List<Point> points = new ArrayList<Point>();
		for (BuildingEntity entity : newBuildings)
		{
			entity.setAllTiles(newBuildings);
			points.add(entity.getPoint());
		}

		Map<Integer, Object> map = new HashMap<Integer, Object>();
		map.put(ClayConstants.EVENT_MAP_UPDATE, points);

		EventBus.publish(new MapUpdateEvent(homeScreen_, map));
	}

	int text = 0;

	public String calculateTexture(BuildingEntity building_)
	{
		CityModel model = (CityModel) building_.getModel();
		Point point = new Point(building_.getGridX(), building_.getGridY());
		String returnState = ClayConstants.T_STATE_DEFAULT;
		for (String stateCode : _stateOrder)
		{
			String[] stateConditions = stateCode.split(",");
			int metConditions = 0;
			for (String stateCondition : stateConditions)
			{
				String[] stateNameAndParams = stateCondition.split(":");
				String stateName = stateNameAndParams[0];
				if (stateName.equals(ClayConstants.T_STATE_NONE_ABOVE))
				{
					if (model.getTileValue(point.x, point.y + 1) == null)
						metConditions++;
					continue;
				}
				else if (stateName
						.equals(ClayConstants.T_STATE_UNNATURAL_ABOVE))
				{
					if (model.getTileValue(point.x, point.y + 1) != null
							&& !model.getTileValue(point.x, point.y + 1)
									.isNatural())
						metConditions++;
					continue;
				}
				else if (stateName
						.equals(ClayConstants.T_STATE_UNNATURAL_BELOW))
				{
					if (model.getTileValue(point.x, point.y - 1) != null
							&& !model.getTileValue(point.x, point.y - 1)
									.isNatural())
						metConditions++;
					continue;
				}
				else if (stateName.equals(ClayConstants.T_STATE_UNNATURAL_LEFT))
				{
					if (model.getTileValue(point.x - 1, point.y) != null
							&& !model.getTileValue(point.x - 1, point.y)
									.isNatural())
						metConditions++;
					continue;
				}
				else if (stateName
						.equals(ClayConstants.T_STATE_UNNATURAL_RIGHT))
				{
					if (model.getTileValue(point.x + 1, point.y) != null
							&& !model.getTileValue(point.x + 1, point.y)
									.isNatural())
						metConditions++;
					continue;
				}
				else if (stateName.equals(ClayConstants.T_STATE_ADJACENT))
				{
					Point above = new Point(point.x, point.y + 1);
					Point below = new Point(point.x, point.y - 1);
					Point left = new Point(point.x - 1, point.y);
					Point right = new Point(point.x + 1, point.y);
					boolean isAdjacent = false;
					if (model.getTileValue(above.x, above.y) != null
							&& model.getTileValue(above.x, above.y)
									.getBuildingTag()
									.equals(stateNameAndParams[1]))
						isAdjacent = true;
					else if (model.getTileValue(below.x, below.y) != null
							&& model.getTileValue(below.x, below.y)
									.getBuildingTag()
									.equals(stateNameAndParams[1]))
						isAdjacent = true;
					else if (model.getTileValue(left.x, left.y) != null
							&& model.getTileValue(left.x, left.y)
									.getBuildingTag()
									.equals(stateNameAndParams[1]))
						isAdjacent = true;
					else if (model.getTileValue(right.x, right.y) != null
							&& model.getTileValue(right.x, right.y)
									.getBuildingTag()
									.equals(stateNameAndParams[1]))
						isAdjacent = true;
					if (isAdjacent)
						metConditions++;
					continue;
				}
				else if (stateName.equals(ClayConstants.T_STATE_TICK_FINISHED))
				{
					if (building_.isTickFinished())
						metConditions++;
					continue;
				}
				else if (stateName.equals(ClayConstants.T_STATE_STORAGE_FULL))
				{
					if (building_.getCopyOfHeldItems().size() >= _storageCapacity)
						metConditions++;
					continue;
				}
				else if (stateName.equals(ClayConstants.T_STATE_BLOCKED_LEFT))
				{
					if (model.getTileValue(
							building_.getGridX() - 1,
							building_.getGridY()) != null)
						metConditions++;
					continue;
				}
				else if (stateName
						.equals(ClayConstants.T_STATE_BLOCKED_LEFT_SUPPORT))
				{
					BuildingEntity left = model.getTileValue(
							building_.getGridX() - 1,
							building_.getGridY());
					if (left != null && left.isSupportBlock())
						metConditions++;
					continue;
				}
				else if (stateName.equals(ClayConstants.T_STATE_BURIED))
				{
					int neededDepth = Integer.parseInt(stateNameAndParams[1]);
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
				else if (stateName.equals(ClayConstants.T_STATE_BUILDING_ABOVE))
				{
					String buildingTag = stateNameAndParams[1];
					if (model.getTileValue(point.x, point.y + 1) != null
							&& !model.getTileValue(point.x, point.y + 1)
									.getBuildingTag().equals(buildingTag))
						metConditions++;
					continue;
				}
				else if (stateName.equals(ClayConstants.T_STATE_BUILDING_LEFT))
				{
					String buildingTag = stateNameAndParams[1];
					if (model.getTileValue(point.x - 1, point.y + 1) != null
							&& !model.getTileValue(point.x - 1, point.y + 1)
									.getBuildingTag().equals(buildingTag))
						metConditions++;
					continue;
				}
				else if (stateName.equals(ClayConstants.T_STATE_BUILDING_RIGHT))
				{
					String buildingTag = stateNameAndParams[1];
					if (model.getTileValue(point.x + 1, point.y) != null
							&& !model.getTileValue(point.x + 1, point.y)
									.getBuildingTag().equals(buildingTag))
						metConditions++;
					continue;
				}
				else if (stateName
						.equals(ClayConstants.T_STATE_BUILDING_ABOVE_NOT_STATE))
				{
					String state = stateNameAndParams[1];
					if (model.getTileValue(point.x, point.y + 1) != null
							&& !state.equals(model.getTileValue(
									point.x,
									point.y + 1).getState()))
						metConditions++;
					continue;
				}
				else if (stateName
						.equals(ClayConstants.T_STATE_BUILDING_LEFT_NOT_STATE))
				{
					String state = stateNameAndParams[1];
					if (model.getTileValue(point.x - 1, point.y) != null
							&& !state.equals(model.getTileValue(
									point.x - 1,
									point.y).getState()))
						metConditions++;
					continue;
				}
				else if (stateName
						.equals(ClayConstants.T_STATE_BUILDING_RIGHT_NOT_STATE))
				{
					String state = stateNameAndParams[1];
					if (model.getTileValue(point.x + 1, point.y) != null
							&& !state.equals(model.getTileValue(
									point.x + 1,
									point.y).getState()))
						metConditions++;
					continue;
				}
			}
			if (metConditions == stateConditions.length)
				returnState = stateCode;
		}
		return returnState;
	}

	public GenericBuilding transform(Point p_, BuildingEntity[][] tiles_)
	{
		String[] forms = _transformCode.split(",");
		GenericBuilding newBuilding = this;
		for (String form : forms)
		{
			String[] commandAndParams = form.split(":");
			if (commandAndParams[0].equals(ClayConstants.TRANSFORM_FLANKED))
			{
				if (tiles_[p_.x - 1][p_.y] != null
						&& tiles_[p_.x + 1][p_.y] != null)
				{
					newBuilding = BuildingData
							.getBuildingByTag(commandAndParams[1]);
				}
			}
			if (commandAndParams[0]
					.equals(ClayConstants.TRANSFORM_FLANKED_SUPPORT))
			{
				BuildingEntity left = tiles_[p_.x - 1][p_.y];
				BuildingEntity right = tiles_[p_.x + 1][p_.y];
				if (left != null && left.isSupportBlock() && right != null
						&& right.isSupportBlock())
				{
					newBuilding = BuildingData
							.getBuildingByTag(commandAndParams[1]);
				}
			}
			if (commandAndParams[0]
					.equals(ClayConstants.TRANSFORM_FLANKED_SUPPORT))
			{
				BuildingEntity left = tiles_[p_.x - 1][p_.y];
				BuildingEntity right = tiles_[p_.x + 1][p_.y];
				if ((left == null || !left.isSupportBlock())
						&& (right == null || !right.isSupportBlock()))
				{
					newBuilding = BuildingData
							.getBuildingByTag(commandAndParams[1]);
				}
			}
		}
		return newBuilding;
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

				Map<Integer, Object> map = new HashMap<Integer, Object>();
				map.put(ClayConstants.EVENT_ITEM_UPDATE, true);
				EventBus.publish(new MapUpdateEvent(building_.getHomeScreen(),
						map));
			}
			if (commandAndParams[0]
					.equals(ClayConstants.TICK_CODE_QUEUE_HARVEST_BEHAVIOR))
			{
				GolemBehaviorProcess behaviorProcess = ((GolemBehaviorProcess) building_
						.getHomeScreen().getProcess(GolemBehaviorProcess.class));

				Behavior behavior = new Behavior(
						BehaviorData
								.getBehavior(ClayConstants.BEHAVIOR_HARVEST),
						building_, commandAndParams[1]);
				behavior.setAssigningBuilding(building_);
				building_.addActiveBehavior(behavior);
				behaviorProcess.queueBehavior(behavior);
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

	public boolean getScalableDownards(String position_)
	{
		return _scalabilityMap.get(position_).contains("s");
	}

	public boolean getScalableUpwards(String position_)
	{
		return _scalabilityMap.get(position_).contains("n");
	}

	public boolean getScalableDiagonal(String position_)
	{
		String val = _scalabilityMap.get(position_);
		return val.contains("e") || val.contains("w");
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

	public String getMenuCategory()
	{
		return _menuCategory;
	}

	public double getLesserManaCost()
	{
		return _lesserManaCost;
	}

	public double getLesserManaCumulativeCost()
	{
		return _lesserManaCumulativeCost;
	}

	public int getCoolingRate()
	{
		return _coolingRate;
	}

	public int getHeatAbsorb()
	{
		return _heatAbsorb;
	}

	public int getInsulation()
	{
		return _insulation;
	}

	public int getHeatResistance()
	{
		return _heatResistance;
	}

	public int getFirstHeatThreshold()
	{
		return _firstHeatThreshold;
	}

	public int getSecondHeatThreshold()
	{
		return _secondHeatThreshold;
	}

	public int getThirdHeadThreshold()
	{
		return _thirdHeadThreshold;
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

	public boolean isStorage()
	{
		return _isStorage;
	}

	public boolean isSupport()
	{
		return _isSupport;
	}

	public boolean isBridge()
	{
		return _isBridge;
	}

	public int getStorageCapacity()
	{
		return _storageCapacity;
	}

	public String getTransform()
	{
		return _transformCode;
	}

	public boolean isUnlockedFromStart()
	{
		return _unlockedFromStart;
	}

	public String getExtraWeightConditions()
	{
		return _extraWeightConditions;
	}

	public String getConstructionItems()
	{
		return _constructionItems;
	}

	public byte getPsychologyType()
	{
		return _psychologyType;
	}

	public byte getPsychologyInfluence()
	{
		return _psychologyInfluence;
	}

	public boolean isValid(BuildingEntity building_)
	{
		return !building_.isInUse();
	}

	public Map<String, String> getValidPlacementMap()
	{
		return _validPlacementMap;
	}

	@Override
	public int compare(GenericBuilding gb1, GenericBuilding gb2)
	{
		return gb1.getBuildingName().compareTo(gb2.getBuildingName());
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
	private final Map<String, String> _scalabilityMap = new HashMap<String, String>();

	private final List<String> _stateOrder;

	private final String _buildingName;
	private final String _buildingTag;
	private final int _buildingIdentifier;
	private final String _buildingDescription;
	private String _menuCategory;
	private String _tickCompleteCode;
	private String _tickResetCode;
	private String _transformCode;
	private String _extraWeightConditions;
	private String _constructionItems;

	private final int _buildTime;
	private final int _tickStart;
	private final int _storageCapacity;

	private final int _coolingRate;
	private final int _heatAbsorb;
	private final int _insulation;
	private final int _heatResistance;
	private final int _firstHeatThreshold;
	private final int _secondHeatThreshold;
	private final int _thirdHeadThreshold;

	private final double _lesserManaCost;
	private final double _lesserManaCumulativeCost;
	private final double _greaterManaCost;
	private final double _greaterManaCumulativeCost;

	private final byte _psychologyType;
	private final byte _psychologyInfluence;

	private final boolean _isHouse;
	private final boolean _isNatural;
	private final boolean _isPassable;
	private final boolean _isStorage;
	private final boolean _isSupport;
	private final boolean _isBridge;

	private final boolean _unlockedFromStart;

}
