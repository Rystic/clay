package org.rystic.city.processes;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;

import org.bushe.swing.event.EventBus;
import org.bushe.swing.event.EventSubscriber;
import org.rystic.city.ai.GolemBrain;
import org.rystic.city.ai.util.BehaviorTriple;
import org.rystic.city.ai.util.BehaviorTripleQuickSort;
import org.rystic.city.generics.data.BehaviorData;
import org.rystic.city.generics.entities.GolemEntity;
import org.rystic.city.generics.objects.Behavior;
import org.rystic.city.util.MapUpdateEvent;
import org.rystic.main.ClayConstants;
import org.rystic.models.CityModel;
import org.rystic.screens.AbstractScreen;

public class GolemBehaviorProcess extends AbstractProcess implements
		EventSubscriber<MapUpdateEvent>
{
	public GolemBehaviorProcess(AbstractScreen homeScreen_)
	{
		super(homeScreen_);
		_golemList = ((CityModel) homeScreen_.getModel()).getGolems();
		_inactiveGolems = new ArrayList<GolemEntity>();
		_neededBehaviorsGolems = new ArrayList<GolemEntity>();
		_unassignedBehaviors = new ArrayList<Behavior>();
		_inProgressBehaviors = new ArrayList<Behavior>();
		_unreachableBehaviors = new ArrayList<Behavior>();
		_noMaterials = new ArrayList<Behavior>();
		_noAvailableGolems = new ArrayList<Behavior>();
		_noStorageAvailable = new ArrayList<Behavior>();
		_noUnoccupiedBuildings = new ArrayList<Behavior>();

		_failedBehaviors = new ArrayBlockingQueue<FinishedBehavior>(256);
		_completedBehaviors = new ArrayBlockingQueue<FinishedBehavior>(256);
		_mapEvents = new ArrayBlockingQueue<MapUpdateEvent>(256);

		EventBus.subscribe(MapUpdateEvent.class, this);
	}

	@Override
	public void execute()
	{
		_inactiveGolems.clear();
		for (GolemEntity golem : _golemList)
		{
			if (golem.isActive())
				golem.executeBehavior();
			else
				_inactiveGolems.add(golem);
		}
		if (_inactiveGolems.size() > 0)
			calculateBehavior();
		if (!_failedBehaviors.isEmpty())
			handleFailedBehaviors();
		if (!_completedBehaviors.isEmpty())
			handleCompletedBehaviors();
		if (!_mapEvents.isEmpty())
			handleMapEvents();
	}

	private void calculateBehavior()
	{
		List<BehaviorTriple> behaviorScores = new ArrayList<BehaviorTriple>();
		for (GolemEntity golem : _inactiveGolems)
		{
			calculateNeededBehaviors(golem);
			if (!golem.isActive())
			{
				int addPersonalTask = _random.nextInt(100);
				if (addPersonalTask > golem.getPersonalBehaviorChance())
				{
					BehaviorTriple golemWantedBehavior = GolemBrain
							.calculateBestBehavior(
									golem,
									BehaviorData.getWantedBehaviors(),
									false);
					if (golemWantedBehavior != null)
					{
						String behaviorTag = golemWantedBehavior._behavior
								.getBehaviorTag();
						golemWantedBehavior._behavior.setBehaviorProcess(this);
						golemWantedBehavior._weight += golem
								.getPersonalBehaviorWeight(behaviorTag);
						golem.addPersonalBehaviorWeight(behaviorTag);
						behaviorScores.add(golemWantedBehavior);
					}
				}
			}
		}
		_inactiveGolems.removeAll(_neededBehaviorsGolems);
		_neededBehaviorsGolems.clear();
		if (_inactiveGolems.isEmpty())
			return;
		List<Behavior> toBeAssigned = new ArrayList<Behavior>();
		_unassignedBehaviors.addAll(_noAvailableGolems);
		_noAvailableGolems.clear();
		toBeAssigned.addAll(_unassignedBehaviors);

		if (!toBeAssigned.isEmpty())
		{
			for (Behavior behavior : toBeAssigned)
			{
				// TODO everything that fails is being treated as unreachable.
				// New list is needed to handle buildings with multiple active
				// behaviors.
				if (_clearInvalid)
					behavior.clearInvalidEntities();
				for (GolemEntity golem : _inactiveGolems)
				{
					if (behavior.isInvalid(golem))
						continue;
					BehaviorTriple triple = new BehaviorTriple(golem, behavior,
							behavior.calculateBehaviorWeight(golem)
									+ behavior.getAddedWeight());
					if (triple._weight <= 0)
						triple._behavior.requiredFailed(golem);
					else
						behaviorScores.add(triple);
				}
				if (behavior.allGolemsInvalid(_golemList))
					_unreachableBehaviors.add(behavior);
			}
		}
		toBeAssigned.removeAll(_unreachableBehaviors);

		if (!behaviorScores.isEmpty())
		{
			BehaviorTriple[] scores = new BehaviorTriple[behaviorScores.size()];
			behaviorScores.toArray(scores);
			BehaviorTriple[] topScores = BehaviorTripleQuickSort.sort(scores);
			List<GolemEntity> invalidGolems = new ArrayList<GolemEntity>();
			List<Behavior> invalidBehaviors = new ArrayList<Behavior>();
			for (BehaviorTriple triple : topScores)
			{
				if (!invalidGolems.contains(triple._golem)
						&& !invalidBehaviors.contains(triple._behavior))
				{
					int requiredComplete = triple._behavior
							.calculateRequired(triple._golem);
					if (requiredComplete == ClayConstants.BEHAVIOR_PASSED)
					{
						triple._golem.setBehavior(triple._behavior);
						triple._behavior.setBehaviorProcess(this);
						triple._behavior.setAssignedGolem(triple._golem);
						_inProgressBehaviors.add(triple._behavior);
						invalidGolems.add(triple._golem);
						invalidBehaviors.add(triple._behavior);
					}
					else if (!triple._behavior.isPersonalTask())
					{
						if (requiredComplete == ClayConstants.BEHAVIOR_FAILED_NO_MATERIALS)
						{
							_noMaterials.add(triple._behavior);
							invalidBehaviors.add(triple._behavior);
						}
						else if (requiredComplete == ClayConstants.BEHAVIOR_FAILED_NO_STORAGE)
						{
							_noStorageAvailable.add(triple._behavior);
							invalidBehaviors.add(triple._behavior);
						}
						else if (requiredComplete == ClayConstants.BEHAVIOR_FAILED_NO_PATH)
						{
							triple._behavior.requiredFailed(triple._golem);
							if (triple._behavior.allGolemsInvalid(_golemList))
							{
								_unreachableBehaviors.add(triple._behavior);
								invalidBehaviors.add(triple._behavior);
							}
						}
						else if (requiredComplete == ClayConstants.BEHAVIOR_FAILED_BUILDING_OCCUPIED)
						{
							_unreachableBehaviors.add(triple._behavior);
							invalidBehaviors.add(triple._behavior);
						}
					}
					else
					{
						if (requiredComplete == ClayConstants.BEHAVIOR_FAILED_NO_PATH)
							triple._golem
									.addUnreachableBehavior(triple._behavior);
						else if (requiredComplete == ClayConstants.BEHAVIOR_FAILED_NO_MATERIALS)
							triple._golem
									.addNoMaterialsBehavior(triple._behavior);
						else if (requiredComplete == ClayConstants.BEHAVIOR_FAILED_NO_STORAGE)
							triple._golem
									.addNoStorageAvailableBehavior(triple._behavior);
						else if (requiredComplete == ClayConstants.BEHAVIOR_FAILED_BUILDING_OCCUPIED)
							triple._golem
									.addNoUnoccupiedBuildingsBehavior(triple._behavior);
					}
				}
			}
		}
		_clearInvalid = false;

		_unassignedBehaviors.removeAll(_unreachableBehaviors);
		_unassignedBehaviors.removeAll(_inProgressBehaviors);
		_unassignedBehaviors.removeAll(_noMaterials);
		_unassignedBehaviors.removeAll(_noStorageAvailable);

		for (Behavior behavior : _unassignedBehaviors)
		{
			if (behavior != null)
				behavior.increaseAddedWeight(ClayConstants.ADDED_WEIGHT_INCREASE);
		}
		_noAvailableGolems.addAll(_unassignedBehaviors);
		_unassignedBehaviors.clear();
	}

	private void calculateNeededBehaviors(GolemEntity golem_)
	{
		BehaviorTriple triple = GolemBrain.calculateBestBehavior(
				golem_,
				golem_.getNeededBehaviors(),
				true);
		if (triple != null)
		{
			triple._behavior.setBehaviorProcess(this);
			int requiredComplete = triple._behavior
					.calculateRequired(triple._golem);
			if (requiredComplete == ClayConstants.BEHAVIOR_PASSED)
			{
				triple._golem.setBehavior(triple._behavior);
				triple._behavior.setBehaviorProcess(this);
				triple._behavior.setAssignedGolem(triple._golem);
				_inProgressBehaviors.add(triple._behavior);
				_neededBehaviorsGolems.add(golem_);
			}
			else if (requiredComplete == ClayConstants.BEHAVIOR_FAILED_NO_PATH)
				triple._golem.addUnreachableBehavior(triple._behavior);
			else if (requiredComplete == ClayConstants.BEHAVIOR_FAILED_NO_MATERIALS)
				triple._golem.addNoMaterialsBehavior(triple._behavior);
			else if (requiredComplete == ClayConstants.BEHAVIOR_FAILED_NO_STORAGE)
				triple._golem.addNoStorageAvailableBehavior(triple._behavior);
			else if (requiredComplete == ClayConstants.BEHAVIOR_FAILED_BUILDING_OCCUPIED)
				triple._golem
						.addNoUnoccupiedBuildingsBehavior(triple._behavior);
		}
	}

	public void queueBehavior(Behavior behavior_)
	{
		behavior_.setBehaviorProcess(this);
		_unassignedBehaviors.add(behavior_);
	}

	public int getBehaviorCount(String behaviorTag_)
	{
		int count = 0;
		for (GolemEntity golem : _golemList)
		{
			Behavior behavior = golem.getCurrentBehavior();
			if (behavior != null
					&& behavior.getBehaviorTag().equals(behaviorTag_))
				count++;
		}
		return count;
	}

	public void behaviorComplete(Behavior behavior_)
	{
		_completedBehaviors.add(new FinishedBehavior(behavior_,
				ClayConstants.BEHAVIOR_PASSED));
	}

	private void handleCompletedBehaviors()
	{
		while (!_completedBehaviors.isEmpty())
		{
			FinishedBehavior completedBehavior = _completedBehaviors.poll();
			updateCompletedBehaviorLists(completedBehavior._behavior);
		}
	}

	private void updateCompletedBehaviorLists(Behavior behavior_)
	{
		_inProgressBehaviors.remove(behavior_);
		if (behavior_.getAssigningBuilding() != null)
			behavior_.getAssigningBuilding().removeActiveBehavior(behavior_);
		if (!_noUnoccupiedBuildings.isEmpty())
		{
			_unassignedBehaviors.addAll(_noUnoccupiedBuildings);
			_noUnoccupiedBuildings.clear();
		}
		clearGolemsNoUnoccupiedBuildingsBehaviors();
	}

	public void behaviorFailed(Behavior behavior_, int reason_)
	{
		_failedBehaviors.add(new FinishedBehavior(behavior_, reason_));
	}

	private void handleFailedBehaviors()
	{
		while (!_failedBehaviors.isEmpty())
		{
			FinishedBehavior failedBehavior = _failedBehaviors.poll();
			updateFailedBehaviorLists(
					failedBehavior._behavior,
					failedBehavior._reason);
		}
	}

	private void updateFailedBehaviorLists(Behavior behavior_, int reason_)
	{
		if (!behavior_.isPersonalTask())
		{
			if (reason_ == ClayConstants.BEHAVIOR_FAILED_NO_PATH)
			{
				if (behavior_.allGolemsInvalid(_golemList))
					_unreachableBehaviors.add(behavior_);
				else
					_unassignedBehaviors.add(behavior_);
			}
			else if (reason_ == ClayConstants.BEHAVIOR_FAILED_NO_MATERIALS)
			{
				_noMaterials.add(behavior_);
			}
			else if (reason_ == ClayConstants.BEHAVIOR_FAILED_NO_STORAGE)
			{
				_noStorageAvailable.add(behavior_);
			}
			else if (reason_ == ClayConstants.BEHAVIOR_FAILED_MISSING_ITEM
					|| reason_ == ClayConstants.BEHAVIOR_FAILED_OBSOLETE)
			{
				if (!behavior_.isHarvestTask())
					System.out.println(behavior_.getBehaviorTag() + " "
							+ reason_);
				_unassignedBehaviors.remove(behavior_);
				_inProgressBehaviors.remove(behavior_);
				_unreachableBehaviors.remove(behavior_);
				_noAvailableGolems.remove(behavior_);
				_noMaterials.remove(behavior_);
				_noStorageAvailable.remove(behavior_);
				_noUnoccupiedBuildings.remove(behavior_);
			}
			// TODO maybe make a map event for unoccupied/multi-active task
			// buildings?
			if (reason_ == ClayConstants.BEHAVIOR_FAILED_BUILDING_OCCUPIED)
			{
				_noUnoccupiedBuildings.add(behavior_);
			}
			else
			{
				_unassignedBehaviors.addAll(_noUnoccupiedBuildings);
				_noUnoccupiedBuildings.clear();
			}
		}
		else
		{
			if (reason_ == ClayConstants.BEHAVIOR_FAILED_NO_PATH)
				behavior_.getAssignedGolem().addUnreachableBehavior(behavior_);
			else if (reason_ == ClayConstants.BEHAVIOR_FAILED_NO_MATERIALS)
				behavior_.getAssignedGolem().addNoMaterialsBehavior(behavior_);
			else if (reason_ == ClayConstants.BEHAVIOR_FAILED_NO_STORAGE)
				behavior_.getAssignedGolem().addNoStorageAvailableBehavior(
						behavior_);
			else if (reason_ == ClayConstants.BEHAVIOR_FAILED_BUILDING_OCCUPIED)
				behavior_.getAssignedGolem().addNoUnoccupiedBuildingsBehavior(
						behavior_);
		}
		// TODO if there's ever a failure task for a building being interrupted
		// mid-task, it needs to clear no unoccupied buildings
		_inProgressBehaviors.remove(behavior_);
	}

	@Override
	public void onEvent(MapUpdateEvent event_)
	{
		_mapEvents.add(event_);
	}

	private void handleMapEvents()
	{
		while (!_mapEvents.isEmpty())
		{
			updateMapEventLists(_mapEvents.poll());
		}
	}

	private void updateMapEventLists(MapUpdateEvent event_)
	{
		// TODO make sure it's the home screen
		if (event_.getPoints() != null)
		{
			for (GolemEntity golem : _golemList)
			{
				golem.recalculatePathIfNecessary(event_.getPoints());
			}
			_clearInvalid = true;
			_unassignedBehaviors.addAll(_unreachableBehaviors);
			_unreachableBehaviors.clear();
			clearGolemsUnreachableBehaviors();
		}

		if (event_.isItemUpdate())
		{
			_unassignedBehaviors.addAll(_noMaterials);
			_noMaterials.clear();
			clearGolemsNoMaterials();
		}

		if (event_.isStorageAvailable())
		{
			_unassignedBehaviors.addAll(_noStorageAvailable);
			_noStorageAvailable.clear();
			clearGolemsNoStorageAvailableBehaviors();
		}
	}

	private void clearGolemsUnreachableBehaviors()
	{
		for (GolemEntity golem : _golemList)
		{
			golem.clearUnreachableBehaviors();
		}
	}

	private void clearGolemsNoMaterials()
	{
		for (GolemEntity golem : _golemList)
		{
			golem.clearNoMaterialsBehaviors();
		}
	}

	private void clearGolemsNoStorageAvailableBehaviors()
	{
		for (GolemEntity golem : _golemList)
		{
			golem.clearNoStorageAvailableBehaviors();
		}
	}

	private void clearGolemsNoUnoccupiedBuildingsBehaviors()
	{
		for (GolemEntity golem : _golemList)
		{
			golem.clearNoUnoccupiedBuildingsBehaviors();
		}
	}

	private class FinishedBehavior
	{
		public FinishedBehavior(Behavior behavior_, int reason_)
		{
			_behavior = behavior_;
			_reason = reason_;
		}

		Behavior _behavior;
		int _reason;
	}

	private Random _random = new Random();

	private List<GolemEntity> _golemList;
	private List<GolemEntity> _inactiveGolems;
	private List<GolemEntity> _neededBehaviorsGolems;

	private Queue<FinishedBehavior> _failedBehaviors;
	private Queue<FinishedBehavior> _completedBehaviors;
	private Queue<MapUpdateEvent> _mapEvents;

	private volatile List<Behavior> _unassignedBehaviors;
	private List<Behavior> _inProgressBehaviors;
	private List<Behavior> _unreachableBehaviors;
	private List<Behavior> _noMaterials;
	private List<Behavior> _noAvailableGolems;
	private List<Behavior> _noStorageAvailable;
	private List<Behavior> _noUnoccupiedBuildings;

	private boolean _clearInvalid;

}
