package city.generics.entities;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;

import main.ClayConstants;
import models.CityModel;

import org.newdawn.slick.opengl.Texture;

import screens.AbstractScreen;
import city.ai.calculators.GolemPersonalityCalculator;
import city.generics.GenericBehavior;
import city.generics.GenericGolem;
import city.generics.objects.Behavior;
import city.generics.objects.Item;

public class GolemEntity extends AbstractEntity
{
	public GolemEntity(GenericGolem golem_, int x_, int y_,
			AbstractScreen homeScreen_, GolemEntity parentGolem_)
	{
		super(new Point(x_, y_), homeScreen_);
		_model = (CityModel) homeScreen_.getModel();
		_golemTemplate = golem_;
		_moveInstructions = new ArrayBlockingQueue<Point>(256);
		_ignoredPersonalBehaviors = new HashMap<String, Integer>();
		_claimedBuilding = null;

		_unreachableBehaviors = new ArrayList<String>();
		_noMaterials = new ArrayList<String>();
		_noStorageAvailable = new ArrayList<String>();
		_noUnoccupiedBuildings = new ArrayList<String>();

		_mana = _golemTemplate.getStartingMana();
		_clay = _golemTemplate.getStartingClay();
		GolemPersonalityCalculator.buildPersonality(
				_homeScreen,
				this,
				parentGolem_);

		System.out.println("personality: " + _personality + "   psychology: "
				+ _psychology + "   golemanity: " + _golemanity);

		_maxSpeed = _golemTemplate.getMoveSpeed();
		calculateMoveVariation();

		_visible = true;
		_facingRight = true;
	}

	private void calculateMoveVariation()
	{
		if (_golemTemplate.getMoveVariation() > 0)
		{
			Random random = new Random();
			double decimalModifier = random.nextDouble();
			if (_golemTemplate.getMoveVariation() < 1)
			{
				if (decimalModifier > _golemTemplate.getMoveVariation())
					_maxSpeed += decimalModifier - _golemTemplate.getMoveVariation();
				else
					_maxSpeed += decimalModifier;
			}
			else if (_golemTemplate.getMoveVariation() == 1)
			{
				_maxSpeed += decimalModifier;
			}
			else
			{
				_maxSpeed += random
						.nextInt((int) (_golemTemplate.getMoveVariation() - 1))
						+ decimalModifier;
			}

		}
	}

	public void executeBehavior()
	{
		_mana -= .001;
		_clay -= .001;
		if (!_moveInstructions.isEmpty())
		{
			updatePosition();
			return;
		}
		else if (_tickCount > 0)
		{
			_tickCount -= _tickCountRate;
			if (_tickCount <= 0)
				_tickComplete = true;
			else
				return;
		}
		else if (_currentBehavior != null)
			_currentBehavior.executeBehavior(this, _commands.get(0));
	}

	public void setBehavior(Behavior behavior_)
	{
		_ignoredPersonalBehaviors.remove(behavior_.getBehaviorTag());
		_currentBehavior = behavior_;
		_currentBehavior.setAssignedGolem(this);
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
				_claimedBuilding.releaseItems();
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
			_currentBehavior = null;
		}
		_visible = true;
		if (_claimedBuilding != null)
		{
			_claimedBuilding.releaseItems();
			_claimedBuilding.setClaimingGolem(null);
			_claimedBuilding = null;
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

		double moveSpeed = _maxSpeed;
		if (!_heldItems.isEmpty())
		{
			moveSpeed /= 1.25;
			if (_golemTemplate.getMaximumClay() < 200)
				_clay -= .01;
			else
				_clay -= .001;
		}

		adjustMana(-_golemTemplate.getManaLostOnMovement());
		if (xDestination > _location.x)
		{
			_location.x += moveSpeed;
			_facingRight = true;
			if (_location.x > xDestination)
				_location.setLocation(xDestination, _location.y);
		}
		else if (xDestination < _location.x)
		{
			_location.x -= moveSpeed;
			_facingRight = false;
			if (_location.x < xDestination)
				_location.setLocation(xDestination, _location.y);
		}
		else
			xDone = true;

		if (yDestination > _location.y)
		{
			_location.y += moveSpeed / 1.5;
			if (_location.y > yDestination)
				_location.setLocation(_location.x, yDestination);
		}
		else if (yDestination < _location.y)
		{
			_location.y -= moveSpeed / 1.5;
			if (_location.y < yDestination)
				_location.setLocation(_location.x, yDestination);
		}
		else
			yDone = true;

		if (xDone && yDone)
			_moveInstructions.poll();
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
				return _golemTemplate.getLowManaLowClayTexture();
			}
			return _golemTemplate.getLowClayTexture();
		}
		else if (isLowMana())
			return _golemTemplate.getLowManaTexture();
		return _golemTemplate.getDefaultTexture();
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
		return _golemTemplate.getMaximumMana();
	}

	public boolean isLowMana()
	{
		return _golemTemplate.getLowManaThreshold() >= _mana;
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
		return _golemTemplate.getMaximumClay();
	}
	
	public boolean isLowClay()
	{
		return _golemTemplate.getLowClayThreshold() >= _clay;
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
		return _golemTemplate.getPersonalBehaviorChance();
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
		return _golemTemplate.getGolemTag();
	}

	public List<GenericBehavior> getNeededBehaviors()
	{
		return _golemTemplate.getNeededBehaviors();
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
		return (float) _golemTemplate.getTextureScaling();
	}

	public int getSpriteSize()
	{
		return _golemTemplate.getSpriteSize();
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
	
	@Override
	public CityModel getModel()
	{
		return (CityModel) _model;
	}

	private GenericGolem _golemTemplate;

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

	private int _tickCount;
	private int _tickCountRate;

	private byte _personality;
	private byte _psychology;
	private byte _golemanity;

	private boolean _tickComplete;
	private boolean _visible;
	private boolean _facingRight;

}
