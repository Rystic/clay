package org.rystic.city.processes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

import org.bushe.swing.event.EventBus;
import org.bushe.swing.event.EventSubscriber;
import org.rystic.city.ai.GolemBrain;
import org.rystic.city.ai.util.BehaviorAndWeight;
import org.rystic.city.entities.golem.GolemEntity;
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

		_behaviorAssignmentRunnable = new BehaviorAssignmentRunnable();
		_behaviorAssignmentThread = new Thread(_behaviorAssignmentRunnable);
		_behaviorAssignmentThread.setDaemon(true);

		_pendingActiveGolems = new ArrayBlockingQueue<GolemEntity>(256);
		_activeGolems = new ArrayList<GolemEntity>();

		_behaviorAssignmentThread.start();

		EventBus.subscribe(MapUpdateEvent.class, this);
	}

	@Override
	public void execute()
	{
		while (!_pendingActiveGolems.isEmpty())
		{
			_activeGolems.add(_pendingActiveGolems.poll());
		}
		_iterator = _activeGolems.iterator();
		for (Iterator<GolemEntity> iterator = _activeGolems.iterator(); iterator
				.hasNext();)
		{
			if (!iterator.next().executeBehavior())
				iterator.remove();
		}
	}

	public void addInactiveGolem(GolemEntity golem_)
	{
		_behaviorAssignmentRunnable._pendingInactiveGolems.add(golem_);
	}

	public void queueBehavior(Behavior behavior_)
	{
		behavior_.setBehaviorProcess(this);
		_behaviorAssignmentRunnable._pendingBehaviors.add(behavior_);
	}

	public int getBehaviorCount(String behaviorTag)
	{
		return 0;
	}

	public void behaviorFailed(Behavior behavior_, int failureReason_, GolemEntity failedGolem_)
	{
		System.out.println("failed: " + failureReason_ + " " + behavior_.getBehaviorTag());
		CityModel model = (CityModel) _homeScreen.getModel();
		if (failedGolem_ != null)
			_behaviorAssignmentRunnable._pendingInactiveGolems
					.add(failedGolem_);
		switch (failureReason_)
		{
		case ClayConstants.BEHAVIOR_FAILED_NO_PATH:
			if (!behavior_.allGolemsInvalid(model.getGolems()))
			{
				_behaviorAssignmentRunnable._pendingBehaviors.add(behavior_);
				return;
			}
			break;
		case ClayConstants.BEHAVIOR_FAILED_OBSOLETE:
		case ClayConstants.BEHAVIOR_FAILED_MISSING_ITEM:
			return;
		default:
			break;
		}
		if (!behavior_.isPersonalTask())
			_behaviorAssignmentRunnable._failedBehaviorMap.get(failureReason_)
					.add(behavior_);
	}

	public void behaviorComplete(Behavior behavior_, GolemEntity golem_)
	{
		if (behavior_.getAssigningBuilding() != null)
		{
			behavior_.getAssigningBuilding().removeActiveBehavior(behavior_);
		}
		_behaviorAssignmentRunnable._pendingInactiveGolems.add(golem_);
	}

	@Override
	public void onEvent(MapUpdateEvent event_)
	{
		if (event_.getPoints() != null)
			_behaviorAssignmentRunnable._mapUpdate = true;
		if (event_.isItemUpdate())
			_behaviorAssignmentRunnable._itemUpdate = true;
	}

	private class BehaviorAssignmentRunnable implements Runnable
	{
		public BehaviorAssignmentRunnable()
		{
			_pendingInactiveGolems = new ArrayBlockingQueue<GolemEntity>(256);
			_pendingBehaviors = new ArrayBlockingQueue<Behavior>(256);

			_failedBehaviorMap = new HashMap<Integer, Queue<Behavior>>();
			_failedBehaviorMap.put(
					ClayConstants.BEHAVIOR_FAILED_NO_PATH,
					new ArrayBlockingQueue<Behavior>(256));
			_failedBehaviorMap.put(
					ClayConstants.BEHAVIOR_FAILED_NO_MATERIALS,
					new ArrayBlockingQueue<Behavior>(256));
			_failedBehaviorMap.put(
					ClayConstants.BEHAVIOR_FAILED_NO_STORAGE,
					new ArrayBlockingQueue<Behavior>(256));

			_mapUpdate = false;
			_itemUpdate = false;
			_storageUpdate = false;
		}

		@Override
		public void run()
		{
			Map<Integer, BehaviorAndWeight> assignedBehaviorMap = new LinkedHashMap<Integer, BehaviorAndWeight>();

			List<GolemEntity> inactiveGolems = new ArrayList<GolemEntity>();
			Queue<Behavior> unassignedBehaviors = new ArrayBlockingQueue<Behavior>(
					256);

			List<GolemEntity> neededBehaviorGolems = new ArrayList<GolemEntity>();

			GolemEntity golem;
			int bestGolem;
			BehaviorAndWeight bestScore;

			while (true)
			{
				while (!_pendingInactiveGolems.isEmpty())
				{
					inactiveGolems.add(_pendingInactiveGolems.poll());
				}

				while (!_pendingBehaviors.isEmpty())
				{
					unassignedBehaviors.add(_pendingBehaviors.poll());
				}

				checkAndReqeueValidBehaviors(unassignedBehaviors);

				if (!inactiveGolems.isEmpty())
				{
					golem = null;
					for (int i = 0; i < inactiveGolems.size(); i++)
					{
						golem = inactiveGolems.get(i);
						BehaviorAndWeight triple = GolemBrain
								.calculateBestBehavior(
										golem,
										golem.getNeededBehaviors(),
										true);
						if (triple != null)
						{
							triple._behavior
									.setBehaviorProcess(GolemBehaviorProcess.this);
							triple._behavior.setAssignedGolem(golem);
							int requiredComplete = triple._behavior
									.calculateRequired(golem);
							if (requiredComplete == ClayConstants.BEHAVIOR_PASSED)
							{
								golem.setBehavior(triple._behavior);
								neededBehaviorGolems.add(golem);
							}
							else
								behaviorFailed(
										triple._behavior,
										requiredComplete, null);
						}
					}
					inactiveGolems.removeAll(neededBehaviorGolems);
					neededBehaviorGolems.clear();
					if (inactiveGolems.isEmpty())
						continue;

					while (!unassignedBehaviors.isEmpty())
					{
						golem = null;
						bestScore = null;
						bestGolem = -1;
						Behavior behavior = unassignedBehaviors.poll();

						// check if behavior can claim it's materials

						// all possible failure conditions should be checked
						// before reaching this point.
						for (int i = 0; i < inactiveGolems.size(); i++)
						{

							golem = inactiveGolems.get(i);
							if (behavior.isInvalid(golem))
								continue;
							BehaviorAndWeight behaviorAndWeight = new BehaviorAndWeight(
									behavior,
									behavior.calculateBehaviorWeight(golem)
											+ behavior.getAddedWeight());
							if (behaviorAndWeight._weight <= 0)
								behaviorAndWeight._behavior
										.requiredFailed(golem);
							else
							{
								BehaviorAndWeight oldBehaviorAndWeight = assignedBehaviorMap
										.get(i);
								if ((oldBehaviorAndWeight == null || oldBehaviorAndWeight._weight < behaviorAndWeight._weight)
										&& (bestScore == null || bestScore._weight < behaviorAndWeight._weight))
								{
									bestScore = behaviorAndWeight;
									bestGolem = i;
								}
							}
						}
						if (bestScore != null)
						{
							BehaviorAndWeight oldBehaviorAndWeight = assignedBehaviorMap
									.get(bestGolem);
							if (oldBehaviorAndWeight != null)
								unassignedBehaviors
										.add(oldBehaviorAndWeight._behavior);
							assignedBehaviorMap.put(bestGolem, bestScore);
						}
						else
							behaviorFailed(
									behavior,
									ClayConstants.BEHAVIOR_FAILED_NO_PATH, null);
					}

					for (Integer i : assignedBehaviorMap.keySet())
					{
						BehaviorAndWeight behaviorAndWeight = assignedBehaviorMap
								.get(i);
						golem = inactiveGolems.get(i);
						int requiredComplete = behaviorAndWeight._behavior
								.calculateRequired(golem);
						if (requiredComplete == ClayConstants.BEHAVIOR_PASSED)
						{
							golem.setBehavior(behaviorAndWeight._behavior);
							behaviorAndWeight._behavior.setAssignedGolem(golem);
							inactiveGolems.remove(golem);
							_pendingActiveGolems.add(golem);
						}
						else
							behaviorFailed(
									behaviorAndWeight._behavior,
									requiredComplete, null);
					}
					assignedBehaviorMap.clear();
				}
			}
		}

		private void checkAndReqeueValidBehaviors(Queue<Behavior> unassignedBehaviors_)
		{
			if (_mapUpdate)
			{
				_mapUpdate = false;
				Queue<Behavior> unreachableBehaviors = _failedBehaviorMap
						.get(ClayConstants.BEHAVIOR_FAILED_NO_PATH);
				while (!unreachableBehaviors.isEmpty())
				{
					Behavior behavior = unreachableBehaviors.poll();
					behavior.clearInvalidEntities();
					unassignedBehaviors_.add(behavior);
				}
			}
			if (_itemUpdate)
			{
				_itemUpdate = false;
				Queue<Behavior> noMaterialBehaviors = _failedBehaviorMap
						.get(ClayConstants.BEHAVIOR_FAILED_NO_MATERIALS);
				while (!noMaterialBehaviors.isEmpty())
				{
					unassignedBehaviors_.add(noMaterialBehaviors.poll());
				}
			}
			if (_storageUpdate)
			{
				_storageUpdate = false;
				Queue<Behavior> noStorageBehaviors = _failedBehaviorMap
						.get(ClayConstants.BEHAVIOR_FAILED_NO_STORAGE);
				while (!noStorageBehaviors.isEmpty())
				{
					unassignedBehaviors_.add(noStorageBehaviors.poll());
				}
			}
		}

		private Queue<GolemEntity> _pendingInactiveGolems;
		private Queue<Behavior> _pendingBehaviors;

		private Map<Integer, Queue<Behavior>> _failedBehaviorMap;

		private boolean _mapUpdate;
		private boolean _itemUpdate;
		private boolean _storageUpdate;

	}

	private Thread _behaviorAssignmentThread;
	private BehaviorAssignmentRunnable _behaviorAssignmentRunnable;

	private Queue<GolemEntity> _pendingActiveGolems;
	private List<GolemEntity> _activeGolems;

	private Iterator<GolemEntity> _iterator;
}
