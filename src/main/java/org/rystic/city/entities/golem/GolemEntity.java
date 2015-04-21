package org.rystic.city.entities.golem;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;

import org.bushe.swing.event.EventBus;
import org.newdawn.slick.opengl.Texture;
import org.rystic.city.ai.calculators.GolemPersonalityCalculator;
import org.rystic.city.entities.AbstractEntity;
import org.rystic.city.entities.building.BuildingEntity;
import org.rystic.city.generics.GenericBehavior;
import org.rystic.city.generics.GenericGolem;
import org.rystic.city.generics.objects.Behavior;
import org.rystic.city.generics.objects.Item;
import org.rystic.city.processes.TrafficProcess;
import org.rystic.city.util.MapUpdateEvent;
import org.rystic.main.ClayConstants;
import org.rystic.models.CityModel;
import org.rystic.screens.AbstractScreen;

public class GolemEntity extends AbstractEntity
{
	public GolemEntity(GenericGolem golem_, int x_, int y_,
			AbstractScreen homeScreen_, GolemEntity parentGolem_)
	{
		super(new Point(x_, y_), homeScreen_);
		_model = (CityModel) homeScreen_.getModel();
		_genericGolem = golem_;
		_moveInstructions = new ArrayBlockingQueue<Point>(256);
		_ignoredPersonalBehaviors = new HashMap<String, Integer>();
		_claimedBuilding = null;

		_unreachableBehaviors = new ArrayList<String>();
		_noMaterials = new ArrayList<String>();
		_noStorageAvailable = new ArrayList<String>();
		_noUnoccupiedBuildings = new ArrayList<String>();

		_mana = _genericGolem.getStartingMana();
		_clay = _genericGolem.getStartingClay();
		GolemPersonalityCalculator.buildPersonality(
				_homeScreen,
				this,
				parentGolem_);

		System.out.println("personality: " + _personality + "   psychology: "
				+ _psychology + "   golemanity: " + _golemanity);

		calculateMoveVariation();
		_horizontalMoveScore = 0;
		_verticalMoveScore = 0;

		_visible = true;
		_facingRight = true;
	}

	private void calculateMoveVariation()
	{
		_maxSpeed = _genericGolem.getMoveSpeed();
		if (_genericGolem.getMoveVariation() > 0)
		{
			Random random = new Random();
			double decimalModifier = random.nextDouble();
			if (_genericGolem.getMoveVariation() < 1)
			{
				if (decimalModifier > _genericGolem.getMoveVariation())
					_maxSpeed += decimalModifier
							- _genericGolem.getMoveVariation();
				else
					_maxSpeed += decimalModifier;
			}
			else if (_genericGolem.getMoveVariation() == 1)
			{
				_maxSpeed += decimalModifier;
			}
			else
			{
				_maxSpeed += random.nextInt((int) (_genericGolem
						.getMoveVariation() - 1)) + decimalModifier;
			}
		}
		_currentSpeed = _maxSpeed;
	}

	public boolean executeBehavior()
	{
		_mana -= .001;
		_clay -= .001;
		if (!_moveInstructions.isEmpty())
		{
			updatePosition();
			return true;
		}
		else if (_tickCount > 0)
		{
			_tickCount -= _tickCountRate;
			if (_tickCount <= 0)
				_tickComplete = true;
			else
				return true;
		}
		else if (_currentBehavior != null)
			_currentBehavior.executeBehavior(this, _commands.get(0));
		else
			return false;
		return true;
	}

	public void setBehavior(Behavior behavior_)
	{
		_ignoredPersonalBehaviors.remove(behavior_.getBehaviorTag());
		_currentBehavior = behavior_;
		_commands = _currentBehavior.getCommands();
	}

	public void instructionComplete()
	{
		if (_currentBehavior == null)
			return;
		_tickComplete = false;
		_tickCount = 0;
		_tickCountRate = 0;
		_commands.remove(0);
		if (_commands.isEmpty())
		{
			_currentBehavior.complete(this);
			_currentBehavior = null;
			_visible = true;
			if (_claimedBuilding != null)
			{
				Map<Integer, Object> paramMap = new HashMap<Integer, Object>();
				paramMap.put(
						ClayConstants.EVENT_BUILDING_UNCLAIMED,
						_claimedBuilding);
				EventBus.publish(new MapUpdateEvent(_homeScreen, paramMap));
				_claimedBuilding.releaseClaimedItems();
				_claimedBuilding.setClaimingGolem(null);
				_claimedBuilding = null;
			}
		}
	}

	public void behaviorFailed(int reason_)
	{
		if (_currentBehavior != null)
		{
			_currentBehavior.failed(this, reason_);
			_commands.clear();
			_currentBehavior = null;
		}
		_visible = true;
		if (_claimedBuilding != null)
		{
			Map<Integer, Object> paramMap = new HashMap<Integer, Object>();
			paramMap.put(
					ClayConstants.EVENT_BUILDING_UNCLAIMED,
					_claimedBuilding);
			EventBus.publish(new MapUpdateEvent(_homeScreen, paramMap));
			try
			{
				_claimedBuilding.releaseClaimedItems();
				_claimedBuilding.setClaimingGolem(null);
				_claimedBuilding = null;
			} catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	public boolean isActive()
	{
		return _currentBehavior != null;
	}

	public void addMoveInstructions(Queue<Point> moveInstructions_)
	{
		_moveInstructions = moveInstructions_;
	}

	public void updatePosition()
	{
		Point destination = _moveInstructions.peek();
		double xDestination = destination.x;
		double yDestination = destination.y;
		boolean xDone = false;
		boolean yDone = false;

		double moveSpeed = _currentSpeed;

		_clay -= .01;

		if (!_heldItems.isEmpty())
			moveSpeed /= 1.25;

		if (isLowMana())
			moveSpeed /= 1.25;

		if (yDestination != _location.y)
		{
			if (yDestination > _location.y)
				_verticalMoveScore += moveSpeed / 1.5;
			else
				_verticalMoveScore -= moveSpeed / 1.5;
		}
		if (xDestination != _location.x)
		{
			if (xDestination > _location.x)
				_horizontalMoveScore += moveSpeed;
			else
				_horizontalMoveScore -= moveSpeed;
		}

		if (_verticalMoveScore != 0)
		{
			if (_verticalMoveScore > 0)
			{
				_location.y += (int) _verticalMoveScore;
				if (_location.y >= yDestination)
				{
					_location.setLocation(_location.x, yDestination);
					_verticalMoveScore = 0;
				}
				else
					_verticalMoveScore %= 1;
			}
			else
			{
				_location.y += (int) _verticalMoveScore;
				if (_location.y <= yDestination)
				{
					_location.setLocation(_location.x, yDestination);
					_verticalMoveScore = 0;
				}
				else
					_verticalMoveScore %= 1;
			}
		}
		else
			yDone = true;

		if (_horizontalMoveScore != 0)
		{
			if (_horizontalMoveScore > 0)
			{
				_location.x += (int) _horizontalMoveScore;
				_facingRight = true;
				if (_location.x >= xDestination)
				{
					_location.setLocation(xDestination, _location.y);
					_horizontalMoveScore = 0;
				}
				else
					_horizontalMoveScore %= 1;
			}
			else
			{
				_location.x += (int) _horizontalMoveScore;
				_facingRight = false;
				if (_location.x <= xDestination)
				{
					_location.setLocation(xDestination, _location.y);
					_horizontalMoveScore = 0;
				}
				else
					_horizontalMoveScore %= 1;
			}
		}
		else
			xDone = true;

		if (xDone && yDone)
		{
			_moveInstructions.poll();
			((TrafficProcess) _homeScreen.getProcess(TrafficProcess.class))
					.doUpdate();
		}
	}

	public void recalculatePathIfNecessary(List<Point> points_)
	{
		if (_moveInstructions.isEmpty())
			return;
		boolean recalculate = false;
		for (Point p : points_)
		{
			if (_moveInstructions.contains(p))
			{
				recalculate = true;
				break;
			}
		}
		if (recalculate)
			_moveInstructions.clear();
	}

	public void claimItemForBuilding(BuildingEntity building_, Item item_)
	{
		for (Item item : _heldItems)
		{
			if (item.equals(item_))
				building_.claimItem(item);
		}
	}

	public void addPersonalBehaviorWeight(String tag_)
	{
		Integer value = _ignoredPersonalBehaviors.get(tag_);
		if (value == null)
			_ignoredPersonalBehaviors.put(tag_, 0);
		int addedWeight = _ignoredPersonalBehaviors.get(tag_)
				+ ClayConstants.ADDED_WEIGHT_INCREASE;
		if (addedWeight > ClayConstants.ADDED_WEIGHT_CAP)
			addedWeight = ClayConstants.ADDED_WEIGHT_CAP;
		_ignoredPersonalBehaviors.put(tag_, addedWeight);
	}

	public int getPersonalBehaviorWeight(String tag_)
	{
		Integer value = _ignoredPersonalBehaviors.get(tag_);
		if (value == null)
			_ignoredPersonalBehaviors.put(tag_, 0);
		return _ignoredPersonalBehaviors.get(tag_);
	}

	public Behavior getCurrentBehavior()
	{
		return _currentBehavior;
	}

	public void setTickAndRate(int tick_, int tickRate_)
	{
		_tickCount = tick_;
		_tickCountRate = tickRate_;
	}

	public boolean isTickComplete()
	{
		return _tickComplete;
	}

	public Texture getCurrentTexture()
	{
		if (isLowClay())
		{
			if (isLowMana())
			{
				return _genericGolem.getLowManaLowClayTexture();
			}
			return _genericGolem.getLowClayTexture();
		}
		else if (isLowMana())
			return _genericGolem.getLowManaTexture();
		return _genericGolem.getDefaultTexture();
	}

	public void adjustMana(double value_)
	{
		_mana += value_;
	}

	public double getMana()
	{
		return _mana;
	}

	public double getMaximumMana()
	{
		return _genericGolem.getMaximumMana();
	}

	public boolean isLowMana()
	{
		return _genericGolem.getLowManaThreshold() >= _mana;
	}

	public void adjustClay(double value_)
	{
		_clay += value_;
	}

	public double getClay()
	{
		return _clay;
	}

	public double getMaximumClay()
	{
		return _genericGolem.getMaximumClay();
	}

	public boolean isLowClay()
	{
		return _genericGolem.getLowClayThreshold() >= _clay;
	}

	public void setVisible(boolean visible_)
	{
		_visible = visible_;
	}

	public boolean isVisible()
	{
		return _visible;
	}

	public int getPersonalBehaviorChance()
	{
		return _genericGolem.getPersonalBehaviorChance();
	}

	public void setClaimedBuilding(BuildingEntity claimedBuilding_)
	{
		_claimedBuilding = claimedBuilding_;
	}

	public BuildingEntity getClaimedBuilding()
	{
		return _claimedBuilding;
	}

	public String getGolemTag()
	{
		return _genericGolem.getGolemTag();
	}

	public List<GenericBehavior> getNeededBehaviors()
	{
		return _genericGolem.getNeededBehaviors();
	}

	public byte getPersonality()
	{
		return _personality;
	}

	public byte getPsychology()
	{
		return _psychology;
	}

	public byte getGolemanity()
	{
		return _golemanity;
	}

	public float getTextureScaling()
	{
		return (float) _genericGolem.getTextureScaling();
	}

	public int getSpriteSize()
	{
		return _genericGolem.getSpriteSize();
	}

	public void setPersonality(byte personality_)
	{
		_personality = personality_;
	}

	public void setPsychology(byte psychology_)
	{
		_psychology = psychology_;
	}

	public void setGolemanity(byte golemanity_)
	{
		_golemanity = golemanity_;
	}

	public List<String> getUnreachableBehaviors()
	{
		return _unreachableBehaviors;
	}

	public List<String> getNoMaterials()
	{
		return _noMaterials;
	}

	public List<String> getNoStorageAvailable()
	{
		return _noStorageAvailable;
	}

	public List<String> getNoUnoccupiedBuildings()
	{
		return _noUnoccupiedBuildings;
	}

	public void addUnreachableBehavior(Behavior behavior_)
	{
		_unreachableBehaviors.add(behavior_.getBehaviorTag());
	}

	public void addNoMaterialsBehavior(Behavior behavior_)
	{
		_noMaterials.add(behavior_.getBehaviorTag());
	}

	public void addNoStorageAvailableBehavior(Behavior behavior_)
	{
		_noStorageAvailable.add(behavior_.getBehaviorTag());
	}

	public void addNoUnoccupiedBuildingsBehavior(Behavior behavior_)
	{
		_noUnoccupiedBuildings.add(behavior_.getBehaviorTag());
	}

	public void clearUnreachableBehaviors()
	{
		_unreachableBehaviors.clear();
	}

	public void clearNoMaterialsBehaviors()
	{
		_noMaterials.clear();
	}

	public void clearNoStorageAvailableBehaviors()
	{
		_noStorageAvailable.clear();
	}

	public void clearNoUnoccupiedBuildingsBehaviors()
	{
		_noUnoccupiedBuildings.clear();
	}

	public boolean isFacingRight()
	{
		return _facingRight;
	}

	public int getTrafficWeight()
	{
		return _genericGolem.getTrafficWeight()
				+ (isLowMana() ? _genericGolem.getTrafficWeight() : 0);
	}

	public void updateMoveSpeed()
	{
		_currentSpeed = _maxSpeed;
		if (_genericGolem.getTrafficWeight() == 0)
			return;
		BuildingEntity building = _model.getTileValue(getGridX(), getGridY());
		if (building != null)
		{
			if (building.getTraffic() == _genericGolem.getTrafficWeight())
				return;
			int netTrafficWeight = building.getNetTrafficWeight();
			if (netTrafficWeight > 0)
			{
				_currentSpeed /= netTrafficWeight;
			}
		}
	}

	@Override
	public CityModel getModel()
	{
		return (CityModel) _model;
	}

	private GenericGolem _genericGolem;

	private Map<String, Integer> _ignoredPersonalBehaviors;

	private CityModel _model;
	private Behavior _currentBehavior;

	private List<String> _unreachableBehaviors;
	private List<String> _noMaterials;
	private List<String> _noStorageAvailable;
	private List<String> _noUnoccupiedBuildings;

	private List<String> _commands;
	private Queue<Point> _moveInstructions;

	private BuildingEntity _claimedBuilding;

	private double _mana;
	private double _clay;

	private double _maxSpeed;
	private double _currentSpeed;
	private double _horizontalMoveScore;
	private double _verticalMoveScore;

	private int _tickCount;
	private int _tickCountRate;

	private byte _personality;
	private byte _psychology;
	private byte _golemanity;

	private boolean _tickComplete;
	private boolean _visible;
	private boolean _facingRight;

}
