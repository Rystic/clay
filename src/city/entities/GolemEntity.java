package city.entities;

import java.awt.Point;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;

import models.CityModel;
import screens.AbstractScreen;
import city.ai.objects.Behavior;
import city.generics.GenericGolem;

public class GolemEntity extends AbstractEntity
{
	public GolemEntity(GenericGolem golem_, int x_, int y_,
			AbstractScreen homeScreen_)
	{
		super(new Point(x_, y_), homeScreen_);
		_model = (CityModel) homeScreen_.getModel();
		_golem = golem_;
		_moveInstructions = new ArrayBlockingQueue<Point>(256);
		_claimedBuilding = null;
		_mana = _golem.getStartingMana();
		_clay = _golem.getStartingClay();
		_maxSpeed = _golem.getMoveSpeed();
		if (_golem.getMoveVariation() > 0)
		{
			Random random = new Random();
			double decimalModifier = random.nextDouble();
			if (_golem.getMoveVariation()  < 1)
			{
				if (decimalModifier > _golem.getMoveVariation())
					_maxSpeed += decimalModifier - _golem.getMoveVariation();
				else _maxSpeed += decimalModifier;
			}
			else if (_golem.getMoveVariation() == 1)
			{
				_maxSpeed += decimalModifier;
			}
			else
			{
				_maxSpeed += random
						.nextInt((int) (_golem.getMoveVariation() - 1))
						+ decimalModifier;
			}

		}
		_visible = true;
	}

	public void calculateBehavior()
	{
		if (!_moveInstructions.isEmpty())
		{
			updatePosition();
			return;
		}
		if (_tickCount > 0)
		{
			_tickCount -= _tickCountRate;
			if (_tickCount <= 0)
				_tickComplete = true;
			else
				return;
		}
		if (_currentBehavior != null)
			_currentBehavior.executeBehavior(this, _commands.get(0));
	}

	public void setBehavior(Behavior behavior_)
	{
		_currentBehavior = behavior_;
		_commands = _currentBehavior.getCommands();
	}

	public void instructionComplete()
	{
		_tickComplete = false;
		_tickCount = 0;
		_tickCountRate = 0;
		_commands.remove(0);
		if (_commands.isEmpty())
		{
			_currentBehavior.complete();
			_currentBehavior = null;
			_visible = true;
			if (_claimedBuilding != null)
				_claimedBuilding.release();
		}
	}

	public void behaviorFailed(int reason_)
	{
		_currentBehavior.failed(this, reason_);
		_currentBehavior = null;
		_visible = true;
		if (_claimedBuilding != null)
			_claimedBuilding.release();
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
		// TODO make sure path didn't get obstructed
		Point destination = _moveInstructions.peek();
		double xDestination = destination.x;
		double yDestination = destination.y;
		boolean xDone = false;
		boolean yDone = false;

		double moveSpeed = _maxSpeed;
		if (!_heldItems.isEmpty())
			moveSpeed /= 1.25;

		adjustMana(-_golem.getManaLostOnMovement());
		if (xDestination > _location.x)
		{
			_location.x += moveSpeed;
			if (_location.x > xDestination)
				_location.setLocation(xDestination, _location.y);
		}
		else if (xDestination < _location.x)
		{
			_location.x -= moveSpeed;
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

	public void setTickAndRate(int tick_, int tickRate_)
	{
		_tickCount = tick_;
		_tickCountRate = tickRate_;
	}

	public boolean isTickComplete()
	{
		return _tickComplete;
	}

	public void adjustMana(double value_)
	{
		_mana += value_;
	}

	public double getMana()
	{
		return _mana;
	}

	public void adjustClay(double value_)
	{
		_clay += value_;
	}

	public double getClay()
	{
		return _clay;
	}

	public void setVisible(boolean visible_)
	{
		_visible = visible_;
	}

	public boolean isVisible()
	{
		return _visible;
	}

	public void setClaimedBuilding(BuildingEntity claimedBuilding_)
	{
		_claimedBuilding = claimedBuilding_;
	}

	public BuildingEntity getClaimedBuilding()
	{
		return _claimedBuilding;
	}

	@Override
	public CityModel getModel()
	{
		return (CityModel) _model;
	}

	private GenericGolem _golem;

	private CityModel _model;
	private Behavior _currentBehavior;

	private List<String> _commands;
	private Queue<Point> _moveInstructions;

	private BuildingEntity _claimedBuilding;

	private double _mana;
	private double _clay;
	private double _maxSpeed;

	private int _tickCount;
	private int _tickCountRate;
	private boolean _tickComplete;

	private boolean _visible;

}
